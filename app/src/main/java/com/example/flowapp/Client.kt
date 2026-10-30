package com.example.flowapp

import android.content.Context
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
        val file = File(context.filesDir, "users.txt")
        val users = file.readLines()

        for (user in users) {
            val split = user.split(",")
            if (split[3] == id && split[2] == generateHash(password)) {
                return "OK"
            }
            else {
                return null
            }
        }

        return null
    }

    public fun register(firstName: String, lastName: String, password: String, email: String, phone: String): String? {
        try {
            val file = File(context.filesDir, "users.txt")
            val user = String.format("%s,%s,%s,%s,%s\n", firstName, lastName, generateHash(password), email, phone)

            file.writeText(user)
            return "OK"
        }
        catch (e: IOException) {
            println(e)
        }

        return null
    }
}

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

fun generateHash(password: String, salt: String = generateRandomSalt()): String {
    val combinedSalt = "$salt$SECRET".toByteArray()

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded



    return hash.toHexString()
}

fun generateRandomSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)

    return salt.toHexString()
}

