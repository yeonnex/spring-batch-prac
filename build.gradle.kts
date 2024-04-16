plugins {
    java
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.ming"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


repositories {
    mavenCentral()
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    compileOnly("org.projectlombok:lombok")
    implementation("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val mingNexusId = properties["mingNexus.id"] as String
val mingNexusPassword = properties["mingNexus.password"] as String
val mingNexusAddress = properties["mingNexus.address"] as String


//tasks {
//    bootBuildImage {
//
//        // nexsus 주소/프로젝트이름:프로젝트버전
//        imageName = "$mingNexusAddress/${project.name}:${project.version}"
//        publish.set(true)
//        docker {
//            publishRegistry {
//                username.set(mingNexusId)
//                password.set(mingNexusPassword)
//            }
//        }
//
//    }
//}
