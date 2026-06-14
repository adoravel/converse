@file:Suppress("unused")

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.*

fun RepositoryHandler.strictMaven(
	url: String, vararg groups: String, configure: MavenArtifactRepository.() -> Unit = {}
) = exclusiveContent {
	forRepository { maven(url) { configure() } }
	filter { groups.forEach(::includeGroup) }
}

fun Project.configureStonecutterReplacements() = stonecutter.apply {
	replacements.string(current.parsed >= "26.1") {
		replace("KeyBindingHelper", "KeyMappingHelper")
		replace("registerKeyBinding", "registerKeyMapping")
		replace("net.fabricmc.fabric.api.client.keybinding.v1", "net.fabricmc.fabric.api.client.keymapping.v1")
		replace("FabricDataOutput", "FabricPackOutput")
		replace("GuiGraphics", "GuiGraphicsExtractor")
		replace("net.minecraft.client.GuiMessage", "net.minecraft.client.multiplayer.chat.GuiMessage")
		replace("net.minecraft.client.GuiMessageTag", "net.minecraft.client.multiplayer.chat.GuiMessageTag")
	}
}

class ModPlatformPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create<Versions>("versions", project)

		val inferredLoaderId = project.buildFile.name.substringAfter('.').replace(".gradle.kts", "")
		val remapped = project.stonecutter.current.version.isRemapped
		val inferredLoader = LoaderManifestGenerator.of(inferredLoaderId, remapped)

		val extension = project.extensions.create<ModPlatformExtension>("platform").apply {
			loader.convention(inferredLoader.id)
			jarTask.convention(inferredLoader.jarTask)
			sourcesJarTask.convention(inferredLoader.sourcesJarTask)
		}

		project.apply(plugin = "org.jetbrains.kotlin.jvm")
		project.apply(plugin = "com.google.devtools.ksp")
		project.apply(plugin = "dev.kikugie.fletching-table")

		when (inferredLoader) {
			is FabricManifestGenerator -> {
				project.apply(plugin = "dev.kikugie.loom-back-compat")
				project.configureFabricMappings()
				project.configureFabricDependencies()
			}
			is NeoForgeManifestGenerator -> {
				project.apply(plugin = "net.neoforged.moddev")
				project.configureNeoForgeEnvironment()
			}
		}

		project.configureStonecutterReplacements()

		project.afterEvaluate {
			val ctx = Context(
				project = project,
				extension = extension,
				loader = LoaderManifestGenerator.of(extension.loader.get(), remapped),
				stonecutter = project.stonecutter
			)
			configureProject(ctx, remapped)
		}
	}

	private fun Project.configureProject(ctx: Context, remapped: Boolean) {
		apply(plugin = "java")
		apply(plugin = "me.modmuss50.mod-publish-plugin")
		apply(plugin = "idea")

		version = ctx.fullVersion
		ctx.extension.minimumJavaVersion.set(ctx.javaVersion)

		if (ctx.loader is FabricManifestGenerator) {
			ctx.extension.dependencies {
				required("java") { fabricLikeVersionRange = ">=${ctx.extension.minimumJavaVersion.get().majorVersion}" }
			}
		}

		configureFletchingTable(ctx)
		registerGenerateManifestTask(ctx)
		configureJarTask(ctx)
		configureIdea()
		configureProcessResources(ctx)
		configureJava(ctx)
		registerBuildAndCollectTask(ctx)
		configureModPublishing(ctx)

		val enableMavenPublishing by project.boolean("PUB_MAVEN_ENABLE", env = true)
		if (enableMavenPublishing) configureMavenPublishing(ctx)
	}
}
