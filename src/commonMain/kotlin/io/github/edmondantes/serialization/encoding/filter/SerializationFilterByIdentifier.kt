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

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Serialization filter that filter by annotations [AllowEncoder] and [IgnoreEncoder]
 * @param id value for checking.
 */
@OptIn(ExperimentalSerializationApi::class)
public class SerializationFilterByIdentifier(private val id: String) : SerializationFilter {
    override fun filter(descriptor: SerialDescriptor): Boolean = checkAnnotations(descriptor.annotations)

    override fun filter(
        parentDescriptor: SerialDescriptor,
        indexInParent: Int,
    ): Boolean = checkAnnotations(parentDescriptor.getElementAnnotations(indexInParent) + parentDescriptor.annotations)

    private fun checkAnnotations(annotations: List<Annotation>): Boolean {
        val allow = annotations.filterIsInstance<AllowEncoder>().flatMap { it.allow.toList() }
        val ignore = annotations.filterIsInstance<IgnoreEncoder>().flatMap { it.ignore.toList() }

        return ignore.none { it == id } && (allow.isEmpty() || allow.any { it == id })
    }
}
