import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withGroovyBuilder

fun Project.configureNeoForgeEnvironment() {
	val modId = requireStonecutterProperty("mod.id")
	val neoForgeVersion = requireContextualisedProperty("deps", "neoforge", loader = "neoforge")

	extensions.getByName("neoForge").withGroovyBuilder {
		setProperty("version", neoForgeVersion)

		"mods" {
			"register"(modId) {
				"sourceSet"(extensions.getByType<JavaPluginExtension>().sourceSets["main"])
			}
		}

		val parchmentProp = findContextualisedProperty("deps", "parchment", loader = "neoforge")
		if (parchmentProp != null) {
			val (mc, ver) = parchmentProp.split(':')
			"parchment" {
				setProperty("mappingsVersion", ver)
				setProperty("minecraftVersion", mc)
			}
		}
	}

	tasks.named("createMinecraftArtifacts") {
		dependsOn(tasks.named("stonecutterGenerate"))
	}
}
