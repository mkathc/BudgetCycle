package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.billing.BillingCycleCalculator
import com.kath.budgetcycle.domain.budget.BudgetFilters
import com.kath.budgetcycle.domain.model.Card
import com.kath.budgetcycle.domain.model.Transaction
import com.kath.budgetcycle.domain.model.signedAmountCents
import kotlinx.datetime.LocalDate

class CalculateCommittedNextPayment(
    private val billingCycleCalculator: BillingCycleCalculator = BillingCycleCalculator()
) {
    operator fun invoke(
        cards: List<Card>,
        transactions: List<Transaction>,
        today: LocalDate
    ): Long {
        if (cards.isEmpty() || transactions.isEmpty()) return 0L

        return cards.sumOf { card ->
            val cycle = billingCycleCalculator.billingCycleKey(today, card.cutOffDay)
            transactions
                .asSequence()
                .filter(BudgetFilters::isPen)
                .filter { it.cardId == card.id && it.method == Transaction.Method.CARD }
                .filter { billingCycleCalculator.billingCycleKey(it.date, card.cutOffDay) == cycle }
                .sumOf(::committedContribution)
        }
    }

    private fun committedContribution(transaction: Transaction): Long =
        when (transaction.type) {
            Transaction.Type.EXPENSE -> transaction.amountCents
            Transaction.Type.ADJUSTMENT -> transaction.signedAmountCents()
            Transaction.Type.INCOME -> 0L
        }
}
