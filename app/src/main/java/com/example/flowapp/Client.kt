package com.example.flowapp

import android.content.Context

import androidx.core.text.isDigitsOnly
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import android.util.Log

import java.security.MessageDigest


// time stuff
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.time.Clock
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

data class QueueSnapshot(
    val customerTicket: Ticket?,
    val nowServing: Ticket?,
    val nextInLine: Ticket?,
    val waitingCount: Int,
    val customersAhead: Int
)
enum class LoginCode {
    OK,
    NOT_FOUND,
    ERROR
}

enum class UpdatePassword
{
    EMPTY_FIELD,
    PASSWORD_SHORT,
    PASSWORD_LONG,
    PASSWORD_MISMATCH,
    CODE_SHORT,
    CODE_LONG,
    INVALID_CODE,
    OK,
    INVALID_PASSWORD,
    ERROR
}

enum class ChangeOldPassword {
    EMPTY_FIELD,
    PASSWORD_SHORT,
    PASSWORD_LONG,
    PASSWORD_MISMATCH,
    SAME_PASSWORD,
    OK,
    INVALID_PASSWORD,
    ERROR
}

enum class RegisterCode {
    OK,
    EMPTY_FIELD,
    TRN_INVALID,
    NAME_INVALID,
    PHONE_INVALID,
    PASSWORD_LONG,
    PASSWORD_SHORT,
    PASSWORD_MISMATCH,
    EMAIL_INVALID,
    TRN_EXISTS,
    EMAIL_EXISTS,
    PHONE_EXISTS,
    ERROR,
    PASSWORD_INVALID
}


@OptIn(ExperimentalTime::class)
class Client() {
    private val context: Context = if (MainActivity.isInitialized) {
        MainActivity.instance.applicationContext
    } else {
        throw IllegalStateException("MainActivity has not been initialized yet.")
    }

    // Connection to the server, opened when the Client is created
    private val socket = Socket(SERVER_HOST, SERVER_PORT)
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

    private companion object {
        const val SERVER_HOST = "10.0.2.2" // TODO: set to your server's address (10.0.2.2 = host machine from the Android emulator)
        const val SERVER_PORT = 5000       // TODO: set to your server's port
    }

    public suspend fun user(trn: String): User? = withContext(Dispatchers.IO) {
        try {
            writer.write("USER,$trn\n")
            writer.flush()

            // A User as JSON, or null if the user does not exist
            val response = reader.readLine()
            Json.decodeFromString<User?>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            null
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            null
        }
    }

