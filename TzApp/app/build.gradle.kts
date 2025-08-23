plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.example.tzapp"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.tzapp"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"
		vectorDrawables.useSupportLibrary = true
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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.10"
	}

	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	val composeBom = platform("androidx.compose:compose-bom:2024.09.01")
	implementation(composeBom)
	androidTestImplementation(composeBom)

	implementation("androidx.activity:activity-compose:1.8.2")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.navigation:navigation-compose:2.7.7")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

	// Room (for Planner local DB and Favorites offline)
	implementation("androidx.room:room-runtime:2.6.1")
	ksp("androidx.room:room-compiler:2.6.1")
	implementation("androidx.room:room-ktx:2.6.1")

	// DataStore Preferences (settings, small state)
	implementation("androidx.datastore:datastore-preferences:1.1.1")

	// WorkManager (reminders)
	implementation("androidx.work:work-runtime-ktx:2.9.0")

	// PDF export via Android Print framework (no extra dep needed), but bring accompanist-permissions if needed later

	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}

