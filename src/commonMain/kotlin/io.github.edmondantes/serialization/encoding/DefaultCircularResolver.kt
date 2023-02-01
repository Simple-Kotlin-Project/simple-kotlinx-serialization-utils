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
 * Default implementation of [CircularResolver]
 * @see CircularResolver
 * @see CircularEncoder
 */
public class DefaultCircularResolver internal constructor(
    private val withoutHashCode: MutableList<Any>?,
    private val withHashCode: MutableMap<Int, MutableList<Any>>?,
    private val useRefEquality: Boolean,
) : CircularResolver {

    /**
     * @param byHashCode If true, [DefaultCircularResolver] will be use object's hash codes for searching same objects
     * @param useRefEquality If false, [DefaultCircularResolver] will use method 'equals' for determining equality
     */
    public constructor(
        byHashCode: Boolean = true,
        useRefEquality: Boolean = false,
    ) : this(if (byHashCode) null else ArrayList(), if (byHashCode) HashMap() else null, useRefEquality)

    override fun add(obj: Any) {
        val elements = getElementFor(obj)
        if (!hasElementIn(elements, obj)) {
            elements.add(obj)
        }
    }

    override fun has(obj: Any): Boolean {
        return hasElementIn(getElementFor(obj), obj)
    }

    private fun getElementFor(obj: Any): MutableList<Any> =
        withHashCode?.let { withHashCode.getOrPut(obj.hashCode()) { ArrayList() } }
            ?: withoutHashCode
            ?: error("Can not find list for store elements")

    private fun hasElementIn(elements: List<Any>, obj: Any): Boolean {
        for (another in elements) {
            if (if (useRefEquality) another === obj else another == obj) {
                return true
            }
        }

        return false
    }
}
