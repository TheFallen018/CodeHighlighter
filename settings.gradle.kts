pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "CodeHighlighter"
include("src:main:java")
findProject(":src:main:java")?.name = "java"
