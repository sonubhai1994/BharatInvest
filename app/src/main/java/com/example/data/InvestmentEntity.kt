package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val schemeType: String, // PPF, SSY, NSC, SCSS, POMIS, KVP, MSSC, POTD_1Y, POTD_2Y, POTD_3Y, POTD_5Y, PORD
    val schemeName: String,
    val principalAmount: Double,
    val isRecurring: Boolean,
    val recurringInterval: String, // "MONTHLY", "ANNUALLY", "NONE"
    val interestRate: Double,
    val tenureYears: Int,
    val startDateMillis: Long = System.currentTimeMillis(),
    val label: String
)
