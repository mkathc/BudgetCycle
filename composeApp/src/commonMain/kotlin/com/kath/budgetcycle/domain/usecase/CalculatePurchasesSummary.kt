package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.budget.BudgetFilters
import com.kath.budgetcycle.domain.model.Category
import com.kath.budgetcycle.domain.model.Transaction
import kotlinx.datetime.YearMonth

class CalculatePurchasesSummary(
    categories: List<Category> = emptyList()
) {
    private val categoriesById = categories.associateBy { it.id }

    operator fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        subLimitCents: Long
    ): PurchasesSummary = invoke(transactions, month, subLimitCents, categoriesById)

    operator fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        subLimitCents: Long,
        categories: List<Category>
    ): PurchasesSummary = invoke(transactions, month, subLimitCents, categories.associateBy { it.id })

    private fun invoke(
        transactions: List<Transaction>,
        month: YearMonth,
        subLimitCents: Long,
        categoriesById: Map<String, Category>
    ): PurchasesSummary {
        val used = transactions
            .asSequence()
            .filter(BudgetFilters::isPen)
            .filter { BudgetFilters.isMonth(it, month) }
            .filter { it.type == Transaction.Type.EXPENSE }
            .filter { BudgetFilters.isPurchase(it, categoriesById) }
            .sumOf { it.amountCents }

        return PurchasesSummary(
            month = month,
            subLimitCents = subLimitCents,
            usedCents = used,
            availableCents = subLimitCents - used
        )
    }
}
