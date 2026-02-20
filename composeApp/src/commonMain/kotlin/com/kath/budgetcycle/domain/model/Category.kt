package com.kath.budgetcycle.domain.model

data class Category(
    val id: String,
    val name: String,
    val isVariable: Boolean,
    val isPurchase: Boolean
)
