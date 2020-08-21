import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

include(":app")
rootProject.name="KakaoBotSourceHub"

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard:dependencies:0.5.7")
}

bootstrapRefreshVersionsAndDependencies()