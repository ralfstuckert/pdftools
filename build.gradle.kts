plugins {
    application
    kotlin("jvm") version "1.1.51"
    id("com.github.johnrengelman.shadow") version "2.0.1"
}

application {
    mainClassName = "rst.pdftools.compare.PdfCompareKt"
}

version = "0.1.0"
group = "rst.pdftools"

dependencies {
    compile(kotlin("stdlib"))
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