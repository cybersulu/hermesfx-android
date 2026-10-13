package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.MarketRatesScreen
import com.example.ui.screens.SavedPairsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    CALCULATOR(
        title = "Converter",
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate,
        testTag = "nav_calculator"
    ),
    PREFERRED(
        title = "Favorites",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.StarOutline,
        testTag = "nav_preferred"
    ),
    RATES(
        title = "Market",
        selectedIcon = Icons.Filled.TrendingUp,
        unselectedIcon = Icons.Outlined.TrendingUp,
        testTag = "nav_rates"
    ),
    SETTINGS(
        title = "Settings",
        selectedIcon = Icons.Filled.Tune,
        unselectedIcon = Icons.Outlined.Tune,
        testTag = "nav_settings"
    )
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleWidgetIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = uiState.themeMode,
                themeColorId = uiState.themeColorId,
                customThemeColor = uiState.customThemeColor
            ) {
                var currentScreenIndex by remember { mutableIntStateOf(0) }

                // BackHandler returns to Calculator if on another tab
                if (currentScreenIndex != 0) {
                    BackHandler {
                        currentScreenIndex = 0
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            Screen.entries.forEachIndexed { index, screen ->
                                val isSelected = currentScreenIndex == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreenIndex = index },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    modifier = Modifier.testTag(screen.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreenIndex,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition",
                        modifier = Modifier.padding(innerPadding)
                    ) { index ->
                        when (Screen.entries[index]) {
                            Screen.CALCULATOR -> {
                                CalculatorScreen(
                                    state = uiState,
                                    onBaseCurrencyChange = viewModel::setBaseCurrency,
                                    onTargetCurrencyChange = viewModel::setTargetCurrency,
                                    onSwap = viewModel::swapCurrencies,
                                    onKeypadPress = viewModel::appendKeypad,
                                    onQuickAmountSelected = viewModel::quickSetAmount,
                                    onToggleSavePair = viewModel::toggleSaveCurrentPair,
                                    onSyncRates = { viewModel.syncRates(force = true) },
                                    onDismissSyncMessage = viewModel::dismissSyncMessage,
                                    onAmountClick = viewModel::selectBaseInput,
                                    onBaseAmountClick = viewModel::selectBaseInput,
                                    onTargetAmountClick = viewModel::selectTargetInput
                                )
                            }
                            Screen.PREFERRED -> {
                                SavedPairsScreen(
                                    state = uiState,
                                    onSelectPair = { base, target ->
                                        viewModel.selectPair(base, target)
                                        currentScreenIndex = 0 // Switch to calculator
                                    },
                                    onDeletePair = viewModel::deleteSavedPair,
                                    onTogglePin = viewModel::togglePinSavedPair,
                                    onAddNewPair = viewModel::addCustomPair
                                )
                            }
                            Screen.RATES -> {
                                MarketRatesScreen(
                                    state = uiState,
                                    onSelectBase = viewModel::setBaseCurrency,
                                    onSelectTargetAndOpen = { targetCode ->
                                        viewModel.setTargetCurrency(targetCode)
                                        currentScreenIndex = 0 // Switch to calculator
                                    },
                                    onSyncRates = { viewModel.syncRates(force = true) }
                                )
                            }
                            Screen.SETTINGS -> {
                                SettingsScreen(
                                    state = uiState,
                                    onCadenceChange = viewModel::setCadenceMinutes,
                                    onThemeModeChange = viewModel::setThemeMode,
                                    onThemeColorChange = viewModel::setThemeColor,
                                    onCustomThemeColorChange = viewModel::setCustomThemeColor,
                                    onDecimalPlacesChange = viewModel::setDecimalPlaces,
                                    onFeeToggle = viewModel::setFeeEnabled,
                                    onFeePercentChange = viewModel::setFeePercent,
                                    onForceSync = { viewModel.syncRates(force = true) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent?) {
        val base = intent?.getStringExtra("EXTRA_BASE_CURRENCY")
        val target = intent?.getStringExtra("EXTRA_TARGET_CURRENCY")
        if (!base.isNullOrBlank() && !target.isNullOrBlank()) {
            viewModel.selectPair(base, target)
        }
    }
}
