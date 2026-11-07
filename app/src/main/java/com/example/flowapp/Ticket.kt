package com.example.flowapp


import java.time.LocalDateTime

data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,

    val paymentMethod: String,
    val paymentStatus: String,
    val ticketStatus: String,

    val cost: Double,
    val totalPaid: Double,

    val created: LocalDateTime = LocalDateTime.now(),
    val serviceStartTime: LocalDateTime? = null,
    val serviceEndTime: LocalDateTime? = null,
    val serviceTime: Int = 0
)


