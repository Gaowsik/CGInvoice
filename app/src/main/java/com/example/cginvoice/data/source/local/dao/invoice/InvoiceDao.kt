package com.example.cginvoice.data.source.local.dao.invoice

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
import com.example.cginvoice.data.source.local.relation.invoice.InvoiceWithItemsAndPayments
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceEntity(invoiceEntity: InvoiceEntity) : Long

    @Delete
    suspend fun deleteInvoice(invoiceEntity: InvoiceEntity)

    @Transaction
    @Query("SELECT * FROM InvoiceEntity WHERE invoiceId = :invoiceId")
    suspend fun getInvoiceWithItemsAndPayments(invoiceId: Long): List<InvoiceWithItemsAndPayments>

    @Query("UPDATE InvoiceEntity SET invoiceObjectId = :newObjectId WHERE invoiceId = :invoiceId")
    suspend fun updateInvoiceObjectId(invoiceId: Int, newObjectId: String)

    @Query("SELECT * FROM InvoiceEntity")
    fun getInvoices(): Flow<List<InvoiceEntity>>

    @Query("UPDATE InvoiceEntity SET syncStatus = :syncStatus WHERE invoiceId = :invoiceId")
    suspend fun updateStatusByInvoiceId(invoiceId: Int, syncStatus: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): Long

    @Update
    suspend fun updateInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): Int

    @Query("UPDATE InvoiceItemEntity SET invoiceItemObjectId = :newObjectId WHERE invoiceItemId = :invoiceItemId")
    suspend fun updateInvoiceItemObjectId(invoiceItemId: Int, newObjectId: String)

    @Query("DELETE FROM InvoiceItemEntity")
    suspend fun deleteAllInvoiceEntity()

    @Delete
    suspend fun deleteInvoiceItem(invoiceItemEntity: InvoiceItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentEntity(paymentEntity: PaymentEntity): Long


    @Update
    suspend fun updatePaymentEntity(paymentEntity: PaymentEntity): Int

    @Query("UPDATE PaymentEntity SET paymentObjectId = :newObjectId WHERE paymentId = :paymentId")
    suspend fun updatePaymentObjectId(paymentId: Int, newObjectId: String)

    @Query("DELETE FROM PaymentEntity")
    suspend fun deleteAllPaymentEntity()

    @Delete
    suspend fun deletePaymentItem(paymentEntity: PaymentEntity)
}