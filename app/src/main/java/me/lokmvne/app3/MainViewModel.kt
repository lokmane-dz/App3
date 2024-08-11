package me.lokmvne.app3

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.lokmvne.app3.Utils.Companion.INITIAL_PIC_URL
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {
    private val _res =
        MutableStateFlow(UserData("no data", "no data", "no data", INITIAL_PIC_URL))
    val res = _res.asStateFlow()

    fun viewSignIn(credentialManager: CredentialManager, context: Context) {
        viewModelScope.launch {
            val res = googleAuthUiClient.signIn(credentialManager, context)
            if (res != null) {
                val userData = googleAuthUiClient.handleSignIn(res)
                _res.update { userData }
            }
        }
    }
}