package com.asfine.mythought.navigation

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.asfine.mythought.ui.components.BottomBar
import com.asfine.mythought.ui.history.HistoryScreen
import com.asfine.mythought.ui.write.WriteThoughtScreen
import com.asfine.mythought.viewmodel.ThoughtViewModel
import com.asfine.mythought.ui.dashboard.DashboardScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.asfine.mythought.ui.auth.ProfileScreen
import com.asfine.mythought.ui.detail.ThoughtDetailScreen
import com.asfine.mythought.viewmodel.AuthViewModel
import com.asfine.mythought.viewmodel.DashboardViewModel
import com.asfine.mythought.viewmodel.DetailViewModel


@Composable
fun MainNavigation(
    authVm: AuthViewModel,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    isFingerprintEnabled: Boolean,
    onFingerprintToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val isBiometricAvailable = remember(context) { isBiometricReady(context) }
    var isUnlocked by rememberSaveable(isFingerprintEnabled, isBiometricAvailable) {
        mutableStateOf(!isFingerprintEnabled || !isBiometricAvailable)
    }
    var shouldLockOnNextStart by rememberSaveable(isFingerprintEnabled, isBiometricAvailable) {
        mutableStateOf(false)
    }
    var shouldAutoPrompt by rememberSaveable(isFingerprintEnabled, isBiometricAvailable) {
        mutableStateOf(isFingerprintEnabled && isBiometricAvailable)
    }
    var biometricError by remember { mutableStateOf<String?>(null) }

    val requestBiometricUnlock = {
        if (activity == null) {
            biometricError = "Biometric tidak tersedia. Fingerprint dinonaktifkan."
            onFingerprintToggle(false)
            isUnlocked = true
        } else {
            shouldAutoPrompt = false
            promptBiometric(
                activity = activity,
                onSuccess = {
                    biometricError = null
                    shouldLockOnNextStart = false
                    isUnlocked = true
                },
                onError = { message ->
                    biometricError = message
                }
            )
        }
    }

    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val vm: ThoughtViewModel = viewModel()
    val dashboardVm: DashboardViewModel = viewModel()
    LaunchedEffect(Unit) {
        vm.loadThoughts()
        dashboardVm.loadDashboard()
    }

    LaunchedEffect(isFingerprintEnabled, isBiometricAvailable, activity) {
        if (isFingerprintEnabled && (!isBiometricAvailable || activity == null)) {
            onFingerprintToggle(false)
            biometricError = null
            isUnlocked = true
            shouldAutoPrompt = false
        }
    }

    DisposableEffect(lifecycleOwner, isFingerprintEnabled, isBiometricAvailable) {
        val observer = LifecycleEventObserver { _, event ->
            if (!isFingerprintEnabled || !isBiometricAvailable) {
                return@LifecycleEventObserver
            }
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    shouldLockOnNextStart = true
                }
                Lifecycle.Event.ON_START -> {
                    if (shouldLockOnNextStart) {
                        biometricError = null
                        shouldAutoPrompt = true
                        isUnlocked = false
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isUnlocked, shouldAutoPrompt, activity) {
        if (!isUnlocked && shouldAutoPrompt) {
            requestBiometricUnlock()
        }
    }

    if (!isUnlocked) {
        BiometricLockScreen(
            error = biometricError,
            onUnlock = requestBiometricUnlock
        )
        return
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigateSingleTop(route)
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStack ->
                val id = backStack.arguments
                    ?.getString("id")
                    ?: return@composable
                val vm: DetailViewModel = viewModel()
                LaunchedEffect(id) {
                    vm.load(id)
                }
                vm.thought?.let {
                    ThoughtDetailScreen(
                        thought = it,
                        onUpdate = { newContent ->
                            vm.updateThought(
                                id = id,
                                content = newContent
                            ) {
                                vm.load(id)
                            }
                        },
                        onDelete = {
                            vm.deleteThought(id) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
            composable(Screen.Dashboard.route) {
                dashboardVm.summary?.let { summary ->
                    DashboardScreen(
                        summary = summary,
                        isRefreshing = dashboardVm.isRefreshing,
                        onRefresh = {
                            dashboardVm.loadDashboard()
                        },
                        onWrite = {
                            navController.navigateSingleTop(Screen.Write.route)
                        },
                        onHistory = {
                            navController.navigateSingleTop(Screen.History.route)
                        },
                        onThoughtClick = { thought ->
                            navController.navigate(
                                Screen.Detail.createRoute(thought.id)
                            )
                        }
                    )
                }
            }
            composable(Screen.Write.route) {
                WriteThoughtScreen(
                    isSaving = vm.isSaving,
                    errorMessage = vm.errorMessage,
                    onClearError = vm::clearError,
                    onSave = { content ->
                        vm.saveThought(content) {
                            navController.navigateSingleTop(Screen.History.route)
                        }
                    }
                )
            }
            composable(Screen.History.route) {
                LaunchedEffect(navController.currentBackStackEntry) {
                    vm.loadThoughts()
                }
                HistoryScreen(
                    thoughts = vm.thoughts,
                    isRefreshing = vm.isRefreshing,
                    onRefresh = {
                        vm.loadThoughts()
                    },
                    onThoughtClick = { thought ->
                        navController.navigate(
                            Screen.Detail.createRoute(
                                thought.id
                            )
                        )
                    }
                )
            }

            composable(Screen.Profile.route) {
                val profile = authVm.profile
                ProfileScreen(
                    name = profile?.name ?: "Pengguna",
                    email = profile?.email ?: "-",
                    photoUrl = profile?.photoUrl,
                    isFingerprintEnabled = isFingerprintEnabled,
                    onFingerprintToggle = onFingerprintToggle,
                    isDarkModeEnabled = isDarkModeEnabled,
                    onDarkModeToggle = onDarkModeToggle,
                    onLogout = { authVm.logout(context) }
                )
            }
        }
    }
}

@Composable
private fun BiometricLockScreen(
    error: String?,
    onUnlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Aplikasi terkunci",
            style = MaterialTheme.typography.headlineSmall
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Verifikasi fingerprint untuk melanjutkan.",
            style = MaterialTheme.typography.bodyMedium
        )
        if (!error.isNullOrBlank()) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onUnlock,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buka dengan Fingerprint")
        }
    }
}

private fun isBiometricReady(context: Context): Boolean {
    val manager = BiometricManager.from(context)
    return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
        BiometricManager.BIOMETRIC_SUCCESS
}

private fun promptBiometric(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Verifikasi Fingerprint")
        .setSubtitle("Konfirmasi identitas untuk masuk")
        .setNegativeButtonText("Batal")
        .build()

    val biometricPrompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                onError("Fingerprint tidak cocok. Coba lagi.")
            }
        }
    )
    biometricPrompt.authenticate(promptInfo)
}