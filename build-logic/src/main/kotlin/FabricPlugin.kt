import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withGroovyBuilder

/** version strings starting with "1." are obfuscated (pre-26.x) */
val String.isRemapped: Boolean get() = startsWith("1.")

fun Project.configureFabricDependencies() {
	dependencies {
		"minecraft"("com.mojang:minecraft:${versions.minecraft}")
		"modImplementation"("net.fabricmc:fabric-loader:${versions.fabric.loader}")
	}
}

fun Project.configureFabricMappings() = dependencies.extensions.getByName("loomx").withGroovyBuilder {
	"applyMojangMappings"()
}
