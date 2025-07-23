package com.example.cginvoice.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cginvoice.data.source.local.dao.client.ClientDao
import com.example.cginvoice.data.source.local.dao.common.CommonDao
import com.example.cginvoice.data.source.local.dao.invoice.InvoiceDao
import com.example.cginvoice.data.source.local.dao.user.UserDao
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity

@Database(
    entities = [AddressEntity::class, ContactEntity::class, UserEntity::class, InvoiceEntity::class, ClientEntity::class],
    version = 1,
    exportSchema = false
)

abstract class CGInvoiceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun commonDao(): CommonDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun clientDao(): ClientDao
}