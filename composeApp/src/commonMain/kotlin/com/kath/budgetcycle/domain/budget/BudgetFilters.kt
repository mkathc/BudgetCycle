package com.kath.budgetcycle.domain.budget

import com.kath.budgetcycle.domain.model.Category
import com.kath.budgetcycle.domain.model.Transaction
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number

internal object BudgetFilters {
    fun isPen(transaction: Transaction): Boolean =
        transaction.currency == Transaction.Currency.PEN

    fun isMonth(transaction: Transaction, month: YearMonth): Boolean =
        transaction.date.year == month.year && transaction.date.month.number == month.month.number

    fun isVariable(transaction: Transaction, categoriesById: Map<String, Category>): Boolean =
        categoriesById[transaction.categoryId]?.isVariable == true

    fun isPurchase(transaction: Transaction, categoriesById: Map<String, Category>): Boolean =
        categoriesById[transaction.categoryId]?.isPurchase == true
}
