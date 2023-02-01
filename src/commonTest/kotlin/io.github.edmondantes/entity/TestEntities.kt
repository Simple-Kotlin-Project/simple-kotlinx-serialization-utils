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
package io.github.edmondantes.entity

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import io.github.edmondantes.serialization.decoding.UniqueDecoder
import io.github.edmondantes.serialization.encoding.AllowContextualFilter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable
class SimpleTestEntity(
    val id: String,
    val name: String,
    val index: Int,
    val collection: List<String>,
)

@Serializable
class TestEntityWithNested(
    val id: String,
    val nested: SimpleTestEntity,
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
    override fun equals(other: Any?): Boolean =
        this === other || other is TestCircleEntityWithEquals && id == other.id

    override fun hashCode(): Int = 1
}

@Serializable
class TestDataCircleEntity(
    val id: String,
    var nested: TestDataCircleEntity?,
) {

    override fun equals(other: Any?): Boolean =
        this === other || other is TestDataCircleEntity && id == other.id

    override fun hashCode(): Int =
        id.hashCode()
}

@Serializable
class FilterEntity(
    val id: String,
    val name: String,
    @AllowEncoder(arrayOf("id2"))
    val password: String,
    @AllowEncoder(arrayOf("id3"))
    val personalData: String,
    @IgnoreEncoder(arrayOf("id1"))
    val transactionId: String,
    val nestedContextualFilteredEntity: ContextualFilterEntity?,
)

@Serializable(ContextualFilterEntitySerializer::class)
class ContextualFilterEntity(val id: String, val value: String)

class ContextualFilterEntitySerializer : KSerializer<ContextualFilterEntity> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("io.github.edmondantes.entity.ContextualFilterEntity") {}

    override fun deserialize(decoder: Decoder): ContextualFilterEntity {
        if (decoder !is UniqueDecoder) {
            throw SerializationException("Can not deserialize filtered value with non-unique decoder")
        }

        return ContextualFilterEntity(decoder.id, decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ContextualFilterEntity) {
        encoder.encodeSerializableValue(serializer(), AllowContextualFilter(value.value, allow = listOf(value.id)))
    }
}
