package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryItem
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTextField
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CollectionGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoriesManagementScreen(
    categories: List<CategoryItem>,
    onBack: () -> Unit,
    onAddCategory: (name: String, type: String) -> Unit,
    onRenameCategory: (id: Long, newName: String) -> Unit,
    onToggleCategoryActive: (id: Long, isActive: Boolean) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Expense, 1 = Donation
    val selectedType = if (selectedTabIndex == 0) "EXPENSE" else "DONATION"

    val filteredCategories = categories.filter { it.type.equals(selectedType, ignoreCase = true) }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_categories_management")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Categories",
                onBack = onBack
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = RoyalPurplePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = RoyalPurplePrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "Expense (${categories.count { it.type == "EXPENSE" }})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "Donation (${categories.count { it.type == "DONATION" }})",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            // Info note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAF5FF))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = RoyalPurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Historical records remain safe. Inactive categories won't appear in new entry forms.",
                    fontSize = 11.sp,
                    color = Color(0xFF6B21A8)
                )
            }

            // Categories List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCategories, key = { it.id }) { cat ->
                    CategoryItemCard(
                        category = cat,
                        onEditClick = { editingCategory = cat },
                        onToggleActive = { onToggleCategoryActive(cat.id, it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // FAB to add category
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = RoyalPurplePrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_category")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Category", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Add Category Dialog
        if (showAddDialog) {
            AddCategoryDialog(
                initialType = selectedType,
                existingNames = categories.map { it.name.lowercase().trim() },
                onDismiss = { showAddDialog = false },
                onAdd = { name, type ->
                    onAddCategory(name, type)
                    showAddDialog = false
                }
            )
        }

        // Edit/Rename Category Dialog
        editingCategory?.let { cat ->
            RenameCategoryDialog(
                category = cat,
                existingNames = categories.filter { it.id != cat.id }.map { it.name.lowercase().trim() },
                onDismiss = { editingCategory = null },
                onRename = { newName ->
                    onRenameCategory(cat.id, newName)
                    editingCategory = null
                }
            )
        }
    }
}

@Composable
private fun CategoryItemCard(
    category: CategoryItem,
    onEditClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (category.isActive) Color.White else Color(0xFFF1F5F9)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (category.isActive) BorderLight else BorderLight.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .testTag("category_card_${category.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (category.isActive) {
                            if (category.type == "EXPENSE") Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                        } else Color(0xFFE2E8F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = if (category.isActive) {
                        if (category.type == "EXPENSE") ExpenseRed else CollectionGreen
                    } else TextSecondary,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (category.isActive) TextPrimary else TextSecondary
                    )
                    if (category.isDefault) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE0E7FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Default", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
                        }
                    }
                }

                Text(
                    text = if (category.isActive) "Active • Visible in forms" else "Archived • Hidden from new entries",
                    fontSize = 11.sp,
                    color = if (category.isActive) CollectionGreen else TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Edit button
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Rename",
                    tint = RoyalPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Switch Active/Archive
            Switch(
                checked = category.isActive,
                onCheckedChange = onToggleActive,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = RoyalPurplePrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Composable
private fun AddCategoryDialog(
    initialType: String,
    existingNames: List<String>,
    onDismiss: () -> Unit,
    onAdd: (name: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(initialType) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Category", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { type = "EXPENSE" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "EXPENSE") ExpenseRed else Color(0xFFF1F5F9),
                            contentColor = if (type == "EXPENSE") Color.White else TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Expense", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { type = "DONATION" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "DONATION") CollectionGreen else Color(0xFFF1F5F9),
                            contentColor = if (type == "DONATION") Color.White else TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Donation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                AppTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        error = null
                    },
                    label = "Category Name *",
                    placeholder = "e.g. Flower Decoration, Prasadam",
                    isError = error != null,
                    errorMessage = error,
                    testTag = "input_new_category_name"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clean = name.trim()
                    if (clean.isBlank()) {
                        error = "Category name cannot be empty"
                    } else if (existingNames.contains(clean.lowercase())) {
                        error = "A category with this name already exists"
                    } else {
                        onAdd(clean, type)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary)
            ) {
                Text("ADD CATEGORY", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun RenameCategoryDialog(
    category: CategoryItem,
    existingNames: List<String>,
    onDismiss: () -> Unit,
    onRename: (newName: String) -> Unit
) {
    var newName by remember { mutableStateOf(category.name) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Rename Category", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Renaming '${category.name}' (${category.type})",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                AppTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        error = null
                    },
                    label = "New Name *",
                    placeholder = "Enter category name",
                    isError = error != null,
                    errorMessage = error,
                    testTag = "input_rename_category"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clean = newName.trim()
                    if (clean.isBlank()) {
                        error = "Category name cannot be empty"
                    } else if (existingNames.contains(clean.lowercase())) {
                        error = "A category with this name already exists"
                    } else {
                        onRename(clean)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary)
            ) {
                Text("SAVE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}
