package com.asfine.mythought

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.asfine.mythought.core.AppSettingsStore
import com.asfine.mythought.navigation.AppNavigation
import com.asfine.mythought.ui.theme.MyThoughtTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private var keepSplash = true
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            keepSplash
        }
        lifecycleScope.launch {
            delay(1500)
            keepSplash = false
        }
        setContent {
            val settingsStore = remember {
                AppSettingsStore(applicationContext)
            }
            val appScope = rememberCoroutineScope()
            val isDarkModeEnabled by settingsStore.darkModeEnabled
                .collectAsState(initial = false)
            val isFingerprintEnabled by settingsStore.fingerprintEnabled
                .collectAsState(initial = false)

            MyThoughtTheme(darkTheme = isDarkModeEnabled) {
                AppNavigation(
                    isDarkModeEnabled = isDarkModeEnabled,
                    onDarkModeToggle = { enabled ->
                        appScope.launch {
                            settingsStore.setDarkModeEnabled(enabled)
                        }
                    },
                    isFingerprintEnabled = isFingerprintEnabled,
                    onFingerprintToggle = { enabled ->
                        appScope.launch {
                            settingsStore.setFingerprintEnabled(enabled)
                        }
                    }
                )
            }
        }
    }
}