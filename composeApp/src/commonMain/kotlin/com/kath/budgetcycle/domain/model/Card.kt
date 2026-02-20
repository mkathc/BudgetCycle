package com.kath.budgetcycle.domain.model

data class Card(
    val id: String,
    val name: String,
    val cutOffDay: Int,
    val paymentDay: Int,
    val budgetPerCycleCents: Long
)
