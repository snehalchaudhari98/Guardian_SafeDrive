plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.safedrive_guardian"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.safedrive_guardian"
        minSdk = 21 // 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
//    implementation("androidx.navigation:navigation-fragment:2.7.1")
//    implementation("androidx.navigation:navigation-ui:2.7.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.exifinterface:exifinterface:1.3.4")
//    ml
    implementation("com.google.mlkit:image-labeling:17.0.7")
    implementation("com.google.mlkit:image-labeling-custom:17.0.1")
    implementation("com.google.mlkit:object-detection:17.0.0")
    implementation("com.google.mlkit:face-detection:16.1.5")
//     androidx camera
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-core:1.1.0-alpha12")
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("com.google.maps.android:android-maps-utils:0.6.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
//    implementation("com.google.android.gms:play-services-location:18.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.json:json:20210307")
}