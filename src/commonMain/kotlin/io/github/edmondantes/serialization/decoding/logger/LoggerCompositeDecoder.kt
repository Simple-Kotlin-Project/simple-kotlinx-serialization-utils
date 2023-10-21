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
package io.github.edmondantes.serialization.decoding.logger

import io.github.edmondantes.serialization.decoding.CustomDeserializationStrategy
import io.github.edmondantes.serialization.decoding.UniqueCompositeDecoder
import io.github.edmondantes.serialization.util.DefaultLoggerOutput
import io.github.edmondantes.serialization.util.LoggerOutput
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.CompositeDecoder.Companion.UNKNOWN_NAME
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to log calls in decoding process in [CompositeDecoder]
 * @param delegate original [CompositeDecoder]
 * @param output object which gets a logging information
 * @param level is nested level at start logging
 */
public open class LoggerCompositeDecoder(
    protected val delegate: CompositeDecoder,
    protected val output: LoggerOutput = DefaultLoggerOutput(::println),
    protected val level: Int = 0,
    override val id: String = LoggerDecoder.DEFAULT_ID,
) : UniqueCompositeDecoder {
    override val serializersModule: SerializersModule
        get() = delegate.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        delegate.decodeElementIndex(descriptor).also { index ->
            if (index != DECODE_DONE && index != UNKNOWN_NAME) {
                log("decodeElementIndex", descriptor = descriptor, index = index, value = index)
            } else {
                val value =
                    when (index) {
                        DECODE_DONE -> "DECODE_DONE"
                        UNKNOWN_NAME -> "UNKNOWN_NAME"
                        else -> index
                    }

                log("decodeElementIndex", descriptor = descriptor, value = value)
            }
        }

    override fun decodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Boolean =
        logValue(
            methodName = "decodeBooleanElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeBooleanElement,
        )

    override fun decodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Byte =
        logValue(
            methodName = "decodeByteElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeByteElement,
        )

    override fun decodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Char =
        logValue(
            methodName = "decodeCharElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeCharElement,
        )

    override fun decodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Short =
        logValue(
            methodName = "decodeShortElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeShortElement,
        )

    override fun decodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Int =
        logValue(
            methodName = "decodeIntElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeIntElement,
        )

    override fun decodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Long =
        logValue(
            methodName = "decodeLongElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeLongElement,
        )

    override fun decodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Float =
        logValue(
            methodName = "decodeFloatElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeFloatElement,
        )

    override fun decodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Double =
        logValue(
            methodName = "decodeDoubleElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeDoubleElement,
        )

    override fun decodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): String =
        logValue(
            methodName = "decodeStringElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeStringElement,
        )

    override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Decoder =
        logValue(
            methodName = "decodeInlineElement",
            descriptor = descriptor,
            index = index,
            CompositeDecoder::decodeInlineElement,
        )

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? {
        log(methodName = "decodeNullableSerializableElement", descriptor = descriptor, index = index)
        return delegate.decodeNullableSerializableElement(
            descriptor,
            index,
            CustomDeserializationStrategy(deserializer) { LoggerDecoder(it, output, level + 1, id) },
            previousValue,
        )
    }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T {
        log(methodName = "decodeSerializableElement", descriptor = descriptor, index = index)
        return delegate.decodeSerializableElement(
            descriptor,
            index,
            CustomDeserializationStrategy(deserializer) { LoggerDecoder(it, output, level + 1, id) },
            previousValue,
        )
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        log(methodName = "endStructure", descriptor = descriptor)
    }

    protected open fun log(
        methodName: String,
        descriptor: SerialDescriptor? = null,
        index: Int? = null,
        value: Any? = null,
    ) {
        output.log(methodName, level, descriptor, index, value)
    }

    protected open fun <T> logValue(
        methodName: String,
        descriptor: SerialDescriptor,
        index: Int,
        block: CompositeDecoder.(SerialDescriptor, Int) -> T,
    ): T =
        block(delegate, descriptor, index).also {
            log(methodName = methodName, descriptor = descriptor, index = index, value = it)
        }
}
