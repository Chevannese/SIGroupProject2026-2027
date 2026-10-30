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
import android.util.Log
import kotlin.io.path.createFile

enum class LoginCode {
    OK,
    NOT_FOUND,
    ERROR
}

enum class RegisterCode {
    OK,
    TRN_INVALID,
    PHONE_INVALID,
    PASSWORD_INVALID,
    EMAIL_INVALID,
    TRN_EXISTS,
    EMAIL_EXISTS,
    PHONE_EXISTS,
    ERROR
}

class Client(private val context: Context) {
    public fun login(trn: String, password: String): LoginCode {
        try {
            val file = File(context.filesDir, "users.txt")
            Log.d("FILE", "File opened: " + file.path.toString())
            Log.d("FILE", "File contents: \n" + file.readText())
            val users = file.readLines()

            for (user in users) {
                val split = user.split(",")
                if (split[0] == trn && split[4] == password) {
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

    public fun register(trn: String, name: String, password: String, email: String, phone: String): RegisterCode {
        try {
            val file = File(context.filesDir, "users.txt")
            //val filewriter = FileWriter(File(context.filesDir, "users.txt"), true)
            Log.d("FILE", "File opened: " + file.path.toString())

            for (line in file.readLines()) {
                val split = line.split(",")

                if (trn.length != 9 || !trn.isDigitsOnly()) {
                    return RegisterCode.TRN_INVALID
                }
                else if (!phone.isDigitsOnly()) {
                    return RegisterCode.PHONE_INVALID
                }
                else if (password.length < 9) { // more checks required
                    return RegisterCode.PASSWORD_INVALID
                }
                else if (!email.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
                    return RegisterCode.EMAIL_INVALID
                }
                else if (split[0] == trn) {
                    return RegisterCode.TRN_EXISTS
                }
                else if (split[1] == email) {
                    return RegisterCode.EMAIL_EXISTS
                }
                else if (split[2] == phone) {
                    return RegisterCode.PHONE_EXISTS
                }
            }

            // trn -> email -> phone -> name -> password
            val user = String.format("%s,%s,%s,%s,%s\n", trn, email, phone, name, password)
            //if (!file.exists()) {
                Log.d("FILE", "File does not exist: " + file.path.toString())
                file.parentFile?.mkdirs()
                file.createNewFile()
                file.writeBytes(byteArrayOf())
                FileWriter(file).write(0)
                Log.d("FILE", "Created File: " + file.path.toString())
                file.toPath().createFile()
            //}


            file.printWriter().use {
                it.println(user)
            }
            Log.d("FILE", "File written: " + file.path.toString())
            return RegisterCode.OK
        }
        catch (e: IOException) {
            Log.d("ERROR", e.message.toString())
            return RegisterCode.ERROR
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