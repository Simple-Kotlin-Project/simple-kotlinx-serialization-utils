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
import io.github.edmondantes.serialization.encoding.element.EncodingElementType
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.ELEMENT
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.NULL
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.PROPERTY
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.STRUCTURE
import io.github.edmondantes.serialization.encoding.element.SimpleEncodingElement
import kotlinx.serialization.descriptors.SerialDescriptor

public abstract class AbstractElementFactory(protected val parentType: EncodingElementType?) : ElementFactory {

    override fun structure(block: StructureEncodingElementBuilder.() -> Unit): EncodingElement<*> =
        prepare { descriptor, childDescriptor, parentDescriptor, index ->
            DefaultStructureEncodingElementBuilder(
                descriptor ?: error("Can not construct structure without descriptor"),
                childDescriptor,
                parentDescriptor,
                index,
            )
                .also(block)
                .build()
        }

    override fun collection(block: CollectionEncodingElementBuilder.() -> Unit): EncodingElement<*> =
        prepare { descriptor, childDescriptor, parentDescriptor, index ->
            DefaultCollectionEncodingElementBuilder(
                descriptor ?: error("Can not construct collection without descriptor"),
                childDescriptor,
                parentDescriptor,
                index,
            )
                .also(block)
                .build()
        }

    override fun value(value: Any?): EncodingElement<*> {
        return prepare { _, childDescriptor, parent, index ->
            if (value == null) {
                SimpleEncodingElement(NULL, value, childDescriptor ?: parent, index)
            } else {
                SimpleEncodingElement(
                    if (parentType == STRUCTURE) PROPERTY else ELEMENT,
                    if (value is Enum<*>) value.name else value,
                    childDescriptor ?: parent,
                    index,
                )
            }
        }
    }

    protected abstract fun <T : EncodingElement<*>> prepare(block: (SerialDescriptor?, SerialDescriptor?, SerialDescriptor?, Int?) -> T): T
}
