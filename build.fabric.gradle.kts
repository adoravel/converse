plugins {
	id("mod-platform")
}

val keyApi =
	if (stonecutter.current.parsed >= "26.1") "fabric-key-mapping-api-v1" else "fabric-key-binding-api-v1"

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange("1.21.11" until "26.2")
		}
		required(keyApi) {
			slug(modrinth = "fabric-api")
		}
		required("fabric-lifecycle-events-v1") {
			slug(modrinth = "fabric-api")
		}
		required("fabricloader") {
			requires atLeast configured("fabric-loader")
		}
		required("yet_another_config_lib_v3") {
			slug("yacl") atLeast "3.8"
		}
		optional("modmenu") {}
	}
}

loom {
	runs.named("client") {
		client()
		generateRunConfig.set(true)
	}
	runs.named("server") {
		server()
		generateRunConfig.set(true)
	}
	accessWidenerPath =
		rootProject.file("src/main/resources/accessWideners/${stonecutter.current.version}.classtweaker")
}

repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
	modImplementation("com.terraformersmc:modmenu:${versions.fabric.modMenu}")
	modImplementation("dev.isxander:yet-another-config-lib:${versions.yacl}")
	modImplementation(fabricApi.module(keyApi, versions.fabric.api))
	modImplementation(fabricApi.module("fabric-lifecycle-events-v1", versions.fabric.api))
}
