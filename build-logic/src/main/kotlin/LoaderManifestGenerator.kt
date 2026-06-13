@file:Suppress("unused")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import net.peanuuutz.tomlkt.Toml
import org.gradle.api.NamedDomainObjectContainer
import java.util.*

val JSON = Json { prettyPrint = true; encodeDefaults = true }
val TOML = Toml { }

sealed class LoaderManifestGenerator<T>(val id: String) {
	abstract val jarTask: String
	abstract val sourcesJarTask: String
	abstract val modManifestPath: String
	abstract val excludedResources: List<String>

	abstract fun generateManifest(ctx: Context): T

	companion object {
		fun of(id: String, remapped: Boolean = false): LoaderManifestGenerator<*> = when (id) {
			FabricManifestGenerator.ID -> FabricManifestGenerator(remapped)
			NeoForgeManifestGenerator.id -> NeoForgeManifestGenerator
			else -> error("Unknown loader: '$id'")
		}
	}
}

data class FabricManifestGenerator(val remapped: Boolean = false) : LoaderManifestGenerator<FabricManifest>(ID) {
	companion object {
		const val ID = "fabric"
	}

	override val jarTask: String
		get() = if (remapped) "remapJar" else "jar"

	override val sourcesJarTask: String
		get() = if (remapped) "remapSourcesJar" else "sourcesJar"

	override val modManifestPath = "fabric.mod.json"

	override val excludedResources = listOf(
		"META-INF/neoforge.mods.toml", "aw/*.cfg", ".cache", "pack.mcmeta"
	)

	override fun generateManifest(ctx: Context): FabricManifest {
		val prefix = ctx.modId.replaceFirstChar(Char::titlecase)
		return FabricManifest(
			id = ctx.modId,
			name = ctx.modName,
			version = ctx.baseVersion,
			authors = ctx.authors,
			license = ctx.licenseName,
			accessWidener = "accessWideners/${ctx.currentMinecraftVersion}.classtweaker",
			contributors = ctx.contributors,
			contact = mapOf(
				"sources" to ctx.sourcesUrl,
				"issues" to ctx.issuesUrl,
				"homepage" to ctx.homepageUrl
			),
			custom = buildJsonObject {
				if (ctx.discordUrl != null)
					putJsonObject("modmenu") {
						putJsonObject("links") {
							put("modmenu.discord", ctx.discordUrl)
						}
					}
			},
			description = ctx.description,
			icon = "assets/${ctx.modId}/icon.png",
			entrypoints = mapOf(
				"client" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.${prefix}FabricClientEntrypoint"),
				"modmenu" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.${prefix}ModMenuIntegration")
			),
			mixins = listOf("${ctx.modId}.mixins.json"),
			depends = ctx.extension.dependencies.required.associate { it.modId.get() to it.fabricLikeVersionRange.get() },
			recommends = ctx.extension.dependencies.optional.associate { it.modId.get() to it.fabricLikeVersionRange.get() },
			breaks = ctx.extension.dependencies.incompatible.associate { it.modId.get() to it.fabricLikeVersionRange.get() },
			provides = ctx.extension.dependencies.embeds.map { it.modId.get() }
		)
	}
}

data object NeoForgeManifestGenerator : LoaderManifestGenerator<NeoForgeManifest>("neoforge") {
	override val jarTask = "jar"
	override val sourcesJarTask = "sourcesJar"
	override val modManifestPath = "META-INF/neoforge.mods.toml"

	override val excludedResources = listOf(
		"fabric.mod.json", "accessWideners/*.classtweaker", ".cache", "pack.mcmeta"
	)

	override fun generateManifest(ctx: Context): NeoForgeManifest {
		val dependencies = buildList {
			addAll(ctx.extension.dependencies.required.toNeoForgeDependencies("required"))
			addAll(ctx.extension.dependencies.optional.toNeoForgeDependencies("optional"))
			addAll(ctx.extension.dependencies.incompatible.toNeoForgeDependencies("incompatible"))
		}

		val credits = buildString {
			append(ctx.authors.joinToString(", "))
			if (ctx.contributors.isNotEmpty())
				append("  Contributors: ${ctx.contributors.joinToString(", ")}")
		}

		return NeoForgeManifest(
			license = ctx.licenseName,
			mods = listOf(
				NeoForgeModDefinition(
					id = ctx.modId,
					name = ctx.modName,
					version = ctx.baseVersion,
					displayURL = ctx.homepageUrl,
					logoFile = "assets/${ctx.modId}/icon.png",
					authors = ctx.authors.joinToString(", "),
					credits = credits,
					description = ctx.description
				)
			),
			dependencies = mapOf(ctx.modId to dependencies),
			mixins = listOf(NeoForgeMixinConfig("${ctx.modId}.mixins.json"))
		)
	}
}

private fun NamedDomainObjectContainer<Dependency>.toNeoForgeDependencies(type: String): List<NeoForgeDependencyDefinition> =
	map { dep ->
		NeoForgeDependencyDefinition(
			modId = dep.modId.get(),
			side = dep.environment.get().uppercase(Locale.getDefault()),
			versionRange = dep.forgeLikeVersionRange.get(),
			mandatory = type == "required",
			type = type
		)
	}
