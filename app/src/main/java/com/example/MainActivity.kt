package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.LivestockDetailScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.NewListingScreen
import com.example.ui.theme.DamBazarTheme
import com.example.ui.viewmodel.LivestockViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LivestockViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(toastMessage) {
                toastMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.dismissToast()
                }
            }

            DamBazarTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: LivestockViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (val screen = currentScreen) {
        is ScreenDestination.Marketplace -> {
            MarketplaceScreen(viewModel = viewModel)
        }
        is ScreenDestination.Detail -> {
            LivestockDetailScreen(livestockId = screen.livestockId, viewModel = viewModel)
        }
        is ScreenDestination.NewListing -> {
            NewListingScreen(viewModel = viewModel)
        }
        is ScreenDestination.AdminPanel -> {
            AdminScreen(viewModel = viewModel)
        }
    }
}
