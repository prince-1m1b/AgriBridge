package com.example.agribridge.ui.dashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agribridge.R
import com.example.agribridge.api.listener.DiscoverApiRequest
import com.example.agribridge.api.repository.DiscoverRepository
import com.example.agribridge.databinding.FragmentHomeBinding
import com.example.agribridge.model.BannerModel
import com.example.agribridge.model.DiscoverResponse
import com.example.agribridge.model.LoanModel
import com.example.agribridge.model.MarketModel
import com.example.agribridge.model.SchemeModel
import com.example.agribridge.model.SubsidyModel
import com.example.agribridge.ui.dashboard.adapters.BannerAdapter
import com.example.agribridge.ui.dashboard.adapters.CategoryAdapter
import com.example.agribridge.ui.dashboard.adapters.LoanAdapter
import com.example.agribridge.ui.dashboard.adapters.MarketAdapter
import com.example.agribridge.ui.dashboard.adapters.SchemeAdapter
import com.example.agribridge.ui.dashboard.adapters.SubsidyAdapter
import com.example.agribridge.utils.Constant.UserDetails
import com.example.agribridge.utils.LanguageManager.getLanguage
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.getData
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.DiscoverViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val discoverViewModel: DiscoverViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                DiscoverViewModel(DiscoverRepository(requireContext().request(DiscoverApiRequest::class.java)))
            }
        }
        ViewModelProvider(requireActivity(), factory)[DiscoverViewModel::class.java]
    }

    private val schemesList = ArrayList<SchemeModel>()
    private val loansList = ArrayList<LoanModel>()
    private val marketsList = ArrayList<MarketModel>()
    private val subsidiesList = ArrayList<SubsidyModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hasData = discoverViewModel.discoverData.value != null
        if (!hasData) {
            binding.scrollView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.scrollView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

        init()
        onClicks()
        setAdapter()
        setObserver()
    }

    private fun init() {
        val preference = Preference(requireContext())
        val state = preference.getStringPreferenceForUser(UserDetails.USER_STATE)
        val district = preference.getStringPreferenceForUser(UserDetails.USER_DISTRICT)
        val langCode = getLanguage(requireContext())
        val language = when (langCode) {
            "hi" -> "hindi"
            "kn" -> "kannada"
            else -> "english"
        }

        if (discoverViewModel.discoverData.value != null && discoverViewModel.currentLanguage == language) {
            return
        }
        discoverViewModel.fetchHomeDiscoverData(state, district, language)
    }

    private fun onClicks() {
        binding.apply {
            tvSchemeViewAll.setOnClickListener {
                startActivity(
                    android.content.Intent(
                        requireContext(),
                        com.example.agribridge.ui.dashboard.activities.DiscoverListActivity::class.java
                    ).apply {
                        putExtra("extra_type", "promotion")
                        putExtra("extra_title", getString(R.string.government_schemes))
                    })
            }
            tvLoanViewAll.setOnClickListener {
                startActivity(
                    android.content.Intent(
                        requireContext(),
                        com.example.agribridge.ui.dashboard.activities.DiscoverListActivity::class.java
                    ).apply {
                        putExtra("extra_type", "loan")
                        putExtra("extra_title", getString(R.string.loans))
                    })
            }
            tvMarketViewAll.setOnClickListener {
                startActivity(
                    android.content.Intent(
                        requireContext(),
                        com.example.agribridge.ui.dashboard.activities.DiscoverListActivity::class.java
                    ).apply {
                        putExtra("extra_type", "marketplace")
                        putExtra("extra_title", getString(R.string.market_place))
                    })
            }
            tvSeeAllSubsidyTitle.setOnClickListener {
                startActivity(
                    android.content.Intent(
                        requireContext(),
                        com.example.agribridge.ui.dashboard.activities.DiscoverListActivity::class.java
                    ).apply {
                        putExtra("extra_type", "subsidies")
                        putExtra("extra_title", getString(R.string.subsidies))
                    })
            }
        }
    }

    private fun setAdapter() {
        binding.apply {
            rvCategories.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvCategories.adapter = CategoryAdapter(
                listOf("All", "Schemes", "Markets", "Subsidies", "Loans")
            ) { category, _ ->
                filterCategory(category)
            }

            // Banner
            rvBanner.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvBanner.adapter = BannerAdapter(
                listOf(
                    BannerModel(
                        "Discover Local Markets Near You", "Find the best prices for your produce"
                    )
                )
            )

            // Schemes
            rvSchemes.layoutManager = LinearLayoutManager(requireContext())
            rvSchemes.adapter = SchemeAdapter { scheme ->
                val dialog =
                    com.example.agribridge.ui.dashboard.dialogs.SchemeDetailDialogFragment.newInstance(
                        scheme
                    )
                dialog.show(
                    parentFragmentManager,
                    com.example.agribridge.ui.dashboard.dialogs.SchemeDetailDialogFragment.TAG
                )
            }.apply {
                submitList(ArrayList(schemesList))
            }

            // Loans
            rvLoans.layoutManager = LinearLayoutManager(requireContext())
            rvLoans.adapter = LoanAdapter { loan ->
                val dialog =
                    com.example.agribridge.ui.dashboard.dialogs.LoanDetailDialogFragment.newInstance(
                        loan
                    )
                dialog.show(
                    parentFragmentManager,
                    com.example.agribridge.ui.dashboard.dialogs.LoanDetailDialogFragment.TAG
                )
            }.apply {
                submitList(ArrayList(loansList))
            }

            // Markets
            rvMarketPlace.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvMarketPlace.adapter = MarketAdapter { market ->
                val dialog =
                    com.example.agribridge.ui.dashboard.dialogs.MarketDetailDialogFragment.newInstance(
                        market
                    )
                dialog.show(
                    parentFragmentManager,
                    com.example.agribridge.ui.dashboard.dialogs.MarketDetailDialogFragment.TAG
                )
            }.apply {
                submitList(ArrayList(marketsList))
            }

            // Subsidies
            rvSubsidies.layoutManager = LinearLayoutManager(requireContext())
            rvSubsidies.adapter = SubsidyAdapter { subsidy ->
                val dialog =
                    com.example.agribridge.ui.dashboard.dialogs.SubsidyDetailDialogFragment.newInstance(
                        subsidy
                    )
                dialog.show(
                    parentFragmentManager,
                    com.example.agribridge.ui.dashboard.dialogs.SubsidyDetailDialogFragment.TAG
                )
            }.apply {
                submitList(ArrayList(subsidiesList))
            }
        }
    }

    private fun filterCategory(category: String) {
        binding.apply {
            val isAll = category == "All"

            // Banner visibility - only show on "All"
            rvBanner.visibility = if (isAll) View.VISIBLE else View.GONE

            // Section visibility based on category
            sectionSchemes.visibility =
                if (isAll || category == "Schemes") View.VISIBLE else View.GONE

            sectionMarket.visibility =
                if (isAll || category == "Markets") View.VISIBLE else View.GONE

            sectionSubsidy.visibility =
                if (isAll || category == "Subsidies") View.VISIBLE else View.GONE

            sectionLoans.visibility = if (isAll || category == "Loans") View.VISIBLE else View.GONE
        }
    }

    private fun setObserver() {
        discoverViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            }
        }

        discoverViewModel.discoverData.observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse?.statusCode == 200 || apiResponse?.type?.equals(
                    "success", ignoreCase = true
                ) == true
            ) {
                apiResponse.data?.let { data ->
                    val response = getData(data, DiscoverResponse::class.java)

                    // Schemes
                    val schemeList = response?.schemes?.map { it.toSchemeModel() } ?: emptyList()
                    schemesList.clear()
                    schemesList.addAll(schemeList)
                    (binding.rvSchemes.adapter as? SchemeAdapter)?.submitList(ArrayList(schemesList))
                    updateEmptyState(
                        schemesList.isEmpty(), binding.rvSchemes, binding.tvEmptySchemes
                    )

                    // Loans
                    val loanList = response?.loans?.map { it.toLoanModel() } ?: emptyList()
                    loansList.clear()
                    loansList.addAll(loanList)
                    (binding.rvLoans.adapter as? LoanAdapter)?.submitList(ArrayList(loansList))
                    updateEmptyState(loansList.isEmpty(), binding.rvLoans, binding.tvEmptyLoans)

                    // Markets
                    val marketList =
                        response?.marketplace?.map { it.toMarketModel() } ?: emptyList()
                    marketsList.clear()
                    marketsList.addAll(marketList)
                    (binding.rvMarketPlace.adapter as? MarketAdapter)?.submitList(
                        ArrayList(
                            marketsList
                        )
                    )
                    updateEmptyState(
                        marketsList.isEmpty(), binding.rvMarketPlace, binding.tvEmptyMarketPlace
                    )

                    // Subsidies
                    val subsidyList =
                        response?.subsidies?.map { it.toSubsidyModel() } ?: emptyList()
                    subsidiesList.clear()
                    subsidiesList.addAll(subsidyList)
                    (binding.rvSubsidies.adapter as? SubsidyAdapter)?.submitList(
                        ArrayList(
                            subsidiesList
                        )
                    )
                    updateEmptyState(
                        subsidiesList.isEmpty(), binding.rvSubsidies, binding.tvEmptySubsidies
                    )
                }

            } else {
                requireContext().toast(apiResponse?.message ?: "Failed to load dashboard data")
                clearAllDataAndShowEmptyStates()
            }
        }
    }

    private fun clearAllDataAndShowEmptyStates() {
        schemesList.clear()
        (binding.rvSchemes.adapter as? SchemeAdapter)?.submitList(emptyList())
        updateEmptyState(true, binding.rvSchemes, binding.tvEmptySchemes)

        loansList.clear()
        (binding.rvLoans.adapter as? LoanAdapter)?.submitList(emptyList())
        updateEmptyState(true, binding.rvLoans, binding.tvEmptyLoans)

        marketsList.clear()
        (binding.rvMarketPlace.adapter as? MarketAdapter)?.submitList(emptyList())
        updateEmptyState(true, binding.rvMarketPlace, binding.tvEmptyMarketPlace)

        subsidiesList.clear()
        (binding.rvSubsidies.adapter as? SubsidyAdapter)?.submitList(emptyList())
        updateEmptyState(true, binding.rvSubsidies, binding.tvEmptySubsidies)
    }

    private fun updateEmptyState(isEmpty: Boolean, recyclerView: View, emptyView: View) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE

        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
