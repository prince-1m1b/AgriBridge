package com.example.agribridge.model

import com.google.gson.annotations.SerializedName

data class DiscoverResponse(
    @SerializedName("schemes") val schemes: List<DiscoverScheme>? = null,
    @SerializedName("loans") val loans: List<DiscoverLoan>? = null,
    @SerializedName("marketplace") val marketplace: List<DiscoverMarketplace>? = null,
    @SerializedName("subsidies") val subsidies: List<DiscoverSubsidy>? = null
)

data class DiscoverScheme(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName(value = "short_description", alternate = ["description"]) val shortDescription: String? = null,
    @SerializedName("eligibility") val eligibility: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName(value = "thumbnail_image", alternate = ["banner_image"]) val thumbnailImage: String? = null,
    @SerializedName(value = "application_deadline", alternate = ["end_date"]) val applicationDeadline: String? = null,
    @SerializedName(value = "source_link", alternate = ["target_url"]) val sourceLink: String? = null,
    @SerializedName("procedure") val procedure: String? = null,
    @SerializedName("documents_required") val documentsRequired: List<String>? = null,
    @SerializedName("benefits") val benefits: List<String>? = null,
    @SerializedName("extra_information") val extraInformation: String? = null,
    @SerializedName("promo_code") val promoCode: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("provider") val provider: String? = null
) {
    fun toSchemeModel(): SchemeModel {
        return SchemeModel(
            title = title.orEmpty(),
            subtitle = shortDescription,
            description = shortDescription,
            officialUrl = sourceLink,
            imageUrl = thumbnailImage,
            eligibility = eligibility,
            timeline = applicationDeadline,
            state = state,
            type = category,
            procedure = procedure,
            documentsRequired = documentsRequired,
            benefits = benefits,
            extraInformation = extraInformation,
            promoCode = promoCode,
            startDate = startDate,
            provider = provider
        )
    }
}

data class DiscoverLoan(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "loan_name", alternate = ["title", "name"]) val loanName: String? = null,
    @SerializedName(value = "provider_name", alternate = ["provider", "bank_name"]) val providerName: String? = null,
    @SerializedName("interest_rate") val interestRate: String? = null,
    @SerializedName("eligibility") val eligibility: String? = null,
    @SerializedName("max_loan_amount") val maxLoanAmount: String? = null,
    @SerializedName("repayment_period") val repaymentPeriod: String? = null,
    @SerializedName(value = "thumbnail_image", alternate = ["banner_image", "image_url", "image"]) val thumbnailImage: String? = null,
    @SerializedName(value = "source_link", alternate = ["target_url", "link", "official_url"]) val sourceLink: String? = null,
    @SerializedName("documents_required") val documentsRequired: List<String>? = null,
    @SerializedName("procedure") val procedure: String? = null
) {
    fun toLoanModel(): LoanModel {
        return LoanModel(
            loanName = loanName.orEmpty(),
            bankProvider = providerName,
            interestRate = interestRate,
            maxAmount = maxLoanAmount,
            eligibility = eligibility,
            loanCategory = repaymentPeriod,
            officialUrl = sourceLink,
            imageUrl = thumbnailImage,
            documentsRequired = documentsRequired,
            procedure = procedure
        )
    }
}

data class DiscoverMarketplace(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "product_name", alternate = ["title", "name"]) val productName: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("price") val price: Any? = null,
    @SerializedName("unit") val unit: String? = null,
    @SerializedName("seller_name") val sellerName: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName(value = "thumbnail_image", alternate = ["banner_image", "image_url", "image"]) val thumbnailImage: String? = null,
    @SerializedName("availability_status") val availabilityStatus: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("where_they_belong") val whereTheyBelong: String? = null
) {
    fun toMarketModel(): MarketModel {
        val priceDouble = price?.toString()?.toDoubleOrNull()
        val priceTag = if (priceDouble != null && unit != null) "₹${priceDouble.toInt()}/$unit" else ""
        return MarketModel(
            marketplaceName = productName.orEmpty(),
            marketplaceType = category,
            address = location,
            operatingHours = availabilityStatus ?: "In Stock",
            marketSizePopularity = sellerName ?: "APMC",
            operatingDays = listOf("Monday", "Sunday"),
            buyingAvailable = true,
            sellingAvailable = false,
            vegetableCategories = if (priceTag.isNotEmpty()) listOf(priceTag) else emptyList(),
            imageUrl = thumbnailImage,
            description = description,
            nearbyAreaInformation = whereTheyBelong
        )
    }
}

data class DiscoverSubsidy(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "subsidy_name", alternate = ["title", "name"]) val subsidyName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("applicable_category") val applicableCategory: String? = null,
    @SerializedName("eligibility") val eligibility: String? = null,
    @SerializedName("subsidy_amount") val subsidyAmount: String? = null,
    @SerializedName("deadline") val deadline: String? = null,
    @SerializedName(value = "thumbnail_image", alternate = ["banner_image", "image_url", "image"]) val thumbnailImage: String? = null,
    @SerializedName(value = "source_link", alternate = ["target_url", "link", "official_url"]) val sourceLink: String? = null
) {
    fun toSubsidyModel(): SubsidyModel {
        return SubsidyModel(
            subsidyName = subsidyName.orEmpty(),
            description = description,
            applicableCategory = applicableCategory,
            eligibility = eligibility,
            subsidyAmount = subsidyAmount,
            deadline = deadline,
            sourceLink = sourceLink,
            state = "National",
            imageUrl = thumbnailImage
        )
    }
}
