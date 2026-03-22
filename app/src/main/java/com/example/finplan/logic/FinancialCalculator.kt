package com.example.finplan.logic

import com.example.finplan.model.*

object FinancialCalculator {

    fun calculateExpenses(salary: Double, investmentPercentage: Double): List<ExpenseCategory> {
        val investmentAmount = salary * (investmentPercentage / 100.0)
        val remaining = salary - investmentAmount
        
        // Simple 50/30/20 rule adaptation
        // If investment is X%, we split the rest proportionally or fix them
        // Let's assume: 50% Essential, 30% Lifestyle, 20% Investment as base.
        // We'll scale Essential and Lifestyle based on what's left.
        
        val essentialPercentage = 50.0
        val lifestylePercentage = 30.0
        val baseSum = essentialPercentage + lifestylePercentage
        
        val actualEssential = (essentialPercentage / baseSum) * (100.0 - investmentPercentage)
        val actualLifestyle = (lifestylePercentage / baseSum) * (100.0 - investmentPercentage)
        
        return listOf(
            ExpenseCategory("Gastos Essenciais", salary * (actualEssential / 100.0), actualEssential),
            ExpenseCategory("Estilo de Vida", salary * (actualLifestyle / 100.0), actualLifestyle),
            ExpenseCategory("Investimentos", investmentAmount, investmentPercentage)
        )
    }

    fun getInvestmentProfiles(investmentAmount: Double): List<InvestmentProfile> {
        return listOf(
            InvestmentProfile(
                type = InvestmentProfileType.CONSERVADOR,
                description = "Foco em segurança e liquidez. Baixa volatilidade.",
                allocations = listOf(
                    InvestmentAllocation("Tesouro Selic / CDB", 90.0, investmentAmount * 0.9),
                    InvestmentAllocation("FIIs (Papel)", 10.0, investmentAmount * 0.1)
                )
            ),
            InvestmentProfile(
                type = InvestmentProfileType.MODERADO,
                description = "Equilíbrio entre segurança e retorno. Volatilidade média.",
                allocations = listOf(
                    InvestmentAllocation("Renda Fixa", 60.0, investmentAmount * 0.6),
                    InvestmentAllocation("Ações (Blue Chips)", 30.0, investmentAmount * 0.3),
                    InvestmentAllocation("FIIs (Tijolo)", 10.0, investmentAmount * 0.1)
                )
            ),
            InvestmentProfile(
                type = InvestmentProfileType.AGRESSIVO,
                description = "Busca maximizar retornos no longo prazo. Alta volatilidade.",
                allocations = listOf(
                    InvestmentAllocation("Renda Fixa (Reserva)", 40.0, investmentAmount * 0.4),
                    InvestmentAllocation("Ações (Crescimento)", 40.0, investmentAmount * 0.4),
                    InvestmentAllocation("FIIs / Imóveis", 15.0, investmentAmount * 0.15),
                    InvestmentAllocation("Cripto / Exterior", 5.0, investmentAmount * 0.05)
                )
            )
        )
    }
}
