package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.BottomNavTab
import com.example.ui.FestivalViewModel
import com.example.ui.components.AppBottomBar
import com.example.ui.screens.ActivityHistoryScreen
import com.example.ui.screens.AddDonationScreen
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.CategoriesManagementScreen
import com.example.ui.screens.ChangePasswordScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DetailedReportScreen
import com.example.ui.screens.DonationReceiptScreen
import com.example.ui.screens.DonorDetailsScreen
import com.example.ui.screens.DonorsScreen
import com.example.ui.screens.ExpensesListScreen
import com.example.ui.screens.FestivalSettingsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreMenuScreen
import com.example.ui.screens.ReportsOverviewScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.UserManagementScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FestivalViewModel by viewModels()
    private var lastBackgroundTimestamp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FestivalApp(
                    viewModel = viewModel,
                    onSetWindowSecure = { secure ->
                        if (secure) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                    }
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lastBackgroundTimestamp = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
        if (lastBackgroundTimestamp > 0) {
            val elapsedMinutes = (System.currentTimeMillis() - lastBackgroundTimestamp) / 1000 / 60
            val adminSec = viewModel.adminSecurity.value
            if (adminSec.isAppLockEnabled) {
                if (adminSec.autoLockTimeoutMinutes == 0 || elapsedMinutes >= adminSec.autoLockTimeoutMinutes) {
                    viewModel.lockApp()
                }
            }
        }
    }
}

