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
package io.github.edmondantes.serialization.encoding.filter

import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Functional interface for filtering the serialization of objects based on value.
 * Will called only if encoder can get value of object
 */
public interface ContextualSerializationFilter {
    /**
     * Filter element for encoding by [parentDescriptor], [indexInParent] and [value]
     * @param parentDescriptor [SerialDescriptor]'s of parent
     * @param indexInParent index in parent [SerialDescriptor]
     * @param value current value for filtering
     * @return true to allow serialization, false to prohibits serialization of current element
     */
    public fun filter(
        parentDescriptor: SerialDescriptor,
        indexInParent: Int,
        value: Any?,
    ): Boolean

    public companion object {
        /**
         * Object of [ContextualSerializationFilter] that allows to serializer all element
         */
        public val SKIP: ContextualSerializationFilter =
            object : ContextualSerializationFilter {
                override fun filter(
                    parentDescriptor: SerialDescriptor,
                    indexInParent: Int,
                    value: Any?,
                ) = true
            }

        /**
         * Object of [ContextualSerializationFilter] that prohibits to serializer all element
         */
        public val FORBID: ContextualSerializationFilter =
            object : ContextualSerializationFilter {
                override fun filter(
                    parentDescriptor: SerialDescriptor,
                    indexInParent: Int,
                    value: Any?,
                ) = false
            }
    }
}
