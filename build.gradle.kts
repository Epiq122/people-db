plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // added these
    implementation("com.mysql:mysql-connector-j:8.1.0")
    implementation("com.h2database:h2:2.1.214")
    testImplementation("org.assertj:assertj-core:3.24.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
