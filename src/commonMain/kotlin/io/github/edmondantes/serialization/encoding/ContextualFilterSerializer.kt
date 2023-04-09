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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [ContextualFilter]
 * @see ContextualFilter
 * @see AllowContextualFilter
 * @see IgnoreContextualFilter
 * @see AllowContextualFilterSerializer
 * @see IgnoreContextualFilterSerializer
 */
public abstract class ContextualFilterSerializer<T : Any, F : ContextualFilter<T>>(protected val dataSerializer: KSerializer<T>) :
    KSerializer<F> {
    override val descriptor: SerialDescriptor = dataSerializer.descriptor
}

/**
 * Serializer for [AllowContextualFilter]
 * @see ContextualFilter
 * @see AllowContextualFilter
 * @see IgnoreContextualFilter
 * @see AllowContextualFilterSerializer
 * @see IgnoreContextualFilterSerializer
 */
public class AllowContextualFilterSerializer<T : Any>(dataSerializer: KSerializer<T>) :
    ContextualFilterSerializer<T, AllowContextualFilter<T>>(dataSerializer) {

    override fun deserialize(decoder: Decoder): AllowContextualFilter<T> =
        AllowContextualFilter(dataSerializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: AllowContextualFilter<T>) {
        encoder.encodeSerializableValue(dataSerializer, value.value)
    }
}

/**
 * Serializer for [IgnoreContextualFilter]
 * @see ContextualFilter
 * @see AllowContextualFilter
 * @see IgnoreContextualFilter
 * @see AllowContextualFilterSerializer
 * @see IgnoreContextualFilterSerializer
 */
public class IgnoreContextualFilterSerializer<T : Any>(dataSerializer: KSerializer<T>) :
    ContextualFilterSerializer<T, IgnoreContextualFilter<T>>(dataSerializer) {

    override fun deserialize(decoder: Decoder): IgnoreContextualFilter<T> =
        IgnoreContextualFilter(dataSerializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: IgnoreContextualFilter<T>) {
        encoder.encodeSerializableValue(dataSerializer, value.value)
    }
}
