package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.billing.BillingCycleCalculator
import com.kath.budgetcycle.domain.budget.BudgetProgress
import com.kath.budgetcycle.domain.budget.BudgetFilters
import com.kath.budgetcycle.domain.budget.from
import com.kath.budgetcycle.domain.model.Card
import com.kath.budgetcycle.domain.model.Transaction
import com.kath.budgetcycle.domain.model.signedAmountCents
import kotlinx.datetime.LocalDate

class CalculateCardCycleSummary(
    private val billingCycleCalculator: BillingCycleCalculator = BillingCycleCalculator()
) {
    operator fun invoke(
        card: Card,
        transactions: List<Transaction>,
        today: LocalDate
    ): CardCycleSummary {
        val cycle = billingCycleCalculator.billingCycleKey(today, card.cutOffDay)
        val cycleRange = billingCycleCalculator.billingCycleRange(cycle, card.cutOffDay)

        val committed = transactions
            .asSequence()
            .filter(BudgetFilters::isPen)
            .filter { it.cardId == card.id && it.method == Transaction.Method.CARD }
            .filter { billingCycleCalculator.billingCycleKey(it.date, card.cutOffDay) == cycle }
            .sumOf(::committedContribution)

        return CardCycleSummary(
            cardId = card.id,
            cycle = cycle,
            cycleStart = cycleRange.first,
            cycleEnd = cycleRange.second,
            budgetPerCycleCents = card.budgetPerCycleCents,
            committedNextPaymentCents = committed,
            remainingBudgetCents = card.budgetPerCycleCents - committed,
            progress = BudgetProgress.from(
                usedCents = committed,
                limitCents = card.budgetPerCycleCents
            )
        )
    }

    private fun committedContribution(transaction: Transaction): Long =
        when (transaction.type) {
            Transaction.Type.EXPENSE -> transaction.amountCents
            Transaction.Type.ADJUSTMENT -> transaction.signedAmountCents()
            Transaction.Type.INCOME -> 0L
        }
}
