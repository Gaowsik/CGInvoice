package com.example.cginvoice.data.source.export.pdf

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import com.example.cginvoice.domain.model.invoice.Invoice
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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
            textSize = 24f
            isFakeBoldText = true
        }

        val subTitleColor = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            textSize = 16f
        }

        val linePaint = Paint().apply {
            strokeWidth = 1f
        }

        var y = 40f

        // -------------------------
        // Header
        // -------------------------
        val boxPadding = 8f
        val boxLeft = 0f
        val boxTop = 0f

        val boxPaint = Paint().apply {
            color = 0xFF7B1FA2.toInt() // Violet
            style = Paint.Style.FILL
        }

        val textPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt() // White
            textSize = 14f
            isFakeBoldText = true
        }

// Prepare text
        val dateText = invoice.generatedDate
        val invoiceIdText = "INVC${invoice.invoiceId}"

// Calculate box height based on two lines of text
        val lineHeight = 20f
        val boxHeight = lineHeight * 2 + boxPadding * 2
        val boxWidth = 180f // adjust width if needed

// Draw the violet rectangle
        canvas.drawRect(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight, boxPaint)

// Draw the date text
        canvas.drawText(invoiceIdText, boxLeft + boxPadding, boxTop + boxPadding + lineHeight / 1.5f, textPaint)

// Draw the invoice ID below the date
        canvas.drawText(dateText, boxLeft + boxPadding, boxTop + boxPadding + lineHeight + lineHeight / 1.5f, textPaint)

// Move y after the box for the next content
        y += boxHeight + 40f




        canvas.drawText("INVOICE", 40f, y, titlePaint)
        y += 30f

        // -------------------------
        // Client Info
        // -------------------------
        canvas.drawText("Bill To:", 40f, y, subTitleColor)
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
        canvas.drawText("Total Amount:", 300f, y, titlePaint)
        canvas.drawText(formatMoney(invoice.totalAmount), 450f, y, titlePaint)

        y += 30f

        // -------------------------
        // Payments summary
        // -------------------------
        val totalPaid = invoice.paymentList.sumOf { it.amount }
        val remaining = (invoice.totalAmount - totalPaid).coerceAtLeast(0.0)

        canvas.drawText("Paid Amount:", 300f, y, normalPaint)
        canvas.drawText(formatMoney(totalPaid), 450f, y, normalPaint)
        y += 20f

        canvas.drawText("Remaining to Pay:", 300f, y, normalPaint)
        canvas.drawText(formatMoney(remaining), 450f, y, normalPaint)
        y += 30f

        // -------------------------
        // Payments list
        // -------------------------
        canvas.drawText("Payments", 40f, y, titlePaint)
        y += 20f

        if (invoice.paymentList.isEmpty()) {
            canvas.drawText("No payments recorded.", 40f, y, normalPaint)
            y += 20f
        } else {
            // Header
            canvas.drawLine(startX, y, endX, y, linePaint)
            y += 15f

            canvas.drawText("Date", 45f, y, normalPaint)
            canvas.drawText("Method", 190f, y, normalPaint)
            canvas.drawText("Amount", 350f, y, normalPaint)
            canvas.drawText("Note", 450f, y, normalPaint)

            y += 10f
            canvas.drawLine(startX, y, endX, y, linePaint)
            y += 20f

            invoice.paymentList
                .sortedBy { it.paymentDate ?: 0L }
                .forEach { payment ->
                    if (y > pageHeight - 100) {
                        pdfDocument.finishPage(page)

                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(
                            pageWidth, pageHeight, pageNumber
                        ).create()

                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        y = 40f

                        // Re-render payments header on the new page for readability
                        canvas.drawText("Payments (cont.)", 40f, y, titlePaint)
                        y += 20f
                        canvas.drawLine(startX, y, endX, y, linePaint)
                        y += 15f
                        canvas.drawText("Date", 45f, y, normalPaint)
                        canvas.drawText("Method", 190f, y, normalPaint)
                        canvas.drawText("Amount", 350f, y, normalPaint)
                        canvas.drawText("Note", 450f, y, normalPaint)
                        y += 10f
                        canvas.drawLine(startX, y, endX, y, linePaint)
                        y += 20f
                    }

                    val dateText = payment.paymentDate?.let { formatDateForPdf(it) } ?: "-"
                    val methodText = payment.paymentMethod.orEmpty().ifBlank { "-" }
                    val amountText = formatMoney(payment.amount)
                    val noteText = payment.note.orEmpty().ifBlank { "-" }

                    canvas.drawText(dateText, 45f, y, normalPaint)
                    canvas.drawText(methodText, 190f, y, normalPaint)
                    canvas.drawText(amountText, 350f, y, normalPaint)
                    canvas.drawText(ellipsize(noteText, 18), 450f, y, normalPaint)

                    y += 20f
                }

            y += 10f
            canvas.drawLine(startX, y, endX, y, linePaint)
            y += 20f
        }

        // -------------------------
        // Note
        // -------------------------
        invoice.note.takeIf { it.isNotBlank() }?.let {
            canvas.drawText("Note:", 40f, y, titlePaint)
            y += 20f
            canvas.drawText(it, 40f, y, normalPaint)
        }

        pdfDocument.finishPage(page)

        return pdfDocument
    }

    private fun formatMoney(amount: Double): String =
        String.format(Locale.getDefault(), "%.2f", amount)

    private fun formatDateForPdf(timeInMillis: Long): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            Instant.ofEpochMilli(timeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)
        } else {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timeInMillis))
        }
    }

    private fun ellipsize(text: String, maxChars: Int): String {
        if (text.length <= maxChars) return text
        if (maxChars <= 3) return text.take(maxChars)
        return text.take(maxChars - 3) + "..."
    }
}
