package com.kath.budgetcycle.domain.model

import com.kath.budgetcycle.domain.budget.BudgetProgress
import com.kath.budgetcycle.domain.usecase.CardCycleSummary
import kotlinx.datetime.LocalDate

data class DashboardSnapshot(
    val today: LocalDate,
    val cardSummaries: List<CardCycleSummary>,
    val variableFundProgress: BudgetProgress,
    val purchasesProgress: BudgetProgress,
    val committedNextPaymentTotalCents: Long,
    val availableEstimateCents: Long
)