@Composable
fun FestivalApp(
    viewModel: FestivalViewModel,
    onSetWindowSecure: (Boolean) -> Unit = {}
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val donorGroups by viewModel.donorGroups.collectAsStateWithLifecycle()
    val allDonorGroups by viewModel.allDonorGroups.collectAsStateWithLifecycle()
    val prefilledDonor by viewModel.prefilledDonor.collectAsStateWithLifecycle()
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allDonations by viewModel.allDonations.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedExpenseCategory.collectAsStateWithLifecycle()
    val selectedPaymentMode by viewModel.selectedExpensePaymentMode.collectAsStateWithLifecycle()
    val donorSearchQuery by viewModel.donorSearchQuery.collectAsStateWithLifecycle()
    val expenseSearchQuery by viewModel.expenseSearchQuery.collectAsStateWithLifecycle()
    val selectedDonationForReceipt by viewModel.selectedDonationForReceipt.collectAsStateWithLifecycle()
    val selectedDonor by viewModel.selectedDonor.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    // More & Settings module states
    val festivalSettings by viewModel.festivalSettings.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val activeDonationCategories by viewModel.activeDonationCategories.collectAsStateWithLifecycle()
    val activeExpenseCategories by viewModel.activeExpenseCategories.collectAsStateWithLifecycle()
    val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val adminSecurity by viewModel.adminSecurity.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Manage FLAG_SECURE for sensitive screens
    LaunchedEffect(currentScreen, isAppLocked) {
        val isSensitive = isAppLocked || currentScreen in listOf(
            AppScreen.LOGIN,
            AppScreen.CHANGE_PASSWORD,
            AppScreen.USER_MANAGEMENT,
            AppScreen.APP_LOCK
        )
        onSetWindowSecure(isSensitive)
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.DASHBOARD && !isAppLocked) {
        when (currentScreen) {
            AppScreen.LOGIN -> viewModel.navigateTo(AppScreen.SPLASH)
            AppScreen.ADD_DONATION -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.RECEIPT -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.ADD_EXPENSE -> viewModel.navigateTo(AppScreen.EXPENSES_LIST)
            AppScreen.DONOR_DETAILS -> viewModel.navigateTo(AppScreen.DONORS_LIST)
            AppScreen.DETAILED_REPORT -> viewModel.navigateTo(AppScreen.REPORTS_OVERVIEW)
            AppScreen.FESTIVAL_SETTINGS,
            AppScreen.USER_MANAGEMENT,
            AppScreen.CATEGORIES_MANAGEMENT,
            AppScreen.CHANGE_PASSWORD,
            AppScreen.ACTIVITY_HISTORY,
            AppScreen.BACKUP_RESTORE -> {
                viewModel.navigateTo(AppScreen.MORE_MENU)
                viewModel.selectTab(BottomNavTab.MORE)
            }
            AppScreen.DONORS_LIST, AppScreen.EXPENSES_LIST, AppScreen.REPORTS_OVERVIEW, AppScreen.MORE_MENU -> {
                viewModel.selectTab(BottomNavTab.HOME)
            }
            else -> viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    // If App Lock is active and user is beyond Splash/Login, display AppLockScreen
    if (isAppLocked && currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.LOGIN) {
        AppLockScreen(
            adminName = adminSecurity.adminName,
            festivalName = festivalSettings.festivalName,
            onUnlock = { input, onResult ->
                viewModel.unlockApp(input, onResult)
            }
        )
        return
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.DASHBOARD,
        AppScreen.DONORS_LIST,
        AppScreen.EXPENSES_LIST,
        AppScreen.REPORTS_OVERVIEW,
        AppScreen.MORE_MENU
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onContinue = { viewModel.navigateTo(AppScreen.LOGIN) }
                    )
                }

                AppScreen.LOGIN -> {
                    LoginScreen(
                        festivalName = "${festivalSettings.festivalName} ${festivalSettings.festivalYear}",
                        organizationName = festivalSettings.organizationName,
                        onLoginClick = { user, pass, onError ->
                            viewModel.login(user, pass) { success ->
                                if (!success) {
                                    onError("Invalid username or password")
                                }
                            }
                        },
                        onLoginSuccess = {
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                            viewModel.selectTab(BottomNavTab.HOME)
                        }
                    )
                }

                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        summary = summary,
                        recentTransactions = recentTransactions,
                        onAddDonationClick = { viewModel.navigateTo(AppScreen.ADD_DONATION) },
                        onAddExpenseClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) },
                        onViewAllDonationsClick = {
                            viewModel.selectTab(BottomNavTab.DONATIONS)
                        },
                        onDonationClick = { donation ->
                            viewModel.viewDonationReceipt(donation)
                        },
                        onNotificationClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Festival: ${festivalSettings.festivalName} (${festivalSettings.startDate} - ${festivalSettings.endDate})")
                            }
                        }
                    )
                }

                AppScreen.ADD_DONATION -> {
                    AddDonationScreen(
                        initialDonorName = prefilledDonor?.donorName ?: "",
                        initialMobileNumber = prefilledDonor?.mobileNumber ?: "",
                        initialAddress = prefilledDonor?.address ?: "",
                        availableCategories = activeDonationCategories.map { it.name },
                        onBack = {
                            if (prefilledDonor != null) {
                                viewModel.navigateTo(AppScreen.DONOR_DETAILS)
                            } else {
                                viewModel.navigateTo(AppScreen.DASHBOARD)
                            }
                        },
                        onSaveDonation = { name, mobile, addr, amt, cat, dt, mode, nts ->
                            viewModel.saveDonation(name, mobile, addr, amt, cat, dt, mode, nts)
                        }
                    )
                }

                AppScreen.RECEIPT -> {
                    DonationReceiptScreen(
                        donation = selectedDonationForReceipt,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        onShowMessage = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }

                AppScreen.ADD_EXPENSE -> {
                    AddExpenseScreen(
                        availableCategories = activeExpenseCategories.map { it.name },
                        onBack = { viewModel.navigateTo(AppScreen.EXPENSES_LIST) },
                        onSaveExpense = { cat, desc, amt, dt, vend, mode, nts ->
                            viewModel.saveExpense(cat, desc, amt, dt, vend, mode, nts)
                        }
                    )
                }

                AppScreen.EXPENSES_LIST -> {
                    ExpensesListScreen(
                        expenses = filteredExpenses,
                        totalExpenses = summary.totalExpenses,
                        selectedCategory = selectedCategory,
                        selectedPaymentMode = selectedPaymentMode,
                        searchQuery = expenseSearchQuery,
                        onCategorySelected = { viewModel.setExpenseCategoryFilter(it) },
                        onPaymentModeSelected = { viewModel.setExpensePaymentModeFilter(it) },
                        onSearchQueryChange = { viewModel.setExpenseSearchQuery(it) },
                        onAddExpenseClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) }
                    )
                }

                AppScreen.DONORS_LIST -> {
                    DonorsScreen(
                        donorGroups = donorGroups,
                        totalDonorsInDb = allDonorGroups.size,
                        searchQuery = donorSearchQuery,
                        onSearchQueryChange = { viewModel.setDonorSearchQuery(it) },
                        onDonorClick = { group ->
                            viewModel.viewDonorDetails(group)
                        },
                        onAddDonationClick = { viewModel.navigateTo(AppScreen.ADD_DONATION) }
                    )
                }

                AppScreen.DONOR_DETAILS -> {
                    DonorDetailsScreen(
                        donorGroup = selectedDonor,
                        onBack = { viewModel.navigateTo(AppScreen.DONORS_LIST) },
                        onDonationClick = { donation ->
                            viewModel.viewDonationReceipt(donation)
                        },
                        onAddDonationClick = {
                            selectedDonor?.let {
                                viewModel.initiateAddDonationForDonor(it)
                            } ?: viewModel.navigateTo(AppScreen.ADD_DONATION)
                        },
                        onViewAllReceiptsClick = {
                            selectedDonor?.donations?.firstOrNull()?.let {
                                viewModel.viewDonationReceipt(it)
                            } ?: run {
                                scope.launch { snackbarHostState.showSnackbar("No receipts to display") }
                            }
                        }
                    )
                }

                AppScreen.REPORTS_OVERVIEW -> {
                    ReportsOverviewScreen(
                        summary = summary,
                        expenses = allExpenses,
                        onViewDetailedReport = { viewModel.navigateTo(AppScreen.DETAILED_REPORT) }
                    )
                }

                AppScreen.DETAILED_REPORT -> {
                    DetailedReportScreen(
                        summary = summary,
                        expenses = allExpenses,
                        donations = allDonations,
                        donors = allDonorGroups,
                        onBack = { viewModel.navigateTo(AppScreen.REPORTS_OVERVIEW) },
                        onShowMessage = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }

                AppScreen.MORE_MENU -> {
                    MoreMenuScreen(
                        adminSecurity = adminSecurity,
                        onNavigateToFestivalSettings = { viewModel.navigateTo(AppScreen.FESTIVAL_SETTINGS) },
                        onNavigateToUserManagement = { viewModel.navigateTo(AppScreen.USER_MANAGEMENT) },
                        onNavigateToCategories = { viewModel.navigateTo(AppScreen.CATEGORIES_MANAGEMENT) },
                        onNavigateToActivityHistory = { viewModel.navigateTo(AppScreen.ACTIVITY_HISTORY) },
                        onNavigateToChangePassword = { viewModel.navigateTo(AppScreen.CHANGE_PASSWORD) },
                        onNavigateToBackupRestore = { viewModel.navigateTo(AppScreen.BACKUP_RESTORE) },
                        onConfigureAppLock = { enabled, pin, timeout, isBio ->
                            viewModel.configureAppLock(enabled, pin, timeout, isBio)
                        },
                        onLogoutClick = {
                            viewModel.logout()
                        },
                        onClearDataClick = {
                            viewModel.clearAllData()
                        },
                        onShowMessage = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }

                AppScreen.BACKUP_RESTORE -> {
                    BackupRestoreScreen(
                        summary = summary,
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        },
                        onGenerateBackupJson = {
                            viewModel.generateBackupJsonString()
                        },
                        onRestoreBackupJson = { jsonStr, onResult ->
                            viewModel.restoreBackupJsonString(jsonStr, onResult)
                        },
                        onShowMessage = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }

                AppScreen.FESTIVAL_SETTINGS -> {
                    FestivalSettingsScreen(
                        currentSettings = festivalSettings,
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        },
                        onSaveSettings = { updated ->
                            viewModel.saveFestivalSettings(updated)
                        }
                    )
                }

                AppScreen.USER_MANAGEMENT -> {
                    UserManagementScreen(
                        adminSecurity = adminSecurity,
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        },
                        onUpdateProfile = { name, mobile ->
                            viewModel.updateAdminProfile(name, mobile)
                        },
                        onChangePasswordClick = {
                            viewModel.navigateTo(AppScreen.CHANGE_PASSWORD)
                        }
                    )
                }

                AppScreen.CATEGORIES_MANAGEMENT -> {
                    CategoriesManagementScreen(
                        categories = allCategories,
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        },
                        onAddCategory = { name, type ->
                            viewModel.addCategory(name, type)
                        },
                        onRenameCategory = { id, newName ->
                            viewModel.renameCategory(id, newName)
                        },
                        onToggleCategoryActive = { id, isActive ->
                            viewModel.toggleCategoryActive(id, isActive)
                        }
                    )
                }

                AppScreen.CHANGE_PASSWORD -> {
                    ChangePasswordScreen(
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        },
                        onSubmitChangePassword = { current, newPass, onResult ->
                            viewModel.changePassword(current, newPass, onResult)
                        }
                    )
                }

                AppScreen.ACTIVITY_HISTORY -> {
                    ActivityHistoryScreen(
                        activityLogs = activityLogs,
                        onBack = {
                            viewModel.navigateTo(AppScreen.MORE_MENU)
                            viewModel.selectTab(BottomNavTab.MORE)
                        }
                    )
                }

                AppScreen.APP_LOCK -> {
                    AppLockScreen(
                        adminName = adminSecurity.adminName,
                        festivalName = festivalSettings.festivalName,
                        onUnlock = { pinOrPass, onResult ->
                            viewModel.unlockApp(pinOrPass, onResult)
                        }
                    )
                }
            }
        }
    }
}

