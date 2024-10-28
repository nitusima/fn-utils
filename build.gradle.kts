import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "com.africapoa.fn"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
    implementation("com.google.cloud:google-cloud-storage:2.20.2")
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
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Your Library Name")
                description.set("A concise description of your library.")
                url.set("http://www.example.com/library")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("your-id")
                        name.set("Nitu")
                        email.set("nitu@africapoa.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://example.com/your-library.git")
                    developerConnection.set("scm:git:ssh://example.com/your-library.git")
                    url.set("http://example.com/your-library/")
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername") as String?
                password = findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
