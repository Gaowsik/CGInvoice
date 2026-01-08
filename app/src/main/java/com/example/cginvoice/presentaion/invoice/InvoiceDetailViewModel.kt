package com.example.cginvoice.presentaion.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.invoice.InvoiceRepository
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.utills.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _baseInvoice = MutableStateFlow<Invoice?>(Invoice())
    val baseInvoice = _baseInvoice.asStateFlow()

    private val _invoiceItems = MutableStateFlow<List<InvoiceItemData>>(emptyList())
    val invoiceItems = _invoiceItems.asStateFlow()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments = _payments.asStateFlow()


    val currentInvoice: StateFlow<Invoice?> = combine(
        _baseInvoice, _invoiceItems, _payments
    ) { base, items, payments ->
        base?.copy(
            invoiceItemList = items,
            paymentList = payments
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, _baseInvoice.value)


    private val derivedTotalAmount: StateFlow<Double> =
        _invoiceItems
            .map { items ->
                items.sumOf { it.defaultUnitPrice * it.quantity }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    private val _manualTotalAmount = MutableStateFlow<Double?>(null)
    val manualTotalAmount = _manualTotalAmount.asStateFlow()

    val totalAmount: StateFlow<Double> =
        combine(derivedTotalAmount, _manualTotalAmount) { derived, manual ->
            manual ?: derived
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )


    val paymentStatus: StateFlow<Boolean> = combine(
        _payments,
        totalAmount
    ) { payments, total ->
        val totalPaid = payments.sumOf { it.amount }
        totalPaid >= total
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly, // ensure it starts emitting immediately
            false
        )


    private val _isSaved = MutableSharedFlow<Boolean>()
    val isSaved = _isSaved.asSharedFlow()

    fun updateInvoiceField(transform: (Invoice) -> Invoice) {
        _baseInvoice.update { invoice ->
            invoice?.let { transform(it) }
        }
    }


    val invoiceDetailState: StateFlow<InvoiceDetailState> =
        currentInvoice.map { invoice ->
            invoice?.let {
                val filteredPayments = it.paymentList
                    .filter { payment ->
                        payment.syncStatus != SyncStatus.DELETE.status
                    }

                val filteredInvoiceItems = it.invoiceItemList
                    .filter { item ->
                        item.syncStatus != SyncStatus.DELETE.status
                    }

                toInvoiceState(
                    it.copy(
                        paymentList = filteredPayments,
                        invoiceItemList = filteredInvoiceItems
                    )
                )
            } ?: InvoiceDetailState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InvoiceDetailState()
        )


    fun getInvoiceByInvoiceId(invoiceId: Int) {
        viewModelScope.launch {
            setLoading(true)
            val response = invoiceRepository.getInvoiceDetailByInvoiceId(invoiceId)
            when (response) {
                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())

                }

                DBResource.Loading -> {
                    TODO()
                }

                is DBResource.Success -> {
                    setLoading(false)
                    setCurrentInvoice(response.value)
                }
            }

        }
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }


    data class InvoiceDetailState(
        val name: String = "",
        val detail: String = "",
        val clientName: String = "",
        val invoiceItemList: List<InvoiceItemState> = emptyList(),
        val total: Double = 0.0,
        val note: String = "",
        val paymentList: List<InvoicePaymentState> = emptyList()
    )

    data class InvoiceItemState(
        val invoiceItemId: Int = 0,
        val itemName: String = "",
        val quantity: Int = 0,
        val unitPrice: Double = 0.0,
        val totalAmount: Double = 0.0,
    )

    data class InvoicePaymentState(
        val invoicePaymentId: Int = 0,
        val paymentId: Int = 0,
        val amount: Double = 0.0,
        val paymentDate: String = "",
    )

    fun toInvoiceState(invoice: Invoice): InvoiceDetailState {
        return InvoiceDetailState(
            name = invoice.invoiceData,
            detail = invoice.dueDate,
            total = invoice.totalAmount,
            note = invoice.note,
            clientName = invoice.clientName,
            invoiceItemList = invoice.invoiceItemList.map {
                InvoiceItemState(
                    invoiceItemId = it.invoiceItemId,
                    itemName = it.itemName.orEmpty(),
                    quantity = it.quantity,
                    unitPrice = it.defaultUnitPrice,
                    totalAmount = it.defaultUnitPrice * it.quantity
                )
            },
            paymentList = invoice.paymentList.map {
                InvoicePaymentState(
                    paymentId = it.paymentId ?: 0,
                    amount = it.amount,
                    paymentDate = it.paymentDate.toString()
                )
            }
        )
    }

    fun setCurrentInvoice(invoice: Invoice) {
        _baseInvoice.value = invoice
        _payments.value = invoice.paymentList
        _invoiceItems.value = invoice.invoiceItemList
    }


    fun addInvoiceItemToCurrentState(item: InvoiceItemData) {
        _invoiceItems.update { invoiceItemList ->
            invoiceItemList + item
        }
    }


    fun deleteInvoiceItemFromCurrentState(itemName: String, invoiceItemId: Int) {
        if (invoiceItemId != 0) {
            updateInvoiceItemStatus(SyncStatus.DELETE.status, invoiceItemId)
        } else {
            updateInvoiceItemStatusByName(SyncStatus.DELETE.status, itemName)
        }
    }

    fun deletePaymentItemFromCurrentState(paymentDate: Long,paymentId: Int) {
        if (paymentId != 0) {
            updateInvoicePaymentStatus(SyncStatus.DELETE.status, paymentId)
        } else {
            updateInvoicePaymentStatusByDate(SyncStatus.DELETE.status, paymentDate)
        }
    }

    fun updateInvoiceItemStatus(status: String, invoiceItemId: Int) {
        _invoiceItems.update { invoiceItemList ->
            invoiceItemList.map { invoiceItem ->
                if (invoiceItem.invoiceItemId == invoiceItemId) {
                    invoiceItem.copy(syncStatus = status)
                } else {
                    invoiceItem
                }
            }
        }
    }

    fun updateInvoiceItemStatusByName(status: String, invoiceItemName: String) {
        _invoiceItems.update { invoiceItemList ->
            invoiceItemList.map { invoiceItem ->
                if (invoiceItem.itemName == invoiceItemName) {
                    invoiceItem.copy(syncStatus = status)
                } else {
                    invoiceItem
                }
            }
        }
    }

    fun updateInvoicePaymentStatus(status: String, invoicePaymentId: Int) {
        _payments.update { paymentItemList ->
            paymentItemList.map { invoicePayment ->
                if (invoicePayment.paymentId == invoicePaymentId) {
                    invoicePayment.copy(syncStatus = status)
                } else {
                    invoicePayment
                }
            }
        }
    }

    fun updateInvoicePaymentStatusByDate(status: String, invoicePaymentDate: Long) {
        _payments.update { paymentItemList ->
            paymentItemList.map { invoicePayment ->
                if (invoicePayment.paymentDate == invoicePaymentDate) {
                    invoicePayment.copy(syncStatus = status)
                } else {
                    invoicePayment
                }
            }
        }
    }


    fun upsertPayment(payment: Payment) {
        _payments.update { paymentsList ->
            paymentsList.map { existing ->
                if (existing.paymentId == payment.paymentId && payment.paymentId != null) {
                    existing.copy(
                        paymentDate = payment.paymentDate,
                        amount = payment.amount,
                        paymentMethod = payment.paymentMethod,
                        note = payment.note
                    )
                } else {
                    existing
                }
            }.let { updatedList ->
                if (payment.paymentId == null) {
                    updatedList + payment
                } else {
                    updatedList
                }
            }
        }
    }


    fun updateInvoiceData() {
        viewModelScope.launch {
            setLoading(true)

            val invoice = currentInvoice.value
            if (invoice == null) {
                setLoading(false)
                return@launch
            }

            val filteredPayments = invoice.paymentList
                .filter { payment ->
                    !(payment.syncStatus == SyncStatus.DELETE.status && payment.paymentId == 0)
                }

            val filteredInvoiceItems = invoice.invoiceItemList
                .filter { item ->
                    !(item.syncStatus == SyncStatus.DELETE.status && item.invoiceItemId == 0)
                }


            val invoiceToSave = invoice.copy(
                totalAmount = totalAmount.value,
                paymentStatus = paymentStatus.value,
                paymentList = filteredPayments,
                invoiceItemList = filteredInvoiceItems
            )

            when (val response = invoiceRepository.insertOrUpdateInvoiceDB(invoiceToSave)) {
                is DBResource.Error -> {
                    _errorMessage.emit(response.exception.message ?: "Unknown error")
                }

                is DBResource.Success -> {
                    _isSaved.emit(true)
                }

                is DBResource.Loading -> {
                    // Optional: handle loading state from repository
                }
            }

            setLoading(false)
        }
    }


    fun updateManualTotalAmount(input: String) {
        val parsed = input.toDoubleOrNull()
        _manualTotalAmount.value = parsed
    }

    fun clearManualTotalAmount() {
        _manualTotalAmount.value = null
    }

}