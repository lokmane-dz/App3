package me.lokmvne.app3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val credentialManager = CredentialManager.create(this)
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel<MainViewModel>()
            AppTheme {
                ProfileScreen(
                    MainViewModel = mainViewModel,
                    credentialManager = credentialManager,
                    this
                )
            }
        }
    }
}

