package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.model.Card
import com.kath.budgetcycle.domain.model.Category
import com.kath.budgetcycle.domain.model.Transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class GetDashboardSnapshotTest {

    @Test
    fun snapshotCalculatesCommittedVariablePurchasesAndAvailableEstimate() {
        val today = LocalDate(2025, 5, 15)

        val cards = listOf(
            Card(
                id = "card-1",
                name = "Visa",
                cutOffDay = 10,
                paymentDay = 20,
                budgetPerCycleCents = 3_000
            ),
            Card(
                id = "card-2",
                name = "Mastercard",
                cutOffDay = 20,
                paymentDay = 28,
                budgetPerCycleCents = 4_000
            )
        )

        val categories = listOf(
            Category(id = "cat-var", name = "Variable", isVariable = true, isPurchase = false),
            Category(id = "cat-purchase", name = "Purchase", isVariable = false, isPurchase = true),
            Category(id = "cat-other", name = "Other", isVariable = false, isPurchase = false)
        )

        val transactions = listOf(
            // Card 1 (cutOff=10, today=15 => current cycle is Jun 2025, range May 11 - Jun 10)
            Transaction(
                id = "c1-expense",
                date = LocalDate(2025, 5, 11),
                amountCents = 1_000,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-other",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "c1-adjust-decrease",
                date = LocalDate(2025, 5, 21),
                amountCents = 200,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-other",
                type = Transaction.Type.ADJUSTMENT,
                adjustmentDirection = Transaction.AdjustmentDirection.DECREASE
            ),
            Transaction(
                id = "c1-income-ignored-in-committed",
                date = LocalDate(2025, 5, 20),
                amountCents = 300,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-other",
                type = Transaction.Type.INCOME
            ),
            Transaction(
                id = "c1-usd-ignored",
                date = LocalDate(2025, 5, 12),
                amountCents = 900,
                currency = Transaction.Currency.USD,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-other",
                type = Transaction.Type.EXPENSE
            ),

            // Card 2 (cutOff=20, today=15 => current cycle is May 2025, range Apr 21 - May 20)
            Transaction(
                id = "c2-expense",
                date = LocalDate(2025, 5, 18),
                amountCents = 700,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-2",
                categoryId = "cat-other",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "c2-adjust-increase",
                date = LocalDate(2025, 5, 17),
                amountCents = 100,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-2",
                categoryId = "cat-other",
                type = Transaction.Type.ADJUSTMENT,
                adjustmentDirection = Transaction.AdjustmentDirection.INCREASE
            ),

            // Variable fund (month calendar May 2025) -> include EXPENSE + ADJUSTMENT, exclude INCOME
            Transaction(
                id = "variable-expense",
                date = LocalDate(2025, 5, 5),
                amountCents = 400,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CASH,
                cardId = null,
                categoryId = "cat-var",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "variable-income-ignored-in-used",
                date = LocalDate(2025, 5, 8),
                amountCents = 150,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.TRANSFER,
                cardId = null,
                categoryId = "cat-var",
                type = Transaction.Type.INCOME
            ),
            Transaction(
                id = "variable-adjust-increase",
                date = LocalDate(2025, 5, 9),
                amountCents = 50,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.TRANSFER,
                cardId = null,
                categoryId = "cat-var",
                type = Transaction.Type.ADJUSTMENT,
                adjustmentDirection = Transaction.AdjustmentDirection.INCREASE
            ),

            // Purchases (month calendar May 2025) -> include EXPENSE + ADJUSTMENT, exclude INCOME
            Transaction(
                id = "purchase-expense",
                date = LocalDate(2025, 5, 6),
                amountCents = 300,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CASH,
                cardId = null,
                categoryId = "cat-purchase",
                type = Transaction.Type.EXPENSE
            ),
            Transaction(
                id = "purchase-adjust-decrease",
                date = LocalDate(2025, 5, 7),
                amountCents = 40,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.TRANSFER,
                cardId = null,
                categoryId = "cat-purchase",
                type = Transaction.Type.ADJUSTMENT,
                adjustmentDirection = Transaction.AdjustmentDirection.DECREASE
            ),
            Transaction(
                id = "purchase-income-ignored-in-used",
                date = LocalDate(2025, 5, 10),
                amountCents = 70,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-2",
                categoryId = "cat-purchase",
                type = Transaction.Type.INCOME
            ),

            // Cash/transfer expenses used in available estimate (EXPENSE only)
            Transaction(
                id = "cash-transfer-expense",
                date = LocalDate(2025, 5, 11),
                amountCents = 250,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.TRANSFER,
                cardId = null,
                categoryId = "cat-other",
                type = Transaction.Type.EXPENSE
            ),

            // Next month should be ignored for calendar-month calculations
            Transaction(
                id = "next-month-ignored",
                date = LocalDate(2025, 6, 1),
                amountCents = 999,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CASH,
                cardId = null,
                categoryId = "cat-var",
                type = Transaction.Type.EXPENSE
            )
        )

        val snapshot = GetDashboardSnapshot().invoke(
            today = today,
            monthlyIncomeCents = 5_000,
            variableFundLimitCents = 1_000,
            purchasesSubLimitCents = 800,
            cards = cards,
            categories = categories,
            transactions = transactions
        )

        // committed totals (card cycles)
        assertEquals(2, snapshot.cardSummaries.size)
        assertEquals(800, snapshot.cardSummaries.first { it.cardId == "card-1" }.committedNextPaymentCents)
        assertEquals(800, snapshot.cardSummaries.first { it.cardId == "card-2" }.committedNextPaymentCents)
        assertEquals(1_600, snapshot.committedNextPaymentTotalCents)

        // variable/purchases "used" (calendar month) -> consumption only (no income)
        // variable: 400 EXPENSE + 50 ADJ INCREASE = 450
        assertEquals(450, snapshot.variableFundProgress.usedCents)
        assertEquals(1_000, snapshot.variableFundProgress.limitCents)

        // purchases: 300 EXPENSE + (-40 ADJ DECREASE) = 260
        assertEquals(260, snapshot.purchasesProgress.usedCents)
        assertEquals(800, snapshot.purchasesProgress.limitCents)

        // available estimate:
        // cash+transfer EXPENSE in May: 400 (variable-expense CASH) + 300 (purchase-expense CASH) + 250 (cash-transfer-expense TRANSFER) = 950
        // available = 5000 - 950 - 450 - 1600 = 2000
        assertEquals(2_000, snapshot.availableEstimateCents)
    }
}
