package com.example.finplan.model

data class UserProfile(
    val monthlySalary: Double = 0.0,
    val investmentPercentage: Double = 0.0
)

data class ExpenseCategory(
    val name: String,
    val amount: Double,
    val percentage: Double
)

enum class InvestmentProfileType {
    CONSERVADOR, MODERADO, AGRESSIVO
}

data class InvestmentAllocation(
    val assetName: String,
    val percentage: Double,
    val amount: Double
)

data class InvestmentProfile(
    val type: InvestmentProfileType,
    val description: String,
    val allocations: List<InvestmentAllocation>
)
