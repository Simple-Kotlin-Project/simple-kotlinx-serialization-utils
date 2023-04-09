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

rootProject.name = "simple-kotlinx-serialization-utils"

pluginManagement {
    val kotlinVersion: String = settings.extra["kotlin.version"]?.toString() ?: error("Can not find 'kotlin.version' property")
    plugins {
        id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    }
}