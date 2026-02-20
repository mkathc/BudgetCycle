package com.kath.budgetcycle.domain.billing

import kotlin.math.min
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.plus

class BillingCycleCalculator {
    fun billingCycleKey(date: LocalDate, cutOffDay: Int): YearMonth {
        validateCutOffDay(cutOffDay)
        val current = YearMonth(date.year, date.monthNumber)
        return if (date.dayOfMonth <= cutOffDay) {
            current
        } else {
            current.plusMonths(1)
        }
    }

    fun billingCycleRange(cycle: YearMonth, cutOffDay: Int): Pair<LocalDate, LocalDate> {
        validateCutOffDay(cutOffDay)

        val cycleFirstDay = LocalDate(cycle.year, cycle.month.number, 1)
        val previousCycleFirstDay = cycleFirstDay.plus(-1, DateTimeUnit.MONTH)

        val currentMonthDays = daysInMonth(cycleFirstDay)
        val previousMonthDays = daysInMonth(previousCycleFirstDay)

        val endDay = min(cutOffDay, currentMonthDays)
        val end = LocalDate(cycle.year, cycle.month.number, endDay)

        val start = if (cutOffDay < previousMonthDays) {
            LocalDate(previousCycleFirstDay.year, previousCycleFirstDay.monthNumber, cutOffDay + 1)
        } else {
            cycleFirstDay
        }

        return start to end
    }

    private fun YearMonth.plusMonths(months: Int): YearMonth {
        val firstDay = LocalDate(year, month.number, 1).plus(months, DateTimeUnit.MONTH)
        return YearMonth(firstDay.year, firstDay.monthNumber)
    }

    private fun daysInMonth(firstDayOfMonth: LocalDate): Int =
        firstDayOfMonth
            .plus(1, DateTimeUnit.MONTH)
            .plus(-1, DateTimeUnit.DAY)
            .dayOfMonth

    private fun validateCutOffDay(cutOffDay: Int) {
        require(cutOffDay in 1..31) { "cutOffDay must be between 1 and 31" }
    }
}
