
package com.example.flowapp

import android.content.Context
import java.io.File
import java.time.LocalDateTime

private const val FILE_NAME = "tickets.txt"

fun getTicketFile(context: Context): File {
    return File(context.filesDir, FILE_NAME)
}

// Convert a ticket into a text record
fun ticketToString(ticket: Ticket): String {
    return listOf(
        ticket.ticketNo,
        ticket.custID,
        ticket.service,
        ticket.branch,
        ticket.paymentMethod,
        ticket.paymentStatus,
        ticket.ticketStatus,
        ticket.cost,
        ticket.totalPaid,
        ticket.created,
        ticket.serviceStartTime ?: "",
        ticket.serviceEndTime ?: "",
        ticket.serviceTime
    ).joinToString("|")
}

// Convert a text record back into a Ticket
fun stringToTicket(line: String): Ticket? {
    return try {
        val data = line.split("|")

        if (data.size != 13) return null

        Ticket(
            ticketNo = data[0],
            custID = data[1],
            service = data[2],
            branch = data[3],
            paymentMethod = data[4],
            paymentStatus = data[5],
            ticketStatus = data[6],
            cost = data[7].toDouble(),
            totalPaid = data[8].toDouble(),
            created = LocalDateTime.parse(data[9]),
            serviceStartTime = data[10].takeIf { it.isNotBlank() }
                ?.let { LocalDateTime.parse(it) },
            serviceEndTime = data[11].takeIf { it.isNotBlank() }
                ?.let { LocalDateTime.parse(it) },
            serviceTime = data[12].toInt()
        )
    } catch (e: Exception) {
        null
    }
}

// Retrieve all tickets
fun getAllTickets(context: Context): List<Ticket> {
    val file = getTicketFile(context)

    if (!file.exists()) return emptyList()

    return file.readLines().mapNotNull {
        stringToTicket(it)
    }
}

// Retrieve tickets belonging to a specific customer
fun getCustomerTickets(
    context: Context,
    trn: String
): List<Ticket> {
    return getAllTickets(context).filter {
        it.custID == trn
    }
}

// Append a ticket to tickets.txt
fun saveTicket(context: Context, ticket: Ticket) {
    val file = getTicketFile(context)

    file.appendText(ticketToString(ticket) + "\n")
}

// Initialize default tickets
fun initializeDefaultTickets(
    context: Context,
    trn: String
) {
    val existingTickets = getAllTickets(context)

    val start = LocalDateTime.now()
    val tenMinutes = 10L

    val defaultTickets = listOf(
        Ticket(
            ticketNo = "TKT001",
            custID = "123456789",
            service = "Phone Service",
            branch = "Portmore",
            paymentMethod = "Cash",
            paymentStatus = "Paid",
            ticketStatus = "Waiting",
            cost = 500.0,
            totalPaid = 500.0,
            created = start
        ),

        Ticket(
            ticketNo = "TKT002",
            custID = "123456789",
            service = "Internet Service",
            branch = "Kingston",
            paymentMethod = "Card",
            paymentStatus = "Partially Paid",
            ticketStatus = "Waiting",
            cost = 1000.0,
            totalPaid = 500.0,
            created = start.plusMinutes(tenMinutes)
        ),

        Ticket(
            ticketNo = "TKT003",
            custID = "123456789",
            service = "Phone Service",
            branch = "Kingston",
            paymentMethod = "Cash",
            paymentStatus = "Unpaid",
            ticketStatus = "Waiting",
            cost = 500.0,
            totalPaid = 0.0,
            created = start.plusMinutes(tenMinutes * 2)
        )
    )

    defaultTickets.forEach { ticket ->
        val alreadyExists = existingTickets.any {
            it.ticketNo == ticket.ticketNo &&
                    it.custID == trn
        }

        if (!alreadyExists) {
            saveTicket(context, ticket)
        }
    }
}
