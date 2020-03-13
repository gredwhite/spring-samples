import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension


plugins {
// For upwards compatibility check
    val springBootVersion = "2.2.0.RELEASE"
    id("org.springframework.boot") version springBootVersion
    java
    id("com.google.cloud.tools.appengine") version "2.0.0-rc5"
    id("com.google.cloud.tools.jib") version "2.1.0"

}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "com.google.cloud.tools.appengine")
apply(plugin = "com.google.cloud.tools.jib")


group = "my.gcp.samples"
version = "0.0.1-SNAPSHOT"

val developmentOnly = configurations.create("developmentOnly")
configurations {
    developmentOnly
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}
repositories {
    mavenCentral()
// Milestone spring repository
    maven(url = "https://repo.spring.io/milestone/")
}

val springCloudVersion = "Greenwich.SR3"
val testcontainersVersion = "1.11.4"
val junitVersion = "5.5.1"

dependencies {
    // GCP bucket
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.cloud", "spring-cloud-gcp-starter")
    implementation("org.springframework.cloud", "spring-cloud-gcp-starter-storage")
    // GCP big query
    implementation("org.springframework.cloud", "spring-cloud-gcp-starter-bigquery")
    implementation("org.springframework.integration", "spring-integration-core")
    implementation("org.springframework.boot", "spring-boot-starter-thymeleaf")
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.springframework.cloud:spring-cloud-gcp-dependencies:1.2.1.RELEASE")
        mavenBom("org.junit:junit-bom:$junitVersion")
    }
}
configure<AppEngineAppYamlExtension> {
    deploy {
        projectId = "spring-samples-269912"
        version = "GCLOUD_CONFIG"
        stopPreviousVersion = true // etc
    }
}

jib {
    to {
        image = "us.gcr.io/spring-samples-269912/kuber_image"
        auth {
            username = "_json_key"
            password = file("secret.json").readText()
        }
    }
}