package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CurrencyUiState
import com.example.ui.components.CurrencySelectSheet
import com.example.ui.components.KeypadView
import com.example.ui.components.MultiCompareSection
import com.example.ui.components.RateDisplayCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CurrencyUiState,
    onBaseCurrencyChange: (String) -> Unit,
    onTargetCurrencyChange: (String) -> Unit,
    onSwap: () -> Unit,
    onKeypadPress: (String) -> Unit,
    onQuickAmountSelected: (Double) -> Unit = {},
    onToggleSavePair: () -> Unit,
    onSyncRates: () -> Unit,
    onDismissSyncMessage: () -> Unit = {},
    onAmountClick: () -> Unit = {},
    onBaseAmountClick: () -> Unit = onAmountClick,
    onTargetAmountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isSelectingBase by remember { mutableStateOf(false) }
    var isSelectingTarget by remember { mutableStateOf(false) }
    var calculatorSubTab by remember { mutableIntStateOf(0) } // 0 = Keypad, 1 = Multi-Compare

    val timeFormatter = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedLastUpdate = remember(state.lastUpdatedMillis) {
        timeFormatter.format(Date(state.lastUpdatedMillis))
    }

    PullToRefreshBox(
        isRefreshing = state.isSyncing,
        onRefresh = onSyncRates,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
        ) {
            // Hero Rate Card
            RateDisplayCard(
                state = state,
                onBaseClick = {
                    isSelectingBase = true
                    coroutineScope.launch { sheetState.show() }
                },
                onTargetClick = {
                    isSelectingTarget = true
                    coroutineScope.launch { sheetState.show() }
                },
                onSwap = onSwap,
                onToggleSave = onToggleSavePair,
                onBaseAmountClick = onBaseAmountClick,
                onTargetAmountClick = onTargetAmountClick,
                onSyncRates = onSyncRates
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sub Tab Row: Keypad vs Multi-Compare
            PrimaryTabRow(
                selectedTabIndex = calculatorSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = calculatorSubTab == 0,
                    onClick = { calculatorSubTab = 0 },
                    text = { Text("Keypad", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_keypad")
                )
                Tab(
                    selected = calculatorSubTab == 1,
                    onClick = { calculatorSubTab = 1 },
                    text = { Text("Multi-Compare", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_compare")
                )
            }

            // Tab Content: Keypad anchored to the bottom at fixed distance above bottom bar
            if (calculatorSubTab == 0) {
                Spacer(modifier = Modifier.weight(1f))
                KeypadView(
                    onKeyPress = onKeypadPress,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    MultiCompareSection(
                        state = state,
                        onSelectTarget = { code ->
                            onTargetCurrencyChange(code)
                            calculatorSubTab = 0
                        }
                    )
                }
            }
        }

        // Base Currency Selector Sheet
        if (isSelectingBase) {
            CurrencySelectSheet(
                sheetState = sheetState,
                title = "Select Base Currency (You Send)",
                selectedCode = state.baseCurrency,
                onDismiss = {
                    isSelectingBase = false
                    coroutineScope.launch { sheetState.hide() }
                },
                onCurrencySelected = { code ->
                    onBaseCurrencyChange(code)
                    isSelectingBase = false
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }

        // Target Currency Selector Sheet
        if (isSelectingTarget) {
            CurrencySelectSheet(
                sheetState = sheetState,
                title = "Select Target Currency (You Receive)",
                selectedCode = state.targetCurrency,
                onDismiss = {
                    isSelectingTarget = false
                    coroutineScope.launch { sheetState.hide() }
                },
                onCurrencySelected = { code ->
                    onTargetCurrencyChange(code)
                    isSelectingTarget = false
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }
    }
}
