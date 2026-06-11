plugins {
	id("mod-platform")
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			versionRange("1.21.11" until "26.2")
		}
		required("neoforge") {
			requires atLeast "1"
		}
		required("yet_another_config_lib_v3") {
			slug("yacl") atLeast "3.8"
		}
	}
	env
}

neoForge {
	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
		}
	}
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
	implementation("dev.isxander:yet-another-config-lib:${versions.yacl}")
}
