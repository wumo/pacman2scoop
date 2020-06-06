plugins {
  kotlin("jvm") version "1.3.72"
  application
  kotlin("plugin.serialization") version "1.3.72"
  id("org.beryx.runtime") version "1.8.5"
  id("com.google.osdetector") version "1.6.2"
  id("com.github.wumo.graalvm") version "0.0.4"
}

group = "com.github.wumo"
version = "0.0.2"

repositories {
  mavenCentral()
  maven(url = "https://jitpack.io")
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.github.wumo:common-utils:1.0.5")
  implementation("com.github.wumo:http-stack:1.0.2")
}

application {
  mainClassName = "com.github.wumo.MainKt"
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  
  register<Zip>("packageDist") {
    dependsOn(jpackage)
    val fileName = "${project.name}-${project.version}-${osdetector.classifier}.${archiveExtension.get()}"
    archiveFileName.set(fileName)
    destinationDirectory.set(file("$buildDir/dist"))
    from("$buildDir/jpackage")
  }
}

runtime {
  addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
  addModules("java.logging")
  
  jpackage {
    imageName = "pacman2scoop"
    appVersion = project.version.toString()
    installerOptions = listOf("--app-version", version.toString())
    
    skipInstaller = true
    
    if(osdetector.os == "windows") {
//      installerType = "msi"
      imageOptions = listOf("--win-console")
      installerOptions =
        installerOptions + listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut")
    }
  }
}

graalvm {
  graalvmHome = System.getenv("GRAALVM_HOME")
  mainClassName = "com.github.wumo.MainKt"
  arguments = listOf(
    "--no-fallback",
    "--enable-all-security-services",
    "--report-unsupported-elements-at-runtime",
    "--allow-incomplete-classpath"
  )
}