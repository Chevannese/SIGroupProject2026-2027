package org.example.flowserver

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,
    val ticketStatus: String = "Waiting",
    val created: LocalDateTime = LocalDateTime.now(),
    val serviceStartTime: LocalDateTime? = null,
    val serviceEndTime: LocalDateTime? = null,
    val serviceTime: Int = 10
)

@Serializable
data class Receipt(
    val receiptNo: String,
    val ticketNo: String,
    val trn: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val cost: Double,
    val surcharge: Double = 0.0,
    val subTotal: Double = 0.0,
    val totalPaid: Double = 0.0
)

@Serializable
data class PaymentCard(
    val trn: String,
    val lastFourDigits: String,
    val balance: Double
)

@Serializable
data class User(val trn: String, val email: String, val phone: String, val name: String, val password: String)