package com.kath.budgetcycle.domain.model

fun Transaction.signedAmountCents(): Long =
    when (type) {
        Transaction.Type.EXPENSE -> -amountCents
        Transaction.Type.INCOME -> amountCents
        Transaction.Type.ADJUSTMENT -> when (adjustmentDirection!!) {
            Transaction.AdjustmentDirection.INCREASE -> amountCents
            Transaction.AdjustmentDirection.DECREASE -> -amountCents
        }
    }

fun Transaction.spendContributionCents(): Long = when (type) {
    Transaction.Type.EXPENSE -> amountCents
    Transaction.Type.ADJUSTMENT -> when (adjustmentDirection) {
        Transaction.AdjustmentDirection.INCREASE -> amountCents
        Transaction.AdjustmentDirection.DECREASE -> -amountCents
        null -> error("ADJUSTMENT requires adjustmentDirection")
    }
    Transaction.Type.INCOME -> 0L
}