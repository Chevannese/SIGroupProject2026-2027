package com.example.flowapp

import java.time.LocalDateTime
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender {

    fun sendVerificationCode(
        recipient: String,
        code: String
    ) {
        val properties = Properties().apply {
            put("mail.smtp.host", "mail.smtp2go.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(
            properties,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(
                        "Flow Service",
                        "H2i3nxWrF3Nl7IfT"
                    )
                }
            }
        )

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("chevannese.ellis@monahighschooljm.org"))
            setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipient)
            )
            subject = "Flow Service - Password Verification Code"

            setText("""
                Hello,

                Your password reset verification code is:

                $code

                This code expires in 5 minutes.

                If you did not request a password reset, ignore this email.
            """.trimIndent())
        }

        Transport.send(message)
    }
}

fun emailFeedback(
    trn: String,
    feedback: String, rating: Int,

) {
    val properties = Properties().apply {
        put("mail.smtp.host", "mail.smtp2go.com")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
    }

    val session = Session.getInstance(
        properties,
        object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    "Flow Service",
                    "H2i3nxWrF3Nl7IfT"
                )
            }
        }
    )
    
    val message = MimeMessage(session).apply {
        setFrom(InternetAddress("chevannese.ellis@monahighschooljm.org"))

        setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse("chevanneseellis@gmail.com")
        )

        subject = "Flow Service - Customer Feedback"

        setText(
            """
            Customer Feedback - Flow Real Time Scheduling App

            Customer ID: $trn
            Rating: $rating / 5 Stars

            Feedback:
            $feedback

            
            """.trimIndent()
        )
    }

    // Send the email OUTSIDE the apply block.
    Transport.send(message)
}

