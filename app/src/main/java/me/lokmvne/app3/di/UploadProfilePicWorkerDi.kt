package me.lokmvne.app3.di

import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.lokmvne.app3.UploadProfilePictureWorker
import java.time.Duration
import javax.inject.Qualifier
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object UploadProfilePicWorkerDi {
//
//    @Qualifier
//    @Retention(AnnotationRetention.BINARY)
//    annotation class UploadProfilePic
//
//    @Provides
//    @Singleton
//    @UploadProfilePic
//    fun provideUploadProfilePicWorkRequest(): WorkRequest {
//        return OneTimeWorkRequestBuilder<UploadProfilePictureWorker>()
//            .setBackoffCriteria(
//                backoffPolicy = BackoffPolicy.LINEAR,
//                duration = Duration.ofSeconds(30)
//            )
//            .build()
//    }
//}