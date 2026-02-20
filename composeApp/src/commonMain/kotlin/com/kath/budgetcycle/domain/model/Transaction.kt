package com.kath.budgetcycle.domain.model

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: String,
    val date: LocalDate,
    val amountCents: Long,
    val currency: Currency,
    val method: Method,
    val cardId: String?,
    val categoryId: String,
    val type: Type,
    val adjustmentDirection: AdjustmentDirection? = null
) {
    init {
        require(amountCents >= 0) { "amountCents must be >= 0" }
        if (type == Type.ADJUSTMENT) {
            require(adjustmentDirection != null) {
                "adjustmentDirection is required for ADJUSTMENT transactions"
            }
        } else {
            require(adjustmentDirection == null) {
                "adjustmentDirection must be null for non-ADJUSTMENT transactions"
            }
        }
    }

    enum class Currency {
        PEN,
        USD
    }

    enum class Method {
        CARD,
        CASH,
        TRANSFER
    }

    enum class Type {
        EXPENSE,
        INCOME,
        ADJUSTMENT
    }

    enum class AdjustmentDirection {
        INCREASE,
        DECREASE
    }
}
