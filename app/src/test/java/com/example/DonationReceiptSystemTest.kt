package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.FestivalRepository
import com.example.ui.CurrencyUtils
import com.example.ui.utils.FinancialReportGenerator
import com.example.ui.utils.PdfReceiptGenerator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DonationReceiptSystemTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FestivalRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FestivalRepository(
            donorDao = database.donorDao(),
            donationDao = database.donationDao(),
            expenseDao = database.expenseDao(),
            festivalSettingsDao = database.festivalSettingsDao(),
            categoryDao = database.categoryDao(),
            activityLogDao = database.activityLogDao(),
            adminSecurityDao = database.adminSecurityDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAmountInWords() {
        assertEquals("Rupees Five Hundred Only", CurrencyUtils.convertNumberToWords(500.0))
        assertEquals("Rupees One Thousand Only", CurrencyUtils.convertNumberToWords(1000.0))
        assertEquals("Rupees Five Thousand Only", CurrencyUtils.convertNumberToWords(5000.0))
        assertEquals("Rupees One Lakh Twenty Five Thousand Only", CurrencyUtils.convertNumberToWords(125000.0))
        assertEquals("Rupees Zero Only", CurrencyUtils.convertNumberToWords(0.0))
        assertEquals("Rupees Fifty Only", CurrencyUtils.convertNumberToWords(50.0))
        assertEquals("Rupees One Lakh Only", CurrencyUtils.convertNumberToWords(100000.0))
    }

    @Test
    fun testSequentialReceiptNumbersAndNoDuplicates() = runBlocking {
        val donation1 = repository.saveDonation(
            donorName = "Ramesh Sharma",
            mobile = "9876543210",
            address = "Mumbai",
            amount = 500.0,
            category = "General Donation",
            date = "28-08-2026",
            paymentMode = "Cash",
            notes = ""
        )

        val donation2 = repository.saveDonation(
            donorName = "Suresh Patel",
            mobile = "9876543211",
            address = "Pune",
            amount = 1000.0,
            category = "Annadhanam",
            date = "28-08-2026",
            paymentMode = "UPI",
            notes = ""
        )

        val donation3 = repository.saveDonation(
            donorName = "Amit Verma",
            mobile = "9876543212",
            address = "Thane",
            amount = 5000.0,
            category = "Pooja Seva",
            date = "28-08-2026",
            paymentMode = "Bank Transfer",
            notes = ""
        )

        assertEquals("GCP-00001", donation1.receiptNo)
        assertEquals("GCP-00002", donation2.receiptNo)
        assertEquals("GCP-00003", donation3.receiptNo)

        assertNotEquals(donation1.receiptNo, donation2.receiptNo)
        assertNotEquals(donation2.receiptNo, donation3.receiptNo)
        assertNotEquals(donation1.receiptNo, donation3.receiptNo)
    }

    @Test
    fun testSameDonorMultipleDonations_SeparateReceiptsAndUpdatedTotals() = runBlocking {
        val mobile = "9812345678"
        val donorName = "Venkata Rao"

        val d1 = repository.saveDonation(
            donorName = donorName,
            mobile = mobile,
            address = "Flat 101, Galaxy Apts",
            amount = 1000.0,
            category = "General Donation",
            date = "28-08-2026",
            paymentMode = "Cash",
            notes = ""
        )

        val d2 = repository.saveDonation(
            donorName = donorName,
            mobile = mobile,
            address = "Flat 101, Galaxy Apts",
            amount = 2500.0,
            category = "Laddu Prasadam",
            date = "28-08-2026",
            paymentMode = "UPI",
            notes = ""
        )

        // Same donor ID
        assertEquals(d1.donorId, d2.donorId)
        // Separate sequential receipts
        assertEquals("GCP-00001", d1.receiptNo)
        assertEquals("GCP-00002", d2.receiptNo)

        // Check Donor in DB
        val donor = database.donorDao().getDonorByMobile(mobile)
        assertNotNull(donor)
        assertEquals(3500.0, donor!!.totalAmount, 0.001)
        assertEquals(2, donor.donationCount)

        // Check financial summary
        val summary = repository.financialSummary.first()
        assertEquals(3500.0, summary.totalCollection, 0.001)
        assertEquals(1000.0, summary.cashCollection, 0.001)
        assertEquals(2500.0, summary.onlineCollection, 0.001)
        assertEquals(1, summary.totalDonorsCount)
    }

    @Test
    fun testPdfGeneration() {
        val testDonation = com.example.data.model.Donation(
            id = 1,
            donorId = 1,
            receiptNo = "GCP-00001",
            donorName = "Rajesh Kumar",
            mobileNumber = "9988776655",
            address = "MG Road, Bengaluru",
            amount = 5000.0,
            category = "Maha Pooja",
            date = "28-08-2026",
            paymentMode = "UPI",
            notes = "Devotee offering"
        )

        val pdfFile = PdfReceiptGenerator.generatePdf(context, testDonation)
        assertTrue(pdfFile.exists())
        assertTrue(pdfFile.length() > 0)
        assertTrue(pdfFile.name.endsWith(".pdf"))
    }

    @Test
    fun testExpenseSaveAndFinancialSummaryCalculation() = runBlocking {
        // Step 1: Add 2 donations (₹5,000 + ₹2,000 = ₹7,000)
        repository.saveDonation(
            donorName = "Anand Sharma",
            mobile = "9900112233",
            address = "Dadar, Mumbai",
            amount = 5000.0,
            category = "General Donation",
            date = "28 Aug 2026",
            paymentMode = "Cash",
            notes = "First donation"
        )

        repository.saveDonation(
            donorName = "Kiran Patil",
            mobile = "9900112244",
            address = "Parel, Mumbai",
            amount = 2000.0,
            category = "Laddu Prasadam",
            date = "28 Aug 2026",
            paymentMode = "UPI",
            notes = "Second donation"
        )

        // Step 2: Add 1 expense (₹1,500 for Tent & Decoration)
        repository.addExpense(
            com.example.data.model.Expense(
                category = "Tent & Decoration",
                description = "Stage Pandal Setup",
                amount = 1500.0,
                date = "28 Aug 2026",
                vendor = "Balaji Mandap Decorators",
                paymentMode = "Cash",
                notes = "Advance payment",
                timestamp = System.currentTimeMillis()
            )
        )

        // Verify summary
        var summary = repository.financialSummary.first()
        assertEquals(7000.0, summary.totalCollection, 0.001)
        assertEquals(1500.0, summary.totalExpenses, 0.001)
        assertEquals(5500.0, summary.currentBalance, 0.001) // 7000 - 1500
        assertEquals(1, summary.totalExpensesCount)
        assertEquals(2, summary.totalDonorsCount)

        // Step 3: Add 2nd expense (₹800 for Sound System)
        repository.addExpense(
            com.example.data.model.Expense(
                category = "Sound System",
                description = "Microphones and Speakers",
                amount = 800.0,
                date = "28 Aug 2026",
                vendor = "Swara Sound Services",
                paymentMode = "UPI",
                notes = "Day 1 hire",
                timestamp = System.currentTimeMillis() + 1000
            )
        )

        // Verify updated summary
        summary = repository.financialSummary.first()
        assertEquals(7000.0, summary.totalCollection, 0.001)
        assertEquals(2300.0, summary.totalExpenses, 0.001) // 1500 + 800
        assertEquals(4700.0, summary.currentBalance, 0.001) // 7000 - 2300
        assertEquals(2, summary.totalExpensesCount) // 2 records

        // Verify Recent Transactions order and representation
        val transactions = repository.recentTransactions.first()
        assertEquals(4, transactions.size) // 2 donations + 2 expenses
        // Newest should be the last added expense
        assertTrue(transactions[0] is com.example.data.model.TransactionItem.ExpenseTx)
        val expenseTx = transactions[0] as com.example.data.model.TransactionItem.ExpenseTx
        assertEquals("Sound System", expenseTx.expense.category)
        assertEquals(800.0, expenseTx.expense.amount, 0.001)
    }

    @Test
    fun testExpenseSearchAndCategoryQueries() = runBlocking {
        repository.addExpense(
            com.example.data.model.Expense(
                category = "Lighting",
                description = "LED Series Lights for Main Gate",
                amount = 2200.0,
                date = "28 Aug 2026",
                vendor = "Bright Light Electricals",
                paymentMode = "Bank Transfer",
                notes = "",
                timestamp = 1000L
            )
        )

        repository.addExpense(
            com.example.data.model.Expense(
                category = "Annadanam",
                description = "Rice and Grocery Supplies",
                amount = 4500.0,
                date = "28 Aug 2026",
                vendor = "Sri Krishna Traders",
                paymentMode = "Cash",
                notes = "Mahaprasad",
                timestamp = 2000L
            )
        )

        val allExpenses = repository.allExpenses.first()
        assertEquals(2, allExpenses.size)

        val lightingList = database.expenseDao().getExpensesByCategory("Lighting").first()
        assertEquals(1, lightingList.size)
        assertEquals("Bright Light Electricals", lightingList[0].vendor)

        val searchVendorList = database.expenseDao().searchExpenses("Krishna").first()
        assertEquals(1, searchVendorList.size)
        assertEquals("Annadanam", searchVendorList[0].category)
    }

    @Test
    fun testDonorsAndDonorDetailsScenario() = runBlocking {
        // Step 1: Ravi Kumar donation 1 -> ₹5,000
        val r1 = repository.saveDonation(
            donorName = "Ravi Kumar",
            mobile = "9876543210",
            address = "Ganesh Nagar, Anantapur",
            amount = 5000.0,
            category = "General Donation",
            date = "18 Aug 2026",
            paymentMode = "Cash",
            notes = "First offering"
        )
        assertEquals("GCP-00001", r1.receiptNo)

        // Step 2: Ravi Kumar donation 2 -> ₹2,000 (same mobile)
        val r2 = repository.saveDonation(
            donorName = "Ravi Kumar",
            mobile = "9876543210",
            address = "Ganesh Nagar, Anantapur",
            amount = 2000.0,
            category = "Laddu Prasadam",
            date = "20 Aug 2026",
            paymentMode = "UPI",
            notes = "Second offering"
        )
        assertEquals("GCP-00002", r2.receiptNo)

        // Step 3: Suresh Babu donation -> ₹3,000
        val r3 = repository.saveDonation(
            donorName = "Suresh Babu",
            mobile = "9123456780",
            address = "Temple Street, Anantapur",
            amount = 3000.0,
            category = "Annadanam",
            date = "21 Aug 2026",
            paymentMode = "UPI",
            notes = "Annadanam Seva"
        )
        assertEquals("GCP-00003", r3.receiptNo)

        // Verify Donors Screen groups
        val groups = repository.donorGroups.first()
        assertEquals(2, groups.size) // Total 2 unique donors

        val ravi = groups.find { it.mobileNumber == "9876543210" }!!
        assertEquals("Ravi Kumar", ravi.donorName)
        assertEquals(7000.0, ravi.totalAmount, 0.001)
        assertEquals(2, ravi.donationCount)
        assertEquals(2, ravi.donations.size)
        // History should have 2 individual donations, newest first
        assertEquals(2000.0, ravi.donations[0].amount, 0.001)
        assertEquals("GCP-00002", ravi.donations[0].receiptNo)
        assertEquals("UPI", ravi.donations[0].paymentMode)
        assertEquals(5000.0, ravi.donations[1].amount, 0.001)
        assertEquals("GCP-00001", ravi.donations[1].receiptNo)
        assertEquals("Cash", ravi.donations[1].paymentMode)

        val suresh = groups.find { it.mobileNumber == "9123456780" }!!
        assertEquals("Suresh Babu", suresh.donorName)
        assertEquals(3000.0, suresh.totalAmount, 0.001)
        assertEquals(1, suresh.donationCount)
        assertEquals(1, suresh.donations.size)
        assertEquals(3000.0, suresh.donations[0].amount, 0.001)
        assertEquals("GCP-00003", suresh.donations[0].receiptNo)

        // Verify total collection & summary
        val summary = repository.financialSummary.first()
        assertEquals(10000.0, summary.totalCollection, 0.001)
        assertEquals(2, summary.totalDonorsCount)

        // Simulate app reopening with new repository instance reading from same SQLite database
        val reopenedRepo = FestivalRepository(database.donorDao(), database.donationDao(), database.expenseDao())
        val persistedGroups = reopenedRepo.donorGroups.first()
        assertEquals(2, persistedGroups.size)
        val persistedRavi = persistedGroups.find { it.mobileNumber == "9876543210" }!!
        assertEquals(7000.0, persistedRavi.totalAmount, 0.001)
        assertEquals(2, persistedRavi.donationCount)
        assertEquals(2, persistedRavi.donations.size)
    }

    @Test
    fun testReportsModuleScenarioAndExportPdfExcel() = runBlocking {
        // Step 1: Zero-data behavior check
        val emptySummary = repository.financialSummary.first()
        assertEquals(0.0, emptySummary.totalCollection, 0.001)
        assertEquals(0.0, emptySummary.cashCollection, 0.001)
        assertEquals(0.0, emptySummary.upiCollection, 0.001)
        assertEquals(0.0, emptySummary.bankCollection, 0.001)
        assertEquals(0.0, emptySummary.totalExpenses, 0.001)
        assertEquals(0.0, emptySummary.currentBalance, 0.001)
        assertEquals(0, emptySummary.totalDonorsCount)
        assertEquals(0, emptySummary.totalDonationsCount)
        assertEquals(0, emptySummary.totalExpensesCount)
        assertEquals(0.0, emptySummary.averageDonation, 0.001)

        // Step 2: Add exact test scenario requested:
        // Donation: Ravi Kumar -> ₹5,000 -> UPI
        repository.saveDonation(
            donorName = "Ravi Kumar",
            mobile = "9876543210",
            address = "Anantapur",
            amount = 5000.0,
            category = "General Donation",
            date = "15 Sep 2026",
            paymentMode = "UPI",
            notes = "UPI offering"
        )

        // Donation: Ravi Kumar -> ₹2,000 -> Cash
        repository.saveDonation(
            donorName = "Ravi Kumar",
            mobile = "9876543210",
            address = "Anantapur",
            amount = 2000.0,
            category = "General Donation",
            date = "16 Sep 2026",
            paymentMode = "Cash",
            notes = "Cash offering"
        )

        // Donation: Suresh Babu -> ₹3,000 -> UPI
        repository.saveDonation(
            donorName = "Suresh Babu",
            mobile = "9123456780",
            address = "Anantapur",
            amount = 3000.0,
            category = "General Donation",
            date = "17 Sep 2026",
            paymentMode = "UPI",
            notes = "UPI offering"
        )

        // Expenses:
        // Tent & Decoration -> ₹1,000 -> Cash
        repository.saveExpense(
            category = "Tent & Decoration",
            description = "Main Pandal setup",
            amount = 1000.0,
            date = "14 Sep 2026",
            vendor = "Sri Rama Tent House",
            paymentMode = "Cash",
            notes = "Advance"
        )

        // Sound System -> ₹2,000 -> UPI
        repository.saveExpense(
            category = "Sound System",
            description = "Speakers & mic rental",
            amount = 2000.0,
            date = "15 Sep 2026",
            vendor = "Balaji Sounds",
            paymentMode = "UPI",
            notes = "Stage audio"
        )

        // Step 3: Verify Expected Totals:
        val summary = repository.financialSummary.first()
        val allExpenses = repository.allExpenses.first()
        val allDonations = repository.allDonations.first()
        val allDonors = repository.donorGroups.first()

        assertEquals(10000.0, summary.totalCollection, 0.001)
        assertEquals(2000.0, summary.cashCollection, 0.001)
        assertEquals(8000.0, summary.upiCollection, 0.001)
        assertEquals(0.0, summary.bankCollection, 0.001)
        assertEquals(3000.0, summary.totalExpenses, 0.001)
        assertEquals(7000.0, summary.currentBalance, 0.001)
        assertEquals(2, summary.totalDonorsCount)
        assertEquals(3, summary.totalDonationsCount)
        assertEquals(2, summary.totalExpensesCount)
        assertEquals(10000.0 / 3.0, summary.averageDonation, 0.001)

        // Expense categories verification:
        val tentExpense = allExpenses.filter { it.category.equals("Tent & Decoration", ignoreCase = true) }.sumOf { it.amount }
        val soundExpense = allExpenses.filter { it.category.equals("Sound System", ignoreCase = true) }.sumOf { it.amount }
        assertEquals(1000.0, tentExpense, 0.001)
        assertEquals(2000.0, soundExpense, 0.001)

        // Step 4: Verify PDF Report Generation from real DB data
        val pdfFile = FinancialReportGenerator.generatePdfReport(
            context = context,
            summary = summary,
            expenses = allExpenses,
            donations = allDonations
        )
        assertNotNull(pdfFile)
        assertTrue(pdfFile.exists())
        assertTrue(pdfFile.length() > 0)

        // Step 5: Verify Excel / CSV Report Generation from real DB data
        val excelFile = FinancialReportGenerator.generateExcelReport(
            context = context,
            summary = summary,
            expenses = allExpenses,
            donations = allDonations,
            donors = allDonors
        )
        assertNotNull(excelFile)
        assertTrue(excelFile.exists())
        assertTrue(excelFile.length() > 0)

        val csvContent = excelFile.readText()
        assertTrue(csvContent.contains("SHREE SIDDHIVINAYAKA PARIVAR"))
        assertTrue(csvContent.contains("1. FINANCIAL SUMMARY"))
        assertTrue(csvContent.contains("Total Collection,10000.0"))
        assertTrue(csvContent.contains("Cash Collection,2000.0"))
        assertTrue(csvContent.contains("UPI Collection,8000.0"))
        assertTrue(csvContent.contains("Total Expenses,3000.0"))
        assertTrue(csvContent.contains("Current Net Balance,7000.0"))
        assertTrue(csvContent.contains("Tent & Decoration,1000.0"))
        assertTrue(csvContent.contains("Sound System,2000.0"))
        assertTrue(csvContent.contains("Ravi Kumar"))
        assertTrue(csvContent.contains("Suresh Babu"))
        assertTrue(csvContent.contains("Sri Rama Tent House"))
        assertTrue(csvContent.contains("Balaji Sounds"))

        // Step 6: Verify Dashboard and Reports use exact same Single Source of Truth
        val dashboardSummary = repository.financialSummary.first()
        assertEquals(summary.totalCollection, dashboardSummary.totalCollection, 0.001)
        assertEquals(summary.totalExpenses, dashboardSummary.totalExpenses, 0.001)
        assertEquals(summary.currentBalance, dashboardSummary.currentBalance, 0.001)
        assertEquals(summary.totalDonorsCount, dashboardSummary.totalDonorsCount)

        // Step 7: Simulate App Restart and verify full database persistence
        val reopenedRepo = FestivalRepository(database.donorDao(), database.donationDao(), database.expenseDao())
        val persistedSummary = reopenedRepo.financialSummary.first()
        assertEquals(10000.0, persistedSummary.totalCollection, 0.001)
        assertEquals(2000.0, persistedSummary.cashCollection, 0.001)
        assertEquals(8000.0, persistedSummary.upiCollection, 0.001)
        assertEquals(0.0, persistedSummary.bankCollection, 0.001)
        assertEquals(3000.0, persistedSummary.totalExpenses, 0.001)
        assertEquals(7000.0, persistedSummary.currentBalance, 0.001)
        assertEquals(2, persistedSummary.totalDonorsCount)
        assertEquals(3, persistedSummary.totalDonationsCount)
        assertEquals(2, persistedSummary.totalExpensesCount)
    }

    @Test
    fun testFestivalSettingsSaveAndPersistence() = runBlocking {
        val initial = repository.getFestivalSettings()
        assertEquals("Shree Siddhivinayaka Parivar", initial.organizationName)
        assertEquals("Ganesh Chaturthi", initial.festivalName)
        assertEquals("2026", initial.festivalYear)

        val updated = initial.copy(
            organizationName = "Shree Siddhivinayaka Utsava Samithi",
            startDate = "15 September 2026",
            endDate = "25 September 2026",
            organizationAddress = "Main Market Chowk, Anantapur"
        )
        repository.saveFestivalSettings(updated)

        val persisted = repository.getFestivalSettings()
        assertEquals("Shree Siddhivinayaka Utsava Samithi", persisted.organizationName)
        assertEquals("15 September 2026", persisted.startDate)
        assertEquals("25 September 2026", persisted.endDate)
        assertEquals("Main Market Chowk, Anantapur", persisted.organizationAddress)

        // Verify activity log was recorded
        val logs = repository.activityLogs.first()
        assertTrue(logs.any { it.action.contains("Settings Updated") })
    }

    @Test
    fun testCategoryManagement_AddRenameToggleArchive_NoLossOfFinancialRecords() = runBlocking {
        repository.ensureDefaultCategories()

        // 1. Initial category count
        val initCategories = repository.allCategories.first()
        assertTrue(initCategories.isNotEmpty())
        assertTrue(initCategories.any { it.name.contains("Annadanam") && it.type == "DONATION" })

        // 2. Add new category
        val addSuccess = repository.addCategory("Special Abhishekham", "DONATION")
        assertTrue(addSuccess)

        // Duplicate name prevention
        val dupSuccess = repository.addCategory("Special Abhishekham", "DONATION")
        assertTrue(!dupSuccess)

        val activeDonations = repository.activeDonationCategories.first()
        assertTrue(activeDonations.any { it.name == "Special Abhishekham" })

        // 3. Make a donation with category "Special Abhishekham"
        val d = repository.saveDonation(
            donorName = "Gopal Rao",
            mobile = "9988112233",
            address = "Anantapur",
            amount = 1100.0,
            category = "Special Abhishekham",
            date = "28 Aug 2026",
            paymentMode = "Cash",
            notes = "Morning pooja"
        )
        assertEquals(1100.0, d.amount, 0.001)

        // 4. Archive/Deactivate category
        val catItem = repository.allCategories.first().find { it.name == "Special Abhishekham" }!!
        repository.toggleCategoryActive(catItem.id, false)

        val activeAfterArchive = repository.activeDonationCategories.first()
        assertTrue(!activeAfterArchive.any { it.name == "Special Abhishekham" })

        // 5. CRITICAL: Ensure historical donation record is preserved intact!
        val preservedDonation = repository.getDonationById(d.id)
        assertNotNull(preservedDonation)
        assertEquals("Special Abhishekham", preservedDonation!!.category)
        assertEquals(1100.0, preservedDonation.amount, 0.001)

        val summary = repository.financialSummary.first()
        assertEquals(1100.0, summary.totalCollection, 0.001)
    }

    @Test
    fun testAdminSecurity_DefaultLogin_ChangePassword_Sha256Hashing_AppLock() = runBlocking {
        val initialSec = repository.ensureAdminSecurity()
        assertEquals("Head of Committee", initialSec.adminName)
        assertEquals("admin", initialSec.username)
        assertTrue(initialSec.passwordHash.isNotBlank())
        assertTrue(initialSec.salt.isNotBlank())
        assertNotEquals("admin", initialSec.passwordHash) // NEVER store in plain text

        // Verify default login with 'admin' / 'admin'
        val loginSuccess = repository.verifyAdminLogin("admin", "admin")
        assertTrue(loginSuccess)

        val wrongPassLogin = repository.verifyAdminLogin("admin", "wrongpassword")
        assertTrue(!wrongPassLogin)

        // Change password
        val changeResult = repository.changePassword("admin", "Vinayaka@2026")
        assertTrue(changeResult.isSuccess)

        // Old password must fail
        val oldPassFails = repository.verifyAdminLogin("admin", "admin")
        assertTrue(!oldPassFails)

        // New password must succeed
        val newPassSucceeds = repository.verifyAdminLogin("admin", "Vinayaka@2026")
        assertTrue(newPassSucceeds)

        // App Lock toggle and unlock verification
        repository.setAppLock(true, "1234")
        val secAfterLock = repository.ensureAdminSecurity()
        assertTrue(secAfterLock.isAppLockEnabled)

        // Verify unlock with PIN
        assertTrue(repository.verifyUnlock("1234"))
        // Verify unlock with master password
        assertTrue(repository.verifyUnlock("Vinayaka@2026"))
        // Verify invalid unlock
        assertTrue(!repository.verifyUnlock("9999"))
    }

    @Test
    fun testLogoutDoesNotDeleteFinancialData() = runBlocking {
        // Add donation and expense
        repository.saveDonation(
            donorName = "Nagaraju",
            mobile = "9440011223",
            address = "Anantapur",
            amount = 5000.0,
            category = "General Donation",
            date = "28 Aug 2026",
            paymentMode = "UPI",
            notes = ""
        )

        repository.saveExpense(
            category = "Lighting",
            description = "Pandal illumination",
            amount = 1200.0,
            date = "28 Aug 2026",
            vendor = "Sri Lakshmi Lights",
            paymentMode = "Cash",
            notes = ""
        )

        // Simulate Logout activity
        repository.logActivity("Admin Logout", "Admin session ended")

        // Re-read DB
        val donations = repository.allDonations.first()
        val expenses = repository.allExpenses.first()
        val summary = repository.financialSummary.first()

        assertEquals(1, donations.size)
        assertEquals(1, expenses.size)
        assertEquals(5000.0, summary.totalCollection, 0.001)
        assertEquals(1200.0, summary.totalExpenses, 0.001)
        assertEquals(3800.0, summary.currentBalance, 0.001)
    }
}
