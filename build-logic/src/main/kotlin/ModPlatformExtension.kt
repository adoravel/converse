@file:Suppress("unused")

import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import javax.inject.Inject

abstract class ModPlatformExtension @Inject constructor(private val project: Project) {
	abstract val minimumJavaVersion: Property<JavaVersion>
	abstract val loader: Property<String>
	abstract val jarTask: Property<String>
	abstract val sourcesJarTask: Property<String>

	@get:Nested
	abstract val dependencies: DependenciesConfig

	operator fun String.unaryPlus(): String = project.requireStonecutterProperty(this)
	operator fun String.unaryMinus(): String? = project.findStonecutterProperty(this)

	init {
		minimumJavaVersion.convention(JavaVersion.VERSION_21)
	}

	fun dependencies(action: DependenciesConfig.() -> Unit) = action(dependencies)
}

abstract class DependenciesConfig @Inject constructor(val objects: ObjectFactory) {
	private fun container() = objects.domainObjectContainer(Dependency::class.java)

	val required: NamedDomainObjectContainer<Dependency> = container()
	val optional: NamedDomainObjectContainer<Dependency> = container()
	val incompatible: NamedDomainObjectContainer<Dependency> = container()
	val embeds: NamedDomainObjectContainer<Dependency> = container()

	fun required(modId: String, action: Dependency.() -> Unit): Dependency =
		required.create(modId).also(action)

	fun optional(modId: String, action: Dependency.() -> Unit): Dependency =
		optional.create(modId).also(action)

	fun incompatible(modId: String, action: Dependency.() -> Unit): Dependency =
		incompatible.create(modId).also(action)

	fun embeds(modId: String, action: Dependency.() -> Unit): Dependency =
		embeds.create(modId).also(action)
}

abstract class Dependency @Inject constructor(val project: Project, val name: String) {
	val requires: String? = null

	abstract val modId: Property<String>
	abstract val modrinth: Property<String>
	abstract val curseforge: Property<String>
	abstract val fabricLikeVersionRange: Property<String>
	abstract val forgeLikeVersionRange: Property<String>
	abstract val environment: Property<String>

	init {
		modId.convention(name)
		fabricLikeVersionRange.convention("*")
		forgeLikeVersionRange.convention("(,]")
		environment.convention("both")
	}

	fun slug(modrinth: String, curseforge: String = modrinth): String? {
		this.modrinth.set(modrinth)
		this.curseforge.set(curseforge)
		return null
	}

	fun versionRange(range: VersionRange) {
		fabricLikeVersionRange.set(range.toFabric())
		forgeLikeVersionRange.set(range.toNeoForge())
	}

	infix fun String?.atLeast(version: String?) {
		if (this != null) slug(this)
		versionRange(VersionRange.min(version ?: project.configured(modId.get())!!))
	}

	infix fun String?.exactly(version: String?) {
		if (this != null) slug(this)
		versionRange(VersionRange.exact(version ?: project.configured(modId.get())!!))
	}

	infix fun String?.between(version: VersionRange) {
		if (this != null) slug(this)
		versionRange(version)
	}
}
