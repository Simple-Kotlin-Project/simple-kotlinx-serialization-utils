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
package io.github.edmondantes.serialization.encoding.inline

import io.github.edmondantes.serialization.annotation.InlineSerialization
import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import io.github.edmondantes.serialization.getElementAllAnnotation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder

public class InlineCompositeEncoder(
    private val delegate: CompositeEncoder,
    private val inlineEncoder: InlineEncoder,
    private val endStructureHandler: () -> Boolean,
) : CompositeEncoder by delegate {

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        inlineDelegate(descriptor, index, serializer, value, delegate::encodeNullableSerializableElement)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        inlineDelegate(descriptor, index, serializer, value) { _, _, inlineSerializer, _ ->
            delegate.encodeSerializableElement(descriptor, index, inlineSerializer as SerializationStrategy<T>, value)
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (endStructureHandler()) {
            delegate.endStructure(descriptor)
        }
    }

    private inline fun <T : Any?> inlineDelegate(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
        delegate: (SerialDescriptor, Int, SerializationStrategy<T>, T?) -> Unit,
    ) {
        val isInline = descriptor.getElementAllAnnotation(index).filterIsInstance<InlineSerialization>().isNotEmpty()

        delegate(
            descriptor,
            index,
            CustomSerializationStrategy(serializer) {
                inlineEncoder.changeEncoder(it, isInline)
            },
            value,
        )
    }
}
