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

    }
            serviceStartTime = null,
            serviceEndTime = null,
            serviceTime = 10
        )

            receiptNo = receiptNo,
}