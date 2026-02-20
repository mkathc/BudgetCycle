package com.kath.budgetcycle.domain.usecase

import com.kath.budgetcycle.domain.budget.BudgetProgress
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

data class CardCycleSummary(
    val cardId: String,
    val cycle: YearMonth,
    val cycleStart: LocalDate,
    val cycleEnd: LocalDate,
    val budgetPerCycleCents: Long,
    val committedNextPaymentCents: Long,
    val remainingBudgetCents: Long,
    val progress: BudgetProgress
)

data class VariableFundSummary(
    val month: YearMonth,
    val limitCents: Long,
    val usedCents: Long,
    val availableCents: Long
)

data class PurchasesSummary(
    val month: YearMonth,
    val subLimitCents: Long,
    val usedCents: Long,
    val availableCents: Long
)
