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

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Implementation of [ContextualFilter], which allow encoding if [UniqueEncoder]'s id will contained in [allow] list
 * @param allow List of ids of encoders which can encode [value]
 */
@Serializable(with = AllowContextualFilterSerializer::class)
public class AllowContextualFilter<T : Any>(
    @Contextual
    override val value: T,
    @Transient
    private val allow: List<String> = emptyList(),
) : ContextualFilter<T> {
    override fun canEncodeWith(encoderId: String): Boolean =
        allow.isEmpty() || allow.contains(encoderId)
}
