package com.example.flowapp

import android.content.Context

import androidx.core.text.isDigitsOnly
import java.io.File
import java.io.IOException
import android.util.Log

import java.security.MessageDigest


// time stuff
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.time.Clock
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime


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

    fun user(trn: String): User? {
        try {
            val file = File(context.filesDir, "users.txt")

            if (file.exists()) {
                for (line in file.readLines()) {
                    val split = line.split(",")

                    if (split[0] == trn.trim()) {
                        return User(
                            trn = split[0],
                            email = split[1],
                            phone = split[2],
                            name = split[3],
                            password = split[4]
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }

        return null
    }

    fun changePassword(
        trn: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ChangeOldPassword {

        // Check for empty fields
        if (listOf(oldPassword, newPassword, confirmPassword).any { it.isBlank() }) {
            return ChangeOldPassword.EMPTY_FIELD
        }

        // Check password length
        if (newPassword.length < 8) {
            return ChangeOldPassword.PASSWORD_SHORT
        }

        if (newPassword.length > 30) {
            return ChangeOldPassword.PASSWORD_LONG
        }

        // Check password confirmation
        if (newPassword != confirmPassword) {
            return ChangeOldPassword.PASSWORD_MISMATCH
        }

        // Check if new password is the same as old password
        if (oldPassword.hash256() == newPassword.hash256()) {
            return ChangeOldPassword.SAME_PASSWORD
        }
        if (newPassword.any { it.isLetter() } || !newPassword.any { it.isDigit() } || !newPassword.any { it == '!' || it == '@' || it == '#' || it == '$' || it == '%' || it == '^' || it == '&' || it == '*' }) {
            return ChangeOldPassword.INVALID_PASSWORD
        }

        try {
            val file = File(context.filesDir, "users.txt")
            val updated = StringBuilder()
            var changed = false

            if (file.exists()) {
                for (line in file.readLines()) {
                    val split = line.split(",")

                    if (split.size >= 5 &&
                        split[0] == trn &&
                        split[4] == oldPassword.hash256()
                    ) {
                        updated.appendLine(
                            "%s,%s,%s,%s,%s".format(
                                split[0],
                                split[1],
                                split[2],
                                split[3],
                                newPassword.hash256()
                            )
                        )

                        changed = true
                    } else {
                        updated.appendLine(line)
                    }
                }
            }

            if (changed) {
                file.writeText(updated.toString())
                return ChangeOldPassword.OK
            }

        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            return ChangeOldPassword.ERROR
        }

        return ChangeOldPassword.INVALID_PASSWORD
    }

    fun login(trn: String, password: String): LoginCode {
        try {
            val file = File(context.filesDir, "users.txt")
            Log.d("FILE", "File opened: " + file.path.toString())
            Log.d("FILE", "File contents: \n" + file.readText())

            if (!file.exists()) {
                return LoginCode.NOT_FOUND
            }

            val users = file.readLines()

            for (user in users) {
                val split = user.split(",")
                if (split[0] == trn && split[4] == password.hash256()) {
                    return LoginCode.OK
                }
            }

            return LoginCode.NOT_FOUND
        } catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        }
    }

    fun validate(
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

    fun register(
        trn: String,
        name: String,
        password: String,
        email: String,
        phone: String,
        confirmPassword: String
    ): RegisterCode {


        val validate = validate(trn, name, password, email, phone, confirmPassword)
        if (validate != null) {
            return validate
        }

        return try {
            val file = File(context.filesDir, "users.txt")
            Log.d("FILE", "File path: ${file.path}")

            // Only read if it exists yet
            if (file.exists()) {
                for (line in file.readLines()) {
                    if (line.isBlank()) continue
                    val split = line.split(",")
                    if (split.size < 3) continue

                    when {
                        split[0] == trn -> return RegisterCode.TRN_EXISTS
                        split[1] == email -> return RegisterCode.EMAIL_EXISTS
                        split[2] == phone -> return RegisterCode.PHONE_EXISTS
                    }
                }
            }

            // trn,email,phone,name,password  (commas stripped from name to protect the format)
            val safeName = name.replace(",", " ").replace("\n", " ")
            val user = "%s,%s,%s,%s,%s\n".format(trn, email, phone, safeName, password.hash256())

            // appendText creates the file if needed and appends otherwise
            file.appendText(user)
            Log.d("FILE", "User written")
            RegisterCode.OK
        } catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            RegisterCode.ERROR
        }
    }

    fun checkEmailAddress(email: String): LoginCode {
        try {
            val file = File(context.filesDir, "users.txt")

            if (!file.exists()) {
                return LoginCode.NOT_FOUND
            }

            val found = file.readLines().any { line ->
                val attributes = line.split(",")

                attributes.size == 5 &&
                        attributes[1].trim().equals(email.trim(), ignoreCase = true)
            }

            return if (found) LoginCode.OK else LoginCode.NOT_FOUND

        } catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        }
    }

    fun codeExists(code: String): Boolean {
        try {
            val file = File(context.filesDir, "recovery_codes.txt")

            if (file.exists()) {
                for (line in file.readLines()) {
                    val split = line.split(",")

                    if (split[0] == code) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            throw e
        }

        return false
    }

    fun writeVerifyCodeToFile(email: String, code: String): Boolean {
        try {
            val userFile = File(context.filesDir, "users.txt")
            val recoveryFile = File(context.filesDir, "recovery_codes.txt")

            if (!userFile.exists()) return false

            // Check whether the email belongs to a registered user
            val userExists = userFile.readLines().any { user ->
                val split = user.split(",")

                split.size == 5 &&
                        split[1].trim().equals(email.trim(), ignoreCase = true)
            }

            if (!userExists) return false

            // Check whether the generated code already exists
            if (codeExists(code)) return false

            // Remove previous recovery codes associated with this email
            if (recoveryFile.exists()) {
                val updatedCodes = recoveryFile.readLines().filter { line ->
                    val split = line.split(",")

                    // Keep records that belong to other email addresses
                    split.size != 3 ||
                            !split[1].trim().equals(email.trim(), ignoreCase = true)
                }

                recoveryFile.writeText(
                    if (updatedCodes.isNotEmpty()) {
                        updatedCodes.joinToString("\n") + "\n"
                    } else {
                        ""
                    }
                )
            }

            // Generate the expiration time (5 minutes)
            val now = Clock.System.now()
            val fiveMinutes = now.plus(5.minutes)

            // Save the new recovery code
            val line = "%s,%s,%s\n".format(
                code,
                email.trim(),
                fiveMinutes.toEpochMilliseconds()
            )

            recoveryFile.appendText(line)

            return true

        } catch (e: Exception) {
            Log.e("ERROR", "Failed to save recovery code", e)
            return false
        }
    }

    fun updatePassword(
        email: String,
        verifyCode: String,
        password: String,
        confirmPassword: String
    ): UpdatePassword {

        if (listOf(verifyCode, password, confirmPassword).any { it.isBlank() }) {
            return UpdatePassword.EMPTY_FIELD
        }
        if (verifyCode.length <6) return UpdatePassword.CODE_SHORT
        if (verifyCode.length >6) return UpdatePassword.CODE_LONG
        if (password.length < 8) return UpdatePassword.PASSWORD_SHORT
        if (password.length > 30) return UpdatePassword.PASSWORD_LONG
        if (password != confirmPassword) return UpdatePassword.PASSWORD_MISMATCH

        return try {
            val recoveryFile = File(context.filesDir, "recovery_codes.txt")
            val userFile = File(context.filesDir, "users.txt")

            if (!recoveryFile.exists() || !userFile.exists()) {
                return UpdatePassword.ERROR
            }

            // Find the recovery code associated with this email
            val recoveryCodes = recoveryFile.readLines().toMutableList()

            val recoveryIndex = recoveryCodes.indexOfFirst { line ->
                val split = line.split(",")

                split.size == 3 &&
                        split[0].trim() == verifyCode.trim() &&
                        split[1].trim().equals(email.trim(), ignoreCase = true)
            }

            if (recoveryIndex == -1) {
                return UpdatePassword.INVALID_CODE
            }

            // Retrieve the recovery code details
            val recoveryDetails = recoveryCodes[recoveryIndex].split(",")

            val email = recoveryDetails[1].trim()
            val expiryTime = recoveryDetails[2].trim().toLongOrNull()
                ?: return UpdatePassword.INVALID_CODE

            // Check whether the code has expired
            val currentTime = Clock.System.now().toEpochMilliseconds()

            if (currentTime > expiryTime) {
                return UpdatePassword.INVALID_CODE
            }

            // Update the user's password
            val users = userFile.readLines().toMutableList()

            val userIndex = users.indexOfFirst { line ->
                val split = line.split(",")

                split.size == 5 &&
                        split[1].trim().equals(email, ignoreCase = true)
            }

            if (userIndex == -1) {
                return UpdatePassword.ERROR
            }

            val userDetails = users[userIndex].split(",").toMutableList()

            // Hash the new password using your existing password-hashing method
            userDetails[4] = password.hash256()

            users[userIndex] = userDetails.joinToString(",")

            // Save the updated user information
            userFile.writeText(users.joinToString("\n") + "\n")

            // Remove the successfully used recovery code
            recoveryCodes.removeAt(recoveryIndex)

            recoveryFile.writeText(
                if (recoveryCodes.isNotEmpty()) {
                    recoveryCodes.joinToString("\n") + "\n"
                } else {
                    ""
                }
            )

            UpdatePassword.OK

        } catch (e: IOException) {
            Log.e("ERROR", "Failed to update password", e)
            UpdatePassword.ERROR
        } catch (e: Exception) {
            Log.e("ERROR", "Unexpected error while updating password", e)
            UpdatePassword.ERROR
        }
    }

    private fun getReceiptFile(context: Context): File {
        return File(context.filesDir, "receipt_records.txt")
    }

    // Convert Receipt into a text record
    fun receiptToString(receipt: Receipt): String {
        return listOf(
            receipt.receiptNo,
            receipt.ticketNo,
            receipt.trn,
            receipt.paymentMethod,
            receipt.paymentStatus,
            receipt.cost,
            receipt.totalPaid
        ).joinToString(",")
    }

    // Convert text record into Receipt
    fun stringToReceipt(line: String): Receipt? {
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
    fun getAllReceipts(context: Context = this.context): List<Receipt> {
        val file = getReceiptFile(context)

        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull {
            stringToReceipt(it)
        }
    }

    // Save all receipts
    private fun saveAllReceipts(receipts: List<Receipt>) {
        val file = getReceiptFile(context)

        file.writeText(
            receipts.joinToString("\n") {
                receiptToString(it)
            } + if (receipts.isNotEmpty()) "\n" else ""
        )
    }

    // Append a receipt
    fun saveReceipt(context: Context, receipt: Receipt) {
        getReceiptFile(context).appendText(
            receiptToString(receipt) + "\n"
        )
    }

    // Retrieve a receipt using its ticket number
    fun getReceipt(ticketNo: String): Receipt? {
        return getAllReceipts(context).find {
            it.ticketNo == ticketNo
        }
    }

    // Retrieve receipts belonging to a customer
    fun getCustomerReceipts(trn: String): List<Receipt> {
        return getAllReceipts(context).filter {
            it.trn == trn
        }
    }

    fun ticketToString(ticket: Ticket): String {
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

    fun stringToTicket(line: String): Ticket? {
        return try {
            val data = line.split(",", limit = 9)

            if (data.size != 9 || data[0] == "ticketNo") {
                return null
            }

            Ticket(
                ticketNo = data[0],
                custID = data[1],
                service = data[2],
                branch = data[3],
                ticketStatus = data[4],
                created = LocalDateTime.parse(data[5]),
                serviceStartTime = data[6]
                    .takeIf { it.isNotBlank() }
                    ?.let { LocalDateTime.parse(it) },
                serviceEndTime = data[7]
                    .takeIf { it.isNotBlank() }
                    ?.let { LocalDateTime.parse(it) },
                serviceTime = data[8].toInt()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createTicket(
        trn: String,
        service: String,
        branch: String
    ): Ticket? {

        if (hasActiveTicket(trn)) {
            return null
        }

        val existingTickets = getAllTickets(context)

        val nextNumber = (existingTickets.mapNotNull {
            it.ticketNo.removePrefix("TKT").toIntOrNull()
        }.maxOrNull() ?: 0) + 1

        val existingReceipts = getAllReceipts(context)

        val nextReceiptNumber = (existingReceipts.mapNotNull {
            it.receiptNo.removePrefix("RCP").toIntOrNull()
        }.maxOrNull() ?: 0) + 1

        val ticketNo = "TKT${nextNumber.toString().padStart(3, '0')}"

        val receiptNo = "RCP${nextReceiptNumber.toString().padStart(3, '0')}"

        val cost = when (service) {
            "Phone Service" -> 500.0
            "Internet Service" -> 1000.0
            else -> 0.0
        }

        val ticket = Ticket(
            ticketNo = ticketNo,
            custID = trn,
            service = service,
            branch = branch,
            ticketStatus = "Waiting",
            created = LocalDateTime.now(),
            serviceStartTime = null,
            serviceEndTime = null,
            serviceTime = 10
        )

        val receipt = Receipt(
            receiptNo = receiptNo,
            ticketNo = ticketNo,
            trn = trn,
            paymentMethod = "Card",
            paymentStatus = "Unpaid",
            cost = cost,
            totalPaid = 0.0
        )

        saveTicket(context, ticket)
        saveReceipt(context, receipt)

        // Check whether this ticket can start immediately.
        startNextTicket(branch)

        return ticket
    }

    fun markNoShow(ticketNo: String): Boolean {

        val tickets = getAllTickets(context)

        val ticket = tickets.find {
            it.ticketNo == ticketNo
        } ?: return false

        val receipt = getReceipt(ticketNo) ?: return false

        if (
            ticket.ticketStatus != "Being Served" ||
            receipt.totalPaid > 0.0 ||
            ticket.serviceEndTime == null ||
            LocalDateTime.now().isBefore(ticket.serviceEndTime)
        ) {
            return false
        }

        val updatedTickets = tickets.map {
            if (it.ticketNo == ticketNo) {
                it.copy(ticketStatus = "No Show")
            } else {
                it
            }
        }

        saveAllTickets(updatedTickets)

        return true
    }

    fun updateQueue(branch: String) {

        val tickets = getAllTickets(context)
        val receipts = getAllReceipts(context)

        val now = LocalDateTime.now()

        val updatedTickets = tickets.map { ticket ->

            if (
                ticket.branch == branch &&
                ticket.ticketStatus == "Being Served" &&
                ticket.serviceEndTime != null &&
                !now.isBefore(ticket.serviceEndTime)
            ) {

                val receipt = receipts.find {
                    it.ticketNo == ticket.ticketNo
                }

                if (receipt == null || receipt.totalPaid <= 0.0) {
                    ticket.copy(ticketStatus = "No Show")
                } else {
                    ticket.copy(ticketStatus = "Served")
                }

            } else {
                ticket
            }
        }

        saveAllTickets(updatedTickets)

        // Start the next ticket if the current service period has ended.
        startNextTicket(branch)
    }

    fun makePayment(
        ticketNo: String,
        amount: Double
    ): Boolean {

        if (amount <= 0.0) return false

        val tickets = getAllTickets(context)

        val ticket = tickets.find {
            it.ticketNo == ticketNo
        } ?: return false

        val receipts = getAllReceipts(context)

        val receipt = receipts.find {
            it.ticketNo == ticketNo
        } ?: return false

        // Only these ticket statuses allow payment.
        if (
            ticket.ticketStatus !in listOf(
                "Being Served",
                "Served"
            )
        ) {
            return false
        }

        if (receipt.paymentStatus == "Fully Paid") {
            return false
        }

        val remainingBalance = receipt.cost - receipt.totalPaid

        // Reject payments that exceed the remaining balance.
        if (amount > remainingBalance) return false

        val newTotalPaid = receipt.totalPaid + amount

        val fullyPaid = newTotalPaid >= receipt.cost

        val now = LocalDateTime.now()

        val updatedReceipts = receipts.map { currentReceipt ->

            if (currentReceipt.ticketNo == ticketNo) {

                currentReceipt.copy(
                    totalPaid = newTotalPaid,
                    paymentStatus = if (fullyPaid) {
                        "Fully Paid"
                    } else {
                        "Partially Paid"
                    }
                )

            } else {
                currentReceipt
            }
        }

        saveAllReceipts(updatedReceipts)

        if (fullyPaid) {

            val updatedTickets = tickets.map { currentTicket ->

                if (currentTicket.ticketNo == ticketNo) {

                    currentTicket.copy(
                        ticketStatus = "Completed",
                        serviceEndTime = now
                    )

                } else {
                    currentTicket
                }
            }

            saveAllTickets(updatedTickets)

            startNextTicket(ticket.branch)
        }

        return true
    }


    //Don't Remove From Here - Chev
    private fun saveAllTickets(tickets: List<Ticket>) {
        val file = getTicketFile(context)

        file.writeText(
            tickets.joinToString("\n") { ticketToString(it) }
        )

        if (tickets.isNotEmpty()) {
            file.appendText("\n")
        }
    }
    fun getTicketFile(context: Context): File {
        return File(context.filesDir, "tickets.txt")
    }

    // Append a ticket to tickets.txt
    fun saveTicket(context: Context, ticket: Ticket) {
        val file = getTicketFile(context)

        file.appendText(ticketToString(ticket) + "\n")
    }

    // Retrieve all tickets
    fun getAllTickets(context: Context = this.context): List<Ticket> {
        val file = getTicketFile(context)

        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull {
            stringToTicket(it)
        }
    }

    // Retrieve tickets belonging to a specific customer
    fun getCustomerTickets(context: Context = this.context, trn: String): List<Ticket> {
        val file = File(context.filesDir, "tickets.txt")

        // Check if file exists
        if (!file.exists()) {
            file.createNewFile()
            println("tickets.txt was created because it didn't exist.")
        }

        // Display file information
        println("File path: ${file.absolutePath}")
        println("File exists: ${file.exists()}")
        println("File contents:\n${file.readText()}")

        // Convert text records into Ticket objects
        val allTickets = file.readLines()
            .filter { it.isNotBlank() }
            .mapNotNull { stringToTicket(it) }

        println("Total tickets loaded: ${allTickets.size}")

        // Filter tickets by customer TRN
        val customerTickets = allTickets.filter {
            it.custID == trn
        }

        println("TRN being searched: $trn")
        println("Customer tickets found: ${customerTickets.size}")

        return customerTickets
    }

    fun startNextTicket(branch: String) {

        val tickets = getAllTickets(context)

        // Do not start another ticket while one is being served.
        val currentlyServing = tickets.any {
            it.branch == branch &&
                    it.ticketStatus == "Being Served"
        }

        if (currentlyServing) return

        // Find the next waiting ticket.
        val nextTicket = tickets
            .filter {
                it.branch == branch &&
                        it.ticketStatus == "Waiting"
            }
            .minByOrNull { it.created }
            ?: return

        val now = LocalDateTime.now()

        val updatedTickets = tickets.map { ticket ->
            if (ticket.ticketNo == nextTicket.ticketNo) {
                ticket.copy(
                    ticketStatus = "Being Served",
                    serviceStartTime = now,
                    serviceEndTime = now.plusMinutes(
                        ticket.serviceTime.toLong()
                    )
                )
            } else {
                ticket
            }
        }

        saveAllTickets(updatedTickets)
    }

    fun hasActiveTicket(trn: String): Boolean {
        return getAllTickets(context).any { ticket ->
            ticket.custID == trn &&
                    ticket.ticketStatus in listOf(
                "Waiting",
                "Being Served",
                "Served"
            )
        }
    }

    fun cancelTicket(ticketNo: String): Boolean {

        val tickets = getAllTickets(context)

        val ticket = tickets.find {
            it.ticketNo == ticketNo
        } ?: return false

        val receipt = getReceipt(ticketNo) ?: return false

        // Only unpaid waiting tickets can be cancelled.
        if (
            ticket.ticketStatus != "Waiting" ||
            receipt.totalPaid > 0.0
        ) {
            return false
        }

        // Update ticket status instead of deleting it.
        val updatedTickets = tickets.map {
            if (it.ticketNo == ticketNo) {
                it.copy(ticketStatus = "Cancelled")
            } else {
                it
            }
        }

        saveAllTickets(updatedTickets)

        // Keep the receipt unchanged.
        // It remains stored in receipt_records.txt.

        startNextTicket(ticket.branch)

        return true
    }


    /**
     * Do not use this function without checking if the card already exists
     * @param trn The TRN of the user
     * @param card The credit card number to add
     */
    fun add_card(trn: String, card: String): Boolean {
        try {
            val file = File(context.filesDir, "cards.txt")
            file.appendText("$trn,$card,5000\n")
            return true
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }

        return false
    }

    fun cards(trn: String): String? {
        try {
            val file = File(context.filesDir, "cards.txt")

            if (file.exists()) {
                for (line in file.readLines()) {
                    val split = line.split(",")

                    if (split[0] == trn) {
                        return split[1]
                    }
                }
                return null
            }
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }

        return null
    }
}

// so like if you have a password variable, to get the hash you type password.hash256()
fun String.hash256(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHexString()
}
