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

import io.github.edmondantes.serialization.encoding.element.CollectionEncodingElement
import io.github.edmondantes.serialization.encoding.element.EncodingElement
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.STRUCTURE
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor

@OptIn(ExperimentalSerializationApi::class)
public open class DefaultCollectionEncodingElementBuilder(
    protected val descriptor: SerialDescriptor,
    protected val childDescriptor: SerialDescriptor? = null,
    parentDescriptor: SerialDescriptor? = null,
    indexInParent: Int? = null,
) : CollectionEncodingElementBuilder {

    private val builder = CollectionEncodingElement.Builder()

    init {
        builder.descriptorName = descriptor.serialName

        if (parentDescriptor != null && indexInParent != null) {
            builder.parentDescriptor = parentDescriptor
            builder.elementIndex = indexInParent
        }
    }

    override fun element(elementDescriptor: SerialDescriptor?): ElementFactory =
        childPrepare { currentDescriptor, index ->
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
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
    ): ElementFactory = childPrepare { currentDescriptor, index ->
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
        elementDescriptor: SerialDescriptor?,
        block: ElementFactory.() -> EncodingElement<*>,
    ): CollectionEncodingElementBuilder = apply {
        childPrepare { currentDescriptor, index ->
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
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
        block: ElementFactory.() -> EncodingElement<*>,
    ): CollectionEncodingElementBuilder = apply {
        childPrepare { currentDescriptor, index ->
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

    override fun build(): CollectionEncodingElement =
        builder.build()

    private fun <T> childPrepare(block: (SerialDescriptor?, Int) -> T): T {
        val index = builder.value?.size ?: 0
        val currentDescriptor = childDescriptor ?: descriptor
        return block(if (currentDescriptor.kind is PrimitiveKind) null else currentDescriptor.getElementDescriptor(index), index)
    }
}
