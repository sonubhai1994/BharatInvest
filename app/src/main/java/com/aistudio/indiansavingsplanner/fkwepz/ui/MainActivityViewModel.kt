package com.aistudio.indiansavingsplanner.fkwepz.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aistudio.indiansavingsplanner.fkwepz.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

data class AggregateYear(
    val year: Int,
    val deposits: Double,
    val interestEarned: Double,
    val closingBalance: Double
)

data class PortfolioAggregate(
    val totalInvested: Double,
    val totalInterestEarned: Double,
    val currentValue: Double,
    val yearlyProjections: List<AggregateYear>
)

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = InvestmentRepository(db.investmentDao())

    private val _activeTab = MutableStateFlow("EXPLORE")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    val selectedScheme = MutableStateFlow<SavingsScheme>(SavingsScheme.ALL_SCHEMES.first())
    val principalInput = MutableStateFlow("")
    val isMonthlyInput = MutableStateFlow(false)
    val customRateInput = MutableStateFlow("")
    val customTenureInput = MutableStateFlow("")
    val investmentLabelInput = MutableStateFlow("")
    val simulateReinvestmentInput = MutableStateFlow(false)

    // Tax Planner interactive states
    val taxAnnualIncomeInput = MutableStateFlow("1200000")
    val taxMarginalRateInput = MutableStateFlow("20.0")
    val taxRegimeInput = MutableStateFlow("NEW")

    init {
        selectScheme(SavingsScheme.ALL_SCHEMES.first())
    }

    fun selectScheme(scheme: SavingsScheme) {
        selectedScheme.value = scheme
        // Defaults
        isMonthlyInput.value = scheme.depositType == DepositType.MONTHLY_RECURRING
        customRateInput.value = scheme.currentInterestRate.toString()
        customTenureInput.value = scheme.defaultTenureYears.toString()
        principalInput.value = ""
        investmentLabelInput.value = scheme.shortName + " Inv"
        simulateReinvestmentInput.value = false
    }

    val calculatedProjection: StateFlow<List<ProjectionYear>> = combine(
        selectedScheme,
        principalInput.debounce(300),
        isMonthlyInput,
        customRateInput.debounce(300),
        customTenureInput.debounce(300),
        simulateReinvestmentInput
    ) { flows ->
        val scheme = (flows[0] as? SavingsScheme) ?: return@combine emptyList()
        val principal = (flows[1] as? String) ?: ""
        val isMonthly = (flows[2] as? Boolean) ?: false
        val rate = (flows[3] as? String) ?: ""
        val tenure = (flows[4] as? String) ?: ""
        val simulate = (flows[5] as? Boolean) ?: false

        val amount = principal.toDoubleOrNull()?.coerceIn(0.0, 1e15) ?: return@combine emptyList()
        val r = rate.toDoubleOrNull()?.coerceIn(0.0, 100.0) ?: scheme.currentInterestRate
        // Cap tenure to 100 years for individual calculation safety
        val t = tenure.toIntOrNull()?.coerceIn(1, 100) ?: scheme.defaultTenureYears
        val depositType = if (isMonthly) DepositType.MONTHLY_RECURRING else DepositType.ANNUAL_RECURRING

        scheme.calculateProjection(
            amount = amount,
            customInterestRate = r,
            recurringTypeOverride = depositType,
            tenureYearsOverride = t,
            simulateReinvestment = simulate
        )
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedInvestments: StateFlow<List<InvestmentEntity>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolioAggregateProjection: StateFlow<PortfolioAggregate> = savedInvestments.map { investments ->
        var totalInvested = 0.0
        var totalInterestEarned = 0.0
        var totalValue = 0.0
        val aggregateYears = mutableMapOf<Int, AggregateYear>()

        // Find max tenure, capped at 100 for portfolio visualization safety
        val maxYears = (investments.maxOfOrNull { it.tenureYears } ?: 0).coerceAtMost(100)

        for (investment in investments) {
            val scheme = SavingsScheme.getSchemeById(investment.schemeType) ?: continue
            
            // Calculate total invested logic based on recurring or lumpsum
            val cycleMaturity = investment.tenureYears.coerceIn(1, 100)
            val amount = investment.principalAmount.coerceIn(0.0, 1e15)
            val investedInItem = if (investment.recurringInterval == "MONTHLY") {
                amount * 12 * (if (scheme.id == "SSY") minOf(15, cycleMaturity) else cycleMaturity)
            } else if (investment.recurringInterval == "ANNUALLY") {
                amount * (if (scheme.id == "SSY") minOf(15, cycleMaturity) else cycleMaturity)
            } else {
                amount
            }
            totalInvested += investedInItem

            // Simulation
            val projections = scheme.calculateProjection(
                amount = amount,
                customInterestRate = investment.interestRate.coerceIn(0.0, 100.0),
                tenureYearsOverride = cycleMaturity,
                simulateReinvestment = investment.isRecurring
            )

            if (projections.isNotEmpty()) {
                val last = projections.last()
                totalValue += last.closingBalance
                totalInterestEarned += last.cumulativeInterest
            }

            for (p in projections) {
                // Also cap p.year in map just in case
                if (p.year > 100) continue
                val existing = aggregateYears[p.year] ?: AggregateYear(p.year, 0.0, 0.0, 0.0)
                aggregateYears[p.year] = AggregateYear(
                    year = p.year,
                    deposits = existing.deposits + p.depositsDuringYear,
                    interestEarned = existing.interestEarned + p.interestEarned,
                    closingBalance = existing.closingBalance + p.closingBalance
                )
            }
        }

        // Final cleanup for safety
        val finalInvested = if (totalInvested.isFinite()) totalInvested else 0.0
        val finalInterest = if (totalInterestEarned.isFinite()) totalInterestEarned else 0.0
        val finalValue = if (totalValue.isFinite()) totalValue else 0.0

        val yearlyProjections = (1..maxYears).map { y ->
            val agg = aggregateYears[y] ?: AggregateYear(y, 0.0, 0.0, 0.0)
            if (agg.closingBalance.isFinite()) agg else AggregateYear(y, 0.0, 0.0, 0.0)
        }

        PortfolioAggregate(
            totalInvested = finalInvested,
            totalInterestEarned = finalInterest,
            currentValue = finalValue,
            yearlyProjections = yearlyProjections
        )
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PortfolioAggregate(0.0, 0.0, 0.0, emptyList()))

    fun updateInvestment(investment: InvestmentEntity) = viewModelScope.launch {
        repository.insert(investment)
    }

    fun deleteInvestment(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }

    fun clearAllInvestments() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun saveCurrentToPortfolio() = viewModelScope.launch {
        val scheme = selectedScheme.value
        val amount = principalInput.value.toDoubleOrNull() ?: return@launch
        val r = customRateInput.value.toDoubleOrNull() ?: scheme.currentInterestRate
        val t = customTenureInput.value.toIntOrNull() ?: scheme.defaultTenureYears
        
        val investment = InvestmentEntity(
            schemeType = scheme.id,
            schemeName = scheme.name,
            principalAmount = amount,
            isRecurring = simulateReinvestmentInput.value,
            recurringInterval = if (isMonthlyInput.value) "MONTHLY" else if (scheme.depositType == DepositType.LUMPSUM) "NONE" else "ANNUALLY",
            interestRate = r,
            tenureYears = t,
            label = investmentLabelInput.value.ifBlank { scheme.shortName + " Inv" }
        )
        repository.insert(investment)
        principalInput.value = "" // clear input after saving
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
