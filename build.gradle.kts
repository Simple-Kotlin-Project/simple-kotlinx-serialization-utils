import org.jetbrains.kotlin.cli.common.toBooleanLenient

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
    signing
    `maven-publish`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.8.10"
    id("com.diffplug.spotless") version "6.18.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("com.palantir.git-version") version "3.0.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "io.github.edmondantes"
version = gitVersion(mapOf("prefix" to "v@"))

java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven {
        name = "Sonatype_releases"
        url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
    maven {
        name = "Sonatype_snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

spotless {
    if (file("./.git").exists()) {
        val defaultBranch = project.extra.properties["git.default.branch"] ?: "master"
        ratchetFrom("origin/${defaultBranch}")
    }
    encoding("UTF-8")

    kotlin {
        target("src/*/kotlin/**/*.kt")
        ktlint("0.48.2")
        licenseHeaderFile("LICENSE_FILE_HEADER")
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val isMainHost: Boolean = extra["publication.only.platform"]?.toString()?.toBooleanStrictOrNull() != true

kotlin {
    explicitApi()
    if (isMainHost) {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            withJava()
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }
        js(IR) {
            val hasBrowser: Boolean = extra["kotlin.js.browser.enable"]?.toString()?.toBooleanLenient() == true
            if (hasBrowser) {
                browser {
                    commonWebpackConfig {
                        cssSupport {
                            enabled.set(true)
                        }
                    }
                }
            }
            nodejs()
        }
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> {
            macosX64("macosX64")
            iosArm64("iosArm64")
            iosX64("iosX64")
        }

        hostOs == "Linux" -> {
            linuxX64("linuxX64")
            linuxArm64("linuxArm64")
        }

        isMingwX64 -> {
            mingwX64()
        }

        else -> throw GradleException("Host OS is not supported for this project")
    }

    val kotlinSerializationVersion: String = extra["kotlin.serialization.version"]?.toString()
        ?: error("Can not get 'kotlin.serialization.version'")

    sourceSets {
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

signing {
    val keyId = findProperty("signingKeyId") as String?
    val privateKey = findProperty("signingPrivateKey") as String?
    val password = findProperty("signingPassword") as String?
    if (!keyId.isNullOrBlank() && !privateKey.isNullOrBlank() && !password.isNullOrBlank()) {
        useInMemoryPgpKeys(keyId, privateKey, password)
        sign(publishing.publications)
    }
}

// Added because it is issue: https://youtrack.jetbrains.com/issue/KT-46466
val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}

publishing {
    publications {
        withType<MavenPublication>().all {
            if (name == "kotlinMultiplatform") {
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == this }
                    .configureEach { onlyIf("If it is main host for all mainly publications") { isMainHost } }
            }

            groupId = project.group.toString()
            version = project.version.toString()

            with(pom) {
                name.set("Simple kotlinx serialization utils")
                description.set("Small library which provide some utilities for koltinx.serialization")
                url.set("https://github.com/EdmonDantes/${project.name}")
                developers {
                    developer {
                        name.set("Ilia Loginov")
                        email.set("masaqaz40@gmail.com")
                        organization.set("github")
                        organizationUrl.set("https://www.github.com")
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/EdmonDantes/${project.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:EdmonDantes/${project.name}.git")
                    url.set("https://github.com/EdmonDantes/${project.name}/tree/master")
                }
            }

            artifact(javadocJar)
        }
    }

    repositories {
        maven {
            val publishRepositoryId: String? by project
            val publishRepositoryUrl: String? by project

            val resultUrl =
                if (publishRepositoryUrl.isNullOrBlank() || publishRepositoryId.isNullOrBlank()) {
                    publishRepositoryUrl.orEmpty().ifEmpty { "./build/repo/" }
                } else {
                    var resolvedUrl = publishRepositoryUrl.orEmpty().ifEmpty { "./build/repo/" }
                    if (!resolvedUrl.endsWith('/') && !publishRepositoryId!!.startsWith('/')) {
                        resolvedUrl += '/'
                    }

                    resolvedUrl + publishRepositoryId
                }

            url = uri(resultUrl)

            val username = project.findProperty("sonatypeUsername") as String?
            val password = project.findProperty("sonatypePassword") as String?
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }
}

tasks.withType<Delete> {
    delete += listOf("$projectDir/kotlin-js-store")
}

tasks.register("enableTestLogging") {
    group = "testEnv"
    doLast {
        val envFile = file("src/commonTest/kotlin/env/Env.kt")
        val text = envFile.readText()
        envFile.writeText(text.replace(Regex(".*isEnableLogging.*"), "    val isEnableLogging: Boolean = true"))
    }
}

tasks.register("disableTestLogging") {
    group = "testEnv"
    doLast {
        val envFile = file("src/commonTest/kotlin/env/Env.kt")
        val text = envFile.readText()
        envFile.writeText(text.replace(Regex(".*isEnableLogging.*"), "    val isEnableLogging: Boolean = false"))
    }
}

tasks.withType<Test> {
    dependsOn("disableTestLogging")
    finalizedBy("enableTestLogging")
}