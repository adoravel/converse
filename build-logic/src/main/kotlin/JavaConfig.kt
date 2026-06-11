import dev.kikugie.fletching_table.extension.FletchingTableExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.model.IdeaModel

fun Project.configureJava(ctx: Context) =
	extensions.configure<JavaPluginExtension>("java") {
		withSourcesJar()
		withJavadocJar()
		sourceCompatibility = ctx.javaVersion
		targetCompatibility = ctx.javaVersion
	}

fun Project.configureIdea() =
	extensions.configure<IdeaModel>("idea") {
		module {
			isDownloadJavadoc = true
			isDownloadSources = true
		}
	}

fun Project.configureFletchingTable(ctx: Context) =
	extensions.configure<FletchingTableExtension> {
		mixins.create("main") { mixin("default", "${ctx.modId}.mixins.json") }
		j52j.register("main") { extension("json", "**/*.json5") }
	}

fun Project.configureProcessResources(ctx: Context) {
	tasks.named<ProcessResources>("processResources") {
		dependsOn(tasks.named("stonecutterGenerate"), "kspKotlin")
		filesMatching("*.mixins.json") {
			expand("java" to "JAVA_${ctx.javaVersion.majorVersion}")
		}
		exclude(ctx.loader.excludedResources)
	}
}

fun Project.configureJarTask(ctx: Context) {
	val generateTask = tasks.named("generateModManifest")
	tasks.withType<Jar>().configureEach {
		archiveBaseName.set(ctx.modId)
		dependsOn(generateTask)
		if (ctx.loader is NeoForgeManifestGenerator) {
			manifest.attributes("MixinConfigs" to "${ctx.modId}.mixins.json")
		}
	}
}