    public suspend fun changePassword(
        trn: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ChangeOldPassword = withContext(Dispatchers.IO) {
        try {
            writer.write("CHANGEPASSWORD,$trn,$oldPassword,$newPassword,$confirmPassword\n")
            writer.flush()

            val response = reader.readLine()
            ChangeOldPassword.valueOf(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            ChangeOldPassword.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            ChangeOldPassword.ERROR
        }
    }

    public suspend fun login(trn: String, password: String): LoginCode = withContext(Dispatchers.IO) {
        try {
            writer.write("LOGIN,$trn,$password\n")
            writer.flush()

            val response = reader.readLine()
            val code = LoginCode.valueOf(response)

            code
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            LoginCode.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            LoginCode.ERROR
        }
    }

    private fun validate(
        trn: String = "123456789",
        name: String = "Joe Mama",
        password: String = "mypass123!",
        email: String = "thisisaperson12@gmail.com",
        phone: String = "8761234567",
        confirmPassword: String = password
    ): RegisterCode? {
        if (listOf(trn, name, password, email, phone).any { it.isBlank() })
            return RegisterCode.EMPTY_FIELD
        else if (!name.all { it.isLetter() || it == ' ' })
            return RegisterCode.NAME_INVALID
        else if (trn.length != 9 || !trn.isDigitsOnly())
            return RegisterCode.TRN_INVALID
        else if (!email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")))
            return RegisterCode.EMAIL_INVALID
        else if (phone.isEmpty() || !phone.isDigitsOnly() || phone.length > 11 || phone.length <10)
            return RegisterCode.PHONE_INVALID
        else if (password.length < 8)
            return RegisterCode.PASSWORD_SHORT
        else if (password.length > 30)
            return RegisterCode.PASSWORD_LONG
        else if (password != confirmPassword)
            return RegisterCode.PASSWORD_MISMATCH
        else if (!password.any { it.isLetter() } || !password.any { it.isDigit() } || !password.any { it == '!' || it == '@' || it == '#' || it == '$' || it == '%' || it == '^' || it == '&' || it == '*' }) {
            return RegisterCode.PASSWORD_INVALID
        }
        else return null
    }

    public suspend fun register(
        trn: String,
        name: String,
        password: String,
        email: String,
        phone: String,
        confirmPassword: String
    ): RegisterCode = withContext(Dispatchers.IO) {
        try {
            writer.write("REGISTER,$trn,$name,$password,$email,$phone,$confirmPassword\n")
            writer.flush()

            val response = reader.readLine()
            RegisterCode.valueOf(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            RegisterCode.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            RegisterCode.ERROR
        }
    }

    public suspend fun checkEmailAddress(email: String): LoginCode = withContext(Dispatchers.IO) {
        try {
            writer.write("CHECKEMAIL,$email\n")
            writer.flush()

            val response = reader.readLine()
            LoginCode.valueOf(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            LoginCode.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            LoginCode.ERROR
        }
    }

    public suspend fun writeVerifyCodeToFile(email: String, code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            writer.write("WRITEVERIFYCODE,$email,$code\n")
            writer.flush()

            val response = reader.readLine()
            response.toBoolean()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            false
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            false
        }
    }

    public suspend fun updatePassword(
        email: String,
        verifyCode: String,
        password: String,
        confirmPassword: String
    ): UpdatePassword = withContext(Dispatchers.IO) {
        try {
            writer.write("UPDATEPASSWORD,$email,$verifyCode,$password,$confirmPassword\n")
            writer.flush()

            val response = reader.readLine()
            UpdatePassword.valueOf(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            UpdatePassword.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            UpdatePassword.ERROR
        }
    }

    private fun getReceiptFile(context: Context): File {
        return File(context.filesDir, "receipt_records.txt")
    }

    // Convert text record into Receipt
    private fun stringToReceipt(line: String): Receipt? {
        return try {
            val data = line.split(",", limit = 7)

            if (data.size != 7 || data[0] == "receiptNo") {
                return null
            }

            Receipt(
                receiptNo = data[0],
                ticketNo = data[1],
                trn = data[2],
                paymentMethod = data[3],
                paymentStatus = data[4],
                cost = data[5].toDouble(),
                totalPaid = data[6].toDouble()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Retrieve all receipts
    private fun getAllReceipts(context: Context = this.context): List<Receipt> {
        val file = getReceiptFile(context)

        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull {
            stringToReceipt(it)
        }
    }

    // Retrieve a receipt using its ticket number
    private fun getReceipt(ticketNo: String): Receipt? {
        return getAllReceipts(context).find {
            it.ticketNo == ticketNo
        }
    }

    // Retrieve receipts belonging to a customer
    public suspend fun getCustomerReceipts(trn: String): List<Receipt> = withContext(Dispatchers.IO) {
        try {
            writer.write("GETCUSTOMERRECEIPTS,$trn\n")
            writer.flush()

            // A JSON array of Receipt
            val response = reader.readLine()
            Json.decodeFromString<List<Receipt>>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
    }

    private fun ticketToString(ticket: Ticket): String {
        return listOf(
            ticket.ticketNo,
            ticket.custID,
            ticket.service,
            ticket.branch,
            ticket.ticketStatus,
            ticket.created,
            ticket.serviceStartTime ?: "",
            ticket.serviceEndTime ?: "",
            ticket.serviceTime
        ).joinToString(",")
    }

    public suspend fun createTicket(
        trn: String,
        service: String,
        branch: String
    ): Ticket? = withContext(Dispatchers.IO) {
        try {
            writer.write("CREATETICKET,$trn,$service,$branch\n")
            writer.flush()

            // A Ticket as JSON, or null if the ticket could not be created
            val response = reader.readLine()
            Json.decodeFromString<Ticket?>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            null
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            null
        }
    }

    public suspend fun updateQueue(branch: String): Unit = withContext(Dispatchers.IO) {
        try {
            writer.write("UPDATEQUEUE,$branch\n")
            writer.flush()

            // The server acknowledges every command with one line
            reader.readLine()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }
    }

    public suspend fun getAllCards(context: Context): List<PaymentCard> = withContext(Dispatchers.IO) {
        try {
            writer.write("GETALLCARDS\n")
            writer.flush()

            // A JSON array of PaymentCard
            val response = reader.readLine()
            Json.decodeFromString<List<PaymentCard>>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
    }

    public suspend fun makePayment(
        ticketNo: String,
        amount: Double,
        cardLastFourDigits: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            writer.write("MAKEPAYMENT,$ticketNo,$amount,$cardLastFourDigits\n")
            writer.flush()

            val response = reader.readLine()
            response.toBoolean()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            false
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            false
        }
    }

    private fun getTicketFile(context: Context): File {
        return File(context.filesDir, "tickets.txt")
    }

    // Append a ticket to tickets.txt
    private fun saveTicket(context: Context, ticket: Ticket) {
        val file = getTicketFile(context)

        file.appendText(ticketToString(ticket) + "\n")
    }

    // Retrieve all tickets
    public suspend fun getAllTickets(context: Context = this.context): List<Ticket> = withContext(Dispatchers.IO) {
        try {
            writer.write("GETALLTICKETS\n")
            writer.flush()

            // A JSON array of Ticket
            val response = reader.readLine()
            Json.decodeFromString<List<Ticket>>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
    }

    // Retrieve tickets belonging to a specific customer
    public suspend fun getCustomerTickets(context: Context = this.context, trn: String): List<Ticket> = withContext(Dispatchers.IO) {
        try {
            writer.write("GETCUSTOMERTICKETS,$trn\n")
            writer.flush()

            // A JSON array of Ticket
            val response = reader.readLine()
            Json.decodeFromString<List<Ticket>>(response)
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            emptyList()
        }
    }

    public suspend fun hasActiveTicket(trn: String): Boolean = withContext(Dispatchers.IO) {
        try {
            writer.write("HASACTIVETICKET,$trn\n")
            writer.flush()

            val response = reader.readLine()
            response.toBoolean()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            false
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            false
        }
    }

    public suspend fun cancelTicket(ticketNo: String): Boolean = withContext(Dispatchers.IO) {
        try {
            writer.write("CANCELTICKET,$ticketNo\n")
            writer.flush()

            val response = reader.readLine()
            response.toBoolean()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            false
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            false
        }
    }

    /**
     * Do not use this function without checking if the card already exists
     * @param trn The TRN of the user
     * @param card The credit card number to add
     */
    public suspend fun addCard(trn: String, card: String): Boolean = withContext(Dispatchers.IO) {
        try {
            writer.write("ADDCARD,$trn,$card\n")
            writer.flush()

            val response = reader.readLine()
            response.toBoolean()
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            false
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            false
        }
    }

    public suspend fun cards(trn: String): String? = withContext(Dispatchers.IO) {
        try {
            writer.write("CARDS,$trn\n")
            writer.flush()

            val response = reader.readLine()
            if (response == "NONE") null else response
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            null
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            null
        }
    }
}