import kotlinx.serialization.encodeToString
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.api.plugins.JavaPluginExtension

abstract class GenerateModManifestTask : DefaultTask() {
	@get:Input
	abstract val content: Property<String>

	@get:OutputFile
	abstract val outputFile: RegularFileProperty

	@TaskAction
	fun generate() = outputFile.get().asFile.run {
		parentFile?.mkdirs()
		writeText(content.get())
	}
}

fun Project.registerGenerateManifestTask(ctx: Context) {
	val manifestOutputDir = layout.buildDirectory.dir("generated/modManifest")

	val generateTask = tasks.register<GenerateModManifestTask>("generateModManifest") {
		val manifestData = ctx.loader.generateManifest(ctx)
		val output = when (ctx.loader) {
			is FabricManifestGenerator -> JSON.encodeToString(manifestData as FabricManifest)
			is NeoForgeManifestGenerator -> TOML.encodeToString(manifestData as NeoForgeManifest)
		}
		content.set(output)
		outputFile.set(layout.buildDirectory.file("generated/modManifest/${ctx.loader.modManifestPath}"))
	}

	configure<JavaPluginExtension> {
		sourceSets.named("main") { resources.srcDir(manifestOutputDir) }
	}

	tasks.named<ProcessResources>("processResources") { dependsOn(generateTask) }
	tasks.withType<Jar>().configureEach {
		if (name == ctx.loader.sourcesJarTask) dependsOn(generateTask)
	}
}

fun Project.registerBuildAndCollectTask(ctx: Context) {
	tasks.register<Copy>("buildAndCollect") {
		from(
			tasks.named(ctx.extension.jarTask.get()),
			tasks.named(ctx.extension.sourcesJarTask.get()),
			tasks.named("javadocJar")
		)
		into(rootProject.layout.buildDirectory.file("libs/${ctx.basicVersion}"))
		dependsOn("build")
		group = "build"
	}
}
