plugins {
    application
    kotlin("jvm") version "1.3.70"
//    id("com.github.johnrengelman.shadow") version "5.2.0"
    `maven-publish`
}

application {
    mainClassName = "rst.pdftools.compare.PdfCompareKt"
}

version = "0.2.0"
group = "com.github.ralfstuckert.pdftools"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.apache.pdfbox:pdfbox:2.0.4")
    compile("us.jimschubert:kopper-typed:0.0.3")
    compile("com.github.salomonbrys.kotson:kotson:2.5.0")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.1")
    testCompile("org.jetbrains.kotlin:kotlin-test")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("com.natpryce:hamkrest:1.4.1.0")
}

repositories {
    jcenter()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
