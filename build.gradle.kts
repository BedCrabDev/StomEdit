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
    implementation("dev.hollowcube:minestom-ce:5ba3d92d83")
    //implementation("com.github.Minestom:Minestom:4f7ff5b474")
    implementation("com.github.UserUNP:instanceguard:1a65638286") {
        exclude(group = "com.github.Minestom", module = "Minestom")
        exclude(group = "com.github.simplix-softworks", module = "simplixstorage")
    }
}
