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
@file:Suppress("UNUSED")

package io.github.edmondantes.util

import io.github.edmondantes.serialization.decoding.element.ElementDecoder
import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.AnyEncodedElementBuilder
import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.serializer

private typealias ConfigureEncoder = (AnyEncodedElementBuilder) -> ElementEncoder
private typealias SerializationBlock = (ElementEncoder) -> Unit
private typealias CheckBlock<T> = CheckContext<T>.() -> Unit

private typealias ConfigureDecoder = (AnyEncodedElement) -> Decoder
private typealias DeserializationBlock = () -> AnyEncodedElement

open class CheckContext<T>(val actual: T)

class SerializableTestTemplate {
    private var configureEncoder: ConfigureEncoder = { ElementEncoder(it, null) }
    private var serializationBlock: SerializationBlock? = null
    private var checkBlock: CheckBlock<AnyEncodedElement> = {}

    fun configureEncoder(block: ConfigureEncoder) {
        this.configureEncoder = block
    }

    fun serialization(block: SerializationBlock) {
        this.serializationBlock = block
    }

    fun check(block: CheckBlock<AnyEncodedElement>) {
        this.checkBlock = block
    }

    fun run(block: SerializableTest.() -> Unit) =
        serializableTest {
            configureEncoder(configureEncoder)
            serializationBlock?.let(::serialization)
            check(checkBlock)
            block()
        }
}

class SerializableTest {
    private var configureEncoder: ConfigureEncoder = { ElementEncoder(it, null) }
    private var serializationBlock: SerializationBlock? = null
    private var checkBlock: CheckBlock<AnyEncodedElement> = {}

    fun configureEncoder(block: ConfigureEncoder) {
        this.configureEncoder = block
    }

    fun serialization(block: SerializationBlock) {
        this.serializationBlock = block
    }

    fun check(block: CheckBlock<AnyEncodedElement>) {
        this.checkBlock = block
    }

    fun start() {
        val serialization = serializationBlock ?: error("Block 'serialization' wasn't set")

        val builder = DefaultEncodedElement.Builder<Any?>()
        val encoder = configureEncoder(builder)
        serialization(encoder)
        checkBlock(CheckContext(builder.build()))
    }
}

private typealias ConfigureMultiplyEncoder<K> = (K, AnyEncodedElementBuilder) -> ElementEncoder
private typealias MultiplySerializationBlock<K> = (K, ElementEncoder) -> Unit
private typealias MultiplyCheckBlock<K, T> = MultiplyCheckContext<K, T>.() -> Unit

class MultiplyCheckContext<K, T>(val key: K, value: T) : CheckContext<T>(value)

class MultiplySerializableTestTemplate<T> {
    private var configureEncoder: ConfigureMultiplyEncoder<T> = { _, it -> ElementEncoder(it) }
    private var serializationBlock: MultiplySerializationBlock<T>? = null
    private var checkBlock: MultiplyCheckBlock<T, AnyEncodedElement> = { }

    fun configureEncoder(block: ConfigureMultiplyEncoder<T>) {
        this.configureEncoder = block
    }

    fun configureEncoder(block: ConfigureEncoder) {
        this.configureEncoder = { _, it -> block(it) }
    }

    fun serialization(block: MultiplySerializationBlock<T>) {
        this.serializationBlock = block
    }

    fun serialization(block: SerializationBlock) {
        this.serializationBlock = { _, it -> block(it) }
    }

    fun check(block: MultiplyCheckBlock<T, AnyEncodedElement>) {
        this.checkBlock = block
    }

    fun checkAll(block: CheckBlock<AnyEncodedElement>) {
        this.checkBlock = { block(this) }
    }

    fun run(
        sequence: Sequence<T>,
        block: MultiplySerializableTest<T>.() -> Unit,
    ) = multiplySerializableTest(sequence) {
        configureEncoder(configureEncoder)
        serializationBlock?.let(::serialization)
        check(checkBlock)
        block()
    }
}

class MultiplySerializableTest<T>(private val sequence: Sequence<T>) {
    private var configureEncoder: ConfigureMultiplyEncoder<T> = { _, it -> ElementEncoder(it) }
    private var serializationBlock: MultiplySerializationBlock<T>? = null
    private var checkBlock: MultiplyCheckBlock<T, AnyEncodedElement> = { }

