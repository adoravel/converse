import dev.kikugie.stonecutter.settings.tree.TreeBuilder

pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
	}
	includeBuild("build-logic")
}

gradle.beforeProject {
	buildscript {
		repositories {
			pluginManagement.repositories.forEach { repo -> add(repo) }
		}
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.5"
	id("dev.kikugie.loom-back-compat") version "0.3"
}

fun TreeBuilder.supports(vararg versions: String) = versions.forEach {
	versions("$it-fabric" to it).buildscript = "build.fabric.gradle.kts"
	versions("$it-neoforge" to it).buildscript = "build.neoforge.gradle.kts"
	vcsVersion = "${versions.first()}-fabric"
}

stonecutter.create(rootProject) {
	supports("1.21.11", "26.1.2")
}
