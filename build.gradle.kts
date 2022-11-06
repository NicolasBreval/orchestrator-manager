import com.google.protobuf.gradle.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Paths

val kotlinVersion: String by System.getProperties()
val micronautVersion: String by System.getProperties()
val creator: String by System.getProperties()
val buildTimestamp: String = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(ZonedDateTime.now())
val buildJDK: String = "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})"
val buildOS: String = "${System.getProperties()["os.name"]} ${System.getProperties()["os.arch"]} ${System.getProperties()["os.version"]}"

plugins {
    val kotlinVersion by System.getProperties()
    kotlin("jvm") version "$kotlinVersion"
    kotlin("kapt") version "$kotlinVersion"
    kotlin("plugin.allopen") version "$kotlinVersion"
    kotlin("plugin.jpa") version "$kotlinVersion"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.5.1"
    id("com.google.protobuf") version "0.8.15"
    id("net.nemerosa.versioning") version "2.7.1"
}

version = "0.0.1"
group = "org.nitb.orchestrator2"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.security:micronaut-security-annotations")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.grpc:grpc-services:1.50.2")
    implementation("io.micronaut.rabbitmq:micronaut-rabbitmq")
    implementation("io.micronaut.jms:micronaut-jms-activemq-classic")
    implementation("org.nitb.orchestrator2:orchestrator-task-base:0.0.1")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.views:micronaut-views-velocity")

    compileOnly("org.graalvm.nativeimage:svm")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql:42.5.0")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11:21.7.0.0")
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:11.2.1.jre17")
    runtimeOnly("mysql:mysql-connector-java:8.0.30")

    testImplementation("org.testcontainers:testcontainers:1.17.5")
    testImplementation("org.testcontainers:junit-jupiter:1.17.5")
    testImplementation("com.github.docker-java:docker-java:3.2.13")

}


application {
    mainClass.set("org.nitb.orchestrator2.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            incremental = true
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
            incremental = true
        }
    }
    test {
        useJUnit()
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes (mapOf(
                "Version" to "${project.version}",
                "Built-By" to creator,
                "Build-Timestamp" to buildTimestamp,
                "Build-Revision" to versioning.info.commit,
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Build-Jdk" to buildJDK,
                "Build-OS" to buildOS,
                "Main-Class" to "${application.mainClass}",
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name },
                "Micronaut-Version" to micronautVersion,
                "Kotlin-Version" to kotlinVersion
            ))
        }
    }

    clean {
        doFirst {
            // DELETE PREVIOUS MICRONAUT BANNER FILE
            delete(Paths.get(Paths.get(projectDir.absolutePath, "src", "main", "resources").toAbsolutePath().toString(), "micronaut-banner.txt").toAbsolutePath().toString())
        }

        doLast {
            // CREATE MICRONAUT BANNER BASED ON ORCHESTRATOR BANNER FILE
            project.logger.info("Creating micronaut banner file")
            val resourcesDir = Paths.get(projectDir.absolutePath, "src", "main", "resources").toAbsolutePath().toString()
            val bannerFile = Paths.get(resourcesDir, "orchestrator-banner.txt").toFile()
            val micronautBannerFile = Paths.get(resourcesDir, "micronaut-banner.txt").toFile()
            if (bannerFile.exists()) {
                try {
                    val bannerWithVersions = bannerFile.readText() + """
                      
                      Version: ${project.version}
                      Micronaut version: $micronautVersion
                      Kotlin version: $kotlinVersion
                      Built by: $creator
                      Build timestamp: $buildTimestamp
                      Build revision: ${versioning.info.commit}
                      Build JDK: $buildJDK
                      Build OS: $buildOS
                    """.trimIndent().replaceIndent("  ")

                    micronautBannerFile.createNewFile()
                    micronautBannerFile.writeText(bannerWithVersions)
                } catch (e: Exception) {
                    project.logger.error("Fatal error creating micronaut banner file: ${e.message}", e)
                }
            }
        }
    }

    jar {
        doLast {
            // REMOVE EXTRA JARS AND RENAME *ALL FILE
            val libsPath = Paths.get(projectDir.absolutePath, "build", "libs").toAbsolutePath().toString()
            val basicLib = Paths.get(libsPath, "${project.name}-${project.version}.jar")
            val runnerLib = Paths.get(libsPath, "${project.name}-${project.version}-runner.jar")
            val allLib = Paths.get(libsPath, "${project.name}-${project.version}-all.jar")

            basicLib.toFile().delete()
            runnerLib.toFile().delete()
            allLib.toFile().renameTo(basicLib.toFile())
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.46.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.nitb.orchestrator2.*")
    }
}
