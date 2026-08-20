package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.AppBottomNavigation
import com.example.components.AppGridFab
import com.example.components.AppTopBar
import com.example.components.AssetDetailSheet
import com.example.components.DepositSheet
import com.example.components.HelpSupportBottomSheet
import com.example.components.InboxBottomSheet
import com.example.components.PriceAlertSheet
import com.example.components.QuickActionMenuSheet
import com.example.components.SearchOverlay
import com.example.components.TradeOrderBottomSheet
import com.example.model.TradeType
import com.example.screens.HomeScreen
import com.example.screens.NewsScreen
import com.example.screens.OnboardingScreen
import com.example.screens.PortfolioScreen
import com.example.screens.ProfileScreen
import com.example.screens.WatchlistsScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TradingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AlphaTradeApp()
            }
        }
    }
}

@Composable
fun AlphaTradeApp(
    viewModel: TradingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userToastMessage) {
        uiState.userToastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    val isOnboardingFlow = uiState.currentTab == 5

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppTopBar(
                    balance = uiState.balance,
                    onSearchClick = { viewModel.toggleSearch(true) },
                    onBalanceClick = { viewModel.toggleDepositSheet(true) },
                    onProfileClick = {
                        if (uiState.currentTab == 4) viewModel.setTab(0) else viewModel.setTab(4)
                    }
                )
            }
        },
        bottomBar = {
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppBottomNavigation(
                    selectedTab = if (uiState.currentTab in 0..3) uiState.currentTab else -1,
                    onTabSelected = { index -> viewModel.setTab(index) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
        ) {
            // Main Screen Content based on Active Tab
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "MainTabAnimation"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        assets = uiState.assets,
                        curatedWatchlists = uiState.curatedWatchlists,
                        priceAlerts = uiState.priceAlerts,
                        positions = uiState.positions,
                        verificationStatus = uiState.verificationStatus,
                        onStartVerification = { viewModel.startOnboarding(1) },
                        onAssetClick = { asset -> viewModel.openAssetDetail(asset) },
                        onAddAlertClick = { viewModel.togglePriceAlertSheet(true) },
                        onExplorePortfolioClick = { viewModel.setTab(2) },
                        onCuratedClick = { viewModel.setTab(1) }
                    )
                    1 -> WatchlistsScreen(
                        assets = uiState.assets,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { cat -> viewModel.setSelectedCategory(cat) },
                        onAssetClick = { asset -> viewModel.openAssetDetail(asset) },
                        onBuyClick = { asset -> viewModel.openTradeSheet(asset, TradeType.BUY) },
                        onSellClick = { asset -> viewModel.openTradeSheet(asset, TradeType.SELL) }
                    )
                    2 -> PortfolioScreen(
                        balance = uiState.balance,
                        investedAmount = uiState.investedAmount,
                        positions = uiState.positions,
                        onDepositClick = { viewModel.toggleDepositSheet(true) },
                        onExploreMarketsClick = { viewModel.setTab(1) },
                        onClosePosition = { posId -> viewModel.closePosition(posId) }
                    )
                    3 -> NewsScreen(
                        newsArticles = uiState.newsArticles,
                        selectedArticle = uiState.selectedNewsArticle,
                        onArticleClick = { article -> viewModel.openNewsDetail(article) },
                        onCloseArticleDetail = { viewModel.closeNewsDetail() }
                    )
                    4 -> ProfileScreen(
                        verificationStatus = uiState.verificationStatus,
                        unreadInboxCount = uiState.unreadNotifications,
                        onInboxClick = { viewModel.toggleInboxSheet(true) },
                        onGetHelpClick = { viewModel.toggleHelpSheet(true) },
                        onStartOnboarding = { viewModel.startOnboarding(1) },
                        onDepositClick = { viewModel.toggleDepositSheet(true) }
                    )
                    5 -> OnboardingScreen(
                        currentStep = uiState.onboardingStep,
                        questionnaireSubStep = uiState.questionnaireSubStep,
                        formData = uiState.onboardingData,
                        onStepChange = { step -> viewModel.setOnboardingStep(step) },
                        onQuestionnaireSubStepChange = { subStep -> viewModel.setQuestionnaireSubStep(subStep) },
                        onKeypadDigit = { d -> viewModel.onKeypadDigit(d) },
                        onKeypadDelete = { viewModel.onKeypadDelete() },
                        onSetCountry = { code, name, flag -> viewModel.setCountry(code, name, flag) },
                        onTogglePolicy = { p -> viewModel.togglePolicy(p) },
                        onAcceptAllPolicies = { list -> viewModel.acceptAllPolicies(list) },
                        onSetDocumentUploaded = { isId, v -> viewModel.setDocumentUploaded(isId, v) },
                        onSetEmployment = { emp -> viewModel.setEmploymentStatus(emp) },
                        onSetCfdExp = { exp -> viewModel.setCfdExperience(exp) },
                        onToggleQualification = { q -> viewModel.toggleQualification(q) },
                        onSetIndustry = { ind -> viewModel.setIndustry(ind) },
                        onSetIncome = { inc -> viewModel.setGrossIncome(inc) },
                        onToggleWealth = { w -> viewModel.toggleWealthOrigin(w) },
                        onSetCurrency = { name, code -> viewModel.setCurrency(name, code) },
                        onComplete = { viewModel.completeOnboarding() },
                        onBackToHome = { viewModel.setTab(0) }
                    )
                }
            }

            // Floating Action Button (FAB) in bottom right corner (above tab bar)
            if (!isOnboardingFlow && !uiState.isSearchOpen) {
                AppGridFab(
                    onClick = { viewModel.toggleFabMenu() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }

            // Full-screen Search Overlay
            if (uiState.isSearchOpen) {
                SearchOverlay(
                    query = uiState.searchQuery,
                    onQueryChange = { q -> viewModel.setSearchQuery(q) },
                    assets = uiState.assets,
                    onClose = { viewModel.toggleSearch(false) },
                    onAssetSelected = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openAssetDetail(asset)
                    },
                    onBuy = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openTradeSheet(asset, TradeType.BUY)
                    },
                    onSell = { asset ->
                        viewModel.toggleSearch(false)
                        viewModel.openTradeSheet(asset, TradeType.SELL)
                    }
                )
            }
        }
    }

    // Modal Bottom Sheets
    if (uiState.isFabMenuOpen) {
        QuickActionMenuSheet(
            onDismiss = { viewModel.toggleFabMenu(false) },
            onDepositClick = { viewModel.toggleDepositSheet(true) },
            onAddAlertClick = { viewModel.togglePriceAlertSheet(true) },
            onSearchClick = {
                viewModel.toggleFabMenu(false)
                viewModel.toggleSearch(true)
            },
            onVerifyClick = { viewModel.startOnboarding(1) },
            onHelpClick = { viewModel.toggleHelpSheet(true) }
        )
    }

    if (uiState.isTradeSheetOpen && uiState.selectedAssetForTrade != null) {
        TradeOrderBottomSheet(
            asset = uiState.selectedAssetForTrade!!,
            initialTradeType = uiState.selectedTradeType,
            onDismiss = { viewModel.closeTradeSheet() },
            onConfirmOrder = { _, _ -> viewModel.executeTrade() }
        )
    }

    if (uiState.selectedAssetForDetail != null) {
        AssetDetailSheet(
            asset = uiState.selectedAssetForDetail!!,
            onDismiss = { viewModel.closeAssetDetail() },
            onBuy = {
                val asset = uiState.selectedAssetForDetail!!
                viewModel.closeAssetDetail()
                viewModel.openTradeSheet(asset, TradeType.BUY)
            },
            onSell = {
                val asset = uiState.selectedAssetForDetail!!
                viewModel.closeAssetDetail()
                viewModel.openTradeSheet(asset, TradeType.SELL)
            }
        )
    }

    if (uiState.isPriceAlertSheetOpen) {
        PriceAlertSheet(
            assets = uiState.assets,
            existingAlerts = uiState.priceAlerts,
            onDismiss = { viewModel.togglePriceAlertSheet(false) },
            onAddAlert = { sym, price, above -> viewModel.addPriceAlert(sym, price, above) },
            onDeleteAlert = { id -> viewModel.deletePriceAlert(id) }
        )
    }

    if (uiState.isDepositSheetOpen) {
        DepositSheet(
            onDismiss = { viewModel.toggleDepositSheet(false) },
            onDepositAmount = { amt -> viewModel.depositFunds(amt) }
        )
    }

    if (uiState.isInboxSheetOpen) {
        InboxBottomSheet(
            onDismiss = { viewModel.toggleInboxSheet(false) }
        )
    }

    if (uiState.isHelpSheetOpen) {
        HelpSupportBottomSheet(
            onDismiss = { viewModel.toggleHelpSheet(false) }
        )
    }
}
