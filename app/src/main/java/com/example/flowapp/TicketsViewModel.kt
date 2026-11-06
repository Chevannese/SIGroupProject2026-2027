package com.example.flowapp


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class TicketsViewModel : ViewModel() {
    val tickets: StateFlow<List<Ticket>> = TicketRepository.tickets
}