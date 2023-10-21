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

import io.github.edmondantes.serialization.decoding.UniqueDecoder
import io.github.edmondantes.serialization.util.DefaultLoggerOutput
import io.github.edmondantes.serialization.util.LoggerOutput
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to log calls in decoding process in [Decoder]
 * @param delegate original [Decoder]
 * @param output object which gets a logging information
 * @param level is nested level at start logging
 */
public open class LoggerDecoder(
    protected val delegate: Decoder,
    protected val output: LoggerOutput = DefaultLoggerOutput(::println),
    protected val level: Int = 0,
    override val id: String = DEFAULT_ID,
) : UniqueDecoder {
    override val serializersModule: SerializersModule
        get() = delegate.serializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        log(methodName = "beginStructure", descriptor = descriptor)
        return LoggerCompositeDecoder(delegate.beginStructure(descriptor), output, level + 1, id)
    }

    override fun decodeBoolean(): Boolean = logValue(methodName = "decodeBoolean", Decoder::decodeBoolean)

    override fun decodeByte(): Byte = logValue(methodName = "decodeByte", Decoder::decodeByte)

    override fun decodeChar(): Char = logValue(methodName = "decodeChar", Decoder::decodeChar)

    override fun decodeShort(): Short = logValue(methodName = "decodeShort", Decoder::decodeShort)

    override fun decodeInt(): Int = logValue(methodName = "decodeInt", Decoder::decodeInt)

    override fun decodeLong(): Long = logValue(methodName = "decodeLong", Decoder::decodeLong)

    override fun decodeFloat(): Float = logValue(methodName = "decodeFloat", Decoder::decodeFloat)

    override fun decodeDouble(): Double = logValue(methodName = "decodeDouble", Decoder::decodeDouble)

    override fun decodeString(): String = logValue(methodName = "decodeString", Decoder::decodeString)

    override fun decodeInline(descriptor: SerialDescriptor): Decoder =
        LoggerDecoder(
            delegate = logValue(methodName = "decodeInline") { decodeInline(descriptor) },
            output = output,
            level = level + 1,
            id,
        )

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = logValue(methodName = "decodeEnum") { decodeEnum(enumDescriptor) }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = logValue(methodName = "decodeNotNullMark", Decoder::decodeNotNullMark)

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = logValue(methodName = "decodeNull", Decoder::decodeNull)

    protected open fun <T> logValue(
        methodName: String,
        block: Decoder.() -> T,
    ): T =
        block(delegate).also {
            log(methodName = methodName, value = it)
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
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.decoding.logger.LoggerDecoder"
    }
}

public fun Decoder.logging(output: LoggerOutput = DefaultLoggerOutput(::println)): Decoder = LoggerDecoder(this, output)
