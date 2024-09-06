package me.lokmvne.app3

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.lokmvne.app3.Utils.Companion.INITIAL_PIC_URL
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
) : ViewModel() {

    var selectedFileUri = mutableStateOf<Uri?>(null)

    fun uploadProfilePicture(context: Context, selectedFileUri: String) {
        viewModelScope.launch {
            val inputData = Data.Builder()
                .putString("selectedFileUri", selectedFileUri)
                .build()
            val uploadProfilePictureWorker: WorkRequest =
                OneTimeWorkRequestBuilder<UploadProfilePictureWorker>()
                    .setBackoffCriteria(
                        backoffPolicy = BackoffPolicy.LINEAR,
                        duration = Duration.ofSeconds(30)
                    )
                    .setInputData(inputData)
                    .build()
            WorkManager.getInstance(context).enqueue(uploadProfilePictureWorker)
        }
    }

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