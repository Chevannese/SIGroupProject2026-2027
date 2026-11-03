package com.example.flowapp

data class Ticket(val date: String, val time: String, val branch: String, val waitTime: String, val status: String = "active") {
    fun default(): Ticket {
        return Ticket(
            date = "TODO()",
            time = "TODO()",
            branch = "TODO()",
            waitTime = "TODO()",
            status = "active"
        )
    }
}