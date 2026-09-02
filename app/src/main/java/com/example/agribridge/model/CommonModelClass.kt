package com.example.agribridge.model

import com.google.gson.annotations.SerializedName


enum class MessageType {
    USER, BOT
}

data class SubsidyModel(

    @SerializedName("subsidy_name") val subsidyName: String,

    @SerializedName("description") val description: String? = null,

    @SerializedName("applicable_category") val applicableCategory: String? = null,

    @SerializedName("eligibility") val eligibility: String? = null,

    @SerializedName("subsidy_amount") val subsidyAmount: String? = null,

    @SerializedName("deadline") val deadline: String? = null,

    @SerializedName("source_link") val sourceLink: String? = null,

    @SerializedName("state") val state: String? = null,

    @SerializedName("image_url") val imageUrl: String? = null
) {
    /**
     * Extracts the leading percentage from subsidy_amount.
     * "60% of total cost (30% central + 30% state)" → "60%"
     */
    val percentHero: String
        get() = Regex("""^(\d+(?:\.\d+)?)\s*%""").find(subsidyAmount?.trim().orEmpty())
            ?.let { "${it.groupValues[1]}%" } ?: subsidyAmount?.substringBefore(" ")?.trim() ?: "—"

    /**
     * Extracts the subtitle label (text after the % value).
     * "60% of total cost (30% central + 30% state)" → "of total cost"
     */
    val percentLabel: String
        get() = subsidyAmount?.replace(Regex("""^\d+(?:\.\d+)?%\s*"""), "")?.substringBefore("(")
            ?.trim()?.uppercase() ?: "SUBSIDISED"

    /**
     * Extracts parenthetical breakdown parts.
     * "(30% central + 30% state subsidy)" → ["30% Central govt", "30% State govt"]
     */
    val breakdown: List<String>
        get() {
            val paren =
                Regex("""\(([^)]+)\)""").find(subsidyAmount.orEmpty())?.groupValues?.getOrNull(1)
                    ?: return emptyList()

            return paren.split("+", ",").map { it.trim() }.filter { it.isNotBlank() }.map { part ->
                    when {
                        part.contains("central", ignoreCase = true) -> "${
                            part.substringBefore("%").trim()
                        }% Central govt"

                        part.contains("state", ignoreCase = true) -> "${
                            part.substringBefore("%").trim()
                        }% State govt"

                        else -> part
                    }
                }
        }

    /** Source URL stripped for display: "https://pmkusum.mnre.gov.in/" → "pmkusum.mnre.gov.in" */
    val displaySourceLink: String
        get() = sourceLink?.removePrefix("https://")?.removePrefix("http://")?.trimEnd('/') ?: ""

    /**
     * Deadline urgency level for color coding.
     * URGENT  = within 60 days  → red pill
     * SOON    = within 180 days → amber pill
     * NORMAL  = beyond 180 days → neutral pill
     */
    enum class DeadlineUrgency { URGENT, SOON, NORMAL }

    val deadlineUrgency: DeadlineUrgency
        get() {
            // Simple string-based heuristic; replace with a real date parse if preferred
            return DeadlineUrgency.NORMAL
        }

    /** Category-driven accent for stripe + chip. */
    enum class AccentColor { PURPLE, BLUE, GREEN, TEAL, AMBER }

    val accent: AccentColor
        get() = when {
            applicableCategory?.contains(
                "irrigation",
                ignoreCase = true
            ) == true || applicableCategory?.contains(
                "solar",
                ignoreCase = true
            ) == true -> AccentColor.PURPLE

            applicableCategory?.contains(
                "equipment",
                ignoreCase = true
            ) == true || applicableCategory?.contains(
                "machine",
                ignoreCase = true
            ) == true -> AccentColor.BLUE

            applicableCategory?.contains(
                "seed",
                ignoreCase = true
            ) == true || applicableCategory?.contains(
                "fertiliser",
                ignoreCase = true
            ) == true -> AccentColor.GREEN

            applicableCategory?.contains("organic", ignoreCase = true) == true -> AccentColor.TEAL
            else -> AccentColor.PURPLE
        }
}

data class BannerModel(val title: String, val description: String)
data class SchemeModel(

    @SerializedName("title") val title: String,

    @SerializedName("subtitle") val subtitle: String? = null,

    @SerializedName("description") val description: String? = null,

    @SerializedName("official_url") val officialUrl: String? = null,

    @SerializedName("image_url") val imageUrl: String? = null,

    @SerializedName("eligibility") val eligibility: String? = null,

    @SerializedName("procedure") val procedure: String? = null,

    @SerializedName("timeline") val timeline: String? = null,

    @SerializedName("documents_required") val documentsRequired: List<String>? = null,

    @SerializedName("benefits") val benefits: List<String>? = null,

    @SerializedName("extra_information") val extraInformation: String? = null,

    @SerializedName("state") val state: String? = null,

    // Used as the scheme category chip label
    val type: String? = null,

    // Legacy field kept for backward compatibility
    val location: String? = null,

    @SerializedName("promo_code") val promoCode: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("provider") val provider: String? = null
)

