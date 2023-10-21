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
 * Functional interface for filtering the serialization of objects based on compile-time information
 */
public interface SerializationFilter {
    /**
     * Filter element by its [SerialDescriptor]
     * @param descriptor current element's [SerialDescriptor]
     * @return true to allow serialization, false to prohibits serialization of current element
     */
    public fun filter(descriptor: SerialDescriptor): Boolean

    /**
     * Filter element by parent [SerialDescriptor]
     * @param parentDescriptor element's parent's [SerialDescriptor]
     * @param indexInParent element's index in parent
     * @return true to allow serialization, false to prohibits serialization of current element
     */
    public fun filter(
        parentDescriptor: SerialDescriptor,
        indexInParent: Int,
    ): Boolean

    public companion object {
        /**
         * Object of [SerializationFilter] that allows to serializer all element
         */
        public val SKIP: SerializationFilter =
            object : SerializationFilter {
                override fun filter(descriptor: SerialDescriptor): Boolean = true

                override fun filter(
                    parentDescriptor: SerialDescriptor,
                    indexInParent: Int,
                ): Boolean = true
            }

        /**
         * Object of [SerializationFilter] that prohibits to serializer all element
         */
        public val FORBID: SerializationFilter =
            object : SerializationFilter {
                override fun filter(descriptor: SerialDescriptor): Boolean = false

                override fun filter(
                    parentDescriptor: SerialDescriptor,
                    indexInParent: Int,
                ): Boolean = false
            }
    }
}
