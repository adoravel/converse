import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import kotlin.error
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val Project.stonecutter: StonecutterBuildExtension
	get() = extensions.getByType<StonecutterBuildExtension>()

class ConfigValue<T>(init: () -> T) : ReadOnlyProperty<Any?, T> {
	private val lazyRef = lazy(init)

	val value: T get() = lazyRef.value

	override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		return when (other) {
			is ConfigValue<*> -> this.lazyRef.value == other.lazyRef.value
			else -> this.lazyRef.value == other
		}
	}

	operator fun unaryPlus(): T = value

	override fun hashCode(): Int = lazyRef.value?.hashCode() ?: 0

	override fun toString(): String = "ConfigValue(${lazyRef.value})"
}

fun Project.findStonecutterProperty(vararg key: String): String? {
	val arguments = if (key.size == 1) key[0].split('.').toTypedArray() else key
	return stonecutter.properties.rawOrNull(*arguments)?.toString()?.trim()?.ifBlank { null }
}

fun Project.requireStonecutterProperty(vararg key: String): String =
	findStonecutterProperty(*key)
		?: error("missing required property '${key.joinToString(".")}' in stonecutter.properties.toml")

fun Project.findContextualisedProperty(vararg key: String, loader: String? = null): String? {
	val loader = loader ?: stonecutter.current.project.substringAfter("-")
	val version = stonecutter.current.version
	return findStonecutterProperty(loader, version, *key)
		?: findStonecutterProperty(loader, *key)
		?: findStonecutterProperty(*key)
}

fun Project.requireContextualisedProperty(vararg key: String, loader: String? = null): String =
	findContextualisedProperty(*key, loader = loader)
		?: error("missing required property '${key.joinToString(".")}' in stonecutter.properties.toml")

@OptIn(StonecutterExperimentalAPI::class)
fun Project.stonecutterProperty(vararg key: String): ConfigValue<String> =
	ConfigValue { requireStonecutterProperty(*key) }

fun Project.stonecutter(vararg key: String, loader: String? = null): ConfigValue<String> =
	ConfigValue { requireContextualisedProperty(*key, loader = loader) }

fun Project.optional(vararg key: String, loader: String? = null): ConfigValue<String?> =
	ConfigValue { findContextualisedProperty(*key, loader = loader) }

fun Project.stonecutterOptional(vararg key: String, fallback: String? = null): ConfigValue<String?> =
	ConfigValue { findStonecutterProperty(*key) ?: fallback }

fun Project.list(key: String): ConfigValue<List<String>> =
	ConfigValue {
		stonecutter.properties.rawOrNull(*key.split('.').toTypedArray())
			?.asList()?.map { it.toString().trim() }
			.orEmpty()
	}

fun Project.envVar(key: String): String? =
	providers.environmentVariable(key).orNull

fun Project.envFlag(key: String): Boolean =
	envVar(key) == "true"

fun Project.boolean(key: String, env: Boolean = false): ConfigValue<Boolean> =
	ConfigValue {
		if (env) envFlag(key)
		else stonecutterProperty(key).value == "true"
	}

@OptIn(StonecutterExperimentalAPI::class)
data class Context(
	val project: Project,
	val extension: ModPlatformExtension,
	val loader: LoaderManifestGenerator<*>,
	val stonecutter: StonecutterBuildExtension
) {
	val currentMinecraftVersion: String by lazy { stonecutter.current.version }

	val modId by project.stonecutterProperty("mod.id")
	val modName by project.stonecutterProperty("mod.name")
	val modGroup by project.stonecutterProperty("mod.group")
	val modVersion by project.stonecutterProperty("mod.version")
	val channelTag by project.stonecutterOptional("mod.channel_tag")
	val description by project.stonecutterOptional("mod.description")

	val licenseName by project.stonecutterProperty("mod.license.name")
	val licenseUrl by project.stonecutterProperty("mod.license.url")
	val licenseDist by project.stonecutterOptional("mod.license.dist", "repo")
	val inceptionYear by project.stonecutterOptional("mod.inception_year")

	val authors by project.list("mod.authors")
	val contributors by project.list("mod.contributors")

	val sourcesUrl by project.stonecutterProperty("mod.sources_url")
	val homepageUrl by project.stonecutterProperty("mod.homepage_url")
	val issuesUrl by project.stonecutterProperty("mod.issues_url")
	val discordUrl: String? by project.stonecutterOptional("mod.discord_url")

	val isRelease by project.boolean("MOD_IS_RELEASE", env = true)
	val baseVersion by lazy { "$modVersion${channelTag.orEmpty()}" }
	val snapshotSuffix by lazy { if (!isRelease) "-SNAPSHOT" else "" }
	val fullVersion by lazy { "$baseVersion-${loader.id}+$currentMinecraftVersion$snapshotSuffix" }
	val basicVersion by lazy { "$baseVersion$snapshotSuffix" }

	val publishAdditionalVersions by project.list("publish.additionalVersions")

	val javaVersion: JavaVersion by lazy {
		if (stonecutter.eval(currentMinecraftVersion, ">=26"))
			JavaVersion.VERSION_25
		else
			JavaVersion.VERSION_21
	}
}

