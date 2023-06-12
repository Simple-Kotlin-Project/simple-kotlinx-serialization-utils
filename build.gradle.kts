import io.github.edmondantes.simple.kotlin.multiplatform.gradle.plugin.util.property.gradleProperty

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
plugins {
    id("io.github.edmondantes.simple.kmm.gradle.plugin")
}

group = "io.github.edmondantes"

kotlin {
    sourceSets {
        val kotlinSerializationVersion: String by gradleProperty { }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

licenses {
    apache2()
}

developers {
    developer {
        name = "Ilia Loginov"
        email = "masaqaz40@gmail.com"
        organizationName("github")
        role("Maintainer")
        role("Developer")
    }
}

simplePom {
    any {
        title = "Simple kotlinx serialization utils"
        description = "Small library which provide some utilities for koltinx.serialization"
        url = "#github::Simple-Kotlin-Project::${project.name}"
        scm {
            url = "#github::Simple-Kotlin-Project::${project.name}::master"
            connection = "#github::Simple-Kotlin-Project::${project.name}"
            developerConnection = "#github::Simple-Kotlin-Project::${project.name}"
        }
    }
}