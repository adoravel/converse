import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class FabricManifest(
	val schemaVersion: Int = 1,
	val id: String,
	val version: String,
	val name: String,
	val description: String?,
	val authors: List<String>,
	val license: String,
	val contributors: List<String>,
	val contact: Map<String, String>,
	val custom: JsonObject,
	val icon: String,
	val environment: String = "*",
	val entrypoints: Map<String, List<String>>,
	val mixins: List<String>,
	val depends: Map<String, String> = emptyMap(),
	val recommends: Map<String, String> = emptyMap(),
	val breaks: Map<String, String> = emptyMap(),
	val provides: List<String> = emptyList()
)

@Serializable
data class NeoForgeManifest(
	val modLoader: String = "javafml",
	val loaderVersion: String = "[2,)",
	val issueTrackerURL: String = "",
	val license: String,
	val mods: List<NeoForgeModDefinition> = emptyList(),
	val dependencies: Map<String, List<NeoForgeDependencyDefinition>> = emptyMap(),
	val mixins: List<NeoForgeMixinConfig> = emptyList()
)


@Serializable
data class NeoForgeModDefinition(
	@SerialName("modId")
	val id: String,
	@SerialName("displayName")
	val name: String,
	val version: String,
	val displayURL: String = "",
	val logoFile: String = "",
	val authors: String,
	val credits: String = "",
	val logoBlur: Boolean = false,
	val description: String?
)

@Serializable
data class NeoForgeDependencyDefinition(
	val modId: String,
	val side: String,
	val versionRange: String,
	val mandatory: Boolean,
	val type: String
)

@Serializable
data class NeoForgeMixinConfig(val config: String)
