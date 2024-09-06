package me.lokmvne.app3

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import me.lokmvne.app3.Utils.Companion.CLIENT_ID
import me.lokmvne.app3.di.NotificationDi.GSignInNotificationBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthUiClient @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @GSignInNotificationBuilder
    private val GSignInnotificationBuilder: NotificationCompat.Builder,
) {
    private val auth = Firebase.auth

    suspend fun signIn(
        credentialManager: CredentialManager,
        context: Context
    ): GetCredentialResponse? {
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

    @SuppressLint("MissingPermission")
    suspend fun handleSignIn(result: GetCredentialResponse): UserData {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val user = auth.signInWithCredential(
                            GoogleAuthProvider.getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )
                        ).await().user


                        if (user != null) {

                            GSignInnotificationBuilder.setContentText("Welcome ${user.displayName}")
                            notificationManager.notify(1, GSignInnotificationBuilder.build())
                            val instant =
                                Instant.ofEpochMilli(user.metadata?.lastSignInTimestamp ?: 0)
                            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            return UserData(
                                userId = user.email ?: "error",
                                name = user.displayName ?: "error",
                                phone = dateTime.format(formatter),
                                profilePictureUrl = user.photoUrl?.toString() ?: "error"
                            )

                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAG", "Received an invalid google id token response", e)
                    } catch (e: Exception) {
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
            //.setAutoSelectEnabled(true)
            //.setNonce("nonce")
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}