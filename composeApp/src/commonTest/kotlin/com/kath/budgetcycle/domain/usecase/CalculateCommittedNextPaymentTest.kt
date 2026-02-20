package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.model.Card
import com.kath.budgetcycle.domain.model.Transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

class CalculateCommittedNextPaymentTest {
    @Test
    fun committedNextPaymentUsesOnlyPenInCurrentCardCycle() {
        val card = Card(
            id = "card-1",
            name = "Visa",
            cutOffDay = 10,
            paymentDay = 20,
            budgetPerCycleCents = 100_00
        )
        val transactions = listOf(
            Transaction(
                id = "t1",
                date = LocalDate(2025, 5, 11),
                amountCents = 1_000,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "t2",
                date = LocalDate(2025, 5, 10),
                amountCents = 200,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "t3",
                date = LocalDate(2025, 5, 15),
                amountCents = 100,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.ADJUSTMENT,
                adjustmentDirection = Transaction.AdjustmentDirection.DECREASE
            ),
            Transaction(
                id = "t4",
                date = LocalDate(2025, 5, 12),
                amountCents = 5_000,
                currency = Transaction.Currency.USD,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "t5",
                date = LocalDate(2025, 5, 12),
                amountCents = 600,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CASH,
                cardId = null,
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            )
        )

        val committed = CalculateCommittedNextPayment().invoke(
            cards = listOf(card),
            transactions = transactions,
            today = LocalDate(2025, 5, 11)
        )

        assertEquals(900, committed)
    }

    @Test
    fun cardCycleSummaryIncludesAvailableEstimated() {
        val card = Card(
            id = "card-1",
            name = "Visa",
            cutOffDay = 10,
            paymentDay = 20,
            budgetPerCycleCents = 2_000
        )
        val transactions = listOf(
            Transaction(
                id = "t1",
                date = LocalDate(2025, 5, 11),
                amountCents = 900,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            )
        )

        val summary = CalculateCardCycleSummary().invoke(
            card = card,
            transactions = transactions,
            today = LocalDate(2025, 5, 12)
        )

        assertEquals(YearMonth(2025, 6), summary.cycle)
        assertEquals(900, summary.committedNextPaymentCents)
        assertEquals(1_100, summary.remainingBudgetCents)
        assertEquals(900, summary.progress.usedCents)
        assertEquals(2_000, summary.progress.limitCents)
        assertEquals(1_100, summary.progress.remainingCents)
    }
}
