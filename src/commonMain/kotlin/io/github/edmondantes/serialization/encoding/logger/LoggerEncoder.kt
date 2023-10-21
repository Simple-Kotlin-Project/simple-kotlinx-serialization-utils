/*
 * Copyright (c) 2023. Ilia Loginov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.edmondantes.serialization.encoding.logger

import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import io.github.edmondantes.serialization.encoding.broadcast.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.sequence.EncoderSequence
import io.github.edmondantes.serialization.util.DefaultLoggerOutput
import io.github.edmondantes.serialization.util.EmptyLoggerOutput
import io.github.edmondantes.serialization.util.LoggerOutput
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to see how [Encoder] and [CompositeEncoder] will encode your object.
 * @param output A function, which gets a logging information
 * @param id A unique id for this [UniqueEncoder] and [UniqueCompositeEncoder]
 * @see Encoder
 * @see CompositeEncoder
 * @see UniqueEncoder
 * @see UniqueCompositeEncoder
 * @see LoggerOutput
 * @see EmptyLoggerOutput
 * @see DefaultLoggerOutput
 */
@OptIn(ExperimentalSerializationApi::class)
public open class LoggerEncoder(
    protected val output: LoggerOutput = DefaultLoggerOutput(::println),
    override val id: String = DEFAULT_ID,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueEncoder, UniqueCompositeEncoder {
    /**
     * Nesting level
     */
    protected open var level: Int = 0

    // ------------------ Encoder methods -------------
    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
    ): CompositeEncoder {
        log(methodName = "beginCollection", descriptor = descriptor)
        level++
        return this
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        log(methodName = "beginStructure", descriptor = descriptor)
        level++
        return this
    }

    override fun encodeBoolean(value: Boolean) {
        log(methodName = "encodeBoolean", value = value)
    }

    override fun encodeByte(value: Byte) {
        log(methodName = "encodeByte", value = value)
    }

    override fun encodeChar(value: Char) {
        log(methodName = "encodeChar", value = value)
    }

    override fun encodeShort(value: Short) {
        log(methodName = "encodeShort", value = value)
    }

    override fun encodeInt(value: Int) {
        log(methodName = "encodeInt", value = value)
    }

    override fun encodeLong(value: Long) {
        log(methodName = "encodeLong", value = value)
    }

    override fun encodeFloat(value: Float) {
        log(methodName = "encodeFloat", value = value)
    }

    override fun encodeDouble(value: Double) {
        log(methodName = "encodeDouble", value = value)
    }

    override fun encodeString(value: String) {
        log(methodName = "encodeString", value = value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        apply {
            log(methodName = "encodeInline", descriptor)
        }

    override fun encodeEnum(
        enumDescriptor: SerialDescriptor,
        index: Int,
    ) {
        log(methodName = "encodeEnum", enumDescriptor, index)
    }

    override fun encodeNull() {
        log(methodName = "encodeNull")
    }

    override fun encodeNotNullMark() {
        log(methodName = "encodeNotNullMark")
    }

    override fun <T : Any> encodeNullableSerializableValue(
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        log(methodName = "encodeNullableSerializableValue", value = value)
        super.encodeNullableSerializableValue(serializer, value)
    }

    override fun <T> encodeSerializableValue(
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        log(methodName = "encodeSerializableValue", value = value)
        super.encodeSerializableValue(serializer, value)
    }

    // ------------------------- CompositeEncoder methods ----------------
    override fun encodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Boolean,
    ) {
        log("encodeBooleanElement", descriptor, index, value)
    }

    override fun encodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Byte,
    ) {
        log("encodeByteElement", descriptor, index, value)
    }

    override fun encodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Char,
    ) {
        log("encodeCharElement", descriptor, index, value)
    }

    override fun encodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Short,
    ) {
        log("encodeShortElement", descriptor, index, value)
    }

    override fun encodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Int,
    ) {
        log("encodeIntElement", descriptor, index, value)
    }

    override fun encodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Long,
    ) {
        log("encodeLongElement", descriptor, index, value)
    }

    override fun encodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Float,
    ) {
        log("encodeFloatElement", descriptor, index, value)
    }

    override fun encodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Double,
    ) {
        log("encodeDoubleElement", descriptor, index, value)
    }

    override fun encodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: String,
    ) {
        log("encodeStringElement", descriptor, index, value)
    }

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Encoder {
        log("encodeInlineElement", descriptor, index)
        return this
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        log("encodeSerializableElement", descriptor, index, value)
        serializer.serialize(this, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        log("encodeNullableSerializableElement", descriptor, index, value)
        val isNullabilitySupported = serializer.descriptor.isNullable
        if (isNullabilitySupported) {
            // Instead of `serializer.serialize` to be able to intercept this
            return encodeSerializableElement(descriptor, index, serializer as SerializationStrategy<T?>, value)
        }

        // Else default path used to avoid allocation of NullableSerializer
        if (value == null) {
            encodeNull()
        } else {
            encodeNotNullMark()
            encodeSerializableValue(serializer, value)
        }
    }

    override fun shouldEncodeElementDefault(
        descriptor: SerialDescriptor,
        index: Int,
    ): Boolean {
        log("shouldEncodeElementDefault", descriptor, index)
        return super.shouldEncodeElementDefault(descriptor, index)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        log("endStructure", descriptor)
        level--
    }

    protected open fun log(
        methodName: String,
        descriptor: SerialDescriptor? = null,
        index: Int? = null,
        value: Any? = null,
    ) {
        output.log(methodName, level, descriptor, index, value)
    }

    public companion object {
        /**
         * Default id for [LoggerEncoder]
         */
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.encoding.logger.LoggerEncoder"
    }
}

/**
 * Add logging to [Encoder]
 */
public fun Encoder.logging(output: LoggerOutput = DefaultLoggerOutput(::println)): BroadcastEncoder =
    BroadcastEncoder(this, LoggerEncoder(output))

/**
 * Add logging for each [Encoder] in [EncoderSequence]
 */
public fun <T : EncoderSequence<T>> T.sequenceLogging(output: LoggerOutput = DefaultLoggerOutput(::println)): T =
    plus(LoggerEncoder(output))
