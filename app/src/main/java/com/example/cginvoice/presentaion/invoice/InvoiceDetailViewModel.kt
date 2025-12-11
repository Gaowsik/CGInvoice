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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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


    private val _currentInvoice = MutableStateFlow<Invoice?>(Invoice())
    val currentInvoice = _currentInvoice.asStateFlow()

    private val _isSaved = MutableSharedFlow<Boolean>()
    val isSaved = _isSaved.asSharedFlow()

    fun updateInvoiceField(transform: (Invoice) -> Invoice) {
        _currentInvoice.update { invoice ->
            invoice?.let { transform(it) }
        }
    }


    val invoiceDetailState: StateFlow<InvoiceDetailState> =
        _currentInvoice.map { invoice ->
            invoice?.let { toInvoiceState(it) } ?: InvoiceDetailState()
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
        val itemName: String = "",
        val quantity: Int = 0,
        val unitPrice: Double = 0.0,
        val totalAmount: Double = 0.0,
    )

    data class InvoicePaymentState(
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
                    paymentDate = it.paymentDate
                )
            }
        )
    }

    fun setCurrentInvoice(invoice: Invoice) {
        _currentInvoice.value = invoice
    }


    fun addInvoiceItemToCurrentState(item: InvoiceItemData) {
        _currentInvoice.update { invoice ->
            invoice?.copy(
                invoiceItemList = invoice.invoiceItemList + item
            )
        }
    }

    fun addPaymentCurrentState(payment: Payment) {
        _currentInvoice.update { invoice ->
            invoice?.copy(
                paymentList = invoice.paymentList + payment
            )
        }
    }


    fun updateInvoiceData() {
        viewModelScope.launch {
            setLoading(true)

            val invoice = _currentInvoice.value
            if (invoice == null) {
                setLoading(false)
                return@launch
            }

            when (val response = invoiceRepository.insertOrUpdateInvoiceDB(invoice)) {
                is DBResource.Error -> {
                    _errorMessage.emit(response.exception.message ?: "Unknown error")
                }

                is DBResource.Success -> {
                    _isSaved.emit(true)
                }

                is DBResource.Loading -> {
                }
            }

            setLoading(false)
        }
    }


}