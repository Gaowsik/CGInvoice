package com.example.cginvoice.domain.model.invoice

import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

data class InvoiceWithInvoiceItems(
        val invoice: Invoice,
        val invoiceItemList: List<InvoiceItemData>
    )
