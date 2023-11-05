plugins {
    kotlin("jvm") version "1.9.10"
    id("dev.architectury.loom").version("1.3.358")
    id("io.github.juuxel.loom-vineflower").version("1.11.0")
}

base {
    archivesName.set(properties["archives_base_name"].toString())
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

loom {
    // use this if you are using the official mojang mappings
    // and want loom to stop warning you about their license
    silentMojangMappingsLicense()

    // since loom 0.10, you are **required** to use the
    // "forge" block to configure forge-specific features,
    // such as the mixinConfigs array or datagen
    forge {
        // specify the mixin configs used in this mod
        // this will be added to the jar manifest as well!
        mixinConfigs.add("teawieks.mixins.json")

        // missing access transformers?
        // don't worry, you can still use them!
        // note that your AT *MUST* be located at
        // src/main/resources/META-INF/accesstransformer.cfg
        // to work as there is currently no config option to change this.
        // also, any names used in your access transformer will need to be
        // in SRG mapped ("func_" / "field_" with MCP class names) to work!
        // (both of these things may be subject to change in the future)
    }
}

repositories {
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    githubMaven("AppliedEnergistics/Applied-Energistics-2")
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Curios"
        url = uri("https://maven.theillusivec4.top/")
    }
    maven {
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        url = uri("https://maven.blamejared.com")
    }
    maven {
        url = uri("https://maven.theillusivec4.top")
    }
//    exclusiveContent {
//        forRepository {
//            maven {
//                name = "CurseMaven"
//                url = uri("https://cursemaven.com")
//            }
//        }
//        filter {
//            includeGroup("curse.maven")
//        }
//    }
}

fun githubMaven(repo: String) {
    repositories {
        maven {
            name = "Github Maven: $repo"
            url = uri("https://maven.pkg.github.com/$repo")
            credentials {
                username = properties["gpr.user"].toString()
                password = properties["gpr.key"].toString()
            }
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })
    forge("net.minecraftforge:forge:${properties["forge_version"]}")

    modImplementation("com.github.Virtuoel:Pehkui:${properties["pehkui_version"]}-1.20.1-forge")

    modCompileOnly("dev.emi:emi-forge:${properties["emi_version"]}:api")
    modRuntimeOnly("dev.emi:emi-forge:${properties["emi_version"]}")

    modImplementation("appeng:appliedenergistics2-forge:${properties["ae2_version"]}")

    modImplementation("maven.modrinth:pNabrMMw:${properties["ae2wtlib_version"]}-forge")

    modImplementation(files("libs/hexcasting-forge-1.20.1-0.11.1-7.jar"))

    // Sophisticated Series also has github maven but
    // its artifacts are..... cursed
//    modImplementation("curse.maven:sophisticated-backpacks-422301:4808060")
//    modImplementation("curse.maven:sophisticated-storage-619320:4808065")
//    modImplementation("curse.maven:sophisticated-core-618298:4808231")

    modRuntimeOnly("vazkii.patchouli:Patchouli:1.20.1-81-FORGE")
    modRuntimeOnly(files("libs/paucal-forge-1.20.1-0.6.0.jar"))
    modRuntimeOnly("top.theillusivec4.caelus:caelus-forge:3.1.0+1.20")

    modRuntimeOnly("top.theillusivec4.curios:curios-forge:5.4.2+1.20.1")
    modRuntimeOnly("dev.architectury:architectury-forge:9.1.12")
    modRuntimeOnly("me.shedaniel.cloth:cloth-config-forge:11.1.106")

    implementation("thedarkcolour:kotlinforforge:4.5.0")
}

tasks.processResources {
    // define properties that can be used during resource processing
    inputs.property("version", project.version)

    // this will replace the property "${version}" in your mods.toml
    // with the version you've defined in your gradle.properties
    filesMatching("META-INF/mods.toml") {
        expand(
            "version" to project.version,
            "mod_id" to properties["mod_id"],
            "author" to properties["mod_author"]
        )
    }
}

tasks.withType<JavaCompile> {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release = 17
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}

tasks.jar {
    // add some additional metadata to the jar manifest
    manifest {
        attributes(mapOf(
            "Specification-Title"      to properties["mod_id"],
            "Specification-Vendor"     to properties["mod_author"],
            "Specification-Version"    to "1",
            "Implementation-Title"     to project.name,
            "Implementation-Version"   to version,
            "Implementation-Vendor"    to properties["mod_author"],
//            "Implementation-Timestamp" to Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
        ))
    }
}
