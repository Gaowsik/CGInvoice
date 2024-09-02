package com.example.cginvoice.di

import android.app.Application
import androidx.core.content.ContextCompat.getString
import com.example.cginvoice.R
import com.example.cginvoice.data.repository.UserRepository
import com.example.cginvoice.data.repository.UserRepositoryImpl
import com.example.cginvoice.data.source.local.LocalUserDataSource
import com.example.cginvoice.data.source.local.LocalUserDataSourceImpl
import com.example.cginvoice.data.source.remote.user.Back4AppUserManager
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSourceImpl
import com.parse.Parse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class CGInvoiceModule {
    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {
        @Singleton
        @Provides
        fun provideUserRepository(
            localUserDataSource: LocalUserDataSource, remoteUserDataSource: RemoteUserDataSource
        ): UserRepository {
            return UserRepositoryImpl(
                localUserDataSource, remoteUserDataSource
            )
        }
    }


    @Module
    @InstallIn(SingletonComponent::class)
    object DataSourceModule {

        @Singleton
        @Provides
        fun provideRemoteUserDataSource(back4AppUserManager: Back4AppUserManager): RemoteUserDataSource {
            return RemoteUserDataSourceImpl(back4AppUserManager)
        }

        @Singleton
        @Provides
        fun provideLocalUserDataSource(
        ): LocalUserDataSource {
            return LocalUserDataSourceImpl()
        }

        @Provides
        @Singleton
        fun provideBack4AppUserManager(): Back4AppUserManager {
            return Back4AppUserManager()
        }

    }

}