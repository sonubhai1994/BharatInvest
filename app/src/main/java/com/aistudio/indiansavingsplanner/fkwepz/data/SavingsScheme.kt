package com.aistudio.indiansavingsplanner.fkwepz.data

import kotlin.math.pow

enum class DepositType {
    LUMPSUM,
    MONTHLY_RECURRING,
    ANNUAL_RECURRING
}

enum class InterestPayoutType {
    ACCUMULATE,  // Compounded, paid at end / maturity
    MONTHLY,     // Disbursed monthly
    QUARTERLY,   // Disbursed quarterly
    ANNUALLY     // Disbursed annually
}

data class ProjectionYear(
    val year: Int,
    val openingBalance: Double,
    val depositsDuringYear: Double,
    val interestEarned: Double,
    val closingBalance: Double,
    val cumulativeInterest: Double
)

data class SavingsScheme(
    val id: String,
    val name: String,
    val shortName: String,
    val currentInterestRate: Double,
    val depositType: DepositType,
    val payoutType: InterestPayoutType,
    val minDeposit: Double,
    val maxDeposit: Double?,
    val defaultTenureYears: Int,
    val isTenureFixed: Boolean,
    val taxTreatment: String,
    val eligibility: String,
    val description: String,
    val lockInPeriod: String
) {
    companion object {
        val ALL_SCHEMES = listOf(
            SavingsScheme(
                id = "PPF",
                name = "Public Provident Fund (PPF)",
                shortName = "PPF",
                currentInterestRate = 7.1,
                depositType = DepositType.ANNUAL_RECURRING, // default annual, can do monthly too
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 500.0,
                maxDeposit = 150000.0,
                defaultTenureYears = 15,
                isTenureFixed = true,
                taxTreatment = "EEE (Exempt, Exempt, Exempt)",
                eligibility = "Resident Indian Citizens",
                description = "Government-backed long-term saving scheme with complete tax exemption on interest and maturity under Section 80C. Best for retirement planning.",
                lockInPeriod = "15 Years"
            ),
            SavingsScheme(
                id = "SSY",
                name = "Sukanya Samriddhi Yojana (SSY)",
                shortName = "SSY",
                currentInterestRate = 8.2,
                depositType = DepositType.ANNUAL_RECURRING,
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 250.0,
                maxDeposit = 150000.0,
                defaultTenureYears = 21,
                isTenureFixed = true,
                taxTreatment = "EEE (Exempt, Exempt, Exempt)",
                eligibility = "Parents of Girl Child (under 10 years)",
                description = "Special savings scheme for the girl child. Offers the highest government interest rate with tax savings. Deposits required only for 15 years, matures in 21 years.",
                lockInPeriod = "21 Years (or marriage after 18)"
            ),
            SavingsScheme(
                id = "NSC",
                name = "National Savings Certificate (NSC)",
                shortName = "NSC",
                currentInterestRate = 7.7,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 1000.0,
                maxDeposit = null,
                defaultTenureYears = 5,
                isTenureFixed = true,
                taxTreatment = "EET (Interest taxable, Section 80C deduction is available)",
                eligibility = "All Resident Individuals",
                description = "Safe fixed-income investment scheme offered by Post Offices. Interest is compounded annually but paid at maturity after 5 years.",
                lockInPeriod = "5 Years"
            ),
            SavingsScheme(
                id = "SCSS",
                name = "Senior Citizens Savings Scheme (SCSS)",
                shortName = "SCSS",
                currentInterestRate = 8.2,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.QUARTERLY,
                minDeposit = 1000.0,
                maxDeposit = 3000000.0,
                defaultTenureYears = 5,
                isTenureFixed = false,
                taxTreatment = "Interest taxable (TDS applies, Section 80C available)",
                eligibility = "Individuals aged 60 years or above (55 for retired employees)",
                description = "Regular income scheme for senior citizens. Guarantees quarterly payouts at a very high sovereign rate. Capital is fully protected.",
                lockInPeriod = "5 Years (extendable by 3 years)"
            ),
            SavingsScheme(
                id = "POMIS",
                name = "Post Office Monthly Income Scheme (MIS)",
                shortName = "POMIS",
                currentInterestRate = 7.4,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.MONTHLY,
                minDeposit = 1000.0,
                maxDeposit = 900000.0, // 9L single, 15L joint
                defaultTenureYears = 5,
                isTenureFixed = true,
                taxTreatment = "Interest fully taxable (No Section 80C deduction)",
                eligibility = "All Resident Individuals (Single or Joint up to 3)",
                description = "Best for fixed monthly income payouts. Keeps your principal safe and provides steady cash flow every month.",
                lockInPeriod = "5 Years"
            ),
            SavingsScheme(
                id = "KVP",
                name = "Kisan Vikas Patra (KVP)",
                shortName = "KVP",
                currentInterestRate = 7.5,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 1000.0,
                maxDeposit = null,
                defaultTenureYears = 9, // precisely 115 months (9 years, 7 months)
                isTenureFixed = true,
                taxTreatment = "Interest taxable (No Section 80C deduction)",
                eligibility = "All Resident Individuals",
                description = "A savings certificate that doubles your one-time principal investment in exactly 115 months (9 years 7 months). High liquidity options.",
                lockInPeriod = "2 Years 6 Months"
            ),
            SavingsScheme(
                id = "MSSC",
                name = "Mahila Samman Savings Certificate (MSSC)",
                shortName = "MSSC",
                currentInterestRate = 7.5,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 1000.0,
                maxDeposit = 200000.0,
                defaultTenureYears = 2,
                isTenureFixed = true,
                taxTreatment = "Interest taxable (TDS applies if limits exceed)",
                eligibility = "Women or Representative of Minor Girl Child",
                description = "Special, short-term sovereign-backed savings scheme launched strictly for women. Compounded quarterly.",
                lockInPeriod = "2 Years"
            ),
            SavingsScheme(
                id = "POTD_5Y",
                name = "Post Office Time Deposit (5-Year TD)",
                shortName = "POTD 5Yr",
                currentInterestRate = 7.5,
                depositType = DepositType.LUMPSUM,
                payoutType = InterestPayoutType.ANNUALLY, // interest payable annually, compounded quarterly
                minDeposit = 1000.0,
                maxDeposit = null,
                defaultTenureYears = 5,
                isTenureFixed = false,
                taxTreatment = "Tax deduction on 5-Year TD under Section 80C. Interest is taxable.",
                eligibility = "All Resident Individuals",
                description = "Fixed deposit with compounding interest calculated quarterly but paid/added to savings annually. Safe government FD.",
                lockInPeriod = "No withdrawal in first 6 months"
            ),
            SavingsScheme(
                id = "PORD",
                name = "Post Office Recurring Deposit (PORD)",
                shortName = "PORD",
                currentInterestRate = 6.7,
                depositType = DepositType.MONTHLY_RECURRING,
                payoutType = InterestPayoutType.ACCUMULATE,
                minDeposit = 100.0,
                maxDeposit = null,
                defaultTenureYears = 5,
                isTenureFixed = true,
                taxTreatment = "Interest taxable (No Section 80C deduction)",
                eligibility = "All Resident Individuals",
                description = "A systematic monthly installation investment scheme that matures after 5 Years. Compounded quarterly with high returns.",
                lockInPeriod = "5 Years (matures)"
            )
        )

        fun getSchemeById(id: String): SavingsScheme? {
            return ALL_SCHEMES.find { it.id == id }
        }
    }

    /**
     * Calculates the year-by-year projections for this scheme based on user parameters,
     * including automatic rollover/reinvestment simulation for multi-cycle compounding.
     */
    fun calculateProjection(
        amount: Double,
        customInterestRate: Double? = null,
        recurringTypeOverride: DepositType? = null,
        tenureYearsOverride: Int? = null,
        simulateReinvestment: Boolean = false
    ): List<ProjectionYear> {
        val rate = customInterestRate ?: this.currentInterestRate
        val years = (tenureYearsOverride ?: this.defaultTenureYears).coerceIn(1, 100)
        val depType = recurringTypeOverride ?: this.depositType

        // Determine natural cycle duration for this scheme
        val cycleMaturity = when (id) {
            "PPF" -> 15
            "SSY" -> 21
            "NSC" -> 5
            "SCSS" -> 5
            "POMIS" -> 5
            "KVP" -> 9
            "MSSC" -> 2
            "POTD_5Y" -> 5
            "PORD" -> 5
            else -> 5
        }

        val list = mutableListOf<ProjectionYear>()
        var cumulativeInterest = 0.0
        var lastCycleMaturedClosingBalance = 0.0
        var runningClosingBalance = 0.0

        for (currYear in 1..years) {
            val deposits: Double
            val interest: Double
            val closingBalance: Double

            // 1. If past natural maturity and reinvestment is OFF, investment stays flat, no interest paid out
            if (currYear > cycleMaturity && !simulateReinvestment) {
                deposits = 0.0
                interest = 0.0
                closingBalance = if (id == "SCSS" || id == "POMIS") amount else runningClosingBalance
                
                list.add(
                    ProjectionYear(
                        year = currYear,
                        openingBalance = closingBalance,
                        depositsDuringYear = deposits,
                        interestEarned = interest,
                        closingBalance = closingBalance,
                        cumulativeInterest = cumulativeInterest
                    )
                )
                continue
            }

            // 2. Determine cycle indicators
            val cycleIndex = (currYear - 1) / cycleMaturity
            val cycleYear = ((currYear - 1) % cycleMaturity) + 1

            // Set the rolled over maturity amount at the transition boundaries
            if (cycleIndex > 0 && cycleYear == 1 && simulateReinvestment) {
                lastCycleMaturedClosingBalance = runningClosingBalance
            }

            val previousClosing = if (currYear == 1) 0.0 else runningClosingBalance
            val cycleOpenBalance = if (cycleYear == 1) {
                if (cycleIndex == 0) 0.0 else lastCycleMaturedClosingBalance
            } else {
                previousClosing
            }

            // Calculate cycle values according to the scheme design
            when (id) {
                "PPF" -> {
                    if (depType == DepositType.ANNUAL_RECURRING) {
                        deposits = amount
                        val baseBalance = cycleOpenBalance + deposits
                        interest = baseBalance * (rate / 100.0)
                        closingBalance = baseBalance + interest
                    } else { // MONTHLY_RECURRING
                        deposits = amount * 12
                        var yearInterestAccrual = 0.0
                        for (month in 1..12) {
                            val monthlyBal = cycleOpenBalance + (month * amount)
                            yearInterestAccrual += monthlyBal * (rate / 1200.0)
                        }
                        interest = yearInterestAccrual
                        closingBalance = cycleOpenBalance + deposits + interest
                    }
                }
                "SSY" -> {
                    // Deposits required only for first 15 years of each cycle
                    if (cycleYear <= 15) {
                        if (depType == DepositType.ANNUAL_RECURRING) {
                            deposits = amount
                            val baseBalance = cycleOpenBalance + deposits
                            interest = baseBalance * (rate / 100.0)
                            closingBalance = baseBalance + interest
                        } else { // MONTHLY_RECURRING
                            deposits = amount * 12
                            var yearInterestAccrual = 0.0
                            for (month in 1..12) {
                                val monthlyBal = cycleOpenBalance + (month * amount)
                                yearInterestAccrual += monthlyBal * (rate / 1200.0)
                            }
                            interest = yearInterestAccrual
                            closingBalance = cycleOpenBalance + deposits + interest
                        }
                    } else {
                        deposits = 0.0
                        interest = cycleOpenBalance * (rate / 100.0)
                        closingBalance = cycleOpenBalance + interest
                    }
                }
                "NSC" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    interest = (base + deposits) * (rate / 100.0)
                    closingBalance = (base + deposits) + interest
                }
                "SCSS" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val principal = if (cycleIndex == 0) amount else lastCycleMaturedClosingBalance
                    interest = principal * (rate / 100.0)
                    closingBalance = principal
                }
                "POMIS" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val principal = if (cycleIndex == 0) amount else lastCycleMaturedClosingBalance
                    interest = principal * (rate / 100.0)
                    closingBalance = principal
                }
                "KVP" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    
                    if (cycleYear == cycleMaturity) {
                        val cycleStartValue = if (cycleIndex == 0) amount else lastCycleMaturedClosingBalance
                        closingBalance = cycleStartValue * 2.0
                        interest = closingBalance - (base + deposits)
                    } else {
                        interest = (base + deposits) * (rate / 100.0)
                        closingBalance = (base + deposits) + interest
                    }
                }
                "MSSC" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    val principal = base + deposits
                    closingBalance = principal * (1 + rate / 400.0).pow(4.0)
                    interest = closingBalance - principal
                }
                "POTD_5Y" -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    val principal = base + deposits
                    closingBalance = principal * (1 + rate / 400.0).pow(4.0)
                    interest = closingBalance - principal
                }
                "PORD" -> {
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    deposits = amount * 12
                    
                    var currentTrackingBalance = base
                    var annualInterestAccumulated = 0.0
                    
                    for (month in 1..12) {
                        currentTrackingBalance += amount
                        if (month % 3 == 0) {
                            val quarterInterest = currentTrackingBalance * (rate / 400.0)
                            annualInterestAccumulated += quarterInterest
                            currentTrackingBalance += quarterInterest
                        }
                    }
                    interest = annualInterestAccumulated
                    closingBalance = base + deposits + interest
                }
                else -> {
                    deposits = if (cycleYear == 1 && cycleIndex == 0) amount else 0.0
                    val base = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance
                    interest = (base + deposits) * (rate / 100.0)
                    closingBalance = (base + deposits) + interest
                }
            }

            cumulativeInterest += if (interest.isFinite()) interest else 0.0
            runningClosingBalance = if (closingBalance.isFinite()) closingBalance else if (previousClosing.isFinite()) previousClosing else 0.0

            list.add(
                ProjectionYear(
                    year = currYear,
                    openingBalance = if (cycleYear == 1 && cycleIndex > 0) lastCycleMaturedClosingBalance else cycleOpenBalance,
                    depositsDuringYear = deposits,
                    interestEarned = if (interest.isFinite()) interest else 0.0,
                    closingBalance = runningClosingBalance,
                    cumulativeInterest = cumulativeInterest
                )
            )
        }

        return list
    }
}
