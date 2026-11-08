package com.example.flowapp
import java.time.LocalDateTime

data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val serviceStartTime: LocalDateTime? = null,
    val serviceEndTime: LocalDateTime? = null,
    val serviceTime: Int = 10
)

data class Receipt(
    val receiptNo: String,
    val ticketNo: String,
    val trn: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val ticketStatus: String,
    val cost: Double,
    val totalPaid: Double,
)



data class User(val trn: String, val email: String, val phone: String, val name: String, val password: String) {
    companion object {
        fun default(): User {
            return User(
                trn = TODO(),
                email = TODO(),
                phone = TODO(),
                name = TODO(),
                password = TODO()
            )
        }
    }
}