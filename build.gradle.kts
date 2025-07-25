plugins {
    id("dev.isxander.modstitch.base") version "0.5.14-unstable"
    id("dev.kikugie.stonecutter")
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val minecraft = property("deps.minecraft") as String;

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaTarget = when (minecraft) {
        "1.19.4" -> 17
        "1.20.1" -> 17
        "1.20.6" -> 21
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = "dedicatedmcupnp"
        modName = "UPNP for Dedicated Servers"
        modVersion = "2.1.1+$name"
        modGroup = "pigcart"
        modAuthor = "PigCart"
        modLicense = "MIT"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_issue_tracker", "https://github.com/pigcart/dedicatedmcupnp/issues")
            put("version_range", property("version_range") as String)
            if (isModDevGradleLegacy) {
                put("forge_or_neoforge", "forge")
            } else {
                put("forge_or_neoforge", "neoforge")
            }
        }
    }

    loom {
        fabricLoaderVersion = "0.16.10"
    }

    moddevgradle {
        enable {
            prop("deps.forge") { forgeVersion = it }
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
            prop("deps.mcp") { mcpVersion = it }
        }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }
}

tasks.named("generateModMetadata") {
    dependsOn("stonecutterGenerate")
}
modstitch.moddevgradle {
    tasks.named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge")
    )
}

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricapi")}")
    }
}