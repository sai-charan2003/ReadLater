import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias (libs.plugins.sqlDelight)
//    alias(libs.plugins.googleServices)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlin.serialization)
    id("io.github.frankois944.spmForKmp") version "1.0.0-Beta03"
}
sqldelight {
    databases {
        create("ReadLaterDatabase") {
            packageName.set("com.charan.readlater")
            generateAsync.set(true)
        }
    }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
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

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
////        outputModuleName = "composeApp"
//        browser {
//            val rootDirPath = project.rootDir.path
//            val projectDirPath = project.projectDir.path
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(rootDirPath)
//                        add(projectDirPath)
//                    }
//                }
//            }
//        }
//        binaries.executable()
//    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.android.driver)
            implementation(libs.koin.compose)
            implementation(libs.firebase.config)
            implementation(libs.ktor.client.okhttp)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
//            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            api(libs.koin.core)
            implementation(libs.koin.compose.viewModel)
            implementation(libs.coroutines.extensions)
            implementation(libs.auth.kt)
            implementation(libs.postgrest.kt)
            implementation(libs.ktor.client.core)
            implementation(libs.material3)
            implementation(libs.material.icons.core)
            implementation(libs.material.kolor)
            implementation(libs.compose.auth.kt)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.uihelper)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.ksoup)
            implementation(libs.ksoup.network)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(compose.materialIconsExtended)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.dialogs)
            implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:3.0.2")



        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqlite.driver)
            implementation(libs.ktor.client.cio)
        }
//        wasmJsMain.dependencies {
//            implementation(libs.web.worker.driver)
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
//            implementation(npm("sql.js", "1.6.2"))
//            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
//            implementation(devNpm("path-browserify", "1.0.1"))
//        }
        iosMain.dependencies {
            implementation(libs.native.driver)
            implementation(libs.ktor.client.darwin)

        }
    }
}

android {
    namespace = "com.charan.readlater"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.charan.readlater"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.charan.readlater.MainKt"


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.charan.readlater"
            packageVersion = "1.0.0"
            macOS {
                packageName = "ReadLater"
                iconFile.set(project.file("src/commonMain/composeResources/drawable/AppIcon.icns"))
            }

        }
    }
}
buildkonfig {
    packageName = "com.charan.readlater"
    defaultConfigs {
        val supabaseUrl : String = gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL")
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)
        val supabaseAnonKey : String = gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANONKEY")
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_ANONKEY", supabaseAnonKey)
        val googleServerId : String = gradleLocalProperties(rootDir,providers).getProperty("GOOGLE_SERVER_ID")
        buildConfigField(FieldSpec.Type.STRING, "GOOGLE_SERVER_ID", googleServerId)


    }
}


