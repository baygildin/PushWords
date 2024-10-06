package com.sbaygildin.pushwords.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.prefs.Preferences
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object SettingsModule {
//    @Provides
//    @Singleton
//    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
//        return context.createDataStore(name = "settings")
//    }
//}