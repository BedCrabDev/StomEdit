plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

group = "dev.bedcrab"
version = "1.0"

dependencies {
    implementation("dev.hollowcube:minestom-ce:74ca1041f3")
}
