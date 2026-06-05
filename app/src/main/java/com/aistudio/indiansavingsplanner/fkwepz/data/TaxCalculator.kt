package com.aistudio.indiansavingsplanner.fkwepz.data

import kotlin.math.min

object TaxCalculator {
    /**
     * Determines if a scheme is eligible for Section 80C deduction.
     */
    fun isEligible80C(schemeId: String): Boolean {
        return when (schemeId) {
            "PPF", "SSY", "NSC", "SCSS", "POTD_5Y" -> true
            else -> false
        }
    }

    /**
     * Determines if the interest of a scheme is completely tax-exempt.
     */
    fun isInterestTaxExempt(schemeId: String): Boolean {
        return when (schemeId) {
            "PPF", "SSY" -> true
            else -> false
        }
    }

    /**
     * Calculates tax under the NEW tax regime for a given gross income.
     * New Regime Slabs (FY 2024-25):
     * - Up to ₹3L: Nil
     * - ₹3L to ₹7L: 5% (Rebate u/s 87A makes tax ₹0 if taxable income <= ₹7L)
     * - ₹7L to ₹10L: 10%
     * - ₹10L to ₹12L: 15%
     * - ₹12L to ₹15L: 20%
     * - Above ₹15L: 30%
     * Standard deduction of ₹75,000 applies.
     */
    fun calculateNewRegimeTax(grossIncome: Double): Double {
        val standardDeduction = 75000.0
        val taxableIncome = (grossIncome - standardDeduction).coerceAtLeast(0.0)
        
        // Full rebate if taxable income is <= 7 Lakhs (FY 24-25)
        if (taxableIncome <= 700000.0) {
            return 0.0
        }
        
        var tax = 0.0
        var remaining = taxableIncome
        
        // Up to 3L (0%)
        val slab1 = min(remaining, 300000.0)
        remaining -= slab1
        
        // 3L to 7L (5%)
        if (remaining > 0) {
            val slab2 = min(remaining, 400000.0)
            tax += slab2 * 0.05
            remaining -= slab2
        }
        
        // 7L to 10L (10%)
        if (remaining > 0) {
            val slab3 = min(remaining, 300000.0)
            tax += slab3 * 0.10
            remaining -= slab3
        }
        
        // 10L to 12L (15%)
        if (remaining > 0) {
            val slab4 = min(remaining, 200000.0)
            tax += slab4 * 0.15
            remaining -= slab4
        }
        
        // 12L to 15L (20%)
        if (remaining > 0) {
            val slab5 = min(remaining, 300000.0)
            tax += slab5 * 0.20
            remaining -= slab5
        }
        
        // Above 15L (30%)
        if (remaining > 0) {
            tax += remaining * 0.30
        }
        
        // Add 4% health & education cess
        return tax * 1.04
    }

    /**
     * Calculates tax under the OLD tax regime for a given gross income, taking 80C deductions into account.
     * Old Regime Slabs:
     * - Up to ₹2.5L: Nil
     * - ₹2.5L to ₹5L: 5% (Rebate u/s 87A makes tax ₹0 if taxable income <= ₹5L)
     * - ₹5L to ₹10L: 20%
     * - Above ₹10L: 30%
     * Standard deduction of ₹50,000 applies. 
     * Max Section 80C deduction is ₹1,50,000.
     */
    fun calculateOldRegimeTax(grossIncome: Double, eligible80CInvestments: Double): Double {
        val standardDeduction = 50000.0
        val max80C = 150000.0
        val deduction80C = min(eligible80CInvestments, max80C)
        
        val taxableIncome = (grossIncome - standardDeduction - deduction80C).coerceAtLeast(0.0)
        
        if (taxableIncome <= 500000.0) {
            return 0.0
        }
        
        var tax = 0.0
        var remaining = taxableIncome
        
        // Up to 2.5L (0%)
        val slab1 = min(remaining, 250000.0)
        remaining -= slab1
        
        // 2.5L to 5L (5%)
        if (remaining > 0) {
            val slab2 = min(remaining, 250000.0)
            tax += slab2 * 0.05
            remaining -= slab2
        }
        
        // 5L to 10L (20%)
        if (remaining > 0) {
            val slab3 = min(remaining, 500000.0)
            tax += slab3 * 0.20
            remaining -= slab3
        }
        
        // Above 10L (30%)
        if (remaining > 0) {
            tax += remaining * 0.30
        }
        
        // Add 4% health & education cess
        return tax * 1.04
    }

    /**
     * Simulates the post-tax yields of a savings scheme over its entire tenure.
     */
    fun calculatePostTaxReturns(
        schemeId: String,
        preTaxMaturityValue: Double,
        preTaxInterestEarned: Double,
        marginalTaxRatePercent: Double,
        isSection80CClaimedInOldRegime: Boolean,
        principalInvestedInAYear: Double
    ): TaxImpact {
        // Find if interest is taxable
        val interestIsFullyTaxExempt = isInterestTaxExempt(schemeId)
        val interestTaxRate = if (interestIsFullyTaxExempt) 0.0 else marginalTaxRatePercent / 100.0
        
        val totalTaxOnInterest = preTaxInterestEarned * interestTaxRate
        val postTaxInterestEarned = preTaxInterestEarned - totalTaxOnInterest
        val postTaxMaturityValue = preTaxMaturityValue - totalTaxOnInterest
        
        // Old regime tax savings from 80C
        val is80C = isEligible80C(schemeId)
        val annualTaxSavingsOldRegime = if (is80C && isSection80CClaimedInOldRegime) {
            val deductionAmount = min(principalInvestedInAYear, 150000.0)
            deductionAmount * (marginalTaxRatePercent / 100.0)
        } else {
            0.0
        }
        
        return TaxImpact(
            preTaxMaturityValue = preTaxMaturityValue,
            preTaxInterestEarned = preTaxInterestEarned,
            totalTaxOnInterest = totalTaxOnInterest,
            postTaxMaturityValue = postTaxMaturityValue,
            postTaxInterestEarned = postTaxInterestEarned,
            annualTaxSavingsOldRegime = annualTaxSavingsOldRegime,
            isInterestTaxExempt = interestIsFullyTaxExempt,
            isEligible80C = is80C
        )
    }
}

data class TaxImpact(
    val preTaxMaturityValue: Double,
    val preTaxInterestEarned: Double,
    val totalTaxOnInterest: Double,
    val postTaxMaturityValue: Double,
    val postTaxInterestEarned: Double,
    val annualTaxSavingsOldRegime: Double,
    val isInterestTaxExempt: Boolean,
    val isEligible80C: Boolean
)
