package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.budget.BudgetFilters
import com.kath.budgetcycle.domain.model.Category
import com.kath.budgetcycle.domain.model.Transaction
import kotlinx.datetime.YearMonth

class CalculateVariableFundSummary(
    categories: List<Category> = emptyList()
) {
    private val categoriesById = categories.associateBy { it.id }

    operator fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        limitCents: Long
    ): VariableFundSummary = invoke(transactions, month, limitCents, categoriesById)

    operator fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        limitCents: Long,
        categories: List<Category>
    ): VariableFundSummary = invoke(transactions, month, limitCents, categories.associateBy { it.id })

    private fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        limitCents: Long,
        categoriesById: Map<String, Category>
    ): VariableFundSummary {
        val used = transactions
            .asSequence()
            .filter(BudgetFilters::isPen)
            .filter { BudgetFilters.isMonth(it, month) }
            .filter { it.type == Transaction.Type.EXPENSE }
            .filter { BudgetFilters.isVariable(it, categoriesById) }
            .sumOf { it.amountCents }

        return VariableFundSummary(
            month = month,
            limitCents = limitCents,
            usedCents = used,
            availableCents = limitCents - used
        )
    }
}
