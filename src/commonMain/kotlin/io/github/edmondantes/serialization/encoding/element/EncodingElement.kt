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

public interface EncodingElement<T> {
    public val elementType: EncodingElementType
    public val descriptorName: String?
    public val elementIndex: Int?
    public val elementName: String?
    public val elementValue: T

    public fun print(appendable: AppendableWithIndent)
}

public interface NotNullEncodingElement<T : Any> : EncodingElement<T>
public interface IterableEncodingElement<E, T : Iterable<E>?> : EncodingElement<T>
public interface NotNullIterableEncodingElement<E, T : Iterable<E>> :
    IterableEncodingElement<E, T>,
    NotNullEncodingElement<T>
