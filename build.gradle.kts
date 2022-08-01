plugins {
    java
    application

    id("com.github.johnrengelman.shadow") version "7.0.0"
}

application {
    mainClass.set("fr.rader.boblite.Main")
}

group = "fr.rader"
version = "1.6.5"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.intellij:forms_rt:7.0.3")

    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.rader.boblite.Main"
    }
}
