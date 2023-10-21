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

import io.github.edmondantes.serialization.util.NullableOptional
import io.github.edmondantes.serialization.util.nullableOptional

/**
 * Default implementation of [EncodedElement]
 * @see AbstractEncodedElement
 * @see EncodedElement
 */
public open class DefaultEncodedElement<T>(
    type: EncodedElementType,
    descriptorName: String?,
    name: String?,
    value: NullableOptional<T>,
) : AbstractEncodedElement<T, DefaultEncodedElement.Builder<T>>(type, descriptorName, name, value) {
    public constructor(type: EncodedElementType, descriptorName: String?, name: String?, value: T) : this(
        type,
        descriptorName,
        name,
        value.nullableOptional(),
    )

    public open class Builder<T> : AbstractEncodedElement.Builder<T, Builder<T>>() {
        override fun build(): EncodedElement<T> =
            DefaultEncodedElement(
                requireNotNull(type) { "EncodedElement type can not be null" },
                descriptorName,
                name,
                value,
            )

        override fun getBuilder(): Builder<T> = this
    }
}
