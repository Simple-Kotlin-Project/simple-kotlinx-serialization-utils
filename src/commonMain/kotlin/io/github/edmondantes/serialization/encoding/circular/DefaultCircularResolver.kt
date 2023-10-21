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
package io.github.edmondantes.serialization.encoding.circular

/**
 * Default implementation of [CircularResolver]
 * @see CircularResolver
 * @see CircularEncoder
 */
public class DefaultCircularResolver internal constructor(
    private val withoutHashCode: MutableList<Any>?,
    private val withHashCode: MutableSet<Any>?,
    private val useRefEquality: Boolean,
) : CircularResolver {

    private val elements: MutableCollection<Any>
        get() = withHashCode ?: withoutHashCode ?: error("Can not find collection for store elements")

    /**
     * @param byHashCode If true, [DefaultCircularResolver] will be use object's hash codes for searching same objects
     * @param useRefEquality If false, [DefaultCircularResolver] will use method 'equals' for determining equality, else will use reference equality
     */
    public constructor(
        byHashCode: Boolean = true,
        useRefEquality: Boolean = false,
    ) : this(if (byHashCode) null else ArrayList(), if (byHashCode) HashSet() else null, useRefEquality)

    override fun add(obj: Any): Boolean {
        if (!has(obj)) {
            elements.add(obj)
            return true
        }
        return false
    }

    override fun has(obj: Any): Boolean {
        for (another in elements) {
            if (if (useRefEquality) another === obj else another == obj) {
                return true
            }
        }
        return false
    }
}
