package com.example.model

enum class AssetCategory(val displayName: String) {
    ALL("All"),
    MOST_TRADED("Most traded"),
    INDICES("Indices"),
    COMMODITIES("Commodities"),
    CRYPTO("Crypto"),
    SHARES("Shares"),
    CURRENCIES("Currencies")
}

data class Asset(
    val id: String,
    val symbol: String,
    val name: String,
    val category: AssetCategory,
    val price: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val sparklinePoints: List<Float>,
    val high24h: Double,
    val low24h: Double,
    val volume: String,
    val marketCap: String,
    val isClosed: Boolean = false,
    val isMostTraded: Boolean = false,
    val isMostVolatile: Boolean = false,
    val iconEmoji: String = "📈",
    val description: String = ""
)

data class CuratedWatchlist(
    val id: String,
    val title: String,
    val subtitle: String,
    val assetCount: Int,
    val iconEmoji: String,
    val accentColorHex: Long,
    val sampleTickers: List<String>
)

data class StockImpactTag(
    val symbol: String,
    val changePercent: Double
)

data class NewsArticle(
    val id: String,
    val headline: String,
    val subtitle: String,
    val timeAgo: String,
    val publisher: String,
    val category: String,
    val tags: List<StockImpactTag>,
    val summary: String,
    val readMinutes: Int = 2
)

data class PriceAlert(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val targetPrice: Double,
    val isAbove: Boolean,
    val isActive: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)

data class TradePosition(
    val id: String,
    val assetSymbol: String,
    val assetName: String,
    val type: TradeType,
    val units: Double,
    val entryPrice: Double,
    val currentPrice: Double,
    val pnl: Double,
    val pnlPercent: Double,
    val leverage: Int = 10,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val openTime: Long = System.currentTimeMillis()
)

enum class TradeType {
    BUY, SELL
}

enum class VerificationStatus {
    NOT_STARTED,
    PHONE_VERIFIED,
    DOCUMENTS_SUBMITTED,
    QUESTIONNAIRE_COMPLETED,
    VERIFIED
}

data class OnboardingFormData(
    val phoneNumber: String = "712 345 678",
    val countryCode: String = "+254",
    val countryName: String = "Kenya",
    val countryFlag: String = "🇰🇪",
    val acceptedPolicies: Set<String> = emptySet(),
    val idUploaded: Boolean = false,
    val selfieTaken: Boolean = false,
    val employmentStatus: String = "Employed",
    val cfdExperience: String = "2-4 years",
    val qualifications: Set<String> = setOf("I have relevant work experience"),
    val industry: String = "Financial services",
    val grossIncome: String = "$100,000-$199,999",
    val originOfWealth: Set<String> = setOf("Employment", "Savings"),
    val accountCurrency: String = "US Dollar (USD)",
    val currencyCode: String = "USD"
)
