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

package io.github.edmondantes.serialization.entity

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import io.github.edmondantes.serialization.annotation.InlineSerialization
import io.github.edmondantes.serialization.annotation.SerializationFormat
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.jvm.JvmInline

@Serializable
data class TestSimpleEntity(
    val id: String,
    val name: String,
    val index: Int,
    val collection: List<String>,
)

@Serializable
data class TestEntityWithNested(
    val id: String,
    val nested: TestSimpleEntity,
)

@Serializable
class TestCircleEntity(
    val id: String,
    var nested: TestCircleEntity?,
)

@Serializable
class TestCircleEntityWithEquals(
    val id: String,
    var nested: TestCircleEntityWithEquals?,
) {
    override fun equals(other: Any?): Boolean = this === other || other is TestCircleEntityWithEquals && id == other.id

    override fun hashCode(): Int = 1
}

@Serializable
class TestDataCircleEntity(
    val id: String,
    var nested: TestDataCircleEntity?,
) {
    override fun equals(other: Any?): Boolean = this === other || other is TestDataCircleEntity && id == other.id

    override fun hashCode(): Int = id.hashCode()
}

@Serializable
data class TestFilterEntity(
    val id: String,
    val name: String,
    @AllowEncoder(["id2"])
    val password: String,
    @AllowEncoder(["id3"])
    val personalData: String,
    @IgnoreEncoder(["id1"])
    val transactionId: String,
)

@Serializable
data class TestEntityWithInlineProperty(
    val notInline: String,
    @InlineSerialization
    val inlineProperty: TestSimpleEntity,
)

@Serializable
data class TestEntityWithInlinePropertyWithSameName(
    @InlineSerialization
    val id: TestSimpleEntity,
)

@Serializable
class TestEntityWithInlineClass(
    val notInline: String,
    val inlineClass: TestInlineEntity,
)

@Serializable
@InlineSerialization
class TestInlineEntity(
    val id: String,
    val name: String,
)

@Serializable
class TestEntityWithFormatProperties(
    val id: String,
    @SerializationFormat("test")
    val nested: TestSimpleEntity? = null,
    @SerializationFormat("test2")
    val test: String? = null,
)

@Serializable
class TestEntityWithNestedEntityWithFormatProperties(
    val id: String,
    val nested: TestEntityWithFormatProperties,
)

@Serializable
enum class TestEnum {
    A,
    B,
    C,
}

@Serializable
data class TestEntityWithEnum(
    val id: String,
    val enum: TestEnum,
)

@JvmInline
@Serializable
value class TestEntityJvmInline(
    @AllowEncoder(allow = ["id1"]) val value: String,
)

@Serializable
class TestEntityWithJvmInlineEntity(val inline: TestEntityJvmInline)

class TestStringFormat : StringFormat {
    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun <T : Any?> encodeToString(
        serializer: SerializationStrategy<T>,
        value: T,
    ): String = value?.toString() ?: "null"

    override fun <T> decodeFromString(
        deserializer: DeserializationStrategy<T>,
        string: String,
    ): T {
        error("This format doesn't support decoding")
    }
}

class TestBinaryFormat : BinaryFormat {
    private var index: UInt = 1u
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun <T> encodeToByteArray(
        serializer: SerializationStrategy<T>,
        value: T,
    ): ByteArray {
        val tmp = (index++)
        return byteArrayOf(
            tmp.and(0xff000000u).toByte(),
            tmp.and(0xff0000u).toByte(),
            tmp.and(0xff00u).toByte(),
            tmp.and(0xffu).toByte(),
        )
    }

    override fun <T> decodeFromByteArray(
        deserializer: DeserializationStrategy<T>,
        bytes: ByteArray,
    ): T {
        error("This format doesn't support decoding")
    }
}
