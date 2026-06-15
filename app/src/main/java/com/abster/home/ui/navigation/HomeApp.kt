package com.abster.home.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abster.home.data.network.XHRManager
import com.abster.home.data.repository.RouterRepository
import com.abster.home.data.ssh.SSHManager
import com.abster.home.domain.model.RouterAbstraction
import com.abster.home.model.AppDestinations
import com.abster.home.model.SettingsTab
import com.abster.home.ui.components.Header
import com.abster.home.ui.screens.*
import com.abster.home.ui.viewmodel.RouterViewModel

@Composable
fun HomeApp(
    initialHost: String,
    initialUser: String,
    initialPass: String,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel: RouterViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val xhrManager = XHRManager(scope)
                val sshManager = SSHManager(initialHost, 22, initialUser, initialPass)
                val repository = RouterRepository(xhrManager, sshManager, scope)
                return RouterViewModel(repository) as T
            }
        }
    )

    val abstraction by viewModel.abstraction.collectAsState()
    val isUpdating by viewModel.isProcessing.collectAsState()
    val stats by viewModel.stats.collectAsState()

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    
    // Global State for Toggles
    var isMain5GEnabled by rememberSaveable { mutableStateOf(true) }
    var isGuestWifiEnabled by rememberSaveable { mutableStateOf(false) }
    var isPersonal24GEnabled by rememberSaveable { mutableStateOf(true) }
    var isPersonal5GEnabled by rememberSaveable { mutableStateOf(true) }
    var isGuestAccessEnabled by rememberSaveable { mutableStateOf(false) }

    // Settings State
    var selectedOperationMode by rememberSaveable { mutableStateOf("Access Point (AP)") }
    var currentSettingsTab by rememberSaveable { mutableStateOf(SettingsTab.NONE) }
    
    // Auth State
    var routerHost by rememberSaveable { mutableStateOf(initialHost) }
    var routerUser by rememberSaveable { mutableStateOf(initialUser) }
    var routerPass by rememberSaveable { mutableStateOf(initialPass) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                        currentDestination = it
                        currentSettingsTab = SettingsTab.NONE
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (!((currentDestination == AppDestinations.SETTINGS) && (currentSettingsTab != SettingsTab.NONE))) {
                    Header(
                        title = when (currentDestination) {
                            AppDestinations.DASHBOARD -> "Home"
                            AppDestinations.CONFIG -> "Configuration"
                            AppDestinations.DEVICES -> "Devices"
                            AppDestinations.SETTINGS -> "Settings"
                        },
                        hasStatusBadge = currentDestination == AppDestinations.DASHBOARD,
                        statusText = if (isUpdating) "UPDATING..." else "LIVE TRAFFIC",
                        onProfileClick = { /* Handle profile click */ }
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize().padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                if ((currentDestination == AppDestinations.SETTINGS) && (currentSettingsTab != SettingsTab.NONE)) {
                    when (currentSettingsTab) {
                        SettingsTab.WIFI_PASSWORD -> WifiPasswordScreen(onBack = { currentSettingsTab = SettingsTab.NONE })
                        SettingsTab.ROUTER_PASSWORD -> RouterPasswordScreen(onBack = { currentSettingsTab = SettingsTab.NONE })
                        SettingsTab.SYSTEM_REFRESH -> SystemRefreshScreen(onBack = { currentSettingsTab = SettingsTab.NONE })
                        SettingsTab.OPERATION_MODE -> OperationModeScreen(
                            selectedMode = selectedOperationMode,
                            onModeSelected = { selectedOperationMode = it },
                            onBack = { currentSettingsTab = SettingsTab.NONE }
                        )
                        SettingsTab.ROUTER_AUTH -> RouterAuthScreen(
                            host = routerHost,
                            user = routerUser,
                            pass = routerPass,
                            onUpdate = { h, u, p -> routerHost = h; routerUser = u; routerPass = p },
                            onBack = { currentSettingsTab = SettingsTab.NONE }
                        )
                        SettingsTab.INTERFACE_CUSTOMIZATION -> InterfaceCustomizationScreen(
                            abstraction = abstraction,
                            onUpdate = { /* Local update if needed */ },
                            onBack = { currentSettingsTab = SettingsTab.NONE }
                        )
                        else -> {}
                    }
                } else {
                    when (currentDestination) {
                        AppDestinations.DASHBOARD -> DashboardScreen(
                            isMain5GEnabled = isMain5GEnabled,
                            onMain5GToggle = { isMain5GEnabled = it; viewModel.onToggleWifi("radio1", it) },
                            isGuestWifiEnabled = isGuestWifiEnabled,
                            onGuestWifiToggle = { isGuestWifiEnabled = it; viewModel.onToggleWifi("default_radio1", it) },
                            abstraction = abstraction,
                            isUpdating = isUpdating
                        )
                        AppDestinations.CONFIG -> ConfigScreen(
                            selectedMode = selectedOperationMode,
                            isPersonal24GEnabled = isPersonal24GEnabled,
                            onPersonal24GToggle = { isPersonal24GEnabled = it; viewModel.onToggleWifi("radio0", it) },
                            isPersonal5GEnabled = isPersonal5GEnabled,
                            onPersonal5GToggle = { isPersonal5GEnabled = it; viewModel.onToggleWifi("radio1", it) },
                            isGuestAccessEnabled = isGuestAccessEnabled,
                            onGuestAccessToggle = { isGuestAccessEnabled = it; viewModel.onToggleWifi("default_radio0", it) },
                            abstraction = abstraction,
                            isUpdating = isUpdating
                        )
                        AppDestinations.DEVICES -> DevicesListScreen(routerHost, routerUser, routerPass)
                        AppDestinations.SETTINGS -> SettingsScreen(
                            onTabClick = { currentSettingsTab = it },
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}
