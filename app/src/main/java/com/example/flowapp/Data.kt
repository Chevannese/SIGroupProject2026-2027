package com.example.flowapp
import java.io.File
import java.time.LocalDateTime






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