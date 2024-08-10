package me.lokmvne.app3

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    private val credentialManager = CredentialManager.create(context)
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
        )
    }

    val initialpictureurl =
        "https://barnimages.com/wp-content/uploads/2024/06/20240627-barnimages-7-960x640.jpg"
    private val _res =
        MutableStateFlow(UserData("no data", "no data", "no data", initialpictureurl))
    val res = _res.asStateFlow()

    fun viewSignIn(context: Context) {
        viewModelScope.launch {
            val res = googleAuthUiClient.signIn(credentialManager)
            if (res != null) {
                val userData = googleAuthUiClient.handleSignIn(res)
                _res.update { userData }
            }
        }
    }
}