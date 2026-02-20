package com.kath.budgetcycle.domain.budget

enum class BudgetStatus {
    GREEN,
    YELLOW,
    RED
}

fun budgetStatus(usedCents: Long, limitCents: Long): BudgetStatus {
    if (limitCents <= 0) return BudgetStatus.RED
    val ratio = (usedCents.toDouble() / limitCents.toDouble()).coerceIn(0.0, 1.0)
    return when {
        ratio < 0.60 -> BudgetStatus.GREEN
        ratio < 0.85 -> BudgetStatus.YELLOW
        else -> BudgetStatus.RED
    }
}

data class BudgetProgress(
    val usedCents: Long,
    val limitCents: Long,
    val remainingCents: Long,
    val usedPercent: Double, // 0..1
    val status: BudgetStatus
) {
    companion object
}

fun BudgetProgress.Companion.from(usedCents: Long, limitCents: Long): BudgetProgress {
    val usedPercent = if (limitCents > 0) {
        (usedCents.toDouble() / limitCents.toDouble()).coerceIn(0.0, 1.0)
    } else {
        1.0
    }

    return BudgetProgress(
        usedCents = usedCents,
        limitCents = limitCents,
        remainingCents = limitCents - usedCents,
        usedPercent = usedPercent,
        status = budgetStatus(usedCents, limitCents)
    )
}