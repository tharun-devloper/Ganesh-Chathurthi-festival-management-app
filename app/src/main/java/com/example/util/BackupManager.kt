package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.ActivityLog
import com.example.data.model.CategoryItem
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.Expense
import com.example.data.model.FestivalSettings
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ParsedBackupData(
    val format: String,
    val backupVersion: Int,
    val appVersion: String,
    val createdAtFormatted: String,
    val festivalSettings: FestivalSettings,
    val donors: List<Donor>,
    val donations: List<Donation>,
    val expenses: List<Expense>,
    val categories: List<CategoryItem>,
    val activityLogs: List<ActivityLog>
)

object BackupManager {

    private const val BACKUP_FORMAT_HEADER = "SHREE_SIDDHIVINAYAKA_PARIVAR_BACKUP"
    private const val BACKUP_VERSION = 1
    private const val APP_VERSION = "1.0.0"

    /**
     * Generates a safe, structured JSON backup string.
     * EXCLUDES ALL passwords, salts, lock PINs, and authentication secrets.
     */
    fun createBackupJson(
        festivalSettings: FestivalSettings,
        donors: List<Donor>,
        donations: List<Donation>,
        expenses: List<Expense>,
        categories: List<CategoryItem>,
        activityLogs: List<ActivityLog>
    ): String {
        val root = JSONObject()
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(now))

        root.put("format", BACKUP_FORMAT_HEADER)
        root.put("backupVersion", BACKUP_VERSION)
        root.put("appVersion", APP_VERSION)
        root.put("appName", "Shree Siddhivinayaka Parivar")
        root.put("createdAtTimestamp", now)
        root.put("createdAtFormatted", formattedDate)

        // Festival Settings
        val settingsObj = JSONObject().apply {
            put("id", festivalSettings.id)
            put("organizationName", festivalSettings.organizationName)
            put("festivalName", festivalSettings.festivalName)
            put("festivalYear", festivalSettings.festivalYear)
            put("startDate", festivalSettings.startDate)
            put("endDate", festivalSettings.endDate)
            put("organizationAddress", festivalSettings.organizationAddress)
            put("updatedAt", festivalSettings.updatedAt)
        }
        root.put("festivalSettings", settingsObj)

        // Donors
        val donorsArray = JSONArray()
        for (d in donors) {
            val obj = JSONObject().apply {
                put("id", d.id)
                put("name", d.name)
                put("mobileNumber", d.mobileNumber)
                put("address", d.address)
                put("totalAmount", d.totalAmount)
                put("donationCount", d.donationCount)
                put("createdAt", d.createdAt)
                put("updatedAt", d.updatedAt)
            }
            donorsArray.put(obj)
        }
        root.put("donors", donorsArray)

        // Donations
        val donationsArray = JSONArray()
        for (dn in donations) {
            val obj = JSONObject().apply {
                put("id", dn.id)
                put("donorId", dn.donorId)
                put("receiptNo", dn.receiptNo)
                put("donorName", dn.donorName)
                put("mobileNumber", dn.mobileNumber)
                put("address", dn.address)
                put("amount", dn.amount)
                put("category", dn.category)
                put("date", dn.date)
                put("paymentMode", dn.paymentMode)
                put("notes", dn.notes)
                put("timestamp", dn.timestamp)
            }
            donationsArray.put(obj)
        }
        root.put("donations", donationsArray)

