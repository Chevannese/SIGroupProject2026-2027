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