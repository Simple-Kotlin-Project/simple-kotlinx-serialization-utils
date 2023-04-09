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
package io.github.edmondantes.serialization.encoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to see how [Encoder] and [CompositeEncoder] will encode your object.
 * @param logger A logger function, which gets a logger message
 * @param id A unique id for this [Encoder]
 * @see Encoder
 * @see CompositeEncoder
 * @see UniqueEncoder
 * @see UniqueCompositeEncoder
 */
public open class LoggerEncoder(
    protected val logger: (String) -> Unit = ::println,
    override val id: String = "io.github.edmondantes.serialization.encoding.LoggerEncoder",
) : UniqueEncoder, UniqueCompositeEncoder {

    /**
     * Nesting level
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var level: Int = 0

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        log("beginCollection", descriptor)
        level++
        return this
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        log("beginStructure", descriptor)
        level++
        return this
    }

    @ExperimentalSerializationApi
    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean {
        log("shouldEncodeElementDefault", descriptor, index)
        return super.shouldEncodeElementDefault(descriptor, index)
    }

    @ExperimentalSerializationApi
    override fun encodeNotNullMark() {
        log("encodeNotNullMark")
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
        log("encodeNullableSerializableValue", value = value)
        if (value != null) {
            serializer.serialize(this, value)
        } else {
            encodeNull()
        }
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        log("encodeSerializableValue", value = value)
        serializer.serialize(this, value)
    }

    override fun encodeBoolean(value: Boolean) {
        log("encodeBoolean", value = value)
    }

    override fun encodeByte(value: Byte) {
        log("encodeByte", value = value)
    }

    override fun encodeChar(value: Char) {
        log("encodeChar", value = value)
    }

    override fun encodeDouble(value: Double) {
        log("encodeDouble", value = value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        log("encodeEnum", enumDescriptor, index)
    }

    override fun encodeFloat(value: Float) {
        log("encodeFloat", value = value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        log("encodeInline", descriptor)
        return this
    }

    override fun encodeInt(value: Int) {
        log("encodeInt", value = value)
    }

    override fun encodeLong(value: Long) {
        log("encodeLong", value = value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        log("encodeNull")
    }

    override fun encodeShort(value: Short) {
        log("encodeShort", value = value)
    }

    override fun encodeString(value: String) {
        log("encodeString", value = value)
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        log("encodeBooleanElement", descriptor, index, value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        log("encodeByteElement", descriptor, index, value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        log("encodeCharElement", descriptor, index, value)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        log("encodeDoubleElement", descriptor, index, value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        log("encodeFloatElement", descriptor, index, value)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        log("encodeInlineElement", descriptor, index)
        return this
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        log("encodeIntElement", descriptor, index, value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        log("encodeLongElement", descriptor, index, value)
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        log("encodeNullableSerializableElement", descriptor, index, value)
        if (value != null) {
            serializer.serialize(this, value)
        } else {
            encodeNull()
        }
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

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        log("encodeShortElement", descriptor, index, value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        log("encodeStringElement", descriptor, index, value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        log("endStructure", descriptor)
        level--
    }

    /**
     * This function construct a logger message and call [logger] function
     * @param methodName Name of method which was called
     * @param descriptor [SerialDescriptor] is for encoding object (It is optional)
     * @param index Index of encoding field (It is optional)
     * @param value Encoding value
     */
    @OptIn(ExperimentalSerializationApi::class)
    protected fun log(
        methodName: String,
        descriptor: SerialDescriptor? = null,
        index: Int? = null,
        value: Any? = null,
    ) {
        logger(
            buildString {
                append("[LoggerEncoder][level=$level] call method ").append(methodName)

                if (descriptor != null) {
                    append(" for descriptor ")
                    append(descriptor.serialName)
                    if (index != null) {
                        append("::").append(descriptor.getElementName(index))
                    }
                }

                if (value != null) {
                    append(" with value = ").append(value)
                }
            },
        )
    }
}
