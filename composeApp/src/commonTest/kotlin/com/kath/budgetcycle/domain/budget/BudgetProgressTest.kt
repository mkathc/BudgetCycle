package com.kath.budgetcycle.domain.budget

import kotlin.test.Test
import kotlin.test.assertEquals

class BudgetProgressTest {
    @Test
    fun statusIsGreenBelowSixtyPercent() {
        assertEquals(BudgetStatus.GREEN, budgetStatus(590, 1_000))
    }

    @Test
    fun statusIsYellowAtSixtyPercent() {
        assertEquals(BudgetStatus.YELLOW, budgetStatus(600, 1_000))
    }

    @Test
    fun statusIsRedAtExactEightyFivePercent() {
        assertEquals(BudgetStatus.RED, budgetStatus(850, 1_000))
    }

    @Test
    fun statusIsRedWhenLimitIsZero() {
        assertEquals(BudgetStatus.RED, budgetStatus(0, 0))
    }

    @Test
    fun progressFromComputesPercentAndRemaining() {
        val progress = BudgetProgress.from(usedCents = 600, limitCents = 1_000)

        assertEquals(400, progress.remainingCents)
        assertEquals(0.6, progress.usedPercent)
        assertEquals(BudgetStatus.YELLOW, progress.status)
    }
}
