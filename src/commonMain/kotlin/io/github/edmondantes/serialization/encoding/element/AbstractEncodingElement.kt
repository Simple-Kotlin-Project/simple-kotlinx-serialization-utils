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
import io.github.edmondantes.serialization.util.DelegateAppendableWithIndent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

@OptIn(ExperimentalSerializationApi::class)
public abstract class AbstractEncodingElement<T>(
    override val elementType: EncodingElementType,
    override val elementValue: T,
    override val descriptorName: String? = null,
    parentDescriptor: SerialDescriptor? = null,
    final override val elementIndex: Int? = null,
) : EncodingElement<T> {

    override val elementName: String? = elementIndex?.let { parentDescriptor?.getElementName(it) }

    protected fun printHeader(appendable: AppendableWithIndent): AppendableWithIndent {
        appendable.append(elementType.name.lowercase())
        if (descriptorName != null) {
            appendable.append('(').append(descriptorName).append(')')
        }
        if (elementIndex != null) {
            appendable.append('@').append(elementIndex.toString())
        }
        if (elementName != null) {
            appendable.append('#').append(elementName)
        }

        appendable.append(" : ")

        return appendable
    }

    protected open fun equalsElementValueType(other: EncodingElement<*>): Boolean =
        elementValue?.let { a -> other.elementValue?.let { b -> a::class == b::class } ?: false }
            ?: (other.elementValue == null)

    protected open fun equalsElementValue(other: EncodingElement<*>): Boolean =
        elementValue == other.elementValue

    override fun toString(): String {
        val builder = StringBuilder()
        print(DelegateAppendableWithIndent(builder))
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncodingElement<*>) return false

        if (elementType != other.elementType) return false
        if (descriptorName != other.descriptorName) return false
        if (elementIndex != other.elementIndex) return false
        if (elementName != other.elementName) return false

        return equalsElementValueType(other) && equalsElementValue(other)
    }

    override fun hashCode(): Int {
        var result = elementType.hashCode()
        result = 31 * result + (elementValue?.hashCode() ?: 0)
        result = 31 * result + (descriptorName?.hashCode() ?: 0)
        result = 31 * result + (elementIndex ?: 0)
        result = 31 * result + (elementName?.hashCode() ?: 0)
        return result
    }
}
