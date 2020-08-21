plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("name.remal.check-dependency-updates") version "1.0.211"
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Application.compileSdk)
    defaultConfig {
        minSdkVersion(Application.minSdk)
        targetSdkVersion(Application.targetSdk)
        versionCode = Application.versionCode
        versionName = Application.versionName
        multiDexEnabled = true
        setProperty("archivesBaseName", "v$versionName ($versionCode)")
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/library_release.kotlin_module")
    }

    compileOptions {
        sourceCompatibility = Application.sourceCompat
        targetCompatibility = Application.targetCompat
    }

    kotlinOptions {
        jvmTarget = Application.jvmTarget
    }
}

dependencies {
    fun def(vararg strings: String) {
        for (string in strings) implementation(string)
    }

    def(
        Dependencies.Firebase.Config,
        Dependencies.Firebase.Storage,
        Dependencies.Firebase.Database,
        Dependencies.Firebase.Messaging,

        Dependencies.Network.GoogleAuth,
        Dependencies.Network.HttpComponents,
        Dependencies.Network.KakaoLogin,
        Dependencies.Network.CommonsIo,
        Dependencies.Network.Jsoup,
        Dependencies.Network.Retrofit,
        Dependencies.Network.OkHttp,

        Dependencies.Rx.Kotlin,
        Dependencies.Rx.Android,
        Dependencies.Rx.Retrofit,

        Dependencies.Essential.AppCompat,
        Dependencies.Essential.Anko,
        Dependencies.Essential.Kotlin,

        Dependencies.Ktx.Core,
        Dependencies.Ktx.Fragment,

        Dependencies.Di.Hilt,

        Dependencies.Ui.YoYo,
        Dependencies.Ui.RichEditor,
        Dependencies.Ui.TedImagePicker,
        Dependencies.Ui.SmartTabLayout,
        Dependencies.Ui.MaterialSpinner,
        Dependencies.Ui.Lottie,
        Dependencies.Ui.MaterialPopupMenu,
        Dependencies.Ui.MarkdownView,
        Dependencies.Ui.SimpleCodeEditor,
        Dependencies.Ui.ToggleBottomLayout,
        Dependencies.Ui.JsonViewer,
        Dependencies.Ui.SweetDialog,
        Dependencies.Ui.Licenser,
        Dependencies.Ui.FileDialog,
        Dependencies.Ui.ChatKit,
        Dependencies.Ui.Material,
        Dependencies.Ui.Glide,
        Dependencies.Ui.CardView,
        Dependencies.Ui.ConstraintLayout,

        Dependencies.Utils.YoyoHelper,
        Dependencies.Utils.HangulParser,
        Dependencies.Utils.AndroidUtils,
        Dependencies.Utils.CrashReporter
    )

    kapt(Dependencies.Di.HiltCompiler)
    kapt(Dependencies.Utils.GlideCompiler)
}