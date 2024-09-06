package me.lokmvne.app3.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.lokmvne.app3.R
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationDi {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GSignInNotificationBuilder

    @Provides
    @Singleton
    @GSignInNotificationBuilder
    fun provideGSignInNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "signin")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SignIn")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
    }


    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat {
        val notificationManager = NotificationManagerCompat.from(context)
        val channel1 = NotificationChannel(
            "signin",
            "signin",
            NotificationManager.IMPORTANCE_HIGH
        )
        val channel2 = NotificationChannel(
            "Download",
            "Download",
            NotificationManager.IMPORTANCE_LOW
        )

        val channel3 = NotificationChannel(
            "Reply",
            "Reply",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel1)
        notificationManager.createNotificationChannel(channel2)
        notificationManager.createNotificationChannel(channel3)

        return notificationManager
    }
}