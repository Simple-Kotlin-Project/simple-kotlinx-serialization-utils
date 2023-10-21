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
@file:OptIn(ExperimentalSerializationApi::class)

package io.github.edmondantes.serialization.element

import io.github.edmondantes.serialization.util.AppendableWithIndent
import io.github.edmondantes.serialization.util.NullableOptional
import io.github.edmondantes.serialization.util.nullableEmpty
import io.github.edmondantes.serialization.util.nullableOptional
import io.github.edmondantes.serialization.util.tryGetElementDescriptor
import io.github.edmondantes.serialization.util.tryGetElementName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

public typealias AnyEncodedElement = EncodedElement<Any?>
public typealias EncodedElementBuilder<T> = EncodedElement.Builder<T, *>
public typealias AnyEncodedElementBuilder = EncodedElementBuilder<Any?>

/**
 * Represent encoded value of class [T]
 * @property type Type of encoded element
 * @property descriptorName Descriptor name of encoded element (_optional_)
 * @property name Name of element in parent (_optional_)
 * @property value Value of element.
 * If [type] is [EncodedElementType.STRUCTURE] or [EncodedElementType.COLLECTION] then [value] is [List] of [AnyEncodedElement]
 */
public interface EncodedElement<out T> {
    public val type: EncodedElementType
    public val descriptorName: String?
    public val name: String?
    public val value: NullableOptional<T>

    /**
     * Print encoded element structure with indents
     */
    public fun printTo(appendable: AppendableWithIndent)

    /**
     * Generic builder of [EncodedElement]
     * @param [T] type of [EncodedElement] value
     * @param [B] type of Builder, that will be returns from each setup methods
     * @property type Type of encoded element
     * @property descriptorName Descriptor name of encoded element (_optional_)
     * @property name Name of element in parent (_optional_)
     * @property value Value of element. (_optional_)
     * If [type] is [EncodedElementType.STRUCTURE] or [EncodedElementType.COLLECTION] then [value] is [List] of [AnyEncodedElement]
     */
    public abstract class Builder<T, B : Builder<T, B>> {
        public var type: EncodedElementType? = null
        public var descriptorName: String? = null
        public var name: String? = null
        public var value: NullableOptional<T> = nullableEmpty()

        /**
         * Create empty builder
         */
        public constructor()

        /**
         * Create builder from [element]
         */
        public constructor(element: EncodedElement<T>) {
            copy(element)
        }

        /**
         * Auto-setting [type]
         * @param isNull true if value is null, else false
         * @param parentType [EncodedElementType] of element's parent
         */
        public fun type(
            isNull: Boolean,
            parentType: EncodedElementType?,
        ): B =
            type(
                if (isNull) {
                    EncodedElementType.NULL
                } else {
                    EncodedElementType.getSimpleElementEncodedType(parentType)
                },
            )

        public fun type(type: EncodedElementType): B =
            builder {
                this.type = type
            }

        public fun descriptorName(descriptorName: String?): B =
            builder {
                this.descriptorName = descriptorName
            }

        /**
         * Sets [descriptorName] from [SerialDescriptor]
         */
        public fun descriptorName(descriptor: SerialDescriptor?): B =
            builder {
                descriptorName(descriptor?.serialName)
            }

        /**
         * Sets [descriptorName] from parent's [SerialDescriptor] and [indexInParent]
         */
        public fun descriptorName(
            parentDescriptor: SerialDescriptor?,
            indexInParent: Int?,
        ): B =
            builder {
                descriptorName = parentDescriptor.tryGetElementDescriptor(indexInParent)?.serialName
            }

        public fun name(name: String?): B =
            builder {
                this.name = name
            }

        /**
         * Sets element [name] from parent's [SerialDescriptor] and [indexInParent]
         */
        public fun name(
            parentDescriptor: SerialDescriptor?,
            indexInParent: Int?,
        ): B =
            builder {
                name = parentDescriptor.tryGetElementName(indexInParent)
            }

        @Suppress("UNCHECKED_CAST")
        public fun value(value: T): B =
            builder {
                this.value =
                    if (value is Enum<*>) {
                        value.name as T
                    } else {
                        value
                    }.nullableOptional()
            }

        /**
         * Sets [value] from [NullableOptional]
         */
        public fun valueByOptional(nullableOptional: NullableOptional<T>): B =
            builder {
                this.value = nullableOptional
            }

        /**
         * Copy values from [EncodedElement]
         */
        public fun copy(element: EncodedElement<T>) {
            this.type = element.type
            this.descriptorName = element.descriptorName
            this.name = element.name
            this.value = element.value
        }

        /**
         * Copy values from builder [B]
         */
        public fun copy(builder: B) {
            this.type = builder.type
            this.descriptorName = builder.descriptorName
            this.name = builder.name
            this.value = builder.value
        }

        /**
         * Build [EncodedElement]
         */
        public abstract fun build(): EncodedElement<T>

        protected open fun builder(block: B.() -> Unit): B {
            val builder: B = getBuilder()
            block(builder)
            return builder
        }

        /**
         * Returns current builder of type [B]
         */
        protected abstract fun getBuilder(): B
    }
}
