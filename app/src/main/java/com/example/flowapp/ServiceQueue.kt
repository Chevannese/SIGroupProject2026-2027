package com.example.flowapp
import java.util.Date
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
class ServiceQueue(
    private val queueFile: String
) {

    private val waitingQueue = ArrayDeque<QueueEntry>()

    fun addCustomer(entry: QueueEntry) {
        waitingQueue.addLast(entry)
        saveQueue()
    }

    fun serveNextCustomer(): QueueEntry? {

        if (waitingQueue.isEmpty()) return null

        val customer = waitingQueue.removeFirst()

        customer.status = QueueStatus.SERVING
        customer.serviceStartTime = LocalDateTime.now()
        customer.serviceEndTime = customer.serviceStartTime!!.plusMinutes(10)

        saveQueue()

        return customer
    }

    private fun saveQueue() {

        File(queueFile).printWriter().use { out ->

            waitingQueue.forEach { entry ->

                out.println(
                    listOf(
                        entry.queueNumber,
                        entry.ticket.ticketNo,
                        entry.status,
                        entry.joinedAt
                    ).joinToString(",")
                )
            }
        }
    }

    fun loadQueue(ticketMap: Map<String, Ticket>) {

        val file = File(queueFile)

        if (!file.exists()) return

        file.forEachLine { line ->

            val parts = line.split(",")

            val queueNumber = parts[0].toInt()
            val ticketNo = parts[1]

            val ticket = ticketMap[ticketNo]

            if (ticket != null) {

                waitingQueue.addLast(
                    QueueEntry(
                        queueNumber = queueNumber,
                        ticket = ticket
                    )
                )
            }
        }


        fun checkCurrentCustomer() {

           // val customer = currentCustomer ?: return

          //  if (LocalDateTime.now().isAfter(customer.serviceEndTime)) {

            //    customer.status = QueueStatus.COMPLETED
            //    customer.ticket.ticketStatus = TicketStatus.COMPLETED

            //    currentCustomer = null

                saveQueue()

                serveNextCustomer()
            }
        }

    }





