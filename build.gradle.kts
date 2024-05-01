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
    // https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-hdfs-client
    implementation("org.apache.hadoop:hadoop-hdfs-client:3.4.0")
    implementation("org.apache.hadoop:hadoop-client:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    runtimeOnly("com.mysql:mysql-connector-j")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val mingNexusId = properties["mingNexus.id"] as String
val mingNexusPassword = properties["mingNexus.password"] as String
val mingNexusAddress = properties["mingNexus.address"] as String


tasks {
    bootBuildImage {

        // nexsus 주소/프로젝트이름:프로젝트버전
        imageName = "$mingNexusAddress/${project.name}:${project.version}"
        /*publish.set(true)
        docker {
            publishRegistry {
                username.set(mingNexusId)
                password.set(mingNexusPassword)
            }
        }*/

    }
}
