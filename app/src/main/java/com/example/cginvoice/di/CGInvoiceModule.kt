package com.example.cginvoice.di

import android.content.Context
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.cginvoice.data.CustomWorkerFactory
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.repository.client.ClientRepositoryImpl
import com.example.cginvoice.data.repository.invoice.InvoiceRepository
import com.example.cginvoice.data.repository.invoice.InvoiceRepositoryImpl
import com.example.cginvoice.data.repository.item.ItemRepository
import com.example.cginvoice.data.repository.item.ItemRepositoryImpl
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.repository.user.UserRepositoryImpl
import com.example.cginvoice.data.source.local.CGInvoiceDatabase
import com.example.cginvoice.data.source.local.dataSource.client.LocalClientDataSource
import com.example.cginvoice.data.source.local.dataSource.client.LocalClientDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSource
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.item.LocalItemDataSource
import com.example.cginvoice.data.source.local.dataSource.item.LocalItemDataSourceImpl
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSourceImpl
import com.example.cginvoice.data.source.remote.back4AppManager.client.Back4AppClientManager
import com.example.cginvoice.data.source.remote.back4AppManager.core.Back4AppImageHandler
import com.example.cginvoice.data.source.remote.back4AppManager.invoice.Back4AppInvoiceManager
import com.example.cginvoice.data.source.remote.back4AppManager.item.Back4AppItemManager
import com.example.cginvoice.data.source.remote.back4AppManager.user.Back4AppUserManager
import com.example.cginvoice.data.source.remote.dataSource.client.RemoteClientDataSource
import com.example.cginvoice.data.source.remote.dataSource.client.RemoteClientDataSourceImpl
import com.example.cginvoice.data.source.remote.dataSource.common.RemoteCommonDataSource
import com.example.cginvoice.data.source.remote.dataSource.common.RemoteCommonDataSourceImpl
import com.example.cginvoice.data.source.remote.dataSource.invoice.RemoteInvoiceDataSource
import com.example.cginvoice.data.source.remote.dataSource.invoice.RemoteInvoiceDataSourceImpl
import com.example.cginvoice.data.source.remote.dataSource.item.RemoteItemDataSource
import com.example.cginvoice.data.source.remote.dataSource.item.RemoteItemDataSourceImpl
import com.example.cginvoice.data.source.remote.dataSource.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.dataSource.user.RemoteUserDataSourceImpl
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
            localCommonDataSource: LocalCommonDataSource,
            remoteCommonDataSource: RemoteCommonDataSource
        ): UserRepository {
            return UserRepositoryImpl(
                localUserDataSource, remoteUserDataSource, localCommonDataSource,remoteCommonDataSource
            )
        }

        @Singleton
        @Provides
        fun provideClientRepository(
            remoteClientDataSource: RemoteClientDataSource,
            localClientDataSource: LocalClientDataSource,
            localCommonDataSource: LocalCommonDataSource
        ): ClientRepository {
            return ClientRepositoryImpl(
                remoteClientDataSource,
                localClientDataSource,
                localCommonDataSource
            )
        }

        @Singleton
        @Provides
        fun provideItemRepository(
            remoteItemDataSource: RemoteItemDataSource,
            localItemDataSource: LocalItemDataSource,
            userRepository: UserRepository
        ): ItemRepository {
            return ItemRepositoryImpl(
                remoteItemDataSource,
                localItemDataSource,
                userRepository
            )
        }

        @Singleton
        @Provides
        fun provideInvoiceRepository(
            remoteInvoiceDataSource: RemoteInvoiceDataSource,
            localInvoiceDataSource: LocalInvoiceDataSource,
            userRepository: UserRepository,
            clientRepository: ClientRepository
        ): InvoiceRepository {
            return InvoiceRepositoryImpl(
                remoteInvoiceDataSource,
                localInvoiceDataSource,
                userRepository,
                clientRepository
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
        fun provideRemoteClientDataSource(back4AppClientManager: Back4AppClientManager): RemoteClientDataSource {
            return RemoteClientDataSourceImpl(back4AppClientManager)
        }

        @Singleton
        @Provides
        fun provideRemoteItemDataSource(back4AppItemManager: Back4AppItemManager): RemoteItemDataSource {
            return RemoteItemDataSourceImpl(back4AppItemManager)
        }

        @Singleton
        @Provides
        fun provideRemoteInvoiceDataSource(back4AppItemManager: Back4AppInvoiceManager): RemoteInvoiceDataSource {
            return RemoteInvoiceDataSourceImpl(back4AppItemManager)
        }

        @Singleton
        @Provides
        fun provideLocalUserDataSource(
            database: CGInvoiceDatabase
        ): LocalUserDataSource {
            return LocalUserDataSourceImpl(database.userDao())
        }

        @Provides
        fun provideLocalClientDataSource(
            database: CGInvoiceDatabase
        ): LocalClientDataSource {
            return LocalClientDataSourceImpl(database.clientDao())
        }

        @Provides
        fun provideLocalItemDataSource(
            database: CGInvoiceDatabase
        ): LocalItemDataSource {
            return LocalItemDataSourceImpl(database.itemDao())
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
        fun provideRemoteCommonDataSource(
            back4AppImageHandler: Back4AppImageHandler
        ): RemoteCommonDataSource {
            return RemoteCommonDataSourceImpl(back4AppImageHandler)
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

        @Provides
        @Singleton
        fun provideBack4AppClientManager(): Back4AppClientManager {
            return Back4AppClientManager()
        }

        @Provides
        @Singleton
        fun provideBack4AppItemManager(): Back4AppItemManager {
            return Back4AppItemManager()
        }

        @Provides
        @Singleton
        fun provideBack4AppInvoiceManager(): Back4AppInvoiceManager {
            return Back4AppInvoiceManager()
        }

        @Provides
        @Singleton
        fun provideBack4AppImageHandler(): Back4AppImageHandler {
            return Back4AppImageHandler()
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

    /*   @Module
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
       }*/

    @Module
    @InstallIn(SingletonComponent::class)
    object WorkerModule {
        @Provides
        fun provideWorkManager(
            @ApplicationContext appContext: Context, workerFactory: CustomWorkerFactory
        ): WorkManager {
            val configuration =
                Configuration.Builder().setWorkerFactory(workerFactory).build()
            // WorkManager.initialize(appContext, configuration) // Remove this line
            return WorkManager.getInstance(appContext)
        }
    }

}