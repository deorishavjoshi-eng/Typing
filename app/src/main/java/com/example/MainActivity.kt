package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.StartMockScreen
import com.example.ui.screens.StenoScreen
import com.example.ui.screens.TestScreen
import com.example.ui.theme.ExamFailRed
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    var showQuitTestDialog by remember { mutableStateOf(false) }

    // Back button handling
    BackHandler(enabled = currentScreen !is AppScreen.Home) {
        if (currentScreen is AppScreen.Test) {
            showQuitTestDialog = true
        } else {
            viewModel.navigateBack()
        }
    }

    // Hide bottom navigation during focused test flow
    val showBottomBar = currentScreen is AppScreen.Home ||
            currentScreen is AppScreen.Steno ||
            currentScreen is AppScreen.Learn ||
            currentScreen is AppScreen.History ||
            currentScreen is AppScreen.Admin

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Home,
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        icon = { Icon(Icons.Default.Keyboard, contentDescription = "Mocks") },
                        label = { Text("Tests", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Steno,
                        onClick = { viewModel.navigateTo(AppScreen.Steno) },
                        icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Steno") },
                        label = { Text("Steno", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Learn,
                        onClick = { viewModel.navigateTo(AppScreen.Learn) },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Rules") },
                        label = { Text("Rules", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.History,
                        onClick = { viewModel.navigateTo(AppScreen.History) },
                        icon = { Icon(Icons.Default.History, contentDescription = "Results") },
                        label = { Text("Results", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Admin,
                        onClick = { viewModel.navigateTo(AppScreen.Admin) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD97706),
                            selectedTextColor = Color(0xFFD97706),
                            indicatorColor = Color(0xFFFEF3C7)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val mod = Modifier.padding(innerPadding)
        when (currentScreen) {
            is AppScreen.Home -> HomeScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.StartMock -> StartMockScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.Test -> TestScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.Result -> ResultScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.Admin -> AdminScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.History -> HistoryScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.Learn -> LearnScreen(viewModel = viewModel, modifier = mod)
            is AppScreen.Steno -> StenoScreen(viewModel = viewModel, modifier = mod)
        }
    }

    if (showQuitTestDialog) {
        AlertDialog(
            onDismissRequest = { showQuitTestDialog = false },
            title = { Text("Quit Typing Test?", fontWeight = FontWeight.Bold) },
            text = { Text("Your ongoing typing test progress will be lost if you leave now.") },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitTestDialog = false
                        viewModel.isTestRunning.value = false
                        viewModel.soundManager.stopHallNoise()
                        viewModel.navigateTo(AppScreen.Home)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExamFailRed)
                ) {
                    Text("Quit Test")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitTestDialog = false }) {
                    Text("Stay & Continue")
                }
            }
        )
    }
}
