package com.example.cginvoice.di

import android.content.Context
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import com.example.cginvoice.data.CustomWorkerFactory
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.repository.user.UserRepositoryImpl
import com.example.cginvoice.data.source.local.CGInvoiceDatabase
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSource
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSourceImpl
import com.example.cginvoice.data.source.remote.user.Back4AppUserManager
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class CGInvoiceModule {
    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {
        @Singleton
        @Provides
        fun provideUserRepository(
            localUserDataSource: LocalUserDataSource,
            remoteUserDataSource: RemoteUserDataSource,
            localCommonDataSource: LocalCommonDataSource
        ): UserRepository {
            return UserRepositoryImpl(
                localUserDataSource, remoteUserDataSource, localCommonDataSource
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
            database: CGInvoiceDatabase
        ): LocalUserDataSource {
            return LocalUserDataSourceImpl(database.userDao())
        }

        @Singleton
        @Provides
        fun provideLocalCommonDataSource(
            database: CGInvoiceDatabase
        ): LocalCommonDataSource {
            return LocalCommonDataSourceImpl(database.commonDao())
        }

        @Singleton
        @Provides
        fun provideLocalInvoiceDataSource(
            database: CGInvoiceDatabase
        ): LocalInvoiceDataSource {
            return LocalInvoiceDataSourceImpl(database.invoiceDao())
        }

        @Provides
        @Singleton
        fun provideBack4AppUserManager(): Back4AppUserManager {
            return Back4AppUserManager()
        }

    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {
        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): CGInvoiceDatabase {
            return Room.databaseBuilder(
                context.applicationContext, CGInvoiceDatabase::class.java, "WMS.db"
            ).build()
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object WorkerModule {
        @Provides
        fun provideWorkManager(
            @ApplicationContext appContext: Context, workerFactory: CustomWorkerFactory
        ): WorkManager {
            val configuration =
                Configuration.Builder().setWorkerFactory(object : DelegatingWorkerFactory() {
                    init {
                        addFactory(workerFactory)
                    }
                }).build()
            WorkManager.initialize(appContext, configuration)
            return WorkManager.getInstance(appContext)
        }
    }

}