package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ActivityLog
import com.example.data.model.AdminSecurity
import com.example.data.model.CategoryItem
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.DonorWithDonations
import com.example.data.model.Expense
import com.example.data.model.FestivalSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY updatedAt DESC")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors")
    suspend fun getAllDonorsList(): List<Donor>

    @Transaction
    @Query("SELECT * FROM donors ORDER BY totalAmount DESC")
    fun getAllDonorsWithDonations(): Flow<List<DonorWithDonations>>

    @Query("SELECT * FROM donors WHERE id = :id LIMIT 1")
    suspend fun getDonorById(id: Long): Donor?

    @Query("SELECT * FROM donors WHERE mobileNumber = :mobile LIMIT 1")
    suspend fun getDonorByMobile(mobile: String): Donor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donors: List<Donor>)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Query("DELETE FROM donors")
    suspend fun deleteAllDonors()

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun getCount(): Int
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Query("SELECT * FROM donations")
    suspend fun getAllDonationsList(): List<Donation>

    @Query("SELECT * FROM donations WHERE id = :id LIMIT 1")
    suspend fun getDonationById(id: Long): Donation?

    @Query("SELECT * FROM donations WHERE receiptNo = :receiptNo LIMIT 1")
    suspend fun getDonationByReceiptNo(receiptNo: String): Donation?

    @Query("SELECT * FROM donations WHERE donorId = :donorId ORDER BY timestamp DESC")
    fun getDonationsByDonorId(donorId: Long): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE category = :category ORDER BY timestamp DESC")
    fun getDonationsByCategory(category: String): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE donorName LIKE '%' || :query || '%' OR mobileNumber LIKE '%' || :query || '%' OR receiptNo LIKE '%' || :query || '%'")
    fun searchDonations(query: String): Flow<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donations: List<Donation>)

    @Query("DELETE FROM donations")
    suspend fun deleteAllDonations()

    @Query("SELECT COUNT(*) FROM donations")
    suspend fun getCount(): Int
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpensesList(): List<Expense>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY timestamp DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE description LIKE '%' || :query || '%' OR vendor LIKE '%' || :query || '%'")
    fun searchExpenses(query: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<Expense>)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun getCount(): Int
}

@Dao
interface FestivalSettingsDao {
    @Query("SELECT * FROM festival_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<FestivalSettings?>

    @Query("SELECT * FROM festival_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): FestivalSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: FestivalSettings)

    @Update
    suspend fun updateSettings(settings: FestivalSettings)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY type ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryItem>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesList(): List<CategoryItem>

    @Query("SELECT * FROM categories WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    fun getActiveCategoriesByType(type: String): Flow<List<CategoryItem>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<CategoryItem>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): CategoryItem?

    @Query("SELECT * FROM categories WHERE name = :name AND type = :type LIMIT 1")
    suspend fun getCategoryByNameAndType(name: String, type: String): CategoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryItem>)

    @Update
    suspend fun updateCategory(category: CategoryItem)

    @Query("UPDATE categories SET isActive = :isActive WHERE id = :id")
    suspend fun setCategoryActive(id: Long, isActive: Boolean)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs")
    suspend fun getAllLogsList(): List<ActivityLog>

    @Query("SELECT * FROM activity_logs WHERE action LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLogs(query: String): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ActivityLog>)

    @Query("DELETE FROM activity_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT COUNT(*) FROM activity_logs")
    suspend fun getCount(): Int
}

@Dao
interface AdminSecurityDao {
    @Query("SELECT * FROM admin_security WHERE id = 1 LIMIT 1")
    fun getSecurityFlow(): Flow<AdminSecurity?>

    @Query("SELECT * FROM admin_security WHERE id = 1 LIMIT 1")
    suspend fun getSecurity(): AdminSecurity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurity(security: AdminSecurity)

    @Update
    suspend fun updateSecurity(security: AdminSecurity)

    @Query("UPDATE admin_security SET isAppLockEnabled = :enabled WHERE id = 1")
    suspend fun setAppLock(enabled: Boolean)
}
