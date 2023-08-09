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
package io.github.edmondantes.serialization.encoding.element.factory

import io.github.edmondantes.serialization.encoding.element.EncodingElement
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.STRUCTURE
import io.github.edmondantes.serialization.encoding.element.StructureEncodingElement
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor

@OptIn(ExperimentalSerializationApi::class)
public open class DefaultStructureEncodingElementBuilder(
    protected val descriptor: SerialDescriptor,
    protected val childDescriptor: SerialDescriptor? = null,
    parentDescriptor: SerialDescriptor? = null,
    indexInParent: Int? = null,
) : StructureEncodingElementBuilder {

    private val builder = StructureEncodingElement.Builder()

    init {
        builder.descriptorName = descriptor.serialName

        if (parentDescriptor != null && indexInParent != null) {
            builder.parentDescriptor = parentDescriptor
            builder.elementIndex = indexInParent
        }
    }

    override fun element(name: String, elementDescriptor: SerialDescriptor?): ElementFactory =
        childPrepare(name) { currentDescriptor, index ->
            CallbackElementFactory(
                elementDescriptor ?: currentDescriptor,
                STRUCTURE,
                null,
                childDescriptor ?: descriptor,
                index,
            ) {
                builder.add(it)
            }
        }

    override fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
    ): ElementFactory = childPrepare(name) { currentDescriptor, index ->
        CallbackElementFactory(
            elementDescriptor ?: currentDescriptor,
            STRUCTURE,
            childDescriptor,
            this.childDescriptor ?: descriptor,
            index,
        ) {
            builder.add(it)
        }
    }

    override fun element(
        name: String,
        elementDescriptor: SerialDescriptor?,
        block: ElementFactory.() -> EncodingElement<*>,
    ): StructureEncodingElementBuilder = apply {
        childPrepare(name) { currentDescriptor, index ->
            val factory = DescriptorElementFactory(
                elementDescriptor ?: currentDescriptor,
                STRUCTURE,
                null,
                childDescriptor ?: descriptor,
                index,
            )
            builder.add(factory.block())
        }
    }

    override fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
        block: ElementFactory.() -> EncodingElement<*>,
    ): StructureEncodingElementBuilder = apply {
        childPrepare(name) { currentDescriptor, index ->
            val factory = DescriptorElementFactory(
                elementDescriptor ?: currentDescriptor,
                STRUCTURE,
                childDescriptor,
                this.childDescriptor ?: descriptor,
                index,
            )
            builder.add(factory.block())
        }
    }

    override fun switch(
        descriptor: SerialDescriptor,
        block: StructureEncodingElementBuilder.() -> Unit,
    ): StructureEncodingElementBuilder {
        DefaultStructureEncodingElementBuilder(descriptor, null, null, null).apply(block).builder.value.also {
            if (builder.value == null) {
                builder.value = it
            } else if (it != null) {
                builder.value?.addAll(it)
            }
        }
        return this
    }

    override fun build(): StructureEncodingElement =
        builder.build()

    private fun <T> childPrepare(
        name: String,
        block: (SerialDescriptor?, Int) -> T,
    ): T {
        val currDescriptor = childDescriptor ?: descriptor
        val index = currDescriptor.getElementIndex(name)
        return block(
            if (currDescriptor.kind is PrimitiveKind) null else currDescriptor.getElementDescriptor(index),
            index,
        )
    }
}
