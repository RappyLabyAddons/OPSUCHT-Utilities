plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "org.example"
version = providers.environmentVariable("VERSION").getOrElse("1.2.2")

labyMod {
    defaultPackageName = "com.rappytv.opsucht"

    addonInfo {
        namespace = "opsucht"
        displayName = "OPSUCHT Utilities"
        author = "RappyTV"
        description = "Adds some ingame utilities for OPSUCHT.net (Inofficial addon)"
        minecraftVersion = "*"
        version = rootProject.version.toString()
    }

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    devLogin = true
                }
            }
        }
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}
