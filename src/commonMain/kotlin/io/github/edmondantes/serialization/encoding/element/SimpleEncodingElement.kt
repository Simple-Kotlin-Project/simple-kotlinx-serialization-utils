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

public class SimpleEncodingElement<T>(
    elementType: EncodingElementType,
    elementValue: T,
    parentDescriptor: SerialDescriptor? = null,
    elementIndex: Int? = null,
) : AbstractEncodingElement<T>(
    elementType = elementType,
    elementValue = elementValue,
    parentDescriptor = parentDescriptor,
    elementIndex = elementIndex,
) {
    override fun print(appendable: AppendableWithIndent) {
        printHeader(appendable).append(elementValue.toString())
    }

    public open class Builder<T>(private val value: T) {
        public var elementType: EncodingElementType? = null
        public var parentDescriptor: SerialDescriptor? = null
        public var elementIndex: Int? = null

        public fun elementType(type: EncodingElementType): Builder<T> = apply {
            elementType = type
        }

        public fun parentDescriptor(descriptor: SerialDescriptor?): Builder<T> = apply {
            parentDescriptor = descriptor
        }

        public fun elementIndex(index: Int?): Builder<T> = apply {
            elementIndex = index
        }

        public open fun build(): SimpleEncodingElement<T> =
            SimpleEncodingElement(
                requireNotNull(elementType),
                value,
                parentDescriptor,
                elementIndex,
            )
    }
}
