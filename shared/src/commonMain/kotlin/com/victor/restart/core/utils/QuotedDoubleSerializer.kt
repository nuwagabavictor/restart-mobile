package com.victor.restart.core.utils


import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Deserializes a JSON field that may arrive as:
 *   - a quoted string:  "amount": "10000.00"
 *   - a raw number:     "amount": 10000.00
 * and always produces a Double in Kotlin.
 * Serializes back as a quoted string to match the API.
 */
object QuotedDoubleSerializer : KSerializer<Double> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("QuotedDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        val raw = decoder.decodeString()
        return raw.toDoubleOrNull() ?: 0.0
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeString(value.toString())
    }
}