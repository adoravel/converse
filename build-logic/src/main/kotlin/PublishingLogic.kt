@file:Suppress("unused", "DuplicatedCode")

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign

fun Project.configureMavenPublishing(ctx: Context) {
	mapOf(
		"PUB_SIGNING_KEY" to "signing.key",
		"PUB_SIGNING_ID" to "signing.keyId",
		"PUB_SIGNING_PASSWORD" to "signing.password",
		"PUB_MAVEN_CENTRAL_USERNAME" to "mavenCentralUsername",
		"PUB_MAVEN_CENTRAL_PASSWORD" to "mavenCentralPassword"
	).forEach { (envKey, propKey) ->
		envVar(envKey)?.let { extensions.extraProperties[propKey] = it }
	}

	extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
		if (envFlag("PUB_MAVEN_CENTRAL_ENABLE") && (ctx.isRelease || envFlag("PUB_MAVEN_CENTRAL_SNAPSHOTS"))) {
			publishToMavenCentral()
		}
		signAllPublications()

		coordinates(ctx.modGroup, ctx.modId, version.toString())
		pom {
			name.set(ctx.modName)
			description.set(ctx.description)
			inceptionYear.set(ctx.inceptionYear)
			url.set(ctx.homepageUrl)

			licenses {
				license {
					name.set(ctx.licenseName)
					url.set(ctx.licenseUrl)
					distribution.set(ctx.licenseDist)
				}
			}

			developers {
				project.stonecutter.properties.raw("mod.pom.developers").asList().forEach { devNode ->
					val dev = devNode.asMap()
					developer {
						id.set(dev["id"]?.toString())
						name.set(dev["name"]?.toString())
						url.set(dev["url"]?.toString())
					}
				}
			}

			scm {
				url.set(ctx.sourcesUrl)
				val gitUrl = ctx.sourcesUrl.removeSuffix("/")
				connection.set(gitUrl.replace("https://", "scm:git:git://") + ".git")
				developerConnection.set(gitUrl.replace("https://", "scm:git:ssh://git@") + ".git")
			}
		}
	}
}

fun Project.configureModPublishing(ctx: Context) {
	val releaseType = ctx.channelTag
		?.substringAfter('-')
		?.substringBefore('.')
		?.ifEmpty { "stable" }
		.let { ReleaseType.of(it ?: "stable") }

	extensions.configure<ModPublishExtension>("publishMods") {
		val isMrStaging = envFlag("PUB_MODRINTH_STAGING")

		if (envFlag("PUB_DRY_RUN") || !envFlag("PUB_MODS_ENABLE")) {
			dryRun = true
		}

		val jarTask = tasks.named(ctx.extension.jarTask.get()).map { it as Jar }
		val srcJarTask = tasks.named(ctx.extension.sourcesJarTask.get()).map { it as Jar }

		file.set(jarTask.flatMap(Jar::getArchiveFile))
		additionalFiles.from(srcJarTask.flatMap(Jar::getArchiveFile))

		type = releaseType
		version = ctx.fullVersion
		modLoaders.add(ctx.loader.id)
		changelog.set(rootProject.file("CHANGELOG.md").readText())
		displayName = "${ctx.modName} ${ctx.basicVersion} ${ctx.loader.id.replaceFirstChar { it.titlecase() }} ${ctx.currentMinecraftVersion}"

		modLoaders.add(ctx.loader.id)
		if(ctx.loader.id == FabricManifestGenerator.ID)
			modLoaders.add("quilt")

		val deps = ctx.extension.dependencies
		modrinth(ctx, ctx.publishAdditionalVersions, isMrStaging, envVar("PUB_MODRINTH_TOKEN"), deps)
	}
}

private inline fun Property<String>.ifPresent(action: (String) -> Unit) =
	orNull?.takeIf(String::isNotBlank)?.let(action)

private fun DependenciesConfig.applyToPlatform(
	slugExtractor: (Dependency) -> Property<String>,
	requiredAction: (String) -> Unit,
	optionalAction: (String) -> Unit,
	incompatibleAction: (String) -> Unit,
	embedsAction: (String) -> Unit
) {
	required.forEach { slugExtractor(it).ifPresent(requiredAction) }
	optional.forEach { slugExtractor(it).ifPresent(optionalAction) }
	incompatible.forEach { slugExtractor(it).ifPresent(incompatibleAction) }
	embeds.forEach { slugExtractor(it).ifPresent(embedsAction) }
}

private fun ModPublishExtension.modrinth(
	ctx: Context, additionalVersions: List<String>, staging: Boolean, accessToken: String?, deps: DependenciesConfig
) = modrinth {
	if (staging) apiEndpoint = "https://staging-api.modrinth.com/v2"

	projectId = project.envVar("PUB_MODRINTH_PROJECT_ID")
	this.accessToken = accessToken

	minecraftVersions.addAll(listOf(ctx.currentMinecraftVersion) + additionalVersions)

	if (!staging) {
		deps.applyToPlatform(
			slugExtractor = { it.modrinth },
			requiredAction = { requires(it) },
			optionalAction = { optional(it) },
			incompatibleAction = { incompatible(it) },
			embedsAction = { embeds(it) }
		)
	}
}
