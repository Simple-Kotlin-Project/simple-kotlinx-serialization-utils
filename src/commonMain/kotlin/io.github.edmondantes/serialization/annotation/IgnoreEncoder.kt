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

import io.github.edmondantes.serialization.encoding.FilterCompositeEncoder
import io.github.edmondantes.serialization.encoding.FilterEncoder
import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

/**
 * This annotation ignores encoding for [UniqueEncoder] or [UniqueCompositeEncoder] with an id that is contained in [ignore].
 * Other encoder will be allowed
 * @see FilterEncoder
 * @see FilterCompositeEncoder
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
@OptIn(ExperimentalSerializationApi::class)
public annotation class IgnoreEncoder(val ignore: Array<String> = emptyArray())
