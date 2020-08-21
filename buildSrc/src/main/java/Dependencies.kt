import org.gradle.api.JavaVersion

object Application {
    const val minSdk = 23
    const val targetSdk = 30
    const val compileSdk = 30
    const val jvmTarget = "1.8"
    const val versionCode = 1
    const val versionName = "1.0.0"

    val targetCompat = JavaVersion.VERSION_1_8
    val sourceCompat = JavaVersion.VERSION_1_8
}

object Versions {
    object Firebase {
        const val Core = "17.5.0"
        const val Auth = "19.3.2"
        const val Config = "19.2.0"
        const val Storage = "19.1.1"
        const val Database = "19.3.1"
        const val Messaging = "20.2.4"
    }

    object Network {
        const val GoogleService = "3.0.0"
        const val Auth = "18.1.0"
        const val HttpComponents = "4.3.5.1"
        const val KakaoLogin = "1.30.1"
        const val CommonsIo = "1.3.2"
        const val Jsoup = "1.13.1"
        const val Retrofit = "2.9.0"
        const val OkHttp = "4.8.1"
    }

    object Rx {
        const val Kotlin = "3.0.0"
        const val Android = "3.0.0"
        const val Retrofit = "2.9.0"
    }

    object Essential {
        const val AppCompat = "1.2.0"
        const val Anko = "0.10.8"
        const val Kotlin = "1.4.0"
        const val Gradle = "4.0.1"
    }

    object Ktx {
        const val Core = "1.3.1"
        const val Fragment = "2.3.0"
    }

    object Di {
        const val Hilt = "2.28-alpha"
    }

    object Ui {
        const val YoYo = "2.3@aar"
        const val RichEditor = "1.2.2"
        const val TedImagePicker = "1.1.3"
        const val SmartTabLayout = "2.0.0@aar"
        const val MaterialSpinner = "1.3.1"
        const val Lottie = "3.4.1"
        const val MaterialPopupMenu = "4.0.1"
        const val MarkdownView = "0.19.0"
        const val SimpleCodeEditor = "1.0.2"
        const val ToggleBottomLayout = "1.3.0"
        const val JsonViewer = "v1.1"
        const val SweetDialog = "1.6.2"
        const val Licenser = "2.0.0"
        const val FileDialog = "1.0"
        const val ChatKit = "0.3.3"
        const val Material = "1.2.0"
        const val Glide = "4.11.0"
        const val CardView = "1.0.0"
        const val ConstraintLayout = "1.1.3"
    }

    object Utils {
        const val YoYoHelper = "2.1@aar"
        const val HangulParser = "1.0.0"
        const val AndroidUtils = "3.2.0"
        const val CarshReporter = "1.1.0"
    }
}

object Dependencies {
    object Firebase {
        const val Auth = "com.google.firebase:firebase-auth:${Versions.Firebase.Auth}"
        const val Config = "com.google.firebase:firebase-config:${Versions.Firebase.Config}"
        const val Storage = "com.google.firebase:firebase-storage:${Versions.Firebase.Storage}"
        const val Database = "com.google.firebase:firebase-database:${Versions.Firebase.Database}"
        const val Messaging =
            "com.google.firebase:firebase-messaging:${Versions.Firebase.Messaging}"
    }

    object Network {
        const val GoogleAuth = "com.google.android.gms:play-services-auth:${Versions.Network.Auth}"
        const val HttpComponents =
            "org.apache.httpcomponents:httpclient-android:${Versions.Network.HttpComponents}"
        const val KakaoLogin = "com.kakao.sdk:usermgmt:${Versions.Network.KakaoLogin}"
        const val CommonsIo = "org.apache.commons:commons-io:${Versions.Network.CommonsIo}"
        const val Jsoup = "org.jsoup:jsoup:${Versions.Network.Jsoup}"
        const val Retrofit = "com.squareup.okhttp3:okhttp:${Versions.Network.OkHttp}"
        const val OkHttp = "com.squareup.retrofit2:retrofit:${Versions.Network.Retrofit}"
    }

