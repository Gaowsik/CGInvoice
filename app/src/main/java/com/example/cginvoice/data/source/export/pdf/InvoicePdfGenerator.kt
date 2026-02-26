package com.example.cginvoice.data.source.export.pdf

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.cginvoice.domain.model.invoice.Invoice


class InvoicePdfGenerator {

    fun generate(invoice: Invoice): PdfDocument {

        val pdfDocument = PdfDocument()

        val pageWidth = 595   // A4 width
        val pageHeight = 842  // A4 height

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(
            pageWidth, pageHeight, pageNumber
        ).create()

        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            textSize = 12f
        }

        val linePaint = Paint().apply {
            strokeWidth = 1f
        }

        var y = 40f

        // -------------------------
        // Header
        // -------------------------
        canvas.drawText("INVOICE", 40f, y, titlePaint)
        y += 30f

        canvas.drawText("Invoice No: ${invoice.invoiceId}", 40f, y, normalPaint)
        y += 20f

        canvas.drawText("Date: ${invoice.invoiceData}", 40f, y, normalPaint)
        y += 20f

        canvas.drawText("Due Date: ${invoice.dueDate}", 40f, y, normalPaint)
        y += 30f

        // -------------------------
        // Client Info
        // -------------------------
        canvas.drawText("Bill To:", 40f, y, titlePaint)
        y += 20f

        canvas.drawText(invoice.clientName.orEmpty(), 40f, y, normalPaint)
        y += 30f

        // -------------------------
        // Table Header
        // -------------------------
        val startX = 40f
        val endX = 555f

        canvas.drawLine(startX, y, endX, y, linePaint)
        y += 15f

        canvas.drawText("Item", 45f, y, normalPaint)
        canvas.drawText("Qty", 300f, y, normalPaint)
        canvas.drawText("Price", 350f, y, normalPaint)
        canvas.drawText("Total", 450f, y, normalPaint)

        y += 10f
        canvas.drawLine(startX, y, endX, y, linePaint)
        y += 20f

        // -------------------------
        // Items with proper pagination
        // -------------------------
        invoice.invoiceItemList.forEach { item ->

            if (y > pageHeight - 100) {
                pdfDocument.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(
                    pageWidth, pageHeight, pageNumber
                ).create()

                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }

            val itemTotal = item.quantity * item.defaultUnitPrice

            canvas.drawText(item.itemName.orEmpty(), 45f, y, normalPaint)
            canvas.drawText(item.quantity.toString(), 300f, y, normalPaint)
            canvas.drawText(item.defaultUnitPrice.toString(), 350f, y, normalPaint)
            canvas.drawText(itemTotal.toString(), 450f, y, normalPaint)

            y += 20f
        }

        y += 10f
        canvas.drawLine(startX, y, endX, y, linePaint)
        y += 25f

        // -------------------------
        // Totals
        // -------------------------
        canvas.drawText("Total Amount:", 350f, y, titlePaint)
        canvas.drawText(invoice.totalAmount.toString(), 450f, y, titlePaint)

        y += 30f

        // -------------------------
        // Note
        // -------------------------
        invoice.note?.let {
            canvas.drawText("Note:", 40f, y, titlePaint)
            y += 20f
            canvas.drawText(it, 40f, y, normalPaint)
        }

        pdfDocument.finishPage(page)

        return pdfDocument
    }
}
