
val serializationVersion = "1.6.+"


plugins {
    val kotlinVersion = "1.9.22"
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    application
    id("maven-publish")
    id("io.kotest.multiplatform") version "5.5.5"
}

group = "cloud.qingyangyunyun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation(project(mapOf("path" to ":common")))
}


kotlin {
    jvm {
        jvmToolchain(19)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                devServer?.proxy = mutableMapOf("/api" to "http://localhost:9000/")
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("com.benasher44:uuid:0.7.0")
                implementation("com.appmattus.crypto:cryptohash:0.10.1")

                implementation(project(":common"))

            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.5.5")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.5.5")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
            }
        }
    }
}


