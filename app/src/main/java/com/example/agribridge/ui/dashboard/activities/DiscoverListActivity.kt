package com.example.agribridge.ui.dashboard.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agribridge.R
import com.example.agribridge.api.listener.DiscoverApiRequest
import com.example.agribridge.api.repository.DiscoverRepository
import com.example.agribridge.databinding.ActivityDiscoverListBinding
import com.example.agribridge.model.DiscoverResponse
import com.example.agribridge.ui.dashboard.adapters.LoanAdapter
import com.example.agribridge.ui.dashboard.adapters.MarketDetailAdapter
import com.example.agribridge.ui.dashboard.adapters.SchemeAdapter
import com.example.agribridge.ui.dashboard.adapters.SubsidyAdapter
import com.example.agribridge.ui.dashboard.dialogs.LoanDetailDialogFragment
import com.example.agribridge.ui.dashboard.dialogs.MarketDetailDialogFragment
import com.example.agribridge.ui.dashboard.dialogs.SchemeDetailDialogFragment
import com.example.agribridge.ui.dashboard.dialogs.SubsidyDetailDialogFragment
import com.example.agribridge.utils.Constant.UserDetails
import com.example.agribridge.utils.LanguageManager
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.applySystemBarsPadding2
import com.example.agribridge.utils.getData
import com.example.agribridge.utils.request
import com.example.agribridge.utils.toast
import com.example.agribridge.viewmodel.DiscoverViewModel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DiscoverListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiscoverListBinding
    private lateinit var type: String
    private lateinit var title: String

    private val discoverViewModel: DiscoverViewModel by lazy {
        val factory = viewModelFactory {
            initializer {
                DiscoverViewModel(DiscoverRepository(request(DiscoverApiRequest::class.java)))
            }
        }
        ViewModelProvider(this, factory)[DiscoverViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDiscoverListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("extra_type") ?: ""
        title = intent.getStringExtra("extra_title") ?: ""

        initView()
        setObservers()
        loadData()
    }

    private fun initView() {
        binding.root.applySystemBarsPadding2(binding.root, binding.toolbar)

        binding.tvToolbarTitle.text = title
        binding.ivBack.setOnClickListener { finish() }
        binding.rvDiscoverList.layoutManager = LinearLayoutManager(this)
    }

    private fun loadData() {
        val preference = Preference(this)
        val state = preference.getStringPreferenceForUser(UserDetails.USER_STATE)
        val district = preference.getStringPreferenceForUser(UserDetails.USER_DISTRICT)
        val langCode = LanguageManager.getLanguage(this)
        val language = when (langCode) {
            "hi" -> "hindi"
            "kn" -> "kannada"
            else -> "english"
        }

        discoverViewModel.fetchDiscoverList(type, state, district, language)
    }

    private fun setObservers() {
        discoverViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        discoverViewModel.discoverData.observe(this) { apiResponse ->
            if (apiResponse?.statusCode == 200 || apiResponse?.type?.equals("success", ignoreCase = true) == true) {
                apiResponse.data?.let { data ->
                    setupAdapter(data)
                }
            } else {
                toast(apiResponse?.message ?: "Failed to load details")
                showEmptyState()
            }
        }
    }

    private fun setupAdapter(data: Any?) {
        if (data == null) {
            showEmptyState()
            return
        }

        val jsonStr = if (data is String) data else Gson().toJson(data)

        when (type) {
            "promotion" -> {
                val listType = object : TypeToken<List<com.example.agribridge.model.DiscoverScheme>>() {}.type
                val rawList: List<com.example.agribridge.model.DiscoverScheme> = Gson().fromJson(jsonStr, listType) ?: emptyList()
                val schemes = rawList.map { it.toSchemeModel() }
                if (schemes.isEmpty()) {
                    showEmptyState()
                } else {
                    binding.rvDiscoverList.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    val adapter = SchemeAdapter { scheme ->
                        val dialog = SchemeDetailDialogFragment.newInstance(scheme)
                        dialog.show(supportFragmentManager, SchemeDetailDialogFragment.TAG)
                    }
                    binding.rvDiscoverList.adapter = adapter
                    adapter.submitList(ArrayList(schemes))
                }
            }
            "loan" -> {
                val listType = object : TypeToken<List<com.example.agribridge.model.DiscoverLoan>>() {}.type
                val rawList: List<com.example.agribridge.model.DiscoverLoan> = Gson().fromJson(jsonStr, listType) ?: emptyList()
                val loans = rawList.map { it.toLoanModel() }
                if (loans.isEmpty()) {
                    showEmptyState()
                } else {
                    binding.rvDiscoverList.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    val adapter = LoanAdapter { loan ->
                        val dialog = LoanDetailDialogFragment.newInstance(loan)
                        dialog.show(supportFragmentManager, LoanDetailDialogFragment.TAG)
                    }
                    binding.rvDiscoverList.adapter = adapter
                    adapter.submitList(ArrayList(loans))
                }
            }
            "marketplace" -> {
                val listType = object : TypeToken<List<com.example.agribridge.model.DiscoverMarketplace>>() {}.type
                val rawList: List<com.example.agribridge.model.DiscoverMarketplace> = try {
                    if (jsonStr.trim().startsWith("{")) {
                        val obj = Gson().fromJson(jsonStr, Map::class.java)
                        val mList = obj["marketplaces"]
                        Gson().fromJson(Gson().toJson(mList), listType) ?: emptyList()
                    } else {
                        Gson().fromJson(jsonStr, listType) ?: emptyList()
                    }
                } catch (e: Exception) {
                    Gson().fromJson(jsonStr, listType) ?: emptyList()
                }
                val markets = rawList.map { it.toMarketModel() }
                if (markets.isEmpty()) {
                    showEmptyState()
                } else {
                    binding.rvDiscoverList.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    val adapter = MarketDetailAdapter { market ->
                        val dialog = MarketDetailDialogFragment.newInstance(market)
                        dialog.show(supportFragmentManager, MarketDetailDialogFragment.TAG)
                    }
                    binding.rvDiscoverList.adapter = adapter
                    adapter.submitList(ArrayList(markets))
                }
            }
            "subsidies" -> {
                val listType = object : TypeToken<List<com.example.agribridge.model.DiscoverSubsidy>>() {}.type
                val rawList: List<com.example.agribridge.model.DiscoverSubsidy> = Gson().fromJson(jsonStr, listType) ?: emptyList()
                val subsidies = rawList.map { it.toSubsidyModel() }
                if (subsidies.isEmpty()) {
                    showEmptyState()
                } else {
                    binding.rvDiscoverList.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    val adapter = SubsidyAdapter { subsidy ->
                        val dialog = SubsidyDetailDialogFragment.newInstance(subsidy)
                        dialog.show(supportFragmentManager, SubsidyDetailDialogFragment.TAG)
                    }
                    binding.rvDiscoverList.adapter = adapter
                    adapter.submitList(ArrayList(subsidies))
                }
            }
            else -> showEmptyState()
        }
    }

    private fun showEmptyState() {
        binding.rvDiscoverList.visibility = View.GONE
        binding.tvEmptyState.visibility = View.VISIBLE
    }
}
