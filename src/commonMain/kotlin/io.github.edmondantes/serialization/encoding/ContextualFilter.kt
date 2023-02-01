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
package io.github.edmondantes.serialization.encoding

/**
 * This interface describe contextual filter which filtering depends on [value]
 *
 * **_WARNING_**. If you want to create a custom class that inherits this interface,
 * please create a custom serializer that inherits [ContextualFilterSerializer].
 * You can find example in classes [AllowContextualFilter] or [IgnoreContextualFilter]
 *
 * @param T type of contextual [value]
 * @see AllowContextualFilter
 * @see IgnoreContextualFilter
 * @see ContextualFilterSerializer
 */
public interface ContextualFilter<T : Any> {

    /**
     * Contextual value
     */
    public val value: T

    /**
     * Calculate whether the [UniqueEncoder] with [encoderId] can encode [value]
     * @return True if the [UniqueEncoder] with [encoderId] can encode [value], else false
     */
    public fun canEncodeWith(encoderId: String): Boolean
}
