package com.example.flowapp



import java.util.Date

data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,
    val queueNumber: Int,
    val paymentMethod: String,
    val paymentStatus: String,
    val ticketStatus: String,
    val cost: Double,
    val totalPaid: Double,
    val created: Date
)