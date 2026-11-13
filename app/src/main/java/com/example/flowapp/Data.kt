package com.example.flowapp
import java.time.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// LocalDateTime is not serializable out of the box, so it is sent as an ISO-8601 string
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

@Serializable
data class Ticket(
    val ticketNo: String,
    val custID: String,
    val service: String,
    val branch: String,
    val ticketStatus: String = "Waiting",
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val serviceStartTime: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val serviceEndTime: LocalDateTime? = null,
    val serviceTime: Int = 10
)

@Serializable
data class Receipt(
    val receiptNo: String,
    val ticketNo: String,
    val trn: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val cost: Double,
    val totalPaid: Double = 0.0
)

@Serializable
data class PaymentCard(
    val trn: String,
    val lastFourDigits: String,
    val balance: Double
)



@Serializable
data class User(val trn: String, val email: String, val phone: String, val name: String, val password: String) {
    companion object {
        fun default(): User {
            return User(
                trn = TODO(),
                email = TODO(),
                phone = TODO(),
                name = TODO(),
                password = TODO()
            )
        }
    }
}