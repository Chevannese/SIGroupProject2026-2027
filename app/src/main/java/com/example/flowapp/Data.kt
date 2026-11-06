package com.example.flowapp
import java.io.File
import java.time.LocalDateTime


enum class PaymentStatus {
    PENDING,
    PAID,
    PARTIALLY_PAID
}

enum class TicketStatus {
    WAITING,
    IN_SERVICE,
    COMPLETED,
    CANCELLED
}
data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,
    val queueNumber: Int,

    var paymentMethod: String,
    var paymentStatus: PaymentStatus,

    var ticketStatus: TicketStatus,

    var cost: Double,
    var totalPaid: Double,

    val createdAt: LocalDateTime = LocalDateTime.now()
)


data class QueueEntry(
    val queueNumber: Int,
    val ticket: Ticket,

    val joinedAt: LocalDateTime = LocalDateTime.now(),

    var serviceStartTime: LocalDateTime? = null,
    var serviceEndTime: LocalDateTime? = null,

    var status: QueueStatus = QueueStatus.WAITING
)

enum class QueueStatus {
    WAITING,
    SERVING,
    COMPLETED
}






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