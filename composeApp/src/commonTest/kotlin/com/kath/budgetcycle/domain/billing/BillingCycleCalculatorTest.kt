package com.kath.budgetcycle.domain.billing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

class BillingCycleCalculatorTest {
    private val calculator = BillingCycleCalculator()

    @Test
    fun dayEqualToCutOffStaysInSameMonth() {
        val cycle = calculator.billingCycleKey(
            date = LocalDate(2025, 6, 15),
            cutOffDay = 15
        )

        assertEquals(YearMonth(2025, 6), cycle)
    }

    @Test
    fun dayAfterCutOffMovesToNextMonth() {
        val cycle = calculator.billingCycleKey(
            date = LocalDate(2025, 6, 16),
            cutOffDay = 15
        )

        assertEquals(YearMonth(2025, 7), cycle)
    }

    @Test
    fun yearChangeIsHandled() {
        val cycle = calculator.billingCycleKey(
            date = LocalDate(2025, 12, 31),
            cutOffDay = 20
        )

        assertEquals(YearMonth(2026, 1), cycle)
    }

    @Test
    fun februaryEdgeDaysAreHandled() {
        val leap28 = calculator.billingCycleKey(
            date = LocalDate(2024, 2, 28),
            cutOffDay = 28
        )
        val leap29 = calculator.billingCycleKey(
            date = LocalDate(2024, 2, 29),
            cutOffDay = 28
        )
        val nonLeapRange = calculator.billingCycleRange(
            cycle = YearMonth(2025, 3),
            cutOffDay = 29
        )

        assertEquals(YearMonth(2024, 2), leap28)
        assertEquals(YearMonth(2024, 3), leap29)
        assertEquals(LocalDate(2025, 3, 1), nonLeapRange.first)
        assertEquals(LocalDate(2025, 3, 29), nonLeapRange.second)
    }
}
