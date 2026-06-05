package com.example.data

import kotlinx.coroutines.flow.Flow

class InvestmentRepository(private val investmentDao: InvestmentDao) {
    val allInvestments: Flow<List<InvestmentEntity>> = investmentDao.getAllInvestments()

    suspend fun insert(investment: InvestmentEntity) {
        investmentDao.insertInvestment(investment)
    }

    suspend fun delete(investment: InvestmentEntity) {
        investmentDao.deleteInvestment(investment)
    }

    suspend fun deleteById(id: Int) {
        investmentDao.deleteInvestmentById(id)
    }

    suspend fun deleteAll() {
        investmentDao.deleteAllInvestments()
    }
}
