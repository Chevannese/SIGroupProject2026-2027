package com.example.flowapp

import android.content.Context

import androidx.core.text.isDigitsOnly
import java.io.File

import java.io.IOException

import android.util.Log
import java.security.MessageDigest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.time.Clock
import java.util.Locale
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.time.Instant


enum class LoginCode {
    OK,
    NOT_FOUND,
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
    ERROR
}

class Client(private val context: Context) {
    val smtpHost = ""
    val smtpUsername = ""
    val smtpPassword = ""

     fun login(trn: String, password: String): LoginCode {
        try {
            val file = File(context.filesDir, "users.txt")
            Log.d("FILE", "File opened: " + file.path.toString())
            Log.d("FILE", "File contents: \n" + file.readText())
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

    fun register(trn: String, name: String, password: String, email: String, phone: String, confirmPassword:String): RegisterCode {
        // Validate first, independent of file contents
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

    /*
    fun verifyRecoveryCode(email: String, enteredCode: String): Boolean {
        // should search existing codes

        // and if an email matches check if the code is expired

        return false
    }

    suspend fun forgetPassword(email: String) {
        // Check if email exists in users file
        try {
            val file = File(context.filesDir, "users.txt")
            val users = file.readLines()

            for (user in users) {
                val split = user.split(",")
                if (split[1] == email) {
                    // create the recovery code and send it to the email
                    // create 6 digit code
                    val code = String.format(locale = Locale.US,"%06d", SecureRandom().nextInt(1_000_000));

                }
            }
        }
        catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }
    }

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

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"


// you can hash it by calling this function
// so like if you have a password variable, to get the hash you type password.hash256()
fun String.hash256(): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray()).toHexString()
}

data class RecoveryToken(
    val code: String,
    val timestampMillis: Long = System.currentTimeMillis()
) {
    fun isExpired(expirationMinutes: Long = 10): Boolean {
        val elapsed = System.currentTimeMillis() - timestampMillis
        return elapsed > (expirationMinutes * 60 * 1000)
    }
}

enum class ForgetPasswordCode {
    OK,
    EMAIL_INVALID,
    EMAIL_NOT_FOUND,
    ERROR
}
