package com.example.flowapp

data class Ticket(val date: String, val time: String, val branch: String, val waitTime: String, val status: String = "active") {

}