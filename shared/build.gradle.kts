import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias (libs.plugins.sqlDelight)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidKmpLibrary)

}
sqldelight {
    databases {
        create("ReadLaterDatabase") {
            packageName.set("com.charan.readlater")
            generateAsync.set(true)
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))

        }
    }
}

kotlin {
    android {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        namespace = "com.charan.readlater.shared"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().forEach{
        it.binaries.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
            .forEach { lib ->
                lib.linkerOpts.add("-lsqlite3")
            }
    }

    jvm()


    sourceSets {
        androidMain.dependencies {

            // SQLDelight
            implementation(libs.android.driver)
            // AndroidX SQLite helpers
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.sqlite.framework)
            // WorkManager
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.androidx.workmanager)
            // Koin
            implementation(libs.koin.android )
            implementation(libs.koin.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(libs.runtime)
            implementation(libs.jetbrains.foundation)
            implementation(libs.jetbrains.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)

            // androidx
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // koin
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose.viewModel)

            //SqlDelight
            implementation(libs.coroutines.extensions)

            // material
            implementation(libs.material3)
            implementation(libs.material.icons.core)
            implementation(libs.material.icons.extended)
            implementation(libs.material.kolor)

            // Supabase
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.compose.auth.kt)
            implementation(libs.auth.kt)
            implementation(libs.postgrest.kt)

            // ktor
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Navigation
            implementation(libs.navigation.compose)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.serialization.kotlinx.serialization.csv)

            // DataStore
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

            // HTML Parsing
            implementation(libs.ksoup)
            implementation(libs.ksoup.network)

            // Image Loading
            implementation(libs.coil.compose)

            // File Handling
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.dialogs)

            // Authentication
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.uihelper)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            // Coroutine
            implementation(libs.kotlinx.coroutinesSwing)
            // SQLDelight
            implementation(libs.sqlite.driver)
            // Ktor
            implementation(libs.ktor.client.cio)
        }
        iosMain.dependencies {
            // SQLDelight
            implementation(libs.native.driver)
            // Ktor
            implementation(libs.ktor.client.darwin)

        }
    }
}
buildkonfig {
    packageName = "com.charan.readlater"

    defaultConfigs {
        val supabaseUrl: String = System.getenv("SUPABASE_URL")
            ?: gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_URL")
            ?: ""
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)

        val supabaseAnonKey: String = System.getenv("SUPABASE_ANONKEY")
            ?: gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_ANONKEY")
            ?: ""
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_ANONKEY", supabaseAnonKey)

        val googleServerId: String = System.getenv("GOOGLE_SERVER_ID")
            ?: gradleLocalProperties(rootDir, providers).getProperty("GOOGLE_SERVER_ID")
            ?: ""
        buildConfigField(FieldSpec.Type.STRING, "GOOGLE_SERVER_ID", googleServerId)
    }

    defaultConfigs("dev"){
        val supabaseUrl: String = System.getenv("SUPABASE_URL_DEBUG")
            ?: gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_URL_DEBUG")
            ?: ""
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)

        val supabaseAnonKey: String = System.getenv("SUPABASE_ANONKEY_DEBUG")
            ?: gradleLocalProperties(rootDir, providers).getProperty("SUPABASE_ANONKEY_DEBUG")
            ?: ""
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_ANONKEY", supabaseAnonKey)

    }
}

tasks.configureEach {
    if (name == "prepareAndroidMainArtProfile") {
        dependsOn(tasks.named("generateBuildKonfig"))
    }
}

