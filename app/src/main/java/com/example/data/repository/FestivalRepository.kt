package com.example.data.repository

import com.example.data.local.ActivityLogDao
import com.example.data.local.AdminSecurityDao
import com.example.data.local.CategoryDao
import com.example.data.local.DonationDao
import com.example.data.local.DonorDao
import com.example.data.local.ExpenseDao
import com.example.data.local.FestivalSettingsDao
import com.example.data.model.ActivityLog
import com.example.data.model.AdminSecurity
import com.example.data.model.CategoryItem
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.DonorGroup
import com.example.data.model.Expense
import com.example.data.model.FestivalSettings
import com.example.data.model.FinancialSummary
import com.example.data.model.TransactionItem
import com.example.util.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FestivalRepository(
    private val donorDao: DonorDao,
    private val donationDao: DonationDao,
    private val expenseDao: ExpenseDao,
    private val festivalSettingsDao: FestivalSettingsDao? = null,
    private val categoryDao: CategoryDao? = null,
    private val activityLogDao: ActivityLogDao? = null,
    private val adminSecurityDao: AdminSecurityDao? = null
) {
    val allDonations: Flow<List<Donation>> = donationDao.getAllDonations()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors()

    // Dynamic Festival Settings
    val festivalSettings: Flow<FestivalSettings> = festivalSettingsDao?.getSettingsFlow()?.map {
        it ?: getDefaultSettings()
    } ?: flowOf(getDefaultSettings())

    // Dynamic Categories
    val allCategories: Flow<List<CategoryItem>> = categoryDao?.getAllCategories() ?: flowOf(getDefaultCategories())

    val activeDonationCategories: Flow<List<CategoryItem>> = categoryDao?.getActiveCategoriesByType("DONATION")
        ?: flowOf(getDefaultCategories().filter { it.type == "DONATION" && it.isActive })

    val activeExpenseCategories: Flow<List<CategoryItem>> = categoryDao?.getActiveCategoriesByType("EXPENSE")
        ?: flowOf(getDefaultCategories().filter { it.type == "EXPENSE" && it.isActive })

    // Activity Logs
    val activityLogs: Flow<List<ActivityLog>> = activityLogDao?.getAllLogs() ?: flowOf(emptyList())

    // Admin Security
    val adminSecurity: Flow<AdminSecurity> = adminSecurityDao?.getSecurityFlow()?.map {
        it ?: getDefaultAdminSecurity()
    } ?: flowOf(getDefaultAdminSecurity())

    val financialSummary: Flow<FinancialSummary> = combine(allDonations, allExpenses, allDonors) { donations, expenses, donors ->
        val totalCollection = donations.sumOf { it.amount }
        val cashCollection = donations
            .filter { it.paymentMode.equals("Cash", ignoreCase = true) }
            .sumOf { it.amount }
        val upiCollection = donations
            .filter { it.paymentMode.equals("UPI", ignoreCase = true) }
            .sumOf { it.amount }
        val bankCollection = donations
            .filter { it.paymentMode.equals("Bank Transfer", ignoreCase = true) || it.paymentMode.equals("Bank", ignoreCase = true) }
            .sumOf { it.amount }
        val onlineCollection = upiCollection + bankCollection + donations
            .filter { it.paymentMode.equals("Online", ignoreCase = true) }
            .sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val currentBalance = totalCollection - totalExpenses
        val totalDonationsCount = donations.size
        val averageDonation = if (totalDonationsCount > 0) totalCollection / totalDonationsCount else 0.0

        FinancialSummary(
            totalCollection = totalCollection,
            cashCollection = cashCollection,
            onlineCollection = onlineCollection,
            upiCollection = upiCollection,
            bankCollection = bankCollection,
            totalExpenses = totalExpenses,
            currentBalance = currentBalance,
            totalDonorsCount = donors.size,
            totalDonationsCount = totalDonationsCount,
            totalExpensesCount = expenses.size,
            averageDonation = averageDonation
        )
    }

    val donorGroups: Flow<List<DonorGroup>> = donorDao.getAllDonorsWithDonations().map { list ->
        list.map { item ->
            val calculatedTotal = if (item.donations.isNotEmpty()) item.donations.sumOf { it.amount } else item.donor.totalAmount
            val calculatedCount = if (item.donations.isNotEmpty()) item.donations.size else item.donor.donationCount
            DonorGroup(
                donorId = item.donor.id,
                donorName = item.donor.name,
                mobileNumber = item.donor.mobileNumber,
                address = item.donor.address,
                totalAmount = calculatedTotal,
                donationCount = calculatedCount,
                donations = item.donations.sortedByDescending { it.timestamp }
            )
        }.sortedByDescending { it.totalAmount }
    }

    val recentTransactions: Flow<List<TransactionItem>> = combine(allDonations, allExpenses) { donations, expenses ->
        val items = mutableListOf<Pair<Long, TransactionItem>>()
        donations.forEach { items.add(it.timestamp to TransactionItem.DonationTx(it)) }
        expenses.forEach { items.add(it.timestamp to TransactionItem.ExpenseTx(it)) }
        items.sortByDescending { it.first }
        items.take(10).map { it.second }
    }

    suspend fun saveDonation(
        donorName: String,
        mobile: String,
        address: String,
        amount: Double,
        category: String,
        date: String,
        paymentMode: String,
        notes: String
    ): Donation {
        val cleanMobile = mobile.trim()
        val cleanName = donorName.trim().ifBlank { "Devotee" }
        val cleanAddress = address.trim()

        val existingDonor = if (cleanMobile.isNotEmpty()) {
            donorDao.getDonorByMobile(cleanMobile)
        } else {
            null
        }

        val donorId = if (existingDonor != null) {
            val updatedDonor = existingDonor.copy(
                name = if (cleanName != "Devotee" && cleanName.isNotBlank()) cleanName else existingDonor.name,
                address = if (cleanAddress.isNotBlank()) cleanAddress else existingDonor.address,
                totalAmount = existingDonor.totalAmount + amount,
                donationCount = existingDonor.donationCount + 1,
                updatedAt = System.currentTimeMillis()
            )
            donorDao.updateDonor(updatedDonor)
            existingDonor.id
        } else {
            val newDonor = Donor(
                name = cleanName,
                mobileNumber = cleanMobile,
                address = cleanAddress,
                totalAmount = amount,
                donationCount = 1,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            donorDao.insertDonor(newDonor)
        }

        val receiptNo = getNextReceiptNumber()

        val donation = Donation(
            donorId = donorId,
            receiptNo = receiptNo,
            donorName = if (existingDonor != null && cleanName == "Devotee") existingDonor.name else cleanName,
            mobileNumber = cleanMobile,
            address = if (cleanAddress.isNotBlank()) cleanAddress else (existingDonor?.address ?: ""),
            amount = amount,
            category = category,
            date = date,
            paymentMode = paymentMode,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )

        val donationId = donationDao.insertDonation(donation)
        val saved = donation.copy(id = donationId)
        logActivity("Donation Added", "Receipt ${saved.receiptNo}: ₹${saved.amount} from ${saved.donorName} ($paymentMode)")
        return saved
    }

    suspend fun addExpense(expense: Expense): Long {
        val id = expenseDao.insertExpense(expense)
        logActivity("Expense Added", "₹${expense.amount} for ${expense.category}: ${expense.description}")
        return id
    }

    suspend fun saveExpense(
        category: String,
        description: String,
        amount: Double,
        date: String,
        vendor: String,
        paymentMode: String,
        notes: String
    ): Expense {
        val expense = Expense(
            category = category,
            description = description,
            amount = amount,
            date = date,
            vendor = vendor,
            paymentMode = paymentMode,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        val id = expenseDao.insertExpense(expense)
        val saved = expense.copy(id = id)
        logActivity("Expense Added", "₹${saved.amount} for ${saved.category}: ${saved.description}")
        return saved
    }

    suspend fun getDonationById(id: Long): Donation? {
        return donationDao.getDonationById(id)
    }

    suspend fun getNextReceiptNumber(): String {
        val count = donationDao.getCount()
        val nextSeq = count + 1
        return "GCP-%05d".format(nextSeq)
    }

    // ==========================================
    // FESTIVAL SETTINGS
    // ==========================================
    suspend fun getFestivalSettings(): FestivalSettings {
        val existing = festivalSettingsDao?.getSettings()
        if (existing != null) return existing
        val defaultSettings = getDefaultSettings()
        festivalSettingsDao?.insertSettings(defaultSettings)
        return defaultSettings
    }

    suspend fun saveFestivalSettings(settings: FestivalSettings) {
        val updated = settings.copy(id = 1, updatedAt = System.currentTimeMillis())
        festivalSettingsDao?.insertSettings(updated)
        logActivity("Festival Settings Updated", "${updated.organizationName} - ${updated.festivalName} ${updated.festivalYear}")
    }

    // ==========================================
    // CATEGORIES MANAGEMENT
    // ==========================================
    suspend fun ensureDefaultCategories() {
        if (categoryDao == null) return
        val count = categoryDao.getCount()
        if (count == 0) {
            val defaults = getDefaultCategories()
            categoryDao.insertAll(defaults)
        }
    }

    suspend fun addCategory(name: String, type: String): Boolean {
        val cleanName = name.trim()
        if (cleanName.isBlank() || categoryDao == null) return false

        val existing = categoryDao.getCategoryByNameAndType(cleanName, type.uppercase())
        if (existing != null) {
            if (!existing.isActive) {
                categoryDao.setCategoryActive(existing.id, true)
                logActivity("Category Reactivated", "Reactivated $type category: $cleanName")
                return true
            }
            return false // Duplicate already active
        }

        val newCat = CategoryItem(
            name = cleanName,
            type = type.uppercase(),
            isDefault = false,
            isActive = true
        )
        categoryDao.insertCategory(newCat)
        logActivity("Category Added", "Added $type category: $cleanName")
        return true
    }

    suspend fun renameCategory(id: Long, newName: String): Boolean {
        val cleanName = newName.trim()
        if (cleanName.isBlank() || categoryDao == null) return false
        val cat = categoryDao.getCategoryById(id) ?: return false

        val existingWithNewName = categoryDao.getCategoryByNameAndType(cleanName, cat.type)
        if (existingWithNewName != null && existingWithNewName.id != id) {
            return false // Duplicate name conflict
        }

        val updated = cat.copy(name = cleanName)
        categoryDao.updateCategory(updated)
        logActivity("Category Renamed", "Renamed ${cat.name} to $cleanName (${cat.type})")
        return true
    }

    suspend fun toggleCategoryActive(id: Long, isActive: Boolean) {
        categoryDao?.setCategoryActive(id, isActive)
        categoryDao?.getCategoryById(id)?.let {
            logActivity("Category Status", "${it.name} (${it.type}) set to ${if (isActive) "Active" else "Archived"}")
        }
    }

    // ==========================================
    // ACTIVITY LOGS
    // ==========================================
    suspend fun logActivity(action: String, description: String) {
        try {
            activityLogDao?.insertLog(
                ActivityLog(
                    action = action,
                    description = description,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearActivityLogs() {
        activityLogDao?.deleteAllLogs()
    }

    // ==========================================
    // SECURITY, PASSWORD & APP LOCK
    // ==========================================
    suspend fun ensureAdminSecurity(): AdminSecurity {
        val existing = adminSecurityDao?.getSecurity()
        if (existing != null) return existing

        val salt = SecurityUtils.generateSalt()
        val defaultSec = AdminSecurity(
            id = 1,
            adminName = "Head of Committee",
            username = "admin",
            mobileNumber = "+91 98765 43210",
            passwordHash = SecurityUtils.hashPassword("admin", salt),
            salt = salt,
            isAppLockEnabled = false,
            lockPinHash = "",
            status = "Active (Super Admin)"
        )
        adminSecurityDao?.insertSecurity(defaultSec)
        return defaultSec
    }

    suspend fun verifyAdminLogin(usernameInput: String, passwordInput: String): Boolean {
        val sec = ensureAdminSecurity()
        val matchesUsername = sec.username.equals(usernameInput.trim(), ignoreCase = true) ||
                sec.mobileNumber.replace(" ", "").contains(usernameInput.trim().replace(" ", "")) ||
                usernameInput.trim().equals("admin", ignoreCase = true)

        val isPasswordCorrect = SecurityUtils.verifyPassword(passwordInput, sec.salt, sec.passwordHash)
        if (matchesUsername && isPasswordCorrect) {
            logActivity("Admin Login", "Logged in as ${sec.adminName}")
            return true
        }
        return false
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        val sec = ensureAdminSecurity()
        if (newPassword.trim().length < 4) {
            return Result.failure(Exception("New password must be at least 4 characters"))
        }

        val isCurrentValid = SecurityUtils.verifyPassword(currentPassword, sec.salt, sec.passwordHash)
        if (!isCurrentValid) {
            return Result.failure(Exception("Current password is incorrect"))
        }

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newPassword, newSalt)
        val updated = sec.copy(
            passwordHash = newHash,
            salt = newSalt,
            updatedAt = System.currentTimeMillis()
        )
        adminSecurityDao?.insertSecurity(updated)
        logActivity("Password Changed", "Admin credentials updated successfully")
        return Result.success("Password changed successfully")
    }

    suspend fun setAppLock(
        enabled: Boolean,
        pin: String = "",
        timeoutMinutes: Int = 0,
        isBiometric: Boolean = false
    ): Boolean {
        val sec = ensureAdminSecurity()
        val pinHash = if (pin.isNotBlank()) SecurityUtils.hashPin(pin, sec.salt) else sec.lockPinHash
        val updated = sec.copy(
            isAppLockEnabled = enabled,
            lockPinHash = pinHash,
            autoLockTimeoutMinutes = timeoutMinutes,
            isBiometricEnabled = isBiometric,
            updatedAt = System.currentTimeMillis()
        )
        adminSecurityDao?.insertSecurity(updated)
        logActivity(
            "App Lock ${if (enabled) "Enabled" else "Disabled"}",
            if (enabled) "App Lock configured (Timeout: ${if (timeoutMinutes == 0) "Immediate" else "$timeoutMinutes min"})" else "App Lock turned off"
        )
        return true
    }

    suspend fun verifyUnlock(input: String): Boolean {
        val sec = ensureAdminSecurity()
        if (!sec.isAppLockEnabled) return true
        val isPinMatch = sec.lockPinHash.isNotBlank() && SecurityUtils.verifyPassword(input, sec.salt, sec.lockPinHash)
        val isPasswordMatch = SecurityUtils.verifyPassword(input, sec.salt, sec.passwordHash)
        return isPinMatch || isPasswordMatch
    }

    suspend fun updateAdminProfile(name: String, mobile: String) {
        val sec = ensureAdminSecurity()
        val updated = sec.copy(
            adminName = name.trim().ifBlank { sec.adminName },
            mobileNumber = mobile.trim().ifBlank { sec.mobileNumber },
            updatedAt = System.currentTimeMillis()
        )
        adminSecurityDao?.insertSecurity(updated)
        logActivity("Profile Updated", "Admin info updated: ${updated.adminName} (${updated.mobileNumber})")
    }

    // ==========================================
    // BACKUP & RESTORE
    // ==========================================
    suspend fun createBackupJsonString(): String {
        val settings = festivalSettingsDao?.getSettings() ?: getDefaultSettings()
        val donors = donorDao.getAllDonorsList()
        val donations = donationDao.getAllDonationsList()
        val expenses = expenseDao.getAllExpensesList()
        val categories = categoryDao?.getAllCategoriesList() ?: getDefaultCategories()
        val logs = activityLogDao?.getAllLogsList() ?: emptyList()

        val json = com.example.util.BackupManager.createBackupJson(
            festivalSettings = settings,
            donors = donors,
            donations = donations,
            expenses = expenses,
            categories = categories,
            activityLogs = logs
        )
        logActivity("Backup Created", "Full database backup generated (${donations.size} donations, ${expenses.size} expenses)")
        return json
    }

    suspend fun restoreFromParsedBackup(backup: com.example.util.ParsedBackupData): Result<String> {
        return try {
            // Update festival settings
            festivalSettingsDao?.insertSettings(backup.festivalSettings)

            // Clear current financial data
            donationDao.deleteAllDonations()
            donorDao.deleteAllDonors()
            expenseDao.deleteAllExpenses()

            // Restore donors and donations
            if (backup.donors.isNotEmpty()) {
                donorDao.insertAll(backup.donors)
            }
            if (backup.donations.isNotEmpty()) {
                donationDao.insertAll(backup.donations)
            }
            if (backup.expenses.isNotEmpty()) {
                expenseDao.insertAll(backup.expenses)
            }

            // Restore categories if present
            if (backup.categories.isNotEmpty()) {
                categoryDao?.deleteAllCategories()
                categoryDao?.insertAll(backup.categories)
            }

            // Restore activity logs if present
            if (backup.activityLogs.isNotEmpty()) {
                activityLogDao?.deleteAllLogs()
                activityLogDao?.insertAll(backup.activityLogs)
            }

            logActivity("Backup Restored", "Restored ${backup.donations.size} donations and ${backup.expenses.size} expenses from backup")
            Result.success("Backup restored successfully!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllData() {
        donationDao.deleteAllDonations()
        donorDao.deleteAllDonors()
        expenseDao.deleteAllExpenses()
        logActivity("Data Cleared", "All donation and expense records cleared")
    }

    companion object {
        fun getDefaultSettings() = FestivalSettings(
            id = 1,
            organizationName = "Shree Siddhivinayaka Parivar",
            festivalName = "Ganesh Chaturthi",
            festivalYear = "2026",
            startDate = "14 September 2026",
            endDate = "18 September 2026",
            organizationAddress = "Ganesh Nagar, Anantapur",
            updatedAt = System.currentTimeMillis()
        )

        fun getDefaultAdminSecurity(): AdminSecurity {
            val salt = "siddhivinayaka2026salt"
            return AdminSecurity(
                id = 1,
                adminName = "Head of Committee",
                username = "admin",
                mobileNumber = "+91 98765 43210",
                passwordHash = SecurityUtils.hashPassword("admin", salt),
                salt = salt,
                isAppLockEnabled = false,
                lockPinHash = "",
                status = "Active (Super Admin)",
                updatedAt = System.currentTimeMillis()
            )
        }

        fun getDefaultCategories(): List<CategoryItem> = listOf(
            // Expense Categories
            CategoryItem(name = "Tent & Decoration", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Sound System", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Lighting", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Annadanam", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Idol", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Printing", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Transport", type = "EXPENSE", isDefault = true, isActive = true),
            CategoryItem(name = "Others", type = "EXPENSE", isDefault = true, isActive = true),

            // Donation Categories
            CategoryItem(name = "General Donation", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Annadanam Donation", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Laddu Prasadam", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Special Pooja", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Idol Sponsorship", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Lighting / Decoration", type = "DONATION", isDefault = true, isActive = true),
            CategoryItem(name = "Others", type = "DONATION", isDefault = true, isActive = true)
        )
    }
}
