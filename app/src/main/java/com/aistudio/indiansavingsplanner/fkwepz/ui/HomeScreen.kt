package com.aistudio.indiansavingsplanner.fkwepz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.indiansavingsplanner.fkwepz.data.*
import java.text.NumberFormat
import java.util.Locale

// Local cache for NumberFormat to avoid expensive re-creations
private val rupeeFormatter: NumberFormat by lazy {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
    formatter.maximumFractionDigits = 0
    formatter
}

// Helper to format currency in Indian Rupees style (Lakh/Crore format)
fun formatRupee(amount: Double): String {
    if (!amount.isFinite()) return "₹ 0"
    return try {
        rupeeFormatter.format(amount).replace("INR", "₹").replace("Rs.", "₹").trim()
    } catch (e: Exception) {
        "₹ ${amount.toLong()}"
    }
}

@Composable
fun HomeScreen(viewModel: MainActivityViewModel, modifier: Modifier = Modifier) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val savedInvestments by viewModel.savedInvestments.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "EXPLORE",
                    onClick = { viewModel.setActiveTab("EXPLORE") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Calculators") },
                    label = { 
                        Text(
                            text = "Calculators",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("tab_explore")
                )
                NavigationBarItem(
                    selected = activeTab == "PORTFOLIO",
                    onClick = { viewModel.setActiveTab("PORTFOLIO") },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (savedInvestments.isNotEmpty()) {
                                    Badge { Text("${savedInvestments.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Tracker")
                        }
                    },
                    label = { 
                        Text(
                            text = "My Tracker",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("tab_portfolio")
                )
                NavigationBarItem(
                    selected = activeTab == "TAX",
                    onClick = { viewModel.setActiveTab("TAX") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Tax Planner") },
                    label = { 
                        Text(
                            text = "Tax Planner",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("tab_tax")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Elegant sovereign header
            AppHeader()

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "tab_fade"
            ) { tab ->
                when (tab) {
                    "EXPLORE" -> ExploreAndCalculateScreen(viewModel)
                    "PORTFOLIO" -> PortfolioTrackerScreen(viewModel)
                    "TAX" -> TaxPlannerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Column {
                    Text(
                        text = "BharatInvest",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Unified Govt. Savings Hub",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Surface(
                onClick = { /* Informational action */ },
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreAndCalculateScreen(viewModel: MainActivityViewModel) {
    val selectedScheme by viewModel.selectedScheme.collectAsStateWithLifecycle()
    val principal by viewModel.principalInput.collectAsStateWithLifecycle()
    val isMonthly by viewModel.isMonthlyInput.collectAsStateWithLifecycle()
    val customRate by viewModel.customRateInput.collectAsStateWithLifecycle()
    val customTenure by viewModel.customTenureInput.collectAsStateWithLifecycle()
    val simulateReinvest by viewModel.simulateReinvestmentInput.collectAsStateWithLifecycle()
    val labelInput by viewModel.investmentLabelInput.collectAsStateWithLifecycle()
    val projection by viewModel.calculatedProjection.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Horizontal list of Schemes
        item {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                Text(
                    text = "Select Government Scheme",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(SavingsScheme.ALL_SCHEMES) { scheme ->
                        val isSelected = scheme.id == selectedScheme.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectScheme(scheme) },
                            label = { Text(scheme.shortName) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("scheme_chip_${scheme.id}")
                        )
                    }
                }
            }
        }

        // Scheme details hero banner
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedScheme.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${customRate.toDoubleOrNull() ?: selectedScheme.currentInterestRate}% p.a.",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = selectedScheme.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Scheme Meta Rows
                    GridMetaRow("Tax Benefit:", selectedScheme.taxTreatment, "Lock-in Period:", selectedScheme.lockInPeriod)
                    Spacer(modifier = Modifier.height(8.dp))
                    GridMetaRow("Min Deposit:", formatRupee(selectedScheme.minDeposit), "Eligibility:", selectedScheme.eligibility)
                }
            }
        }

        // Calculator Interactive Form
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Interactive Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Principal Input
                    val isRecurScheme = selectedScheme.id == "PPF" || selectedScheme.id == "SSY" || selectedScheme.id == "PORD"
                    val depositLabel = if (isRecurScheme) {
                        if (selectedScheme.id == "PORD" || isMonthly) "Monthly Deposit Commitment" else "Annual Deposit Amount"
                    } else {
                        "One-time Capital Investment"
                    }

                    OutlinedTextField(
                        value = principal,
                        onValueChange = { viewModel.principalInput.value = it },
                        label = { Text(depositLabel) },
                        prefix = { Text("₹", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_principal"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Quick select amounts matching typical limits
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val quickAmounts = when (selectedScheme.id) {
                            "PPF", "SSY" -> listOf(10000.0, 50000.0, 150000.0)
                            "PORD" -> listOf(1000.0, 5000.0, 10000.0)
                            "SCSS", "POMIS" -> listOf(100000.0, 450000.0, 900000.0)
                            else -> listOf(10000.0, 50000.0, 100000.0)
                        }
                        quickAmounts.forEach { amt ->
                            SuggestionChip(
                                onClick = { viewModel.principalInput.value = amt.toInt().toString() },
                                label = { Text(formatRupee(amt), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Flexible toggle for Monthly vs Annual for PPF and SSY
                    if (selectedScheme.id == "PPF" || selectedScheme.id == "SSY") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Contribution",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Deposit systematic amount every month",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = isMonthly,
                                onCheckedChange = { viewModel.isMonthlyInput.value = it },
                                modifier = Modifier.testTag("switch_monthly")
                            )
                        }
                    }

                    // Advanced Projection Horizon Slider
                    val currentTenureVal = customTenure.toIntOrNull() ?: selectedScheme.defaultTenureYears
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Projection Horizon",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$currentTenureVal Years",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Slider(
                            value = currentTenureVal.toFloat(),
                            onValueChange = { viewModel.customTenureInput.value = it.toInt().toString() },
                            valueRange = 1f..25f,
                            steps = 24,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slider_tenure"),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1 Year", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("Long-term Compound Horizon", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("25 Years", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulate Maturity Reinvestment Toggle control
                    val simulateReinvestment by viewModel.simulateReinvestmentInput.collectAsStateWithLifecycle()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Reinvest on Maturity",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Simulate rolling over matured capital to show exponential compounded growth.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = simulateReinvestment,
                            onCheckedChange = { viewModel.simulateReinvestmentInput.value = it },
                            modifier = Modifier.testTag("switch_reinvestment")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Collapsible Advanced settings (Custom rates)
                    var showAdvanced by remember { mutableStateOf(false) }
                    TextButton(
                        onClick = { showAdvanced = !showAdvanced },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (showAdvanced) "Hide Advanced Settings" else "Show Advanced Settings (Custom rates)")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (showAdvanced) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(visible = showAdvanced) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = customRate,
                                    onValueChange = { viewModel.customRateInput.value = it },
                                    label = { Text("Interest Rate Override") },
                                    suffix = { Text("%") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("override_rate")
                                )
                            }
                            Text(
                                text = "Adjust interest rate to simulate possible prospective policy rate revisions.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account Label input for custom tracking identification
                    OutlinedTextField(
                        value = labelInput,
                        onValueChange = { viewModel.investmentLabelInput.value = it },
                        label = { Text("Portfolio Label (e.g. My Retirement PPF)") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_label"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save to Tracker button
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.saveCurrentToPortfolio()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_to_portfolio"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to My Portfolio Tracker", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Calculation Results Banner
        item {
            val totalInvestmentSum = projection.lastOrNull()?.let {
                if (selectedScheme.id == "SCSS" || selectedScheme.id == "POMIS") {
                    // Payout schemes: principal remains constant
                    it.openingBalance
                } else {
                    projection.sumOf { p -> p.depositsDuringYear }
                }
            } ?: 0.0

            val totalMaturityValue = projection.lastOrNull()?.closingBalance ?: 0.0
            val totalInterestEarned = projection.lastOrNull()?.cumulativeInterest ?: 0.0

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PROJECTED MATURITY WEALTH",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = formatRupee(totalMaturityValue),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Invested Capital", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                                Text(formatRupee(totalInvestmentSum), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(36.dp)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Consolidated Gains", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                                Text(formatRupee(totalInterestEarned), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Tax Implication Card
        if (projection.isNotEmpty()) {
            item {
                val totalMaturityValue = projection.lastOrNull()?.closingBalance ?: 0.0
                val totalInterestEarned = projection.lastOrNull()?.cumulativeInterest ?: 0.0
                val annualEstimate = if (projection.isNotEmpty()) {
                    val cycleMaturity = projection.size.coerceAtLeast(1)
                    if (selectedScheme.depositType == DepositType.MONTHLY_RECURRING) {
                        val amount = projection.firstOrNull()?.depositsDuringYear ?: 0.0
                        amount
                    } else if (selectedScheme.depositType == DepositType.ANNUAL_RECURRING) {
                        projection.firstOrNull()?.depositsDuringYear ?: 0.0
                    } else {
                        val sum = projection.sumOf { it.depositsDuringYear }
                        if (sum.isFinite()) sum / cycleMaturity else 0.0
                    }
                } else 0.0

                val taxRateText by viewModel.taxMarginalRateInput.collectAsStateWithLifecycle()
                val taxRateVal = taxRateText.toDoubleOrNull() ?: 20.0
                val taxRegimeText by viewModel.taxRegimeInput.collectAsStateWithLifecycle()
                val is80CClaimedInOld = taxRegimeText == "OLD"

                val taxImpact = TaxCalculator.calculatePostTaxReturns(
                    schemeId = selectedScheme.id,
                    preTaxMaturityValue = totalMaturityValue,
                    preTaxInterestEarned = totalInterestEarned,
                    marginalTaxRatePercent = taxRateVal,
                    isSection80CClaimedInOldRegime = is80CClaimedInOld,
                    principalInvestedInAYear = annualEstimate
                )

                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tax Implications",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Tax Liability Implications",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = if (taxImpact.isInterestTaxExempt) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (taxImpact.isInterestTaxExempt) "Tax Exempt (EEE)" else "Fully Taxable",
                                    color = if (taxImpact.isInterestTaxExempt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Under Indian Income Tax Act, the tax treatment of ${selectedScheme.shortName} is specified as: ${selectedScheme.taxTreatment}. Let's see how this affects your maturity returns at your marginal slab rate.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 12.dp))

                        // Slab rate selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Tax Bracket:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("5.0", "15.0", "20.0", "30.0").forEach { rate ->
                                    val isSelected = taxRateText == rate
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.taxMarginalRateInput.value = rate },
                                        label = { Text("$rate%", fontSize = 11.sp) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (taxImpact.isInterestTaxExempt) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Maximized Post-Tax Earnings!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Since ${selectedScheme.shortName} is a completely tax-exempt instrument (EEE), you pay ₹0 tax on the interest earned. Your net interest yield remains a pure ${formatRupee(totalInterestEarned)}.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            // Tax break-down row
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Gross Interest Return:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    Text(formatRupee(totalInterestEarned), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Estimated Tax @ $taxRateText% Bracket:", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    Text("- ${formatRupee(taxImpact.totalTaxOnInterest)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Post-Tax Net Compound Return:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(formatRupee(taxImpact.postTaxInterestEarned), fontSize = 13.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net Maturity Value (Post-Tax):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(formatRupee(taxImpact.postTaxMaturityValue), fontSize = 13.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            // Added tax advice
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Optimization Tip: Under both regimes, some schemes are completely tax-free (like PPF, SSY). Keeping your capital there maximizes actual net post-tax compound annual growth rate (CAGR).",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 80C Deduction benefit display
                        if (taxImpact.isEligible80C) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Claimable",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Eligible for Section 80C deductions (Old Regime). You save up to ${formatRupee(minOf(annualEstimate, 150000.0) * (taxRateVal / 100.0))} in taxes on this investment's annual principal!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Graphical Compounding Chart Card
        if (projection.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Projected Compounding trajectory",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap individual bars to inspect year-by-year scale of credited interest",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        SingleSchemeProjectionsChart(projection)
                    }
                }
            }
        }

        // Side-by-Side Scheme Comparison Card
        if (projection.isNotEmpty()) {
            item {
                var comparisonSchemeId by remember { mutableStateOf<String?>(null) }
                var showCompareSection by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compare schemes",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Side-by-Side Yield Comparison",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(
                                onClick = { showCompareSection = !showCompareSection }
                            ) {
                                Text(if (showCompareSection) "Hide" else "Compare")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (showCompareSection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showCompareSection) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "Compare the returns of ${selectedScheme.shortName} with another government scheme using the identical deposit amount and tenure horizon.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                val eligibleCompareSchemes = SavingsScheme.ALL_SCHEMES.filter { it.id != selectedScheme.id }

                                Text(
                                    text = "Select Second Scheme for comparison:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 12.dp)
                                ) {
                                    items(eligibleCompareSchemes) { scheme ->
                                        val isChosen = comparisonSchemeId == scheme.id
                                        FilterChip(
                                            selected = isChosen,
                                            onClick = {
                                                comparisonSchemeId = if (isChosen) null else scheme.id
                                            },
                                            label = { Text(scheme.shortName) },
                                            leadingIcon = if (isChosen) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                            } else null,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }

                                if (comparisonSchemeId != null) {
                                    val secondScheme = SavingsScheme.getSchemeById(comparisonSchemeId!!)
                                    if (secondScheme != null) {
                                        val principalVal = principal.toDoubleOrNull() ?: selectedScheme.minDeposit
                                        val currentTenureVal = customTenure.toIntOrNull() ?: selectedScheme.defaultTenureYears

                                        // Calculate secondary projections
                                        val compProj = secondScheme.calculateProjection(
                                            amount = principalVal,
                                            customInterestRate = null,
                                            recurringTypeOverride = null,
                                            tenureYearsOverride = currentTenureVal,
                                            simulateReinvestment = simulateReinvest
                                        )

                                        val secondMaturity = compProj.lastOrNull()?.closingBalance ?: 0.0
                                        val secondInterest = compProj.lastOrNull()?.cumulativeInterest ?: 0.0
                                        val secondDeposits = if (secondScheme.id == "SCSS" || secondScheme.id == "POMIS") {
                                            principalVal
                                        } else {
                                            compProj.sumOf { p -> p.depositsDuringYear }
                                        }

                                        val totalInvestmentSum = projection.lastOrNull()?.let {
                                            if (selectedScheme.id == "SCSS" || selectedScheme.id == "POMIS") {
                                                principalVal
                                            } else {
                                                projection.sumOf { p -> p.depositsDuringYear }
                                            }
                                        } ?: 0.0
                                        val totalMaturityValue = projection.lastOrNull()?.closingBalance ?: 0.0
                                        val totalInterestEarned = projection.lastOrNull()?.cumulativeInterest ?: 0.0

                                        val maturityDiff = totalMaturityValue - secondMaturity

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("", modifier = Modifier.weight(1.2f))
                                                Text(selectedScheme.shortName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                                Text(secondScheme.shortName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                            }

                                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("Interest Rate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.weight(1.2f))
                                                val primaryRate = customRate.toDoubleOrNull() ?: selectedScheme.currentInterestRate
                                                Text("$primaryRate%", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                                Text("${secondScheme.currentInterestRate}%", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                            }

                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("Total Assets Saved", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.weight(1.2f))
                                                Text(formatRupee(totalInvestmentSum), modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                                Text(formatRupee(secondDeposits), modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                            }

                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("Compound Interest", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.weight(1.2f))
                                                Text(formatRupee(totalInterestEarned), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                                Text(formatRupee(secondInterest), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 12.sp)
                                            }

                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("Maturity Value", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1.2f))
                                                Text(formatRupee(totalMaturityValue), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 13.sp)
                                                Text(formatRupee(secondMaturity), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 13.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Surface(
                                            color = if (maturityDiff >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (maturityDiff >= 0) Icons.Default.CheckCircle else Icons.Default.Info,
                                                    contentDescription = null,
                                                    tint = if (maturityDiff >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                val explanation = if (maturityDiff >= 0) {
                                                    "${selectedScheme.shortName} yields ${formatRupee(maturityDiff)} more in total maturity value than ${secondScheme.shortName} over $currentTenureVal years!"
                                                } else {
                                                    "${secondScheme.shortName} yields ${formatRupee(-maturityDiff)} more in total maturity value than ${selectedScheme.shortName} over $currentTenureVal years."
                                                }
                                                Text(
                                                    text = explanation,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (maturityDiff >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Select a comparison scheme above to see side-by-side details.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Year-by-year Amortization Header
        item {
            Text(
                text = "Yearly Projection Schedule",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 4.dp)
            )
        }

        // Year-by-year table
        if (projection.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No calculations loaded. Please verify input.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            items(projection) { yr ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Year ${yr.year}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Closing: ${formatRupee(yr.closingBalance)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${formatRupee(yr.interestEarned)} Interest",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Deposits: ${formatRupee(yr.depositsDuringYear)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridMetaRow(label1: String, val1: String, label2: String, val2: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label1, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(val1, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label2, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(val2, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
        }
    }
}

@Composable
fun PortfolioTrackerScreen(viewModel: MainActivityViewModel) {
    val savedInvestments by viewModel.savedInvestments.collectAsStateWithLifecycle()
    val aggregateProjection by viewModel.portfolioAggregateProjection.collectAsStateWithLifecycle()
    var editingInvestment by remember { mutableStateOf<InvestmentEntity?>(null) }

    var showClearAllConfirmation by remember { mutableStateOf(false) }

    if (showClearAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmation = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllInvestments()
                        showClearAllConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirmation = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Confirm Clear Portfolio", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            },
            text = {
                Text("Are you sure you want to permanently clear all saved scheme calculations from your tracker? This action cannot be undone.")
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (editingInvestment != null) {
        EditInvestmentDialog(
            investment = editingInvestment!!,
            onDismiss = { editingInvestment = null },
            onSave = { updated -> viewModel.updateInvestment(updated) }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        if (savedInvestments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Portfolio is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Save your government scheme calculations to track your consolidated interest income trajectory over multiple years.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.setActiveTab("EXPLORE") },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explore & Add Schemes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Aggregate wealth summaries banner
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "CONSOLIDATED PORTFOLIO VALUATION",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                    letterSpacing = 1.sp
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${savedInvestments.size} Assets",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Total Projected Maturity Wealth
                            Text(
                                text = formatRupee(aggregateProjection.currentValue),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = "Ultimate wealth after full-period compounding",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 20.dp),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Principal Invested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                                    Text(formatRupee(aggregateProjection.totalInvested), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.2f)) {
                                    Text("Consolidated Gains", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                                    Text(
                                        formatRupee(aggregateProjection.totalInterestEarned),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 80C Tax-Saving Tracker Card
            item {
                val eligibleSchemes = listOf("PPF", "SSY", "NSC", "SCSS", "POTD_5Y")
                val taxSavingsInvestments = savedInvestments.filter { it.schemeType in eligibleSchemes }
                val totalTaxInvestment = taxSavingsInvestments.sumOf { investment ->
                    if (investment.recurringInterval == "MONTHLY" && investment.isRecurring) {
                        investment.principalAmount * 12
                    } else {
                        investment.principalAmount
                    }
                }
                val limit80C = 150000.0
                val progressFraction = (totalTaxInvestment / limit80C).toFloat().coerceIn(0f, 1f)

                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tax planning info",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Section 80C Tax-Saving Tracker",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = if (progressFraction >= 1f) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (progressFraction >= 1f) "Maximized" else "${(progressFraction * 100).toInt()}%",
                                    color = if (progressFraction >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Annual 80C Contribution: ${formatRupee(totalTaxInvestment)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Limit: ${formatRupee(limit80C)}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(0.6f),
                                textAlign = TextAlign.End
                            )
                        }

                        if (totalTaxInvestment > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val advice = if (totalTaxInvestment < limit80C) {
                                "You can invest another ${formatRupee(limit80C - totalTaxInvestment)} to fully claim your ₹1.5 Lakh tax deductions."
                            } else {
                                "Awesome! You have fully utilized your Section 80C tax deduction limit."
                            }
                            Text(
                                text = advice,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                lineHeight = 16.sp
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enrolled investments in PPF, SSY, NSC, SCSS, or 5-Year TD help you save tax here.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Interactive Multi-Year Projection Chart Section
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Interest Income Projection",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Swipe/Tap bars for details",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render our Custom Canvas Interactive Projections Bar Chart
                        InteractiveProjectionsBarChart(aggregateProjection.yearlyProjections)
                    }
                }
            }

            // List of Saved Investments Detail Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp, end = 12.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Enrolled Investments",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = { showClearAllConfirmation = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Saved investment details list
            items(savedInvestments) { investment ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = investment.label,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = investment.schemeName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { editingInvestment = investment },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("btn_edit_${investment.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit investment",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteInvestment(investment.id) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("btn_delete_${investment.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove investment",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Investment specifics summary row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                val intValStr = if (investment.recurringInterval == "MONTHLY") {
                                    "${formatRupee(investment.principalAmount)} / month"
                                } else if (investment.recurringInterval == "ANNUALLY") {
                                    "${formatRupee(investment.principalAmount)} / year"
                                } else {
                                    "${formatRupee(investment.principalAmount)} Lumpsum"
                                }
                                Text("Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text(intValStr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Yield", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text("${investment.interestRate}% p.a.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                Text("Tenure", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text("${investment.tenureYears} Years", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Consolidated multi-year table listing
            item {
                Text(
                    text = "Consolidated Year-by-Year Tracker Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 8.dp)
                )
            }

            items(aggregateProjection.yearlyProjections) { aggYr ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Year ${aggYr.year}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Closing Portfolio Value: ${formatRupee(aggYr.closingBalance)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "+${formatRupee(aggYr.interestEarned)} Interest",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (aggYr.deposits > 0) {
                                Text(
                                    text = "Deposited: ${formatRupee(aggYr.deposits)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * An interactive, lightweight Canvas-drawn Bar Chart mapping multi-year interest projections.
 * Users can tap or drag along individual bars to see detailed popups.
 */
@Composable
fun InteractiveProjectionsBarChart(projectedYears: List<AggregateYear>) {
    if (projectedYears.isEmpty()) return

    // Track chosen bar for detail hover popup
    var selectedIndex by remember(projectedYears) { 
        mutableStateOf(if (projectedYears.isNotEmpty()) (projectedYears.size / 2).coerceAtMost(projectedYears.size - 1) else -1)
    }

    val maxInterest = projectedYears.maxOfOrNull { it.interestEarned }?.coerceAtLeast(100.0) ?: 100.0
    val barColor = MaterialTheme.colorScheme.primary
    val barSelectedColor = MaterialTheme.colorScheme.secondary
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Column {
        // Detailed bubble overlay for selected year
        if (selectedIndex >= 0 && selectedIndex < projectedYears.size) {
            val selectedData = projectedYears[selectedIndex]
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = "Year ${selectedData.year} Projected Returns",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${formatRupee(selectedData.interestEarned)}\nInterest Credited",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text("Accruing Wealth", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.61f))
                        Text(formatRupee(selectedData.closingBalance), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Draw Canvas Chart
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val componentWidth = maxWidth
            val componentHeight = maxHeight
            
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(projectedYears) {
                        detectTapGestures { offset ->
                            val totalBars = projectedYears.size
                            if (totalBars > 0) {
                                val barWidthWithSpacing = size.width / totalBars
                                if (barWidthWithSpacing > 0) {
                                    val tappedIdx = (offset.x / barWidthWithSpacing).toInt()
                                    if (tappedIdx in 0 until totalBars) {
                                        selectedIndex = tappedIdx
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val bottomLabelHeight = 24.dp.toPx()
                val topPadding = 12.dp.toPx()
                val chartHeight = (canvasHeight - bottomLabelHeight - topPadding).coerceAtLeast(0f)

                // 1. Draw horizontal grid lines
                val numGridLines = 3
                for (i in 0..numGridLines) {
                    val y = topPadding + (chartHeight / numGridLines) * i
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 2. Plot bars for each year
                val totalBars = projectedYears.size
                if (totalBars > 0) {
                    val barWidthWithSpacing = canvasWidth / totalBars
                    val barSpacing = 4.dp.toPx()
                    val individualBarWidth = (barWidthWithSpacing - barSpacing).coerceAtLeast(2.dp.toPx())

                    for (idx in 0 until totalBars) {
                        val pYear = projectedYears[idx]
                        
                        // Normalize bar height based on relative maximum interest
                        val rawFraction = if (maxInterest > 0 && maxInterest.isFinite()) (pYear.interestEarned / maxInterest) else 0.0
                        val fraction = rawFraction.coerceIn(0.06, 1.0).let { if (it.isFinite()) it else 0.06 }
                        val barHeightPixels = (chartHeight * fraction).toFloat()

                        val xOffset = idx * barWidthWithSpacing + (barSpacing / 2)
                        val yOffset = topPadding + chartHeight - barHeightPixels

                        val isBarSelected = idx == selectedIndex

                        // Draw rounded bar
                        drawRoundRect(
                            color = if (isBarSelected) barSelectedColor else barColor,
                            topLeft = Offset(xOffset, yOffset),
                            size = Size(individualBarWidth, barHeightPixels),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Draw small indicator dot for closing value on top of selected bar
                        if (isBarSelected) {
                            drawCircle(
                                color = barSelectedColor,
                                radius = 4.dp.toPx(),
                                center = Offset(xOffset + (individualBarWidth / 2), yOffset - 6.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Timeline Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Year 1", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text("Duration Timeline Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            Text("Year ${projectedYears.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

/**
 * An interactive, Canvas-drawn bar chart showing year-by-year scale of credited interest for a single savings scheme.
 */
@Composable
fun SingleSchemeProjectionsChart(projectedYears: List<ProjectionYear>) {
    if (projectedYears.isEmpty()) return

    var selectedIndex by remember(projectedYears) { 
        mutableStateOf(if (projectedYears.isNotEmpty()) (projectedYears.size / 2).coerceAtMost(projectedYears.size - 1) else -1)
    }

    val maxInterest = projectedYears.maxOfOrNull { it.interestEarned }?.coerceAtLeast(100.0) ?: 100.0
    val barColor = MaterialTheme.colorScheme.primary
    val barSelectedColor = MaterialTheme.colorScheme.secondary
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Column {
        // Year-by-year detailed summary popup bubble
        if (selectedIndex >= 0 && selectedIndex < projectedYears.size) {
            val selectedData = projectedYears[selectedIndex]
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Year ${selectedData.year} Compounded Return",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${formatRupee(selectedData.interestEarned)} Interest Accrued",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Ledger Balance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.61f))
                        Text(formatRupee(selectedData.closingBalance), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(projectedYears) {
                        detectTapGestures { offset ->
                            val totalBars = projectedYears.size
                            if (totalBars > 0) {
                                val barWidthWithSpacing = size.width / totalBars
                                if (barWidthWithSpacing > 0) {
                                    val tappedIdx = (offset.x / barWidthWithSpacing).toInt()
                                    if (tappedIdx in 0 until totalBars) {
                                        selectedIndex = tappedIdx
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val bottomLabelHeight = 24.dp.toPx()
                val topPadding = 12.dp.toPx()
                val chartHeight = (canvasHeight - bottomLabelHeight - topPadding).coerceAtLeast(0f)

                // Horizontal grid lines
                val numGridLines = 3
                for (i in 0..numGridLines) {
                    val y = topPadding + (chartHeight / numGridLines) * i
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Plot bars
                val totalBars = projectedYears.size
                if (totalBars > 0) {
                    val barWidthWithSpacing = canvasWidth / totalBars
                    val barSpacing = 4.dp.toPx()
                    val individualBarWidth = (barWidthWithSpacing - barSpacing).coerceAtLeast(2.dp.toPx())

                    for (idx in 0 until totalBars) {
                        val pYear = projectedYears[idx]
                        
                        val rawFraction = if (maxInterest > 0 && maxInterest.isFinite()) (pYear.interestEarned / maxInterest) else 0.0
                        val fraction = rawFraction.coerceIn(0.06, 1.0).let { if (it.isFinite()) it else 0.06 }
                        val barHeightPixels = (chartHeight * fraction).toFloat()

                        val xOffset = idx * barWidthWithSpacing + (barSpacing / 2)
                        val yOffset = topPadding + chartHeight - barHeightPixels

                        val isBarSelected = idx == selectedIndex

                        drawRoundRect(
                            color = if (isBarSelected) barSelectedColor else barColor,
                            topLeft = Offset(xOffset, yOffset),
                            size = Size(individualBarWidth, barHeightPixels),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        if (isBarSelected) {
                            drawCircle(
                                color = barSelectedColor,
                                radius = 4.dp.toPx(),
                                center = Offset(xOffset + (individualBarWidth / 2), yOffset - 6.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Year 1", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text("Projection Horizon Timeline", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            Text("Year ${projectedYears.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

/**
 * A dialog allowing the user to edit a saved investment's details (Label, Amount, Rate, Tenure).
 */
@Composable
fun EditInvestmentDialog(
    investment: InvestmentEntity,
    onDismiss: () -> Unit,
    onSave: (InvestmentEntity) -> Unit
) {
    val scheme = SavingsScheme.getSchemeById(investment.schemeType)
    
    var label by remember { mutableStateOf(investment.label) }
    var principalInput by remember { mutableStateOf(investment.principalAmount.toInt().toString()) }
    var interestRateInput by remember { mutableStateOf(investment.interestRate.toString()) }
    var tenureInput by remember { mutableStateOf(investment.tenureYears.toFloat()) }
    
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val principalVal = principalInput.toDoubleOrNull()
                    val rateVal = interestRateInput.toDoubleOrNull()
                    val tenureVal = tenureInput.toInt()
                    
                    if (label.isBlank()) {
                        isError = true
                        errorMessage = "Label cannot be blank."
                        return@Button
                    }
                    if (principalVal == null || principalVal <= 0) {
                        isError = true
                        errorMessage = "Please enter a valid positive deposit amount."
                        return@Button
                    }
                    if (scheme != null) {
                        if (principalVal < scheme.minDeposit) {
                            isError = true
                            errorMessage = "Minimum deposit for ${scheme.shortName} is ${formatRupee(scheme.minDeposit)}."
                            return@Button
                        }
                        if (scheme.maxDeposit != null && principalVal > scheme.maxDeposit) {
                            isError = true
                            errorMessage = "Maximum deposit for ${scheme.shortName} is ${formatRupee(scheme.maxDeposit)}."
                            return@Button
                        }
                    }
                    if (rateVal == null || rateVal <= 0.0 || rateVal > 30.0) {
                        isError = true
                        errorMessage = "Please enter a realistic interest rate (0% - 30%)."
                        return@Button
                    }
                    
                    val updated = investment.copy(
                        label = label.trim(),
                        principalAmount = principalVal,
                        interestRate = rateVal,
                        tenureYears = tenureVal
                    )
                    onSave(updated)
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Edit Investment",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Scheme: ${investment.schemeName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                OutlinedTextField(
                    value = label,
                    onValueChange = { 
                        label = it
                        isError = false
                    },
                    label = { Text("Account Label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_label"),
                    shape = RoundedCornerShape(12.dp)
                )
                
                val amountLabel = if (investment.recurringInterval == "MONTHLY") "Monthly Installment" 
                                  else if (investment.recurringInterval == "ANNUALLY") "Annual Installment"
                                  else "One-time Lumpsum"
                
                OutlinedTextField(
                    value = principalInput,
                    onValueChange = { 
                        principalInput = it
                        isError = false
                    },
                    label = { Text(amountLabel) },
                    prefix = { Text("₹ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_principal"),
                    shape = RoundedCornerShape(12.dp)
                )
                
                OutlinedTextField(
                    value = interestRateInput,
                    onValueChange = { 
                        interestRateInput = it
                        isError = false
                    },
                    label = { Text("Interest Rate (% p.a.)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_rate"),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tenure Horizon:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("${tenureInput.toInt()} Years", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = tenureInput,
                        onValueChange = { tenureInput = it },
                        valueRange = 1f..30f,
                        steps = 29,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_slider_tenure"),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                
                if (isError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * An interactive Tax Planner and Calculator Screen.
 * Evaluates Indian Income Tax slabs under Old & New regimes, and integrates
 * with active portfolio savings statistics to show tax optimization guidelines.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxPlannerScreen(viewModel: MainActivityViewModel) {
    val savedInvestments by viewModel.savedInvestments.collectAsStateWithLifecycle()
    val incomeText by viewModel.taxAnnualIncomeInput.collectAsStateWithLifecycle()
    val taxRateText by viewModel.taxMarginalRateInput.collectAsStateWithLifecycle()

    var customSimulateAmountInput by remember { mutableStateOf("100000") }

    // Deduce Section 80C investments automatically from active portfolio
    val eligibleSchemes80C = listOf("PPF", "SSY", "NSC", "SCSS", "POTD_5Y")
    val taxSavingsPortfolio = savedInvestments.filter { it.schemeType in eligibleSchemes80C }
    val auto80CAmount = taxSavingsPortfolio.sumOf { investment ->
        if (investment.recurringInterval == "MONTHLY" && investment.isRecurring) {
            investment.principalAmount * 12
        } else {
            investment.principalAmount
        }
    }.coerceAtMost(150000.0)

    val incomeVal = incomeText.toDoubleOrNull() ?: 1200000.0
    val simVal = customSimulateAmountInput.toDoubleOrNull() ?: 100000.0

    val oldRegimeTax = TaxCalculator.calculateOldRegimeTax(incomeVal, auto80CAmount)
    val newRegimeTax = TaxCalculator.calculateNewRegimeTax(incomeVal)

    // Automatically determine marginal tax rate from income (marginal slab bracket under New Regime u/s 115BAC)
    val computedMarginalRate = when {
        incomeVal <= 300000.0 -> 0.0
        incomeVal <= 700000.0 -> 5.0
        incomeVal <= 1000000.0 -> 10.0
        incomeVal <= 1200000.0 -> 15.0
        incomeVal <= 1500000.0 -> 20.0
        else -> 30.0
    }

    // Update VM slab rate if computed value changes to synchronize with investment calculators
    LaunchedEffect(computedMarginalRate) {
        if (computedMarginalRate >= 0.0) {
            viewModel.taxMarginalRateInput.value = computedMarginalRate.toString()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Income Tax slab estimator card
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "SMART TAX OPTIMIZER & PLANNER",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = incomeText,
                            onValueChange = { viewModel.taxAnnualIncomeInput.value = it },
                            label = { Text("Gross Annual Taxable Income", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)) },
                            prefix = { Text("₹", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tax_input_income"),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Standard deduction and eligible Section 80C limits (auto-synced from portfolio) are applied to arrive at your estimated tax liability.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Side-by-Side Tax Regimes comparisons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Old Regime
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("OLD REGIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatRupee(oldRegimeTax), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Standard Ded: ₹50k\nSection 80C: ${formatRupee(auto80CAmount)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), lineHeight = 15.sp)
                        }
                    }

                    // New Regime
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("NEW REGIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatRupee(newRegimeTax), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Standard Ded: ₹75k\nSection 80C: N/A\n(Zero rebate above ₹7L)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), lineHeight = 15.sp)
                        }
                    }
                }

                // Regime advice banner
                val taxDiff = oldRegimeTax - newRegimeTax
                val regimeAdvise = if (taxDiff > 0) {
                    "The **New Tax Regime** is more cost-effective for you, saving **${formatRupee(taxDiff)}** in tax!"
                } else if (taxDiff < 0) {
                    "The **Old Tax Regime** is more cost-effective for you, saving **${formatRupee(-taxDiff)}** due to Section 80C savings!"
                } else {
                    "Both tax regimes result in the exact same tax liability."
                }

                Surface(
                    color = if (taxDiff == 0.0) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Tax regime recommendations",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = regimeAdvise,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Interactive "How investment decisions affect taxation" comparer section
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Investment Options Yield Comparer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Evaluate post-tax returns across ALL government savings schemes at your estimated tax bracket.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = customSimulateAmountInput,
                        onValueChange = { customSimulateAmountInput = it },
                        label = { Text("Simulate One-Time Deposit Amount") },
                        prefix = { Text("₹ ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tax_simulate_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Header for schemes
        item {
            Text(
                text = "Scheme Post-Tax Return Yield Comparison",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp)
            )
        }

        // List each scheme and compute post-tax yields
        items(SavingsScheme.ALL_SCHEMES) { scheme ->
            val preTaxRate = scheme.currentInterestRate
            val isExempt = TaxCalculator.isInterestTaxExempt(scheme.id)
            val is80C = TaxCalculator.isEligible80C(scheme.id)

            // Dynamic post-tax yield
            val slabVal = taxRateText.toDoubleOrNull() ?: 20.0
            val effectiveSlab = if (isExempt) 0.0 else slabVal
            val postTaxRate = preTaxRate * (1 - effectiveSlab / 100.0)

            // Sim net returns for 1 year
            val grossYield = simVal * (preTaxRate / 100.0)
            val taxPaid = if (isExempt) 0.0 else grossYield * (effectiveSlab / 100.0)
            val postTaxYield = if ((grossYield - taxPaid).isFinite()) (grossYield - taxPaid) else 0.0
            val annual80CSavings = if (is80C) simVal * (slabVal / 100.0) else 0.0

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = scheme.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Maturity: ${scheme.lockInPeriod} Lock-in",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        // Tax treatment badge
                        Surface(
                            color = if (isExempt) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isExempt) "Tax Free (EEE)" else "Slab Taxed (EET/T)",
                                color = if (isExempt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Pre-Tax Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("$preTaxRate% p.a.", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Post-Tax Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(String.format(Locale.US, "%.2f%% p.a.", postTaxRate), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Net Interest (1 Yr)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text(formatRupee(postTaxYield), fontSize = 13.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (is80C) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "80C Benefit: saves up to ${formatRupee(annual80CSavings)} in upfront tax!",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