    object Rx {
        const val Kotlin = "io.reactivex.rxjava3:rxkotlin:${Versions.Rx.Kotlin}"
        const val Android = "io.reactivex.rxjava3:rxandroid:${Versions.Rx.Android}"
        const val Retrofit = "com.squareup.retrofit2:adapter-rxjava3:${Versions.Rx.Retrofit}"
    }

    object Essential {
        const val AppCompat = "androidx.appcompat:appcompat:${Versions.Essential.AppCompat}"
        const val Anko = "org.jetbrains.anko:anko:${Versions.Essential.Anko}"
        const val Kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Essential.Kotlin}"
    }

    object Ktx {
        const val Core = "androidx.core:core-ktx:${Versions.Ktx.Core}"
        const val Fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.Ktx.Fragment}"
    }

    object Di {
        const val Hilt = "com.google.dagger:hilt-android:${Versions.Di.Hilt}"
        const val HiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Di.Hilt}"
    }

    object Ui {
        const val CaulyAd = "libs/CaulySDK-3.4.23.jar"
        const val YoYo = "com.daimajia.androidanimations:library:${Versions.Ui.YoYo}"
        const val RichEditor = "jp.wasabeef:richeditor-android:${Versions.Ui.RichEditor}"
        const val TedImagePicker = "gun0912.ted:tedimagepicker:${Versions.Ui.TedImagePicker}"
        const val SmartTabLayout =
            "com.ogaclejapan.smarttablayout:library:${Versions.Ui.SmartTabLayout}"
        const val MaterialSpinner =
            "com.jaredrummler:material-spinner:${Versions.Ui.MaterialSpinner}"
        const val Lottie = "com.airbnb.android:lottie:${Versions.Ui.Lottie}"
        const val MaterialPopupMenu =
            "com.github.zawadz88.materialpopupmenu:material-popup-menu:${Versions.Ui.MaterialPopupMenu}"
        const val MarkdownView =
            "com.github.tiagohm.MarkdownView:library:${Versions.Ui.MarkdownView}"
        const val SimpleCodeEditor =
            "com.github.sungbin5304:SimpleCodeEditor:${Versions.Ui.SimpleCodeEditor}"
        const val ToggleBottomLayout =
            "com.github.savvyapps:ToggleButtonLayout:${Versions.Ui.ToggleBottomLayout}"
        const val JsonViewer = "com.github.pvarry:android-json-viewer:${Versions.Ui.JsonViewer}"
        const val SweetDialog = "com.github.f0ris.sweetalert:library:${Versions.Ui.SweetDialog}"
        const val Licenser = "com.github.marcoscgdev:Licenser:${Versions.Ui.Licenser}"
        const val FileDialog = "com.github.rustamg:file-dialogs:${Versions.Ui.FileDialog}"
        const val ChatKit = "com.github.stfalcon:chatkit:${Versions.Ui.ChatKit}"
        const val Material = "com.google.android.material:material:${Versions.Ui.Material}"
        const val Glide = "com.github.bumptech.glide:glide:${Versions.Ui.Glide}"
        const val CardView = "androidx.cardview:cardview:${Versions.Ui.CardView}"
        const val ConstraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.Ui.ConstraintLayout}"
    }

    object Utils {
        const val GlideCompiler = "com.github.bumptech.glide:compiler:${Versions.Ui.Glide}"
        const val YoyoHelper = "com.daimajia.easing:library:${Versions.Utils.YoYoHelper}"
        const val HangulParser = "com.github.kimkevin:hangulparser:${Versions.Utils.HangulParser}"
        const val AndroidUtils = "com.github.sungbin5304:AndroidUtils:${Versions.Utils.AndroidUtils}"
        const val CrashReporter = "com.balsikandar.android:crashreporter:${Versions.Utils.CarshReporter}"
    }
}