data class LoanModel(

    @SerializedName("loan_name") val loanName: String,

    @SerializedName("bank_provider") val bankProvider: String? = null,

    @SerializedName("interest_rate") val interestRate: String? = null,

    @SerializedName("max_amount") val maxAmount: String? = null,

    @SerializedName("eligibility") val eligibility: String? = null,

    @SerializedName("loan_category") val loanCategory: String? = null,

    @SerializedName("documents_required") val documentsRequired: List<String>? = null,

    @SerializedName("procedure") val procedure: String? = null,

    @SerializedName("official_url") val officialUrl: String? = null,

    @SerializedName("state") val state: String? = null,

    // ── Legacy field aliases kept for backward compatibility ──────────────
    // The old adapter used: item.title, item.amount, item.interest
    @SerializedName("title") val title: String? = null,

    @SerializedName("amount") val amount: String? = null,

    @SerializedName("interest") val interest: String? = null,

    @SerializedName("image_url") val imageUrl: String? = null
) {
    /** Resolved loan name — falls back to legacy `title` field. */
    val displayName: String get() = loanName.ifBlank { title.orEmpty() }

    /** Resolved interest rate — falls back to legacy `interest` field. */
    val displayInterest: String get() = interestRate ?: interest.orEmpty()

    /** Resolved max amount — falls back to legacy `amount` field. */
    val displayAmount: String get() = maxAmount ?: amount.orEmpty()

    /**
     * Compact interest value for the metric box.
     * "4% per annum (with prompt repayment subsidy)" → "4%"
     */
    val interestShort: String
        get() = displayInterest.substringBefore(" ").trim().ifEmpty { displayInterest }

    /**
     * Compact amount value for the metric box.
     * "Up to Rs. 3 Lakhs" → "₹3 L"
     */
    val amountShort: String
        get() {
            val raw = displayAmount
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(raw)?.value ?: return raw
            return when {
                raw.contains("Crore", ignoreCase = true) -> "₹${num} Cr"
                raw.contains("Lakh", ignoreCase = true) || raw.contains(
                    "Lacs", ignoreCase = true
                ) -> "₹${num} L"

                else -> "₹$num"
            }
        }
}

data class MarketModel(

    @SerializedName("marketplace_name") val marketplaceName: String,

    @SerializedName("marketplace_type") val marketplaceType: String? = null,

    @SerializedName("short_description") val shortDescription: String? = null,

    @SerializedName("description") val description: String? = null,

    @SerializedName("address") val address: String? = null,

    @SerializedName("nearby_area_information") val nearbyAreaInformation: String? = null,

    @SerializedName("buying_available") val buyingAvailable: Boolean = false,

    @SerializedName("selling_available") val sellingAvailable: Boolean = false,

    @SerializedName("vegetable_categories") val vegetableCategories: List<String>? = null,

    @SerializedName("operating_days") val operatingDays: List<String>? = null,

    @SerializedName("operating_hours") val operatingHours: String? = null,

    @SerializedName("transport_information") val transportInformation: String? = null,

    @SerializedName("market_size_popularity") val marketSizePopularity: String? = null,

    @SerializedName("contact_information") val contactInformation: String? = null,

    @SerializedName("entry_requirements") val entryRequirements: String? = null,

    @SerializedName("additional_information") val additionalInformation: String? = null,

    @SerializedName("state") val state: String? = null,

    @SerializedName("district") val district: String? = null,

    @SerializedName("area") val area: String? = null,

    // Legacy fields for backward compat with old adapter
    @SerializedName("marketName") val marketName: String? = null,

    @SerializedName("location") val location: String? = null,

    @SerializedName("price") val price: String? = null,

    @SerializedName("image_url") val imageUrl: String? = null
) {
    /** Compact hours for the card — strips seconds/extra text. */
    val hoursShort: String get() = operatingHours?.replace(":00", "")?.trim() ?: "—"

    /** "Large" extracted from "Large - high retail density..." */
    val sizeShort: String get() = marketSizePopularity?.substringBefore("-")?.trim() ?: "—"

    /** "Open daily" if 7 days, else "Mon–Sat" style. */
    val daysShort: String
        get() {
            val days = operatingDays ?: return "—"
            return if (days.size == 7) "Open daily"
            else "${days.firstOrNull()?.take(3)} – ${days.lastOrNull()?.take(3)}"
        }

    /**
     * 31 - 2 ==> WFH  |   ==> 31th Leave
     * Resolves the category accent color token.
     * "Retail & Wholesale" → orange, "APMC" → teal, "e-Market" → blue
     */
    enum class Category { RETAIL_WHOLESALE, APMC_MANDI, EMARKET, OTHER }

    val category: Category
        get() = when {
            marketplaceType?.contains(
                "wholesale", ignoreCase = true
            ) == true || marketplaceType?.contains(
                "retail", ignoreCase = true
            ) == true -> Category.RETAIL_WHOLESALE

            marketplaceType?.contains(
                "apmc", ignoreCase = true
            ) == true || marketplaceType?.contains(
                "mandi", ignoreCase = true
            ) == true -> Category.APMC_MANDI

            marketplaceType?.contains(
                "online", ignoreCase = true
            ) == true || marketplaceType?.contains(
                "e-market", ignoreCase = true
            ) == true -> Category.EMARKET

            else -> Category.OTHER
        }
}