        // Expenses
        val expensesArray = JSONArray()
        for (e in expenses) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("category", e.category)
                put("description", e.description)
                put("amount", e.amount)
                put("date", e.date)
                put("vendor", e.vendor)
                put("paymentMode", e.paymentMode)
                put("notes", e.notes)
                put("timestamp", e.timestamp)
            }
            expensesArray.put(obj)
        }
        root.put("expenses", expensesArray)

        // Categories
        val categoriesArray = JSONArray()
        for (c in categories) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("type", c.type)
                put("isDefault", c.isDefault)
                put("isActive", c.isActive)
                put("createdAt", c.createdAt)
            }
            categoriesArray.put(obj)
        }
        root.put("categories", categoriesArray)

        // Activity Logs
        val logsArray = JSONArray()
        for (l in activityLogs) {
            val obj = JSONObject().apply {
                put("id", l.id)
                put("action", l.action)
                put("description", l.description)
                put("timestamp", l.timestamp)
            }
            logsArray.put(obj)
        }
        root.put("activityLogs", logsArray)

        return root.toString(2)
    }

    /**
     * Validates and parses a backup JSON string.
     * Rejects invalid/corrupted files gracefully.
     */
    fun validateAndParseBackup(jsonString: String): Result<ParsedBackupData> {
        return try {
            if (jsonString.isBlank()) {
                return Result.failure(IllegalArgumentException("Selected backup file is empty."))
            }

            val root = JSONObject(jsonString)

            if (!root.has("format") || !root.getString("format").startsWith("SHREE_SIDDHIVINAYAKA")) {
                return Result.failure(IllegalArgumentException("Invalid backup file: Format identifier not recognized."))
            }

            val version = root.optInt("backupVersion", 1)
            val appVersion = root.optString("appVersion", "1.0")
            val createdAt = root.optString("createdAtFormatted", "Unknown date")

            // Parse festival settings
            val settingsObj = root.optJSONObject("festivalSettings")
            val festivalSettings = if (settingsObj != null) {
                FestivalSettings(
                    id = 1,
                    organizationName = settingsObj.optString("organizationName", "Shree Siddhivinayaka Parivar"),
                    festivalName = settingsObj.optString("festivalName", "Ganesh Chaturthi"),
                    festivalYear = settingsObj.optString("festivalYear", "2026"),
                    startDate = settingsObj.optString("startDate", "14 September 2026"),
                    endDate = settingsObj.optString("endDate", "18 September 2026"),
                    organizationAddress = settingsObj.optString("organizationAddress", "Ganesh Nagar, Anantapur"),
                    updatedAt = settingsObj.optLong("updatedAt", System.currentTimeMillis())
                )
            } else {
                FestivalSettings()
            }

            // Parse donors
            val donorsList = mutableListOf<Donor>()
            val donorsArray = root.optJSONArray("donors") ?: JSONArray()
            for (i in 0 until donorsArray.length()) {
                val obj = donorsArray.getJSONObject(i)
                donorsList.add(
                    Donor(
                        id = obj.optLong("id", 0),
                        name = obj.optString("name", "Unknown Donor"),
                        mobileNumber = obj.optString("mobileNumber", ""),
                        address = obj.optString("address", ""),
                        totalAmount = obj.optDouble("totalAmount", 0.0),
                        donationCount = obj.optInt("donationCount", 0),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }

            // Parse donations
            val donationsList = mutableListOf<Donation>()
            val donationsArray = root.optJSONArray("donations") ?: JSONArray()
            for (i in 0 until donationsArray.length()) {
                val obj = donationsArray.getJSONObject(i)
                donationsList.add(
                    Donation(
                        id = obj.optLong("id", 0),
                        donorId = obj.optLong("donorId", 0),
                        receiptNo = obj.optString("receiptNo", "REC-${System.currentTimeMillis()}-$i"),
                        donorName = obj.optString("donorName", "Unknown Donor"),
                        mobileNumber = obj.optString("mobileNumber", ""),
                        address = obj.optString("address", ""),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "General Donation"),
                        date = obj.optString("date", ""),
                        paymentMode = obj.optString("paymentMode", "Cash"),
                        notes = obj.optString("notes", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }

            // Parse expenses
            val expensesList = mutableListOf<Expense>()
            val expensesArray = root.optJSONArray("expenses") ?: JSONArray()
            for (i in 0 until expensesArray.length()) {
                val obj = expensesArray.getJSONObject(i)
                expensesList.add(
                    Expense(
                        id = obj.optLong("id", 0),
                        category = obj.optString("category", "General"),
                        description = obj.optString("description", ""),
                        amount = obj.optDouble("amount", 0.0),
                        date = obj.optString("date", ""),
                        vendor = obj.optString("vendor", ""),
                        paymentMode = obj.optString("paymentMode", "Cash"),
                        notes = obj.optString("notes", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }

            // Parse categories
            val categoriesList = mutableListOf<CategoryItem>()
            val categoriesArray = root.optJSONArray("categories") ?: JSONArray()
            for (i in 0 until categoriesArray.length()) {
                val obj = categoriesArray.getJSONObject(i)
                categoriesList.add(
                    CategoryItem(
                        id = obj.optLong("id", 0),
                        name = obj.optString("name", "Category $i"),
                        type = obj.optString("type", "EXPENSE"),
                        isDefault = obj.optBoolean("isDefault", false),
                        isActive = obj.optBoolean("isActive", true),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            // Parse activity logs
            val logsList = mutableListOf<ActivityLog>()
            val logsArray = root.optJSONArray("activityLogs") ?: JSONArray()
            for (i in 0 until logsArray.length()) {
                val obj = logsArray.getJSONObject(i)
                logsList.add(
                    ActivityLog(
                        id = obj.optLong("id", 0),
                        action = obj.optString("action", "General Activity"),
                        description = obj.optString("description", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }

            Result.success(
                ParsedBackupData(
                    format = root.getString("format"),
                    backupVersion = version,
                    appVersion = appVersion,
                    createdAtFormatted = createdAt,
                    festivalSettings = festivalSettings,
                    donors = donorsList,
                    donations = donationsList,
                    expenses = expensesList,
                    categories = categoriesList,
                    activityLogs = logsList
                )
            )
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Corrupted or incompatible backup format: ${e.localizedMessage ?: "Invalid JSON structure"}"))
        }
    }

    /**
     * Saves a safety backup snapshot to the app's internal storage before executing a restore.
     */
    fun saveInternalSafetyBackup(context: Context, backupJson: String): File {
        val backupsDir = File(context.filesDir, "safety_backups")
        if (!backupsDir.exists()) {
            backupsDir.mkdirs()
        }
        val file = File(backupsDir, "safety_backup_${System.currentTimeMillis()}.json")
        FileOutputStream(file).use { out ->
            out.write(backupJson.toByteArray(Charsets.UTF_8))
        }
        return file
    }

    /**
     * Writes backup string to a user-selected SAF Uri.
     */
    fun writeBackupToUri(context: Context, uri: Uri, jsonString: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Reads backup string from a SAF Uri.
     */
    fun readBackupFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Shares backup JSON file using Android's native share sheet.
     */
    fun shareBackup(context: Context, backupJson: String, fileName: String) {
        try {
            val cacheFile = File(context.cacheDir, fileName)
            FileOutputStream(cacheFile).use { it.write(backupJson.toByteArray(Charsets.UTF_8)) }

            val uri: Uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cacheFile
                )
            } catch (e: Exception) {
                Uri.fromFile(cacheFile)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Shree Siddhivinayaka Parivar Backup")
                putExtra(Intent.EXTRA_TEXT, "Shree Siddhivinayaka Parivar Database Backup file: $fileName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Database Backup")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback plain text share if file sharing fails
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Shree Siddhivinayaka Parivar Backup")
                putExtra(Intent.EXTRA_TEXT, backupJson)
            }
            val chooser = Intent.createChooser(textIntent, "Share Backup Data")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
}
