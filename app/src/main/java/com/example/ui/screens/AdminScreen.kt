package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TypingMockEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ExamTagBadge
import com.example.ui.components.LevelBadge
import com.example.ui.theme.ExamFailRed

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val allMocks by viewModel.allMocks.collectAsState()

    var activeAdminTab by remember { mutableIntStateOf(0) } // 0: Upload & Add, 1: Manage Mocks, 2: Bulk Tools
    var editingMock by remember { mutableStateOf<TypingMockEntity?>(null) }
    var mockToDelete by remember { mutableStateOf<TypingMockEntity?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Dedicated Admin Dark Studio Theme
    ) {
        // Admin Portal Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Home) },
                    modifier = Modifier.testTag("admin_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFBBF24)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admin Studio & Backend",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Dedicated portal for mock uploading, cutoffs & management",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF334155), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${allMocks.size} Mocks",
                    color = Color(0xFFFBBF24),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Dedicated Tab Navigation for Admin Functions
        val adminTabs = listOf(
            "Upload Mock" to Icons.Default.CloudUpload,
            "Manage Repository" to Icons.Default.LibraryBooks,
            "Tools & JSON" to Icons.Default.Settings
        )

        TabRow(
            selectedTabIndex = activeAdminTab,
            containerColor = Color(0xFF1E293B),
            contentColor = Color(0xFFFBBF24),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeAdminTab]),
                    color = Color(0xFFFBBF24)
                )
            }
        ) {
            adminTabs.forEachIndexed { index, (label, icon) ->
                Tab(
                    selected = activeAdminTab == index,
                    onClick = { activeAdminTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = label, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = Color(0xFFFBBF24),
                    unselectedContentColor = Color(0xFF94A3B8)
                )
            }
        }

        // Body based on selected Admin Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
        ) {
            when (activeAdminTab) {
                0 -> AdminUploadMockView(
                    onSaveMock = { mock ->
                        viewModel.saveCustomMock(mock)
                        Toast.makeText(context, "Mock '${mock.title}' published successfully!", Toast.LENGTH_SHORT).show()
                        activeAdminTab = 1 // Navigate to repository to view
                    }
                )

                1 -> AdminRepositoryView(
                    allMocks = allMocks,
                    onEdit = { mock -> editingMock = mock },
                    onDelete = { mock -> mockToDelete = mock }
                )

                2 -> AdminToolsView(
                    allMocks = allMocks,
                    onResetDefaults = { showResetDialog = true },
                    onExportJson = {
                        val jsonExport = allMocks.joinToString(prefix = "[\n", postfix = "\n]", separator = ",\n") { m ->
                            """  {"id":"${m.id}","title":"${m.title}","level":${m.level},"exam":"${m.examTag}","duration":${m.durationMinutes},"words":${m.wordCount},"ur_cutoff":${m.cutoffGeneral}}"""
                        }
                        clipboardManager.setText(AnnotatedString(jsonExport))
                        Toast.makeText(context, "Mocks manifest JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Edit Existing Mock Dialog
    if (editingMock != null) {
        MockEditorDialog(
            initialMock = editingMock,
            onDismiss = { editingMock = null },
            onSave = { updatedMock ->
                viewModel.updateMock(updatedMock)
                editingMock = null
                Toast.makeText(context, "Mock updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    if (mockToDelete != null) {
        val m = mockToDelete!!
        AlertDialog(
            onDismissRequest = { mockToDelete = null },
            title = { Text("Delete Mock?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${m.title}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMock(m)
                        mockToDelete = null
                        Toast.makeText(context, "Mock deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExamFailRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { mockToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Factory Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Factory Mocks?", fontWeight = FontWeight.Bold) },
            text = { Text("This will restore all default 40+ mocks for Easy, Moderate, Hard, NTPC, CGL, CHSL, and DSSSB.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToFactoryMocks()
                        showResetDialog = false
                        Toast.makeText(context, "Default mocks restored!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------- TAB 0: UPLOAD & ADD MOCK VIEW ----------------
@Composable
fun AdminUploadMockView(
    onSaveMock: (TypingMockEntity) -> Unit
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var level by remember { mutableIntStateOf(2) }
    var examTag by remember { mutableStateOf("ntpc") }
    var durationMins by remember { mutableStateOf("10") }
    var targetKds by remember { mutableStateOf("1500") }
    var cutoffUr by remember { mutableStateOf("5.0") }
    var cutoffEws by remember { mutableStateOf("5.0") }
    var cutoffObc by remember { mutableStateOf("5.0") }
    var cutoffSc by remember { mutableStateOf("5.0") }
    var cutoffSt by remember { mutableStateOf("5.0") }
    var cutoffPwd by remember { mutableStateOf("5.0") }
    var passageText by remember { mutableStateOf("") }

    val wordsCount = passageText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    val charsCount = passageText.length

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📤 UPLOAD / ADD NEW TYPING MOCK",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                )
                Text(
                    text = "Configure exam time, candidate cutoffs, word count, and paste or upload passage story below:",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                // Title
                AdminField(
                    label = "Mock Title / Name",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "e.g. RRB NTPC: Rail Freight & Logistics 2026"
                )

                // Difficulty Level
                Text("Select Difficulty Level:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1 to "1: Easy", 2 to "2: Moderate", 3 to "3: Hard", 4 to "4: Advanced").forEach { (lvl, lbl) ->
                        val isSel = level == lvl
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFFFBBF24) else Color(0xFF334155))
                                .clickable { level = lvl }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lbl,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color(0xFF0F172A) else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                // Target Exam
                AdminField(
                    label = "Target Exam Tag (ntpc / cgl / chsl / dsssb / kvs_nvs / all)",
                    value = examTag,
                    onValueChange = { examTag = it },
                    placeholder = "ntpc"
                )

                // Duration & Target Keystrokes
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminField(
                        label = "Duration (Minutes)",
                        value = durationMins,
                        onValueChange = { durationMins = it },
                        placeholder = "10",
                        modifier = Modifier.weight(1f)
                    )
                    AdminField(
                        label = "Target Key Depressions",
                        value = targetKds,
                        onValueChange = { targetKds = it },
                        placeholder = "1500",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Category Cutoff Percentages
                Text("Official Allowed Error Cutoffs (%):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminField("UR %", cutoffUr, { cutoffUr = it }, "5.0", Modifier.weight(1f))
                    AdminField("EWS %", cutoffEws, { cutoffEws = it }, "5.0", Modifier.weight(1f))
                    AdminField("OBC %", cutoffObc, { cutoffObc = it }, "5.0", Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminField("SC %", cutoffSc, { cutoffSc = it }, "5.0", Modifier.weight(1f))
                    AdminField("ST %", cutoffSt, { cutoffSt = it }, "5.0", Modifier.weight(1f))
                    AdminField("PwD %", cutoffPwd, { cutoffPwd = it }, "5.0", Modifier.weight(1f))
                }

                // Passage Text Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Passage Story Text:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("$wordsCount words · $charsCount chars", fontSize = 11.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = passageText,
                    onValueChange = { passageText = it },
                    placeholder = { Text("Paste passage story or upload text here...", color = Color(0xFF64748B)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFFFBBF24),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Save Mock Button
                Button(
                    onClick = {
                        if (title.isBlank() || passageText.isBlank()) return@Button
                        val newMock = TypingMockEntity(
                            id = "mock_${System.currentTimeMillis()}",
                            title = title.trim(),
                            level = level,
                            examTag = examTag.trim().lowercase(),
                            durationMinutes = durationMins.toIntOrNull() ?: 10,
                            targetKeystrokes = targetKds.toIntOrNull() ?: 1500,
                            wordCount = wordsCount,
                            text = passageText.trim().replace("[\r\n\t]+".toRegex(), " "),
                            cutoffGeneral = cutoffUr.toDoubleOrNull() ?: 5.0,
                            cutoffEws = cutoffEws.toDoubleOrNull() ?: 5.0,
                            cutoffObc = cutoffObc.toDoubleOrNull() ?: 5.0,
                            cutoffSc = cutoffSc.toDoubleOrNull() ?: 5.0,
                            cutoffSt = cutoffSt.toDoubleOrNull() ?: 5.0,
                            cutoffPwd = cutoffPwd.toDoubleOrNull() ?: 5.0,
                            minSpeedWpm = if (examTag == "ntpc") 30 else 27,
                            isCustom = true
                        )
                        onSaveMock(newMock)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = "Publish", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Publish Mock to App", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(3.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF64748B), fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedBorderColor = Color(0xFFFBBF24),
                unfocusedBorderColor = Color(0xFF475569),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

// ---------------- TAB 1: REPOSITORY VIEW ----------------
@Composable
fun AdminRepositoryView(
    allMocks: List<TypingMockEntity>,
    onEdit: (TypingMockEntity) -> Unit,
    onDelete: (TypingMockEntity) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = allMocks.filter {
        search.isBlank() || it.title.contains(search, ignoreCase = true) || it.examTag.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Filter mocks in database...", color = Color(0xFF64748B), fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedBorderColor = Color(0xFFFBBF24),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered, key = { it.id }) { mock ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LevelBadge(level = mock.level)
                                ExamTagBadge(tag = mock.examTag)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(mock.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${mock.wordCount} words · ${mock.durationMinutes}m · UR Cutoff ≤${mock.cutoffGeneral}%", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }

                        Row {
                            IconButton(onClick = { onEdit(mock) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(mock) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExamFailRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 2: TOOLS & JSON ----------------
@Composable
fun AdminToolsView(
    allMocks: List<TypingMockEntity>,
    onResetDefaults: () -> Unit,
    onExportJson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🛠 DATABASE TOOLS & BATCH EXPORT", color = Color(0xFFFBBF24), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Export the current 40+ mock repository to JSON or restore default factory data:", color = Color(0xFF94A3B8), fontSize = 11.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onExportJson,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Export", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export All Mocks as JSON", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onResetDefaults,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to 40+ Factory Default Mocks", color = Color(0xFFF87171), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MockEditorDialog(
    initialMock: TypingMockEntity?,
    onDismiss: () -> Unit,
    onSave: (TypingMockEntity) -> Unit
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf(initialMock?.title ?: "") }
    var level by remember { mutableIntStateOf(initialMock?.level ?: 1) }
    var examTag by remember { mutableStateOf(initialMock?.examTag ?: "all") }
    var durationMins by remember { mutableStateOf(initialMock?.durationMinutes?.toString() ?: "15") }
    var targetKds by remember { mutableStateOf(initialMock?.targetKeystrokes?.toString() ?: "2000") }
    var cutoffUr by remember { mutableStateOf(initialMock?.cutoffGeneral?.toString() ?: "5.0") }
    var cutoffEws by remember { mutableStateOf(initialMock?.cutoffEws?.toString() ?: "7.0") }
    var cutoffObc by remember { mutableStateOf(initialMock?.cutoffObc?.toString() ?: "7.0") }
    var cutoffSc by remember { mutableStateOf(initialMock?.cutoffSc?.toString() ?: "7.0") }
    var cutoffSt by remember { mutableStateOf(initialMock?.cutoffSt?.toString() ?: "7.0") }
    var cutoffPwd by remember { mutableStateOf(initialMock?.cutoffPwd?.toString() ?: "10.0") }
    var passageText by remember { mutableStateOf(initialMock?.text ?: "") }

    val wordsCount = passageText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Mock Passage", fontWeight = FontWeight.Bold, color = Color.White) },
        containerColor = Color(0xFF1E293B),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminField("Title", title, { title = it }, "Title")
                AdminField("Exam Tag", examTag, { examTag = it }, "ntpc")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminField("Duration (mins)", durationMins, { durationMins = it }, "10", Modifier.weight(1f))
                    AdminField("Target KDs", targetKds, { targetKds = it }, "1500", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AdminField("UR %", cutoffUr, { cutoffUr = it }, "5.0", Modifier.weight(1f))
                    AdminField("OBC %", cutoffObc, { cutoffObc = it }, "7.0", Modifier.weight(1f))
                    AdminField("SC/ST %", cutoffSc, { cutoffSc = it }, "7.0", Modifier.weight(1f))
                }
                Text("Passage Text ($wordsCount words):", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = passageText,
                    onValueChange = { passageText = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFFFBBF24),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || passageText.isBlank()) return@Button
                    val updated = (initialMock ?: TypingMockEntity(
                        id = "mock_${System.currentTimeMillis()}",
                        title = title.trim(),
                        level = level,
                        examTag = examTag.trim().lowercase(),
                        durationMinutes = durationMins.toIntOrNull() ?: 10,
                        targetKeystrokes = targetKds.toIntOrNull() ?: 1500,
                        wordCount = wordsCount,
                        text = passageText.trim().replace("[\r\n\t]+".toRegex(), " "),
                        cutoffGeneral = cutoffUr.toDoubleOrNull() ?: 5.0,
                        cutoffEws = cutoffEws.toDoubleOrNull() ?: 5.0,
                        cutoffObc = cutoffObc.toDoubleOrNull() ?: 5.0,
                        cutoffSc = cutoffSc.toDoubleOrNull() ?: 5.0,
                        cutoffSt = cutoffSt.toDoubleOrNull() ?: 5.0,
                        cutoffPwd = cutoffPwd.toDoubleOrNull() ?: 5.0,
                        minSpeedWpm = 30,
                        isCustom = true
                    )).copy(
                        title = title.trim(),
                        level = level,
                        examTag = examTag.trim().lowercase(),
                        durationMinutes = durationMins.toIntOrNull() ?: 10,
                        targetKeystrokes = targetKds.toIntOrNull() ?: 1500,
                        wordCount = wordsCount,
                        text = passageText.trim().replace("[\r\n\t]+".toRegex(), " "),
                        cutoffGeneral = cutoffUr.toDoubleOrNull() ?: 5.0,
                        cutoffEws = cutoffEws.toDoubleOrNull() ?: 5.0,
                        cutoffObc = cutoffObc.toDoubleOrNull() ?: 5.0,
                        cutoffSc = cutoffSc.toDoubleOrNull() ?: 5.0,
                        cutoffSt = cutoffSt.toDoubleOrNull() ?: 5.0,
                        cutoffPwd = cutoffPwd.toDoubleOrNull() ?: 5.0
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
            ) {
                Text("Save Changes", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
