import org.gradle.api.Project
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class VersionRange(
	val min: String?,
	val max: String?,
	val minInclusive: Boolean = true,
	val maxInclusive: Boolean = false
) : ReadOnlyProperty<Any?, String> {

	override fun getValue(thisRef: Any?, property: KProperty<*>): String = toString()

	companion object {
		val Any = VersionRange(null, null)

		/** Exact version match */
		fun exact(version: String) = VersionRange(version, version, minInclusive = true, maxInclusive = true)

		/** Minimum version inclusive (e.g., >=1.21.1 or [1.21.1,)) */
		fun min(version: String) = VersionRange(version, null, true, maxInclusive = false)
	}

	fun toNeoForge(): String = when {
		min == null && max == null -> ""
		min != null && max == null -> if (minInclusive) "[$min,)" else "($min,)"
		min == null && max != null -> if (maxInclusive) "(,$max]" else "(,$max)"
		else -> {
			val left = if (minInclusive) "[" else "("
			val right = if (maxInclusive) "]" else ")"
			"$left$min,$max$right"
		}
	}

	fun toFabric(): String = when {
		min == null && max == null -> "*"
		min != null && max == null -> if (minInclusive) ">=$min" else ">$min"
		min == null && max != null -> if (maxInclusive) "<=$max" else "<$max"
		else -> {
			val left = if (minInclusive) ">=$min" else ">$min"
			val right = if (maxInclusive) "<=$max" else "<$max"
			"$left $right"
		}
	}

	override fun toString(): String = "VersionRange(min=$min, max=$max)"

	operator fun rangeTo(other: String): VersionRange {
		require(min != null) { "cannot create range from a VersionRange with no minimum" }
		return VersionRange(min, other, minInclusive, maxInclusive)
	}
}

/** Allows syntax: "1.21"..="1.21.11" */
operator fun String.rangeTo(other: String): VersionRange =
	VersionRange(this, other, minInclusive = true, maxInclusive = true)

/** "1.21".."1.21.11" (exclusive upper bound) */
infix fun String.until(other: String): VersionRange =
	VersionRange(this, other, minInclusive = true, maxInclusive = false)

fun String.min() = VersionRange.min(this)

/** Allows syntax: "1.21".exact() */
fun String.exact() = VersionRange.exact(this)

fun Project.configured(vararg path: String): String? {
	if (path.isEmpty()) return null
	val loader = project.stonecutter.current.project.substringAfterLast('-')
	return project.findStonecutterProperty("deps", *path)
		?: project.findStonecutterProperty(loader, "deps", *path)
		?: project.requireStonecutterProperty(loader, project.stonecutter.current.version, "deps", *path)
}
