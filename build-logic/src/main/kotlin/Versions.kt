import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

abstract class Versions(val project: Project) {
	inner class Fabric {
		val loader by project.stonecutter("deps", "fabric-loader", loader = "fabric")

		val api by project.stonecutter("deps", "fabric-api", loader = "fabric")

		val modMenu by project.stonecutter("deps", "modmenu", loader = "fabric")
	}

	val fabric = Fabric()

	val minecraft by lazy {
		project.stonecutter.current.version
	}

	val yacl by project.stonecutter("deps", "yacl")
}

val Project.versions: Versions
	get() = extensions.findByType<Versions>() ?: error("oopsie where are the versions qwq")
