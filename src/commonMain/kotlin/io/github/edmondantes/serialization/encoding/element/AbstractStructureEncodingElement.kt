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

import kotlinx.serialization.descriptors.SerialDescriptor

public abstract class AbstractStructureEncodingElement(
    elementType: EncodingElementType,
    descriptorName: String,
    elementValue: List<EncodingElement<*>>,
    parentDescriptor: SerialDescriptor? = null,
    elementIndex: Int? = null,
) : AbstractEncodingElement<List<EncodingElement<*>>>(
    elementType,
    elementValue,
    descriptorName,
    parentDescriptor,
    elementIndex,
),
    IterableEncodingElement<EncodingElement<*>, List<EncodingElement<*>>> {

    public abstract class Builder<E : EncodingElement<*>, T : Builder<E, T>> {

        protected abstract val currentBuilder: T

        public var descriptorName: String? = null
        public var value: MutableList<EncodingElement<*>>? = null
        protected var futureValue: MutableList<() -> EncodingElement<*>> = mutableListOf()
        public var parentDescriptor: SerialDescriptor? = null
        public var elementIndex: Int? = null

        public fun descriptorName(name: String): T {
            descriptorName = name
            return currentBuilder
        }

        public fun value(value: List<EncodingElement<*>>): T {
            this.value = value.toMutableList()
            return currentBuilder
        }

        public fun add(element: EncodingElement<*>): T {
            if (value == null) {
                value = mutableListOf()
            }

            value!!.add(element)
            return currentBuilder
        }

        public fun add(block: () -> EncodingElement<*>): T {
            futureValue.add(block)
            return currentBuilder
        }

        public fun parentDescriptor(descriptor: SerialDescriptor?): T {
            parentDescriptor = descriptor
            return currentBuilder
        }

        public fun elementIndex(index: Int?): T {
            elementIndex = index
            return currentBuilder
        }

        public abstract fun build(): AbstractStructureEncodingElement

        protected fun beforeBuild() {
            if (value == null) {
                value = mutableListOf()
            }

            value!!.addAll(futureValue.map { it() })
        }
    }
}
