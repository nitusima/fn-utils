import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.20"
    `java-library`
    `maven-publish`
    id("signing")
    id("org.jreleaser") version "1.17.0"
}

group = "com.africapoa.fn"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))
    implementation(platform("com.google.cloud:libraries-bom:26.50.0"))
    implementation("com.google.cloud:google-cloud-storage")
    implementation("com.google.auth:google-auth-library-oauth2-http")

    // Google API Services
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20230707-2.0.0") // Updated version
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.5")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}


java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.africapoa.fn"
            artifactId = "util"

            from(components["java"])
//            artifact(tasks.named("sourcesJar"))
//            artifact(tasks.named("javadocJar"))
            pom {
                name.set("util")
                description.set("Sample application")
                url.set("https://github.com/nitusima/fn-utils")
                inceptionYear.set("2021")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://spdx.org/licenses/Apache-2.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("nitusima")
                        name.set("Nitu")
                        email.set("nitu@africapoa.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/nitusima/fn-utils.git")
                    developerConnection.set("scm:git:ssh://github.com/nitusima/fn-utils.git")
                    url.set("http://github.com/nitusima/fn-utils.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
        mavenLocal()
    }
}

//jreleaser {
//    signing {
//        active.set(Active.ALWAYS)
//        armored.set(true)
//    }
//    deploy {
//        maven {
//            nexus2 {
//                create("maven-central") {
//                    active.set(Active.ALWAYS)
//                    stagingRepository(layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath)
//                    url.set("https://s01.oss.sonatype.org/service/local")
////                    url.set("https://central.sonatype.com/api/v1/publisher")
//                    snapshotUrl.set("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//                    closeRepository.set(true)
//                    releaseRepository.set(true)
//                }
//            }
//        }
//    }
//}
//
jreleaser {
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
    }
    deploy {
        maven {
            mavenCentral {
                    active.set(Active.ALWAYS)
//                    url.set("https://central.sonatype.com/api/v1/publisher")
//                    stagingRepository(layout.buildDirectory.dir("stagingDeploy").get().asFile.absolutePath)
            }
        }
    }
}