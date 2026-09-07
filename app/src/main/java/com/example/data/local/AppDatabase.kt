package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AdminSecurity
import com.example.data.model.CategoryItem
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.Expense
import com.example.data.model.FestivalSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Donor::class,
        Donation::class,
        Expense::class,
        FestivalSettings::class,
        CategoryItem::class,
        ActivityLog::class,
        AdminSecurity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun donationDao(): DonationDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun festivalSettingsDao(): FestivalSettingsDao
    abstract fun categoryDao(): CategoryDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun adminSecurityDao(): AdminSecurityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS festival_settings (
                        id INTEGER PRIMARY KEY NOT NULL,
                        organizationName TEXT NOT NULL,
                        festivalName TEXT NOT NULL,
                        festivalYear TEXT NOT NULL,
                        startDate TEXT NOT NULL,
                        endDate TEXT NOT NULL,
                        organizationAddress TEXT NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isDefault INTEGER NOT NULL,
                        isActive INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_categories_name_type ON categories (name, type)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS activity_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        action TEXT NOT NULL,
                        description TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS admin_security (
                        id INTEGER PRIMARY KEY NOT NULL,
                        adminName TEXT NOT NULL,
                        username TEXT NOT NULL,
                        mobileNumber TEXT NOT NULL,
                        passwordHash TEXT NOT NULL,
                        salt TEXT NOT NULL,
                        isAppLockEnabled INTEGER NOT NULL,
                        lockPinHash TEXT NOT NULL,
                        status TEXT NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE admin_security ADD COLUMN autoLockTimeoutMinutes INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE admin_security ADD COLUMN isBiometricEnabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "siddhivinayaka_festival.db"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



