package com.example.flowapp

import android.content.Context
import android.provider.ContactsContract
import androidx.core.text.isDigitsOnly
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class Client(private val context: Context) {
    public fun login(id: String, password: String): String? {
        try {
            val file = File(context.filesDir, "users.txt")
            val users = file.readLines()

            for (user in users) {
                val split = user.split(",")
                if (split[3] == id && split[2] == generateHash(password)) {
                    return "OK"
                }
            }

            return null
        }
        catch (e: IOException) {
            return "ERROR: " + e.message
        }
    }

    public fun register(trn: String, name: String, password: String, email: String, phone: String): String {
        try {
            val file = File(context.filesDir, "users.txt")

            for (line in file.readLines()) {
                val split = line.split(",")

                if (trn.length != 9 || !trn.isDigitsOnly()) {
                    return "TRN_INVALID"
                }
                else if (!phone.isDigitsOnly()) {
                    return "PHONE_INVALID"
                }
                else if (password.length !in 8..50) { // more checks required
                    return "PASSWORD_INVALID"
                }
                else if (!email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
                    return "EMAIL_INVALID"
                }
                else if (split[0] == trn) {
                    return "TRN_EXISTS"
                }
                else if (split[1] == email) {
                    return "EMAIL_EXISTS"
                }
                else if (split[2] == phone) {
                    return "PHONE_EXISTS"
                }
            }

            // trn -> email -> phone -> name -> password
            val user = String.format("%s,%s,%s,%s,%s\n", trn, email, phone, name, generateHash(password))
            file.appendText(user)
            return "OK"
        }
        catch (e: IOException) {
            println(e)
            return "ERROR: " + e.message
        }
    }
}

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

fun generateHash(password: String,): String {

    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    val finalsalt = salt.toHexString()
    val combinedSalt = "${finalsalt}$SECRET".toByteArray()

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded

    return hash.toHexString()
}