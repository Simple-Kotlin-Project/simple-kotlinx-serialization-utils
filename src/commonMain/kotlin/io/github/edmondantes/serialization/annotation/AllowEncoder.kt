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
package io.github.edmondantes.serialization.annotation

import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import io.github.edmondantes.serialization.encoding.filter.FilterCompositeEncoder
import io.github.edmondantes.serialization.encoding.filter.FilterEncoder
import io.github.edmondantes.serialization.encoding.filter.filterBy
import io.github.edmondantes.serialization.encoding.filter.filterByIdentifier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.encoding.Encoder

/**
 * For enable with feature please use [filterBy] or [filterByIdentifier]
 * This annotation allows encoding for [UniqueEncoder] or [UniqueCompositeEncoder] with an id that is contained in [allow].
 * Other [Encoder]s will be ignored
 *
 * @see FilterEncoder
 * @see FilterCompositeEncoder
 * @see filterBy
 * @see filterIsInstance
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
@OptIn(ExperimentalSerializationApi::class)
public annotation class AllowEncoder(val allow: Array<String> = [])
