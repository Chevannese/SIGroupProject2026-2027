package com.example.flowapp

import android.content.Context
import java.io.File
import java.util.Date

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
        ticket.queueNumber,
        ticket.paymentMethod,
        ticket.paymentStatus,
        ticket.ticketStatus,
        ticket.cost,
        ticket.totalPaid,
        ticket.created
    ).joinToString("|")
}

// Convert a text record back into a Ticket
fun stringToTicket(line: String): Ticket? {
    return try {
        val data = line.split("|")

        if (data.size != 11) return null

        Ticket(
            ticketNo = data[0],
            custID = data[1],
            service = data[2],
            branch = data[3],
            queueNumber = data[4].toInt(),
            paymentMethod = data[5],
            paymentStatus = data[6],
            ticketStatus = data[7],
            cost = data[8].toDouble(),
            totalPaid = data[9].toDouble(),
            created = Date(data[10].toLong())
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

fun initializeDefaultTickets(
    context: Context,
    trn: String
) {
    val existingTickets = getAllTickets(context)

    val start = System.currentTimeMillis()
    val tenMinutes = 10 * 60 * 1000L

    val defaultTickets = listOf(
        Ticket(
            "TKT001", trn, "Phone Service", "Portmore",
            1, "Cash", "Paid", "Waiting",
            500.0, 500.0, Date(start)
        ),
        Ticket(
            "TKT002", trn, "Internet Service", "Kingston",
            2, "Card", "Partially Paid", "Waiting",
            1000.0, 500.0, Date(start + tenMinutes)
        ),
        Ticket(
            "TKT003", trn, "Phone Service", "Kingston",
            3, "Cash", "Unpaid", "Waiting",
            500.0, 0.0, Date(start + 2 * tenMinutes)
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