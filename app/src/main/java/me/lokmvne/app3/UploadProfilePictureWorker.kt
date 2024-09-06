package me.lokmvne.app3

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadProfilePictureWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun doWork(): Result {
        val selectedFileUri = inputData.getString("selectedFileUri")?.let { Uri.parse(it) }
            ?: return Result.failure()
        val downurl = uploadProfilePicture(applicationContext, selectedFileUri)
        val outputData = Data.Builder()
            .putString("downloadUrl", downurl)
            .build()
        return Result.success(outputData)
    }


    fun uploadProfilePicture(context: Context, selectedFileUri: Uri): String {
        var downloadUrl = ""
        try {
            val fileRef = storageRef.child("images/${selectedFileUri.lastPathSegment}")
            val uploadTask = fileRef.putFile(selectedFileUri)

            uploadTask.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                fileRef.downloadUrl.addOnSuccessListener {
                    downloadUrl = it.toString()
                }
            }
            return downloadUrl
        } catch (e: Exception) {
            Toast.makeText(context, "uknown error. Failed to upload image", Toast.LENGTH_SHORT)
                .show()
        }
        return downloadUrl
    }
}