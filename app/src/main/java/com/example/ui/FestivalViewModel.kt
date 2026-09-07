package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AdminSecurity
import com.example.data.model.CategoryItem
import com.example.data.model.Donation
import com.example.data.model.DonorGroup
import com.example.data.model.Expense
import com.example.data.model.FestivalSettings
import com.example.data.model.FinancialSummary
import com.example.data.model.TransactionItem
import com.example.data.repository.FestivalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class AppScreen {
    SPLASH,
    LOGIN,
    DASHBOARD,
    ADD_DONATION,
    RECEIPT,
    ADD_EXPENSE,
    EXPENSES_LIST,
    DONORS_LIST,
    DONOR_DETAILS,
    REPORTS_OVERVIEW,
    DETAILED_REPORT,
    MORE_MENU,
    FESTIVAL_SETTINGS,
    USER_MANAGEMENT,
    CATEGORIES_MANAGEMENT,
    CHANGE_PASSWORD,
    ACTIVITY_HISTORY,
    BACKUP_RESTORE,
    APP_LOCK
}

enum class BottomNavTab {
    HOME,
    DONATIONS,
    EXPENSES,
    REPORTS,
    MORE
}

class FestivalViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FestivalRepository(
        donorDao = database.donorDao(),
        donationDao = database.donationDao(),
        expenseDao = database.expenseDao(),
        festivalSettingsDao = database.festivalSettingsDao(),
        categoryDao = database.categoryDao(),
        activityLogDao = database.activityLogDao(),
        adminSecurityDao = database.adminSecurityDao()
    )

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    // App Lock runtime lock status
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // Active selection state
    private val _selectedDonationForReceipt = MutableStateFlow<Donation?>(null)
    val selectedDonationForReceipt: StateFlow<Donation?> = _selectedDonationForReceipt.asStateFlow()

    private val _selectedDonorMobile = MutableStateFlow<String?>(null)
    val selectedDonor: StateFlow<DonorGroup?> = combine(repository.donorGroups, _selectedDonorMobile) { groups, mobile ->
        if (mobile == null) null
        else groups.find { it.mobileNumber.equals(mobile, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _prefilledDonor = MutableStateFlow<DonorGroup?>(null)
    val prefilledDonor: StateFlow<DonorGroup?> = _prefilledDonor.asStateFlow()

    private val _donorSearchQuery = MutableStateFlow("")
    val donorSearchQuery: StateFlow<String> = _donorSearchQuery.asStateFlow()

    private val _expenseSearchQuery = MutableStateFlow("")
    val expenseSearchQuery: StateFlow<String> = _expenseSearchQuery.asStateFlow()

    private val _selectedExpenseCategory = MutableStateFlow("All")
    val selectedExpenseCategory: StateFlow<String> = _selectedExpenseCategory.asStateFlow()

    private val _selectedExpensePaymentMode = MutableStateFlow("All")
    val selectedExpensePaymentMode: StateFlow<String> = _selectedExpensePaymentMode.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Data streams
    val festivalSettings: StateFlow<FestivalSettings> = repository.festivalSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FestivalRepository.getDefaultSettings()
    )

    val allCategories: StateFlow<List<CategoryItem>> = repository.allCategories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FestivalRepository.getDefaultCategories()
    )

    val activeDonationCategories: StateFlow<List<CategoryItem>> = repository.activeDonationCategories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FestivalRepository.getDefaultCategories().filter { it.type == "DONATION" }
    )

    val activeExpenseCategories: StateFlow<List<CategoryItem>> = repository.activeExpenseCategories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FestivalRepository.getDefaultCategories().filter { it.type == "EXPENSE" }
    )

    val activityLogs: StateFlow<List<ActivityLog>> = repository.activityLogs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val adminSecurity: StateFlow<AdminSecurity> = repository.adminSecurity.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FestivalRepository.getDefaultAdminSecurity()
    )

    val financialSummary: StateFlow<FinancialSummary> = repository.financialSummary.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialSummary(
            totalCollection = 0.0,
            cashCollection = 0.0,
            onlineCollection = 0.0,
            upiCollection = 0.0,
            bankCollection = 0.0,
            totalExpenses = 0.0,
            currentBalance = 0.0,
            totalDonorsCount = 0,
            totalDonationsCount = 0,
            totalExpensesCount = 0,
            averageDonation = 0.0
        )
    )

    val recentTransactions: StateFlow<List<TransactionItem>> = repository.recentTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allDonations: StateFlow<List<Donation>> = repository.allDonations.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allDonorGroups: StateFlow<List<DonorGroup>> = repository.donorGroups.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val donorGroups: StateFlow<List<DonorGroup>> = combine(repository.donorGroups, _donorSearchQuery) { groups, query ->
        if (query.isBlank()) {
            groups
        } else {
            groups.filter {
                it.donorName.contains(query, ignoreCase = true) ||
                it.mobileNumber.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val filteredExpenses: StateFlow<List<Expense>> = combine(
        repository.allExpenses,
        _selectedExpenseCategory,
        _selectedExpensePaymentMode,
        _expenseSearchQuery
    ) { expenses, category, paymentMode, query ->
        var list = expenses
        if (category != "All") {
            list = list.filter {
                it.category.equals(category, ignoreCase = true) || it.category.contains(category, ignoreCase = true)
            }
        }
        if (paymentMode != "All") {
            list = list.filter {
                it.paymentMode.equals(paymentMode, ignoreCase = true)
            }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.description.contains(query, ignoreCase = true) ||
                it.vendor.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.paymentMode.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        // Seed default categories, settings, and admin security if DB is newly created
        viewModelScope.launch {
            repository.ensureDefaultCategories()
            repository.getFestivalSettings()
            val sec = repository.ensureAdminSecurity()
            if (sec.isAppLockEnabled) {
                _isAppLocked.value = true
            }
        }
    }

    // Navigation actions
    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.ADD_DONATION) {
            _prefilledDonor.value = null
        }
        _currentScreen.value = screen
    }

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
        _currentScreen.value = when (tab) {
            BottomNavTab.HOME -> AppScreen.DASHBOARD
            BottomNavTab.DONATIONS -> AppScreen.DONORS_LIST
            BottomNavTab.EXPENSES -> AppScreen.EXPENSES_LIST
            BottomNavTab.REPORTS -> AppScreen.REPORTS_OVERVIEW
            BottomNavTab.MORE -> AppScreen.MORE_MENU
        }
    }

    fun setDonorSearchQuery(query: String) {
        _donorSearchQuery.value = query
    }

    fun setExpenseSearchQuery(query: String) {
        _expenseSearchQuery.value = query
    }

    fun setExpenseCategoryFilter(category: String) {
        _selectedExpenseCategory.value = category
    }

    fun setExpensePaymentModeFilter(mode: String) {
        _selectedExpensePaymentMode.value = mode
    }

    fun viewDonorDetails(donorGroup: DonorGroup) {
        _selectedDonorMobile.value = donorGroup.mobileNumber
        _currentScreen.value = AppScreen.DONOR_DETAILS
    }

    fun initiateAddDonationForDonor(donorGroup: DonorGroup) {
        _prefilledDonor.value = donorGroup
        _currentScreen.value = AppScreen.ADD_DONATION
    }

    fun viewDonationReceipt(donation: Donation) {
        _selectedDonationForReceipt.value = donation
        _currentScreen.value = AppScreen.RECEIPT
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun saveDonation(
        name: String,
        mobile: String,
        address: String,
        amount: Double,
        category: String,
        date: String,
        paymentMode: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val savedDonation = repository.saveDonation(
                    donorName = name,
                    mobile = mobile,
                    address = address,
                    amount = amount,
                    category = category,
                    date = date,
                    paymentMode = paymentMode,
                    notes = notes
                )
                _selectedDonationForReceipt.value = savedDonation
                _toastMessage.value = "Donation saved successfully! Receipt: ${savedDonation.receiptNo}"
                _currentScreen.value = AppScreen.RECEIPT
            } catch (e: Exception) {
                _toastMessage.value = "Failed to save donation: ${e.localizedMessage}"
            }
        }
    }

    fun saveExpense(
        category: String,
        description: String,
        amount: Double,
        date: String,
        vendor: String,
        paymentMode: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.saveExpense(
                category = category,
                description = description,
                amount = amount,
                date = date,
                vendor = vendor,
                paymentMode = paymentMode,
                notes = notes
            )
            _toastMessage.value = "Expense saved successfully!"
            _currentScreen.value = AppScreen.EXPENSES_LIST
            _currentTab.value = BottomNavTab.EXPENSES
        }
    }

    // ==========================================
    // FESTIVAL SETTINGS
    // ==========================================
    fun saveFestivalSettings(settings: FestivalSettings) {
        viewModelScope.launch {
            repository.saveFestivalSettings(settings)
            _toastMessage.value = "Festival settings updated successfully!"
            _currentScreen.value = AppScreen.MORE_MENU
        }
    }

    // ==========================================
    // CATEGORIES MANAGEMENT
    // ==========================================
    fun addCategory(name: String, type: String) {
        viewModelScope.launch {
            val success = repository.addCategory(name, type)
            if (success) {
                _toastMessage.value = "Category '$name' added successfully!"
            } else {
                _toastMessage.value = "Category '$name' already exists."
            }
        }
    }

    fun renameCategory(id: Long, newName: String) {
        viewModelScope.launch {
            val success = repository.renameCategory(id, newName)
            if (success) {
                _toastMessage.value = "Category renamed to '$newName'!"
            } else {
                _toastMessage.value = "Could not rename. Name might already be taken."
            }
        }
    }

    fun toggleCategoryActive(id: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleCategoryActive(id, isActive)
            _toastMessage.value = if (isActive) "Category activated" else "Category archived"
        }
    }

    // ==========================================
    // SECURITY, PASSWORD & APP LOCK
    // ==========================================
    fun login(usernameInput: String, passwordInput: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.verifyAdminLogin(usernameInput, passwordInput)
            if (success) {
                _currentScreen.value = AppScreen.DASHBOARD
                _currentTab.value = BottomNavTab.HOME
            }
            onResult(success)
        }
    }

    fun changePassword(currentPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.changePassword(currentPass, newPass)
            if (res.isSuccess) {
                _toastMessage.value = "Password changed successfully!"
                onResult(true, "Password updated")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Failed to change password")
            }
        }
    }

    fun toggleAppLock(enable: Boolean) {
        viewModelScope.launch {
            repository.setAppLock(enable)
            _toastMessage.value = if (enable) "App Lock enabled" else "App Lock disabled"
        }
    }

    fun configureAppLock(
        enabled: Boolean,
        pin: String = "",
        timeoutMinutes: Int = 0,
        isBiometric: Boolean = false
    ) {
        viewModelScope.launch {
            repository.setAppLock(
                enabled = enabled,
                pin = pin,
                timeoutMinutes = timeoutMinutes,
                isBiometric = isBiometric
            )
            _toastMessage.value = if (enabled) "App Lock settings updated!" else "App Lock disabled"
        }
    }

    fun unlockApp(pinOrPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.verifyUnlock(pinOrPassword)
            if (success) {
                _isAppLocked.value = false
            }
            onResult(success)
        }
    }

    fun lockApp() {
        if (adminSecurity.value.isAppLockEnabled) {
            _isAppLocked.value = true
        }
    }

    fun updateAdminProfile(name: String, mobile: String) {
        viewModelScope.launch {
            repository.updateAdminProfile(name, mobile)
            _toastMessage.value = "Admin profile updated successfully!"
        }
    }

    suspend fun generateBackupJsonString(): String {
        return repository.createBackupJsonString()
    }

    suspend fun restoreBackupJsonString(jsonString: String, onResult: (Boolean, String) -> Unit) {
        val validation = com.example.util.BackupManager.validateAndParseBackup(jsonString)
        if (validation.isFailure) {
            val errorMsg = validation.exceptionOrNull()?.message ?: "Invalid backup format"
            onResult(false, errorMsg)
            return
        }

        val parsed = validation.getOrNull()
        if (parsed == null) {
            onResult(false, "Could not parse backup file.")
            return
        }

        val result = repository.restoreFromParsedBackup(parsed)
        if (result.isSuccess) {
            _selectedDonationForReceipt.value = null
            _selectedDonorMobile.value = null
            _prefilledDonor.value = null
            _toastMessage.value = "Backup restored successfully!"
            onResult(true, "Backup restored successfully!")
        } else {
            val err = result.exceptionOrNull()?.message ?: "Unknown database error during restore"
            onResult(false, err)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logActivity("Admin Logout", "Admin session ended")
            _currentScreen.value = AppScreen.LOGIN
            _selectedDonationForReceipt.value = null
            _selectedDonorMobile.value = null
            _prefilledDonor.value = null
            _toastMessage.value = "Logged out successfully"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedDonationForReceipt.value = null
            _selectedDonorMobile.value = null
            _prefilledDonor.value = null
            _toastMessage.value = "All sample and local data cleared successfully!"
        }
    }
}

