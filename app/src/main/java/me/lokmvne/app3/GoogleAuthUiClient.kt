package me.lokmvne.app3

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import me.lokmvne.app3.Utils.Companion.CLIENT_ID
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthUiClient(private val context: Context) {


    suspend fun signIn(credentialManager: CredentialManager): GetCredentialResponse? {
        return try {
            val result = credentialManager.getCredential(
                request = buildSignInRequest(),
                context = context
            )
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAGGGGGGGGGGG", "Received an invalid google id token response", e)
            if (e is CancellationException) throw e
            null
        }
    }

    fun handleSignIn(result: GetCredentialResponse): UserData {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        return UserData(
                            userId = googleIdTokenCredential.id,
                            name = googleIdTokenCredential.givenName,
                            phone = googleIdTokenCredential.phoneNumber.toString(),
                            profilePictureUrl = googleIdTokenCredential.profilePictureUri.toString()
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAG", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("TAG", "Unexpected type of credential")
                }
            }

            else -> {
                Log.e("TAG", "Unexpected type of credential")
            }
        }
        return UserData(
            userId = "error",
            name = "error",
            phone = "error",
            profilePictureUrl = "error"
        )
    }

    private fun buildSignInRequest(): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(CLIENT_ID)
            .setAutoSelectEnabled(true)
            .setNonce("nonce")
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}