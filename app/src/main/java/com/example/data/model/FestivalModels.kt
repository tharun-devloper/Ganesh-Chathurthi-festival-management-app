package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "donors",
    indices = [Index(value = ["mobileNumber"])]
)
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mobileNumber: String = "",
    val address: String = "",
    val totalAmount: Double = 0.0,
    val donationCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "donations",
    foreignKeys = [
        ForeignKey(
            entity = Donor::class,
            parentColumns = ["id"],
            childColumns = ["donorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["receiptNo"], unique = true),
        Index(value = ["donorId"])
    ]
)
data class Donation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val donorId: Long = 0,
    val receiptNo: String,
    val donorName: String,
    val mobileNumber: String = "",
    val address: String = "",
    val amount: Double,
    val category: String = "General Donation",
    val date: String,
    val paymentMode: String = "Cash", // "Cash", "UPI", "Bank Transfer"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "Tent & Decoration", "Sound System", "Flower Decoration", "Lighting", "Flex & Printing", "Annadanam", "Idol", "Transport", "Others"
    val description: String,
    val amount: Double,
    val date: String,
    val vendor: String = "",
    val paymentMode: String = "Cash", // "Cash", "UPI", "Bank Transfer"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class DonorWithDonations(
    @Embedded val donor: Donor,
    @Relation(
        parentColumn = "id",
        entityColumn = "donorId"
    )
    val donations: List<Donation>
)

data class DonorGroup(
    val donorId: Long = 0,
    val donorName: String,
    val mobileNumber: String,
    val address: String,
    val totalAmount: Double,
    val donationCount: Int,
    val donations: List<Donation>
)

data class FinancialSummary(
    val totalCollection: Double,
    val cashCollection: Double,
    val onlineCollection: Double,
    val upiCollection: Double = 0.0,
    val bankCollection: Double = 0.0,
    val totalExpenses: Double,
    val currentBalance: Double,
    val totalDonorsCount: Int,
    val totalDonationsCount: Int = 0,
    val totalExpensesCount: Int,
    val averageDonation: Double = 0.0
)

sealed class TransactionItem {
    data class DonationTx(val donation: Donation) : TransactionItem()
    data class ExpenseTx(val expense: Expense) : TransactionItem()
}

@Entity(tableName = "festival_settings")
data class FestivalSettings(
    @PrimaryKey val id: Int = 1,
    val organizationName: String = "Shree Siddhivinayaka Parivar",
    val festivalName: String = "Ganesh Chaturthi",
    val festivalYear: String = "2026",
    val startDate: String = "14 September 2026",
    val endDate: String = "18 September 2026",
    val organizationAddress: String = "Ganesh Nagar, Anantapur",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class CategoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "DONATION" or "EXPENSE"
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // e.g. "Donation Added", "Expense Added", "Settings Updated", "Password Changed", "App Lock Enabled", "Backup Created", "Login", "Logout"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_security")
data class AdminSecurity(
    @PrimaryKey val id: Int = 1,
    val adminName: String = "Head of Committee",
    val username: String = "admin",
    val mobileNumber: String = "+91 98765 43210",
    val passwordHash: String = "",
    val salt: String = "",
    val isAppLockEnabled: Boolean = false,
    val lockPinHash: String = "",
    val autoLockTimeoutMinutes: Int = 0, // 0 = Immediately, 1 = 1 min, 5 = 5 min, 15 = 15 min
    val isBiometricEnabled: Boolean = false,
    val status: String = "Active (Super Admin)",
    val updatedAt: Long = System.currentTimeMillis()
)