object CurrencyUtils {
    private val indianFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatInr(amount: Double): String {
        val formatted = NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount.toLong())
        return "₹$formatted"
    }

    fun convertNumberToWords(number: Double): String {
        val num = number.toLong()
        if (num == 0L) return "Rupees Zero Only"

        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        fun convertLessThanThousand(n: Int): String {
            var current: String
            if (n % 100 < 20) {
                current = units[n % 100]
                var num = n / 100
                if (num > 0) {
                    current = "${units[num]} Hundred ${if (current.isNotEmpty()) "and " + current else ""}"
                }
            } else {
                current = tens[(n % 100) / 10]
                val unit = units[n % 10]
                if (unit.isNotEmpty()) {
                    current = "$current $unit"
                }
                val num = n / 100
                if (num > 0) {
                    current = "${units[num]} Hundred ${if (current.isNotEmpty()) "and " + current else ""}"
                }
            }
            return current.trim()
        }

        var result = ""
        var rem = num

        val crores = rem / 10000000
        if (crores > 0) {
            result += "${convertLessThanThousand(crores.toInt())} Crore "
            rem %= 10000000
        }

        val lakhs = rem / 100000
        if (lakhs > 0) {
            result += "${convertLessThanThousand(lakhs.toInt())} Lakh "
            rem %= 100000
        }

        val thousands = rem / 1000
        if (thousands > 0) {
            result += "${convertLessThanThousand(thousands.toInt())} Thousand "
            rem %= 1000
        }

        if (rem > 0) {
            result += convertLessThanThousand(rem.toInt())
        }

        return "Rupees ${result.trim()} Only"
    }
}
