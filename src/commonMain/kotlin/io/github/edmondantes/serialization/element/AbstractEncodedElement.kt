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
package io.github.edmondantes.serialization.element

import io.github.edmondantes.serialization.util.AppendableWithIndent
import io.github.edmondantes.serialization.util.NullableOptional
import io.github.edmondantes.serialization.util.newLine
import io.github.edmondantes.serialization.util.nullableEmpty

/**
 * Abstract implementation of [EncodedElement]
 * @see DefaultEncodedElement
 * @see EncodedElement
 */
public abstract class AbstractEncodedElement<T, B : AbstractEncodedElement.Builder<T, B>>(
    override val type: EncodedElementType,
    override val descriptorName: String? = null,
    override val name: String? = null,
    override val value: NullableOptional<T> = nullableEmpty(),
) : EncodedElement<T> {
    override fun printTo(appendable: AppendableWithIndent) {
        appendable.append(type.name.lowercase())
        if (descriptorName != null) {
            appendable.append('(').append(descriptorName).append(')')
        }
        if (name != null) {
            appendable.append('#').append(name)
        }

        printValueTo(appendable)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun printValueTo(appendable: AppendableWithIndent) {
        if (value.isEmpty) {
            return
        }

        appendable.append(" : ")

        val iter =
            value.value.run {
                when (this) {
                    is Iterable<*> -> iterator()
                    is Sequence<*> -> iterator()
                    is Array<*> -> iterator()
                    else -> {
                        appendable.append(value.value.toString())
                        return
                    }
                }
            }

        val element = if (!iter.hasNext()) null else iter.next() as? AnyEncodedElement
        if (element == null) {
            appendable.append(value.value.toString())
            return
        }

        appendable.withIdent {
            it.newLine()
            element.printTo(it)
            (iter as Iterator<AnyEncodedElement>).forEach { element ->
                it.newLine()
                element.printTo(it)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEncodedElement<*, *>) return false

        if (type != other.type) return false
        if (descriptorName != other.descriptorName) return false
        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (descriptorName?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + value.hashCode()
        return result
    }

    public abstract class Builder<T, B : EncodedElement.Builder<T, B>> : EncodedElement.Builder<T, B>()
}
