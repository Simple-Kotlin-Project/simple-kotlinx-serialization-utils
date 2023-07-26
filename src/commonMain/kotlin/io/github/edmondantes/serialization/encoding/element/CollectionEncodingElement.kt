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
package io.github.edmondantes.serialization.encoding.element

import io.github.edmondantes.serialization.util.AppendableWithIndent
import kotlinx.serialization.descriptors.SerialDescriptor

public class CollectionEncodingElement(
    descriptorName: String,
    elementValue: List<EncodingElement<*>>,
    parentDescriptor: SerialDescriptor? = null,
    elementIndex: Int? = null,
) : AbstractStructureEncodingElement(
    EncodingElementType.COLLECTION,
    descriptorName,
    elementValue,
    parentDescriptor,
    elementIndex,
) {

    override fun print(appendable: AppendableWithIndent) {
        printHeader(appendable).append("[").withIdent {
            elementValue.forEachIndexed { index, element ->
                if (index > 0) {
                    it.append(',')
                }
                it.append('\n')
                element.print(it)
            }
        }

        if (elementValue.isNotEmpty()) {
            appendable.append('\n')
        }

        appendable.append(']')
    }

    public open class Builder : AbstractStructureEncodingElement.Builder<CollectionEncodingElement, Builder>() {
        override val currentBuilder: Builder
            get() = this

        override fun build(): CollectionEncodingElement {
            beforeBuild()
            return CollectionEncodingElement(
                requireNotNull(descriptorName),
                requireNotNull(value),
                parentDescriptor,
                elementIndex,
            )
        }
    }
}
