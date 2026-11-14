package org.example.flowserver

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import kotlinx.serialization.encodeToString
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.time.Instant
import java.util.concurrent.Executors

import java.security.MessageDigest
import java.time.LocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

import kotlinx.serialization.json.Json

const val PORT = 9000
const val IDLE_TIMEOUT_MS = 60_000
const val MAX_CLIENTS = 64

fun main() {
    println("Preparing to start server")

    run_server()
}

fun run_server() {
    val pool = Executors.newFixedThreadPool(MAX_CLIENTS)
    val server = ServerSocket(PORT)

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down...")
        server.close()
        pool.shutdown()
    })

    println("Server listening on port $PORT")

    while (!server.isClosed) {
        try {
            val client = server.accept()
            pool.submit { handle_client(client) }
        }
        catch (e: SocketException) {
            break // server socket closed
        }
    }
}

fun handle_client(socket: Socket) {
    val remote = socket.remoteSocketAddress
    println("Connected: $remote")

    try {
        socket.soTimeout = IDLE_TIMEOUT_MS
        val reader = socket.getInputStream().bufferedReader()
        val writer = PrintWriter(socket.getOutputStream(), true)

        while (true) {
            val line = reader.readLine() ?: break // client closed connection
            val parts = line.split(",")

            when (parts[0]) {
                "PING" -> writer.println("PONG")
                "USER" -> { writer.println(Json.encodeToString(Client().user(parts[1]))) }
                "CHANGEPASSWORD" -> { Client().changePassword(parts[1], parts[2], parts[3], parts[4]) }
                "LOGIN" -> { writer.println(Client().login(parts[1], parts[2])) }
                "REGISTER" -> { writer.println(Client().register(parts[1], parts[2], parts[3],parts[4], parts[5], parts[6])) }
                "CHECKEMAIL" -> { writer.println(Client().checkEmailAddress(parts[1])) }
                "WRITEVERIFYCODE" -> writer.println(Client().writeVerifyCodeToFile(parts[1], parts[2]))
                "UPDATEPASSWORD" -> writer.println(Client().updatePassword(parts[1], parts[2], parts[3], parts[4]))
                "GETCUSTOMERRECEIPTS" -> writer.println(Json.encodeToString(Client().getCustomerReceipts(parts[1])))
                "CREATETICKET" -> writer.println(Json.encodeToString(Client().createTicket(parts[1], parts[2], parts[3])))
                "UPDATEQUEUE" -> writer.println(Client().updateQueue(parts[1]))
                "GETALLCARDS" -> writer.println(Json.encodeToString(Client().getAllCards()))
                "MAKEPAYMENT" -> writer.println(Client().makePayment(parts[1], parts[2].toDouble(), parts[3]))
                "GETALLTICKETS" -> writer.println(Json.encodeToString(Client().getAllTickets()))
                "GETTICKET" -> writer.println(Json.encodeToString(Client().getTicket(parts[1])))
                "GETCUSTOMERTICKETS" -> writer.println(Json.encodeToString(Client().getCustomerTickets(parts[1])))
                "HASACTIVETICKET" -> writer.println(Client().hasActiveTicket(parts[1]))
                "CANCELTICKET" -> writer.println(Client().cancelTicket(parts[1]))
                "ADDCARD" -> writer.println(Client().add_card(parts[1], parts[2]))
                "CARDS" -> writer.println(Client().cards(parts[1]))
                "MARKNOSHOW" -> writer.println(Client().markNoShow(parts[1]))
                "REMOVECARD" -> writer.println(Client().removeCard(parts[1]))
                "CHANGEPHONE" -> writer.println(Client().changePhone(parts[1], parts[2]))
                "GETQUEUESNAPSHOT" -> writer.println(Json.encodeToString(Client().getQueueSnapshot(parts[1])))
                else -> "ERR unknown request: ${parts[0]}"
            }
        }

        println("Closing connection to $remote")
        socket.close()
        println("Connection to $remote closed")
    }
    catch (e: SocketTimeoutException) {
        println("Timed out: $remote")
    }
    catch (e: IndexOutOfBoundsException) {
        println("Invalid request")
    }
    catch (e: IOException) {
        println("Connection error ($remote): ${e.message}")
    }
    catch (e: Exception) {
        println("Some error: ${e.message}")
    }

    println("Disconnected: $remote")
}

fun add_feedback(trn: String, feedback: String, rating: Int): Boolean {
    try {
        val file = File("feedback_$trn.txt")
        file.appendText("date: ${Instant.now()}\nrating: $rating\ntext: $feedback\n\n")
        return true
    }
    catch (e: Exception) {
        println(e.message)
        return false
    }
}