    fun configureEncoder(block: ConfigureMultiplyEncoder<T>) {
        this.configureEncoder = block
    }

    fun configureEncoderAll(block: ConfigureEncoder) {
        this.configureEncoder = { _, it -> block(it) }
    }

    fun serialization(block: MultiplySerializationBlock<T>) {
        this.serializationBlock = block
    }

    fun serializationAll(block: SerializationBlock) {
        this.serializationBlock = { _, it -> block(it) }
    }

    fun check(block: MultiplyCheckBlock<T, AnyEncodedElement>) {
        this.checkBlock = block
    }

    fun checkAll(block: CheckBlock<AnyEncodedElement>) {
        this.checkBlock = { block(this) }
    }

    fun start() {
        val serialization = serializationBlock ?: error("Block 'serialization' wasn't set")

        sequence.forEach {
            val builder = DefaultEncodedElement.Builder<Any?>()
            val encoder = configureEncoder(it, builder)
            serialization(it, encoder)
            checkBlock(MultiplyCheckContext(it, builder.build()))
        }
    }
}

class DeserializableTestTemplate<T> {
    private var configureDecoder: ConfigureDecoder? = null
    private var deserializationBlock: DeserializationBlock? = null
    private var checkBlock: CheckBlock<T>? = null

    fun configureDecoder(block: ConfigureDecoder) {
        this.configureDecoder = block
    }

    fun deserialization(block: DeserializationBlock) {
        this.deserializationBlock = block
    }

    fun check(block: CheckBlock<T>) {
        this.checkBlock = block
    }

    fun <R : T> run(
        deserializer: DeserializationStrategy<R>,
        block: DeserializableTest<R>.() -> Unit,
    ) = deserializableTest(deserializer) {
        configureDecoder?.let { configureDecoder(it) }
        deserializationBlock?.let { deserialization(it) }
        checkBlock?.let { check(it) }

        block(this)
    }

    inline fun <reified R : T> run(noinline block: DeserializableTest<R>.() -> Unit) = run(serializer<R>(), block)
}

class DeserializableTest<T>(private val deserializer: DeserializationStrategy<T>) {
    private var configureDecoder: ConfigureDecoder = { ElementDecoder(it, null) }
    private var deserializationBlock: DeserializationBlock? = null
    private var checkBlock: CheckBlock<T> = {}

    fun configureDecoder(block: ConfigureDecoder) {
        this.configureDecoder = block
    }

    fun deserialization(block: DeserializationBlock) {
        this.deserializationBlock = block
    }

    fun check(block: CheckBlock<T>) {
        this.checkBlock = block
    }

    fun start() {
        log("\n------------------------")
        val deserialization = deserializationBlock ?: error("Block 'deserialization' wasn't set")
        val decoder = configureDecoder(deserialization())
        checkBlock(CheckContext(deserializer.deserialize(decoder)))
        log("------------------------\n")
    }
}

inline fun serializableTest(block: SerializableTest.() -> Unit) {
    SerializableTest().also(block).start()
}

inline fun serializableTestTemplate(block: SerializableTestTemplate.() -> Unit): SerializableTestTemplate =
    SerializableTestTemplate().also(block)

inline fun <T> multiplySerializableTest(
    sequence: Sequence<T>,
    block: MultiplySerializableTest<T>.() -> Unit,
) {
    MultiplySerializableTest(sequence).also(block).start()
}

fun <T> multiplySerializableTemplate(block: MultiplySerializableTestTemplate<T>.() -> Unit): MultiplySerializableTestTemplate<T> =
    MultiplySerializableTestTemplate<T>().also(block)

inline fun <T> deserializableTest(
    deserializer: DeserializationStrategy<T>,
    block: DeserializableTest<T>.() -> Unit,
) {
    DeserializableTest(deserializer).also(block).start()
}

inline fun <reified T> deserializableTest(block: DeserializableTest<T>.() -> Unit) = deserializableTest(serializer<T>(), block)

inline fun <T> deserializableTestTemplate(block: DeserializableTestTemplate<T>.() -> Unit): DeserializableTestTemplate<T> =
    DeserializableTestTemplate<T>().also(block)
