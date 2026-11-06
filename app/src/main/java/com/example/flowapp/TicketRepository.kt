package com.example.flowapp


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TicketRepository {
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets

    fun addTicket(ticket: Ticket) {
        _tickets.value = _tickets.value + ticket
    }

    fun updateTicket(updated: Ticket) {
        _tickets.value = _tickets.value.map {
            if (it.ticketNo == updated.ticketNo) updated else it
        }
    }

    fun asMap(): Map<String, Ticket> = _tickets.value.associateBy { it.ticketNo }
}

fun defaultTickets(): List<Ticket> {
    val branch = "Half Way Tree"

    return listOf(
        Ticket(
            ticketNo = "T001",
            custID = "123456789",
            service = "Bill Payment",
            branch = branch,
            queueNumber = 1,
            paymentMethod = "Cash",
            paymentStatus = PaymentStatus.PENDING,
            ticketStatus = TicketStatus.WAITING,
            cost = 5000.0,
            totalPaid = 0.0
        ),
        Ticket(
            ticketNo = "T002",
            custID = "987654321",
            service = "Device Repair",
            branch = branch,
            queueNumber = 2,
            paymentMethod = "Card",
            paymentStatus = PaymentStatus.PARTIALLY_PAID,
            ticketStatus = TicketStatus.WAITING,
            cost = 8000.0,
            totalPaid = 3000.0
        )
    )
}
