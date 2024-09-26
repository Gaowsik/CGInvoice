package com.example.cginvoice.domain.model.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.domain.model.invoice.Invoice

data class ClientWithInvoices(
    val client: Client,
    val invoiceList: List<Invoice>
)