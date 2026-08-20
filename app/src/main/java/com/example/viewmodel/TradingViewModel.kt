package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Asset
import com.example.model.AssetCategory
import com.example.model.CuratedWatchlist
import com.example.model.NewsArticle
import com.example.model.OnboardingFormData
import com.example.model.PriceAlert
import com.example.model.StockImpactTag
import com.example.model.TradePosition
import com.example.model.TradeType
import com.example.model.VerificationStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TradingUiState(
    val currentTab: Int = 0, // 0: Home, 1: Watchlists, 2: Portfolio, 3: News, 4: Profile, 5: OnboardingWizard
    val assets: List<Asset> = emptyList(),
    val curatedWatchlists: List<CuratedWatchlist> = emptyList(),
    val newsArticles: List<NewsArticle> = emptyList(),
    val priceAlerts: List<PriceAlert> = emptyList(),
    val positions: List<TradePosition> = emptyList(),
    val balance: Double = 0.00,
    val investedAmount: Double = 0.00,
    val selectedCategory: AssetCategory = AssetCategory.ALL,
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val isFabMenuOpen: Boolean = false,
    val isTradeSheetOpen: Boolean = false,
    val isPriceAlertSheetOpen: Boolean = false,
    val isDepositSheetOpen: Boolean = false,
    val isHelpSheetOpen: Boolean = false,
    val isInboxSheetOpen: Boolean = false,
    val selectedAssetForTrade: Asset? = null,
    val selectedTradeType: TradeType = TradeType.BUY,
    val tradeLotSize: Double = 1.0,
    val selectedAssetForDetail: Asset? = null,
    val selectedNewsArticle: NewsArticle? = null,
    val unreadNotifications: Int = 1,
    // Onboarding flow state
    val verificationStatus: VerificationStatus = VerificationStatus.NOT_STARTED,
    val onboardingStep: Int = 1, // 1: Phone, 2: Terms, 3: Complete Setup prompt, 4: Document verify, 5: Questionnaire (sub-steps 1-7)
    val questionnaireSubStep: Int = 1, // 1: Employment, 2: CFD Experience, 3: Qualifications, 4: Industry, 5: Income, 6: Wealth Origin, 7: Currency
    val onboardingData: OnboardingFormData = OnboardingFormData(),
    val userToastMessage: String? = null
)

class TradingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TradingUiState())
    val uiState: StateFlow<TradingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        startLivePriceTicker()
    }

    private fun loadInitialData() {
        val initialAssets = listOf(
            Asset(
                id = "gold",
                symbol = "Gold",
                name = "Gold Spot / US Dollar",
                category = AssetCategory.COMMODITIES,
                price = 2654.80,
                changePercent = 0.08,
                changeAmount = 2.10,
                sparklinePoints = listOf(2648f, 2650f, 2649f, 2652f, 2651f, 2653f, 2654.8f),
                high24h = 2661.20,
                low24h = 2642.50,
                volume = "$14.8B",
                marketCap = "$17.2T",
                isMostTraded = true,
                iconEmoji = "🥇",
                description = "Spot gold against US Dollar, the world's benchmark safe haven commodity asset."
            ),
            Asset(
                id = "us100",
                symbol = "US100",
                name = "US Tech 100 Cash Index",
                category = AssetCategory.INDICES,
                price = 20842.15,
                changePercent = -0.82,
                changeAmount = -172.40,
                sparklinePoints = listOf(21050f, 21010f, 20960f, 20890f, 20910f, 20842f),
                high24h = 21090.00,
                low24h = 20780.30,
                volume = "$38.2B",
                marketCap = "Index",
                isMostTraded = true,
                iconEmoji = "💻",
                description = "Nasdaq 100 index tracking the largest non-financial companies listed on the Nasdaq."
            ),
            Asset(
                id = "oil",
                symbol = "Oil",
                name = "Brent Crude Oil",
                category = AssetCategory.COMMODITIES,
                price = 74.85,
                changePercent = 2.66,
                changeAmount = 1.94,
                sparklinePoints = listOf(72.5f, 72.8f, 73.4f, 73.9f, 74.2f, 74.85f),
                high24h = 75.20,
                low24h = 72.30,
                volume = "$9.1B",
                marketCap = "Commodity",
                isMostTraded = true,
                iconEmoji = "🛢️",
                description = "Global benchmark for Atlantic basin crude oils."
            ),
            Asset(
                id = "silver",
                symbol = "Silver",
                name = "Silver Spot",
                category = AssetCategory.COMMODITIES,
                price = 31.42,
                changePercent = 1.71,
                changeAmount = 0.53,
                sparklinePoints = listOf(30.6f, 30.8f, 30.9f, 31.1f, 31.3f, 31.42f),
                high24h = 31.80,
                low24h = 30.50,
                volume = "$4.2B",
                marketCap = "$1.8T",
                isMostTraded = true,
                iconEmoji = "🥈",
                description = "Silver spot commodity price against US Dollar."
            ),
            Asset(
                id = "eth",
                symbol = "ETH/USD",
                name = "Ethereum / US Dollar",
                category = AssetCategory.CRYPTO,
                price = 2845.60,
                changePercent = 5.13,
                changeAmount = 138.90,
                sparklinePoints = listOf(2680f, 2710f, 2730f, 2790f, 2815f, 2845.6f),
                high24h = 2890.00,
                low24h = 2670.00,
                volume = "$19.4B",
                marketCap = "$342B",
                isMostTraded = true,
                iconEmoji = "💎",
                description = "Leading smart contract decentralized blockchain platform."
            ),
            Asset(
                id = "j225",
                symbol = "J225",
                name = "Japan 225 Cash Index",
                category = AssetCategory.INDICES,
                price = 38920.00,
                changePercent = -1.11,
                changeAmount = -436.50,
                sparklinePoints = listOf(39400f, 39310f, 39150f, 39020f, 38920f),
                high24h = 39550.00,
                low24h = 38800.00,
                volume = "$6.8B",
                marketCap = "Index",
                isMostTraded = true,
                iconEmoji = "🗾",
                description = "Nikkei 225 price-weighted stock market index for the Tokyo Stock Exchange."
            ),
            // Most Volatile & Additional items
            Asset(
                id = "grtbtc",
                symbol = "GRT/BTC",
                name = "The Graph / Bitcoin",
                category = AssetCategory.CRYPTO,
                price = 0.00000318,
                changePercent = 14.82,
                changeAmount = 0.00000041,
                sparklinePoints = listOf(0.0000027f, 0.0000028f, 0.0000030f, 0.00000318f),
                high24h = 0.00000330,
                low24h = 0.00000265,
                volume = "$2.1M",
                marketCap = "$2.4B",
                isClosed = true,
                isMostVolatile = true,
                iconEmoji = "⚡",
                description = "Decentralized indexing protocol for querying blockchain networks."
            ),
            Asset(
                id = "neobtc",
                symbol = "NEO/Bitcoin",
                name = "Neo / Bitcoin",
                category = AssetCategory.CRYPTO,
                price = 0.000174,
                changePercent = -8.45,
                changeAmount = -0.000016,
                sparklinePoints = listOf(0.000192f, 0.000188f, 0.000180f, 0.000174f),
                high24h = 0.000195,
                low24h = 0.000170,
                volume = "$1.4M",
                marketCap = "$890M",
                isClosed = true,
                isMostVolatile = true,
                iconEmoji = "🟢",
                description = "Open source blockchain ecosystem designed for the smart economy."
            ),
            Asset(
                id = "ustech100",
                symbol = "US Tech 100",
                name = "US Tech 100 Mini",
                category = AssetCategory.INDICES,
                price = 29225.40,
                changePercent = -0.81,
                changeAmount = -238.10,
                sparklinePoints = listOf(29500f, 29420f, 29380f, 29280f, 29225.4f),
                high24h = 29550.0,
                low24h = 29150.0,
                volume = "$42.1B",
                marketCap = "Index",
                iconEmoji = "📊",
                description = "Core tech index futures tracking blue-chip software, AI and hardware leaders."
            ),
            Asset(
                id = "ustreasury",
                symbol = "US Treasury Bond",
                name = "10Y US Treasury Note",
                category = AssetCategory.INDICES,
                price = 108.95,
                changePercent = 0.34,
                changeAmount = 0.37,
                sparklinePoints = listOf(108.4f, 108.5f, 108.7f, 108.85f, 108.95f),
                high24h = 109.10,
                low24h = 108.30,
                volume = "$112B",
                marketCap = "Sovereign Debt",
                iconEmoji = "🏛️",
                description = "United States 10-Year Benchmark Treasury Note yield derivative."
            ),
            Asset(
                id = "brentoil",
                symbol = "Brent Oil",
                name = "Brent Crude Spot",
                category = AssetCategory.COMMODITIES,
                price = 78.42,
                changePercent = 1.95,
                changeAmount = 1.50,
                sparklinePoints = listOf(76.2f, 76.8f, 77.4f, 77.9f, 78.42f),
                high24h = 78.90,
                low24h = 75.90,
                volume = "$8.3B",
                marketCap = "Commodity",
                iconEmoji = "🔥",
                description = "Spot physical delivery grade Brent crude extracted from North Sea fields."
            ),
            Asset(
                id = "aapl",
                symbol = "AAPL",
                name = "Apple Inc.",
                category = AssetCategory.SHARES,
                price = 232.40,
                changePercent = 1.48,
                changeAmount = 3.39,
                sparklinePoints = listOf(228f, 229f, 230.5f, 231.2f, 232.4f),
                high24h = 234.10,
                low24h = 227.50,
                volume = "$64.2M",
                marketCap = "$3.52T",
                iconEmoji = "🍎",
                description = "Global consumer electronics and software ecosystem powerhouse."
            ),
            Asset(
                id = "tsla",
                symbol = "TSLA",
                name = "Tesla Inc.",
                category = AssetCategory.SHARES,
                price = 214.65,
                changePercent = -2.15,
                changeAmount = -4.72,
                sparklinePoints = listOf(221f, 219f, 217.5f, 215.2f, 214.65f),
                high24h = 223.00,
                low24h = 212.80,
                volume = "$88.4M",
                marketCap = "$685B",
                iconEmoji = "⚡",
                description = "Electric vehicle, autonomous AI driving, and battery energy pioneer."
            ),
            Asset(
                id = "eurusd",
                symbol = "EUR/USD",
                name = "Euro / US Dollar",
                category = AssetCategory.CURRENCIES,
                price = 1.0845,
                changePercent = 0.22,
                changeAmount = 0.0024,
                sparklinePoints = listOf(1.0815f, 1.0820f, 1.0832f, 1.0840f, 1.0845f),
                high24h = 1.0862,
                low24h = 1.0805,
                volume = "$450B",
                marketCap = "Forex",
                iconEmoji = "💶",
                description = "The most heavily traded currency pair in global foreign exchange markets."
            ),
            Asset(
                id = "gbpusd",
                symbol = "GBP/USD",
                name = "British Pound / US Dollar",
                category = AssetCategory.CURRENCIES,
                price = 1.2980,
                changePercent = -0.15,
                changeAmount = -0.0019,
                sparklinePoints = listOf(1.3010f, 1.3000f, 1.2990f, 1.2975f, 1.2980f),
                high24h = 1.3025,
                low24h = 1.2960,
                volume = "$280B",
                marketCap = "Forex",
                iconEmoji = "💷",
                description = "Cable forex pair measuring the strength of the British Pound against the Greenback."
            )
        )

        val watchlists = listOf(
            CuratedWatchlist(
                id = "energy",
                title = "Energy",
                subtitle = "Renewables & Power Grids",
                assetCount = 18,
                iconEmoji = "⚡",
                accentColorHex = 0xFFF59E0B,
                sampleTickers = listOf("XLE", "NEE", "ENPH", "FSLR")
            ),
            CuratedWatchlist(
                id = "gasoil",
                title = "Gas and oil",
                subtitle = "Crude & LNG Giants",
                assetCount = 24,
                iconEmoji = "🛢️",
                accentColorHex = 0xFF3B82F6,
                sampleTickers = listOf("BRENT", "WTI", "XOM", "CVX")
            ),
            CuratedWatchlist(
                id = "bigtech",
                title = "Big tech",
                subtitle = "Magnificent 7 & AI Leaders",
                assetCount = 12,
                iconEmoji = "🤖",
                accentColorHex = 0xFF8B5CF6,
                sampleTickers = listOf("NVDA", "AAPL", "MSFT", "GOOGL")
            ),
            CuratedWatchlist(
                id = "crypto",
                title = "Crypto Titans",
                subtitle = "Layer 1 & DeFi Protocols",
                assetCount = 30,
                iconEmoji = "🪙",
                accentColorHex = 0xFF10B981,
                sampleTickers = listOf("BTC", "ETH", "SOL", "AVAX")
            )
        )

        val news = listOf(
            NewsArticle(
                id = "n1",
                headline = "Canada says trade deal with US is 'very close' amid tariff discussions",
                subtitle = "7 minutes ago • Reuters News",
                timeAgo = "7m ago",
                publisher = "Reuters News",
                category = "Latest",
                tags = listOf(
                    StockImpactTag("HIMS", 5.51),
                    StockImpactTag("IBRX", -2.35)
                ),
                summary = "Senior trade officials confirmed bilateral negotiations are nearing completion on key cross-border energy and industrial equipment tariffs, sparking optimism across North American equities.",
                readMinutes = 3
            ),
            NewsArticle(
                id = "n2",
                headline = "Semiconductor rally surges as AI hyperscalers accelerate custom silicon orders",
                subtitle = "24 minutes ago • Bloomberg Markets",
                timeAgo = "24m ago",
                publisher = "Bloomberg Markets",
                category = "Latest",
                tags = listOf(
                    StockImpactTag("NVDA", 3.12),
                    StockImpactTag("TSLA", -1.45)
                ),
                summary = "Leading foundries reported unprecedented backlog demand for next-generation 3nm compute chips, fueling upside projections across semiconductor suppliers.",
                readMinutes = 2
            ),
            NewsArticle(
                id = "n3",
                headline = "Federal Reserve signals measured easing path as inflation metrics soften towards 2% target",
                subtitle = "1 hour ago • Financial Times",
                timeAgo = "1h ago",
                publisher = "Financial Times",
                category = "For you",
                tags = listOf(
                    StockImpactTag("US100", 0.94),
                    StockImpactTag("GOLD", 0.42)
                ),
                summary = "Policy committee minutes emphasize data dependency while acknowledging moderating core CPI and wage growth trends.",
                readMinutes = 4
            ),
            NewsArticle(
                id = "n4",
                headline = "OPEC+ extends voluntary production caps through fourth quarter to balance global inventories",
                subtitle = "3 hours ago • Wall Street Journal",
                timeAgo = "3h ago",
                publisher = "Wall Street Journal",
                category = "For you",
                tags = listOf(
                    StockImpactTag("OIL", 2.66),
                    StockImpactTag("XOM", 1.80)
                ),
                summary = "Delegates reiterated solidarity behind production curbs to maintain market stability amidst evolving seasonal refinery turnaround cycles.",
                readMinutes = 3
            )
        )

        val initialAlerts = listOf(
            PriceAlert(
                id = "a1",
                assetSymbol = "Gold",
                assetName = "Gold Spot",
                targetPrice = 2700.00,
                isAbove = true,
                isActive = true
            ),
            PriceAlert(
                id = "a2",
                assetSymbol = "US100",
                assetName = "US Tech 100",
                targetPrice = 20500.00,
                isAbove = false,
                isActive = true
            )
        )

        _uiState.update {
            it.copy(
                assets = initialAssets,
                curatedWatchlists = watchlists,
                newsArticles = news,
                priceAlerts = initialAlerts
            )
        }
    }

    private fun startLivePriceTicker() {
        viewModelScope.launch {
            while (true) {
                delay(3500)
                _uiState.update { state ->
                    val updatedAssets = state.assets.map { asset ->
                        // Subtle tick change
                        val deltaPercent = (Random.nextDouble(-0.18, 0.22) * 100).toInt() / 100.0
                        val newPrice = (asset.price * (1.0 + deltaPercent / 100.0)).coerceAtLeast(0.000001)
                        val newChange = asset.changePercent + (deltaPercent * 0.25)
                        val formattedPrice = (newPrice * 100.0).toLong() / 100.0
                        val lastPoints = asset.sparklinePoints.toMutableList()
                        if (lastPoints.size > 8) lastPoints.removeAt(0)
                        lastPoints.add(newPrice.toFloat())

                        asset.copy(
                            price = if (asset.price < 1.0) newPrice else formattedPrice.toDouble(),
                            changePercent = ((newChange * 100).toInt() / 100.0),
                            sparklinePoints = lastPoints
                        )
                    }

                    // Update positions PnL if any
                    val updatedPositions = state.positions.map { pos ->
                        val currentAsset = updatedAssets.find { it.symbol == pos.assetSymbol }
                        val currentP = currentAsset?.price ?: pos.currentPrice
                        val priceDiff = if (pos.type == TradeType.BUY) (currentP - pos.entryPrice) else (pos.entryPrice - currentP)
                        val pnl = priceDiff * pos.units * pos.leverage
                        val pnlPercent = ((priceDiff / pos.entryPrice) * 100.0 * pos.leverage)
                        pos.copy(
                            currentPrice = currentP,
                            pnl = (pnl * 100).toLong() / 100.0,
                            pnlPercent = (pnlPercent * 100).toLong() / 100.0
                        )
                    }

                    val totalPnl = updatedPositions.sumOf { it.pnl }
                    state.copy(
                        assets = updatedAssets,
                        positions = updatedPositions
                    )
                }
            }
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(currentTab = index, isFabMenuOpen = false) }
    }

    fun setSelectedCategory(category: AssetCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch(open: Boolean) {
        _uiState.update { it.copy(isSearchOpen = open, searchQuery = if (!open) "" else it.searchQuery) }
    }

    fun toggleFabMenu(open: Boolean? = null) {
        _uiState.update { it.copy(isFabMenuOpen = open ?: !it.isFabMenuOpen) }
    }

    fun openTradeSheet(asset: Asset, type: TradeType) {
        _uiState.update {
            it.copy(
                isTradeSheetOpen = true,
                selectedAssetForTrade = asset,
                selectedTradeType = type,
                tradeLotSize = 1.0,
                isFabMenuOpen = false
            )
        }
    }

    fun closeTradeSheet() {
        _uiState.update { it.copy(isTradeSheetOpen = false, selectedAssetForTrade = null) }
    }

    fun setTradeLotSize(size: Double) {
        _uiState.update { it.copy(tradeLotSize = size.coerceIn(0.1, 100.0)) }
    }

    fun executeTrade() {
        val state = _uiState.value
        val asset = state.selectedAssetForTrade ?: return
        val requiredMargin = (asset.price * state.tradeLotSize) / 10.0 // 10x leverage margin

        if (state.balance < requiredMargin && state.balance == 0.0) {
            // Give simulated funding if empty or prompt deposit
            _uiState.update {
                it.copy(
                    balance = 10000.00,
                    userToastMessage = "Demo deposit of $10,000 added to fund your position!"
                )
            }
        }

        val newPosition = TradePosition(
            id = "pos_${System.currentTimeMillis()}",
            assetSymbol = asset.symbol,
            assetName = asset.name,
            type = state.selectedTradeType,
            units = state.tradeLotSize,
            entryPrice = asset.price,
            currentPrice = asset.price,
            pnl = 0.0,
            pnlPercent = 0.0,
            leverage = 10
        )

        _uiState.update {
            val updatedPositions = listOf(newPosition) + it.positions
            val updatedInvested = updatedPositions.sumOf { p -> p.entryPrice * p.units }
            it.copy(
                positions = updatedPositions,
                investedAmount = updatedInvested,
                isTradeSheetOpen = false,
                selectedAssetForTrade = null,
                userToastMessage = "${state.selectedTradeType} order executed for ${state.tradeLotSize} units of ${asset.symbol}!"
            )
        }
    }

    fun closePosition(positionId: String) {
        _uiState.update { state ->
            val pos = state.positions.find { it.id == positionId }
            val remaining = state.positions.filterNot { it.id == positionId }
            val newBalance = state.balance + (pos?.pnl ?: 0.0)
            state.copy(
                positions = remaining,
                balance = (newBalance * 100).toLong() / 100.0,
                userToastMessage = "Closed position ${pos?.assetSymbol} with P&L: $${pos?.pnl}"
            )
        }
    }

    fun openAssetDetail(asset: Asset) {
        _uiState.update { it.copy(selectedAssetForDetail = asset) }
    }

    fun closeAssetDetail() {
        _uiState.update { it.copy(selectedAssetForDetail = null) }
    }

    fun openNewsDetail(news: NewsArticle) {
        _uiState.update { it.copy(selectedNewsArticle = news) }
    }

    fun closeNewsDetail() {
        _uiState.update { it.copy(selectedNewsArticle = null) }
    }

    fun togglePriceAlertSheet(open: Boolean) {
        _uiState.update { it.copy(isPriceAlertSheetOpen = open, isFabMenuOpen = false) }
    }

    fun addPriceAlert(symbol: String, targetPrice: Double, isAbove: Boolean) {
        val asset = _uiState.value.assets.find { it.symbol.equals(symbol, ignoreCase = true) }
        val newAlert = PriceAlert(
            id = "alert_${System.currentTimeMillis()}",
            assetSymbol = asset?.symbol ?: symbol.uppercase(),
            assetName = asset?.name ?: "$symbol Spot",
            targetPrice = targetPrice,
            isAbove = isAbove,
            isActive = true
        )
        _uiState.update {
            it.copy(
                priceAlerts = listOf(newAlert) + it.priceAlerts,
                isPriceAlertSheetOpen = false,
                userToastMessage = "Price alert set for ${newAlert.assetSymbol} at $$targetPrice"
            )
        }
    }

    fun deletePriceAlert(alertId: String) {
        _uiState.update { it.copy(priceAlerts = it.priceAlerts.filterNot { a -> a.id == alertId }) }
    }

    fun toggleDepositSheet(open: Boolean) {
        _uiState.update { it.copy(isDepositSheetOpen = open, isFabMenuOpen = false) }
    }

    fun depositFunds(amount: Double) {
        _uiState.update {
            val newBal = it.balance + amount
            it.copy(
                balance = newBal,
                isDepositSheetOpen = false,
                userToastMessage = "Successfully deposited $${amount.toInt()} into trading balance!"
            )
        }
    }

    fun toggleHelpSheet(open: Boolean) {
        _uiState.update { it.copy(isHelpSheetOpen = open, isFabMenuOpen = false) }
    }

    fun toggleInboxSheet(open: Boolean) {
        _uiState.update {
            it.copy(
                isInboxSheetOpen = open,
                unreadNotifications = if (open) 0 else it.unreadNotifications,
                isFabMenuOpen = false
            )
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(userToastMessage = null) }
    }

    // ONBOARDING WIZARD METHODS
    fun startOnboarding(step: Int = 1) {
        _uiState.update {
            it.copy(
                currentTab = 5, // Onboarding tab
                onboardingStep = step,
                isFabMenuOpen = false
            )
        }
    }

    fun setOnboardingStep(step: Int) {
        _uiState.update { it.copy(onboardingStep = step) }
    }

    fun setQuestionnaireSubStep(subStep: Int) {
        _uiState.update { it.copy(questionnaireSubStep = subStep) }
    }

    fun onKeypadDigit(digit: String) {
        _uiState.update { state ->
            val current = state.onboardingData.phoneNumber.replace(" ", "")
            if (current.length < 10) {
                val next = current + digit
                // format e.g. 712 345 678
                val formatted = buildFormattedPhone(next)
                state.copy(onboardingData = state.onboardingData.copy(phoneNumber = formatted))
            } else {
                state
            }
        }
    }

    fun onKeypadDelete() {
        _uiState.update { state ->
            val current = state.onboardingData.phoneNumber.replace(" ", "")
            if (current.isNotEmpty()) {
                val next = current.dropLast(1)
                val formatted = buildFormattedPhone(next)
                state.copy(onboardingData = state.onboardingData.copy(phoneNumber = formatted))
            } else {
                state
            }
        }
    }

    private fun buildFormattedPhone(raw: String): String {
        return when {
            raw.length <= 3 -> raw
            raw.length <= 6 -> "${raw.substring(0, 3)} ${raw.substring(3)}"
            else -> "${raw.substring(0, 3)} ${raw.substring(3, 6)} ${raw.substring(6)}"
        }
    }

    fun setCountry(code: String, name: String, flag: String) {
        _uiState.update {
            it.copy(
                onboardingData = it.onboardingData.copy(
                    countryCode = code,
                    countryName = name,
                    countryFlag = flag
                )
            )
        }
    }

    fun togglePolicy(policyName: String) {
        _uiState.update { state ->
            val current = state.onboardingData.acceptedPolicies.toMutableSet()
            if (current.contains(policyName)) current.remove(policyName) else current.add(policyName)
            state.copy(onboardingData = state.onboardingData.copy(acceptedPolicies = current))
        }
    }

    fun acceptAllPolicies(allPolicies: List<String>) {
        _uiState.update { state ->
            state.copy(onboardingData = state.onboardingData.copy(acceptedPolicies = allPolicies.toSet()))
        }
    }

    fun setDocumentUploaded(isId: Boolean, value: Boolean) {
        _uiState.update { state ->
            val updated = if (isId) {
                state.onboardingData.copy(idUploaded = value)
            } else {
                state.onboardingData.copy(selfieTaken = value)
            }
            state.copy(onboardingData = updated)
        }
    }

    fun setEmploymentStatus(status: String) {
        _uiState.update { it.copy(onboardingData = it.onboardingData.copy(employmentStatus = status)) }
    }

    fun setCfdExperience(exp: String) {
        _uiState.update { it.copy(onboardingData = it.onboardingData.copy(cfdExperience = exp)) }
    }

    fun toggleQualification(q: String) {
        _uiState.update { state ->
            val current = state.onboardingData.qualifications.toMutableSet()
            if (q == "All of the above") {
                current.clear()
                current.addAll(listOf("I have a relevant qualification", "I have relevant work experience", "All of the above"))
            } else if (q == "None of the above") {
                current.clear()
                current.add("None of the above")
            } else {
                current.remove("None of the above")
                if (current.contains(q)) current.remove(q) else current.add(q)
            }
            state.copy(onboardingData = state.onboardingData.copy(qualifications = current))
        }
    }

    fun setIndustry(ind: String) {
        _uiState.update { it.copy(onboardingData = it.onboardingData.copy(industry = ind)) }
    }

    fun setGrossIncome(income: String) {
        _uiState.update { it.copy(onboardingData = it.onboardingData.copy(grossIncome = income)) }
    }

    fun toggleWealthOrigin(origin: String) {
        _uiState.update { state ->
            val current = state.onboardingData.originOfWealth.toMutableSet()
            if (current.contains(origin)) current.remove(origin) else current.add(origin)
            state.copy(onboardingData = state.onboardingData.copy(originOfWealth = current))
        }
    }

    fun setCurrency(currencyName: String, code: String) {
        _uiState.update {
            it.copy(
                onboardingData = it.onboardingData.copy(
                    accountCurrency = currencyName,
                    currencyCode = code
                )
            )
        }
    }

    fun completeOnboarding() {
        _uiState.update {
            it.copy(
                verificationStatus = VerificationStatus.VERIFIED,
                balance = if (it.balance == 0.0) 10000.0 else it.balance,
                currentTab = 0, // return to Home
                userToastMessage = "Account verified! Welcome to AlphaTrade. $10,000 starting demo funds credited."
            )
        }
    }
}
