package vn.icheck.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object WorkerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context:Context):WorkManager{
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context):SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}