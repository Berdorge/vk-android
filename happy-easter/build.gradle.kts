group = "happy.easter"

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(group = "com.android.tools.build", name = "gradle", version = "7.3.1")
        classpath(kotlin("gradle-plugin", version = "1.7.20"))
    }
}

allprojects {
   repositories {
       google()
       mavenCentral()
   }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
