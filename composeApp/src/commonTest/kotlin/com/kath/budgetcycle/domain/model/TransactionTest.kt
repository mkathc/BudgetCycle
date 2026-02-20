package com.kath.budgetcycle.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.datetime.LocalDate

class TransactionTest {
    @Test
    fun amountCentsMustBeNonNegative() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = "t-1",
                date = LocalDate(2025, 1, 1),
                amountCents = -1,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE
            )
        }
    }

    @Test
    fun adjustmentRequiresDirection() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = "t-2",
                date = LocalDate(2025, 1, 1),
                amountCents = 100,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.ADJUSTMENT
            )
        }
    }

    @Test
    fun nonAdjustmentCannotReceiveDirection() {
        assertFailsWith<IllegalArgumentException> {
            Transaction(
                id = "t-3",
                date = LocalDate(2025, 1, 1),
                amountCents = 100,
                currency = Transaction.Currency.PEN,
                method = Transaction.Method.CARD,
                cardId = "card-1",
                categoryId = "cat-1",
                type = Transaction.Type.EXPENSE,
                adjustmentDirection = Transaction.AdjustmentDirection.INCREASE
            )
        }
    }

    @Test
    fun signedAmountMatchesTypeAndDirection() {
        val expense = Transaction(
            id = "e",
            date = LocalDate(2025, 1, 1),
            amountCents = 500,
            currency = Transaction.Currency.PEN,
            method = Transaction.Method.CARD,
            cardId = "card-1",
            categoryId = "cat-1",
            type = Transaction.Type.EXPENSE
        )
        val income = Transaction(
            id = "i",
            date = LocalDate(2025, 1, 1),
            amountCents = 500,
            currency = Transaction.Currency.PEN,
            method = Transaction.Method.TRANSFER,
            cardId = null,
            categoryId = "cat-1",
            type = Transaction.Type.INCOME
        )
        val increase = Transaction(
            id = "a1",
            date = LocalDate(2025, 1, 1),
            amountCents = 250,
            currency = Transaction.Currency.PEN,
            method = Transaction.Method.CARD,
            cardId = "card-1",
            categoryId = "cat-1",
            type = Transaction.Type.ADJUSTMENT,
            adjustmentDirection = Transaction.AdjustmentDirection.INCREASE
        )
        val decrease = Transaction(
            id = "a2",
            date = LocalDate(2025, 1, 1),
            amountCents = 250,
            currency = Transaction.Currency.PEN,
            method = Transaction.Method.CARD,
            cardId = "card-1",
            categoryId = "cat-1",
            type = Transaction.Type.ADJUSTMENT,
            adjustmentDirection = Transaction.AdjustmentDirection.DECREASE
        )

        assertEquals(-500, expense.signedAmountCents())
        assertEquals(500, income.signedAmountCents())
        assertEquals(250, increase.signedAmountCents())
        assertEquals(-250, decrease.signedAmountCents())
    }
}
