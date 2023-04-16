import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(group = "com.android.tools.build", name = "gradle", version = "7.3.1")

    implementation(kotlin("stdlib"))

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
}

listOf("compileKotlin", "compileTestKotlin").forEach {
    tasks.getByName<KotlinCompile>(it) {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
