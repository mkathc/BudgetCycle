package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.budget.BudgetProgress
import com.kath.budgetcycle.domain.budget.from
import com.kath.budgetcycle.domain.model.Card
import com.kath.budgetcycle.domain.model.Category
import com.kath.budgetcycle.domain.model.DashboardSnapshot
import com.kath.budgetcycle.domain.model.Transaction
import com.kath.budgetcycle.domain.model.spendContributionCents
import kotlinx.datetime.LocalDate

class GetDashboardSnapshot(
    private val cardCycleSummary: CalculateCardCycleSummary = CalculateCardCycleSummary(),
    private val committed: CalculateCommittedNextPayment = CalculateCommittedNextPayment()
) {
    fun invoke(
        today: LocalDate,
        monthlyIncomeCents: Long,
        variableFundLimitCents: Long,
        purchasesSubLimitCents: Long,
        cards: List<Card>,
        categories: List<Category>,
        transactions: List<Transaction>
    ): DashboardSnapshot {
        val categoriesById = categories.associateBy { it.id }
        val calendarMonthPenTransactions = transactions
            .asSequence()
            .filter { it.currency == Transaction.Currency.PEN }
            .filter { it.date.year == today.year && it.date.monthNumber == today.monthNumber }
            .toList()
        val monthSpending = calendarMonthPenTransactions
            .asSequence()
            .filter { it.type != Transaction.Type.INCOME }
        val variableFundUsedCents = monthSpending
            .filter { categoriesById[it.categoryId]?.isVariable == true }
            .sumOf { it.spendContributionCents() }
        val purchasesUsedCents = monthSpending
            .filter { categoriesById[it.categoryId]?.isPurchase == true }
            .sumOf { it.spendContributionCents() }
        val cashAndTransferExpenseCents = calendarMonthPenTransactions
            .asSequence()
            .filter { it.type == Transaction.Type.EXPENSE }
            .filter { it.method == Transaction.Method.CASH || it.method == Transaction.Method.TRANSFER }
            .sumOf { it.amountCents }

        val committedNextPaymentTotalCents = committed(
            cards = cards,
            transactions = transactions,
            today = today
        )

        val availableEstimateCents =
            monthlyIncomeCents -
                cashAndTransferExpenseCents -
                variableFundUsedCents -
                committedNextPaymentTotalCents

        return DashboardSnapshot(
            today = today,
            cardSummaries = cards.map { card ->
                cardCycleSummary(
                    card = card,
                    transactions = transactions,
                    today = today
                )
            },
            variableFundProgress = BudgetProgress.from(
                usedCents = variableFundUsedCents,
                limitCents = variableFundLimitCents
            ),
            purchasesProgress = BudgetProgress.from(
                usedCents = purchasesUsedCents,
                limitCents = purchasesSubLimitCents
            ),
            committedNextPaymentTotalCents = committedNextPaymentTotalCents,
            availableEstimateCents = availableEstimateCents
        )
    }
}
