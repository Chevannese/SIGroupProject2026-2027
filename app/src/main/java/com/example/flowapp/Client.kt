package com.example.flowapp

import android.content.Context

import androidx.core.text.isDigitsOnly
import java.io.File
import java.io.IOException
import android.util.Log

import java.security.MessageDigest


// time stuff
import java.security.SecureRandom
import kotlin.time.Clock
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

import java.time.Duration
import java.time.Instant


// mail stuff
/*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties
*/

enum class LoginCode {
    OK,
    NOT_FOUND,
    ERROR
}
/*
enum class UpdatePassword
{
    OK,
    EMPTY_FIELD,
    PASSWORD_LONG,
    PASSWORD_SHORT,
    PASSWORD_MISMATCH,
    //Code_Expired,
    //Code_Invalid,
    ERROR
}
 */
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
    ERROR
}

enum class RecoveryCode {
    OK,
    INVALID_CODE,
    INVALID_PASSWORD,
    ERROR
}

@OptIn(ExperimentalTime::class)
class Client(private val context: Context = MainActivity.instance.applicationContext) {
    /* Not needed until mail is re-added
    private val smtpHost = ""
    private val smtpUsername = ""
    private val smtpPassword = ""
    */

    /*
    enum class ValidateEmail
    {
        EXIST,

        NOT_EXISTS,

        ERROR
    }
    */

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
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            return LoginCode.ERROR
        }
    }

    fun validate(trn: String, name: String, password: String, email: String, phone: String, confirmPassword: String): RegisterCode? {
        if (listOf(trn, name, password, email, phone).any { it.isBlank() })
            return RegisterCode.EMPTY_FIELD
        else if (!name.all{ it.isLetter()  || it == ' '})
            return RegisterCode.NAME_INVALID
        else if (trn.length != 9 || !trn.isDigitsOnly())
            return RegisterCode.TRN_INVALID
        else if (!email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")))
            return RegisterCode.EMAIL_INVALID
        else if (phone.isEmpty() || !phone.isDigitsOnly() || phone.length >12)
            return RegisterCode.PHONE_INVALID
        else if (password.length < 8)
            return RegisterCode.PASSWORD_SHORT
        else if (password.length > 30)
            return RegisterCode.PASSWORD_LONG
        else if (password != confirmPassword)
            return RegisterCode.PASSWORD_MISMATCH
        else return null
    }

    fun register(trn: String, name: String, password: String, email: String, phone: String, confirmPassword:String): RegisterCode {
        // moved out to separate function
        // Validate first, independent of file contents
        /*
        if (listOf(trn, name, password, email, phone).any { it.isBlank() }) {
            return RegisterCode.EMPTY_FIELD
        }
        if (!name.all{ it.isLetter()  || it == ' '}) return RegisterCode.NAME_INVALID
        if (trn.length != 9 || !trn.isDigitsOnly()) return RegisterCode.TRN_INVALID
        if (!email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")))
            return RegisterCode.EMAIL_INVALID
        if (phone.isEmpty() || !phone.isDigitsOnly() || phone.length >12) return RegisterCode.PHONE_INVALID
        if (password.length < 8) return RegisterCode.PASSWORD_SHORT
        if (password.length > 30) return RegisterCode.PASSWORD_LONG
        if (password != confirmPassword) return RegisterCode.PASSWORD_MISMATCH
         */

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
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            RegisterCode.ERROR
        }
    }

    fun verifyRecoveryCode(code: String, password: String, confirmPassword: String): RecoveryCode {
        // should search existing codes
        try {
            val codeFile = File(context.filesDir, "recovery_codes.txt")
            val now = Clock.System.now()

            if (codeFile.exists()) {
                val lines = codeFile.readLines()

                for (line in lines) {
                    val split = line.split(",")
                    if (split[0] == code && split[2].toLong() > now.toEpochMilliseconds()) {
                        if (validate("123456789", "Joe Mama", password, split[1], "8761234567", confirmPassword) != null) {
                            updatePassword(split[1], password)
                            removeCode(code)
                            return RecoveryCode.OK
                        }
                        else {
                            return RecoveryCode.INVALID_PASSWORD
                        }
                    }
                }
            }

            return RecoveryCode.INVALID_CODE
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            return RecoveryCode.ERROR
        }
    }

    fun removeCode(code: String) {
        try {
            val file = File(context.filesDir, "recovery_codes.txt")
            val updated = StringBuilder()

            if (!file.exists()) {
                return
            }

            for (line in file.readLines()) {
                val split = line.split(",")

                // if code matches
                if (split[0] != code) {
                    // don't add back to file
                    updated.appendLine(line)
                }
            }

            // write all remaining
            file.writeText(updated.toString())
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            throw e
        }
    }

    fun updatePassword(email: String, password: String): Boolean {
        try {
            val file = File(context.filesDir, "users.txt")
            val updated = StringBuilder()
            var found = false

            if (!file.exists()) {
                return false
            }

            for (line in file.readLines()) {
                if (line.isBlank()) continue
                val split = line.split(",")

                if (split[1] == email) {
                    // trn,email,phone,name,password
                    updated.appendLine("%s,%s,%s,%s,%s".format(split[0], split[1], split[2], split[3], password.hash256()))
                    found = true
                }
                else {
                    updated.appendLine(line)
                }
            }

            if (found) {
                file.writeText(updated.toString())
                return true
            }
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            throw e
        }
        return false
    }

    fun forgetPassword(email: String): String? {
        // Check if email exists in users file
        try {
            val userFile = File(context.filesDir, "users.txt")
            val recoveryFile = File(context.filesDir, "recovery_codes.txt")

            if (!userFile.exists()) {
                return null
            }

            for (user in userFile.readLines()) {
                val split = user.split(",")

                // find user by email
                if (split[1] == email.trim()) {
                    // create the 6 digit recovery code
                    val code = String.format(locale = Locale.US, "%06d", SecureRandom().nextInt(1_000_000));
                    // 5 minutes from now
                    val now = Clock.System.now()
                    val fiveMinutes = now.plus(5.minutes)

                    val line = "%s,%s,%s\n".format(code, email, fiveMinutes.toEpochMilliseconds())
                    // TODO: check if a code already exists for this email
                    recoveryFile.appendText(line)

                    // mail(email, "Password Recovery Code", "Your password recovery code is: $code")
                    // just show the user the code cuz lazy
                    return code
                }
            }
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }

        return null
    }

    /*
    fun updatePassword(verifyCode:String, password: String, confirmPassword: String): UpdatePassword {
        if(listOf(verifyCode,password,confirmPassword).any(){ it.isBlank() }) {
            return UpdatePassword.EMPTY_FIELD
        }
        if (password.length < 8) return UpdatePassword.PASSWORD_SHORT
        if (password.length > 30) return UpdatePassword.PASSWORD_LONG
        if (password != confirmPassword) return UpdatePassword.PASSWORD_MISMATCH
        return try {
            UpdatePassword.OK
        }
        catch (e : IOException) {
            Log.d("ERROR", e.message.toString())
            UpdatePassword.ERROR
        }
    }



        private val file = File("reset_codes.txt")
        private val validFor = Duration.ofMinutes(10)
        private val random = SecureRandom()
        private val lock = Any()

        private data class Entry(val username: String, val code: String, val createdAt: Instant)

        private fun readEntries(): List<Entry> {
            if (!file.exists()) return emptyList()
            return file.readLines().mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size != 3) return@mapNotNull null
                val time = runCatching { Instant.parse(parts[2]) }.getOrNull()
                    ?: return@mapNotNull null
                Entry(parts[0], parts[1], time)
            }
        }

        private fun writeEntries(entries: List<Entry>) {
            file.writeText(entries.joinToString("") { "${it.username},${it.code},${it.createdAt}\n" })
        }

        private fun isExpired(e: Entry) = Duration.between(e.createdAt, Instant.now()) > validFor

        /** "Receive verification code" button. Returns the code so you can email/SMS it. */
        fun generateCode(username: String): String = synchronized(lock) {


            require(username.isNotBlank() && ',' !in username && '\n' !in username) { "Invalid username" }

            val code = random.nextInt(1_000_000).toString().padStart(6, '0')

            // Drop expired codes and any older code for this user, then add the new one
            val kept = readEntries().filter { !isExpired(it) && it.username != username }
            writeEntries(kept + Entry(username, code, Instant.now()))
            code
        }

        /** "Enter verification code" button. True = let the user go to the next page. */
        fun verifyAndConsume(username: String, code: String): Boolean = synchronized(lock) {
            // Delete expired codes from the file
            val active = readEntries().filter { !isExpired(it) }

            val match = active.firstOrNull { it.username == username && it.code == code }

            // If matched, delete that code too (one-time use)
            writeEntries(if (match != null) active - match else active)

            match != null
        }


     */

    /*
    suspend fun mail(to: String, subject: String, body: String) = withContext(Dispatchers.IO) {
        val props = Properties().apply {
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(smtpUsername, smtpPassword)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(smtpUsername))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            setSubject(subject)
            setText(body)
        }

        Transport.send(message)
    }
    */
}

// so like if you have a password variable, to get the hash you type password.hash256()
fun String.hash256(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHexString()
}

