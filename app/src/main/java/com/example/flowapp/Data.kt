package com.example.flowapp

data class Ticket(val date: String, val time: String, val branch: String, val waitTime: String, val status: String = "active") {
    companion object {
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