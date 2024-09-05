package com.example.cginvoice.domain.model.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.invoice.Invoice

data class UserWithInvoices(
    val user: User,
    val invoiceList: List<Invoice>
)