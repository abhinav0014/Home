package com.abster.home.ui.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.abster.home.model.RouterDevice
import com.abster.home.ui.screens.AuthenticationScreen
import com.abster.home.ui.screens.DiscoveryScreen
import com.abster.home.ui.screens.OnboardingScreen
import com.abster.home.ui.screens.SplashScreen

enum class NavState {
    ONBOARDING, SPLASH, DISCOVERY, AUTH, MAIN
}

@Composable
fun AppNavigator() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val isOnboardingCompletedInitial = remember { sharedPrefs.getBoolean("onboarding_completed", false) }
    
    var currentState by rememberSaveable { 
        mutableStateOf(if (isOnboardingCompletedInitial) NavState.SPLASH else NavState.ONBOARDING) 
    }

    var selectedDevice by remember { mutableStateOf<RouterDevice?>(null) }
    var routerHost by rememberSaveable { mutableStateOf("") }
    var routerUser by rememberSaveable { mutableStateOf("root") }
    var routerPass by rememberSaveable { mutableStateOf("") }

    when (currentState) {
        NavState.ONBOARDING -> {
            OnboardingScreen(onFinished = {
                sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                currentState = NavState.DISCOVERY // Go to discovery after onboarding
            })
        }
        NavState.SPLASH -> {
            SplashScreen(onTimeout = { currentState = NavState.DISCOVERY })
        }
        NavState.DISCOVERY -> {
            DiscoveryScreen(onDeviceSelected = { device ->
                selectedDevice = device
                currentState = NavState.AUTH
            })
        }
        NavState.AUTH -> {
            selectedDevice?.let { device ->
                AuthenticationScreen(
                    device = device,
                    onBack = { currentState = NavState.DISCOVERY },
                    onAuthenticated = { host, user, pass ->
                        routerHost = host
                        routerUser = user
                        routerPass = pass
                        currentState = NavState.MAIN
                    }
                )
            }
        }
        NavState.MAIN -> {
            HomeApp(
                initialHost = routerHost,
                initialUser = routerUser,
                initialPass = routerPass,
                onLogout = {
                    routerPass = ""
                    currentState = NavState.DISCOVERY
                }
            )
        }
    }
}
