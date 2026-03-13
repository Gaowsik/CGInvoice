package com.example.cginvoice.presentaion.invoice

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.SyncDataWorker
import com.example.cginvoice.data.repository.invoice.InvoiceRepository
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_INVOICE_DATA
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.Constants.KEY_WORK_MANAGER_RESPONSE
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.fromJsonList
import com.example.cginvoice.utills.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _getInvoices = MutableStateFlow<List<Invoice>>(emptyList())
    val getInvoices = _getInvoices.asStateFlow()

    val paidInvoices: StateFlow<List<Invoice>> = _getInvoices
        .map { it.filter { invoice -> invoice.paymentStatus } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unpaidInvoices: StateFlow<List<Invoice>> = _getInvoices
        .map { it.filter { invoice -> !invoice.paymentStatus } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    fun getInvoices() {
        viewModelScope.launch {
            setLoading(true)
            val response = invoiceRepository.getInvoiceList()

            when (response) {
                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())
                }

                DBResource.Loading -> {

                }

                is DBResource.Success -> {
                    setLoading(false)
                    val originalList = response.value
                    _getInvoices.value = originalList
                    val userObjectId = userRepository.getUserObjectId()

                    if (userObjectId.isNullOrBlank()) {
                        _errorMessage.emit("Cannot sync: Missing user ID.")
                        return@launch
                    }

                    if (originalList.isNotEmpty()) {
                        startSyncDataWorker(originalList)
                    }

                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startSyncDataWorker(invoiceList: List<Invoice>) {

        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED) // Requires internet
                .build()
        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>().setInputData(
            workDataOf(
                KEY_SYNC_DATA_REQUEST to invoiceList.toJson(),
                KEY_SYNC_TYPE to SyncType.INVOICE.type
            )
        ).setConstraints(constraints).setInitialDelay(Duration.ofSeconds(20))
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10)).build()

        workManager.enqueueUniqueWork(
            KEY_SYNC_INVOICE_DATA, ExistingWorkPolicy.KEEP, workRequest
        )
        observeWorkStatus(workRequest.id)
    }


    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    private fun observeWorkStatus(workId: UUID) {
        workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
            workInfo?.let {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val responseData = it.outputData.getString(KEY_WORK_MANAGER_RESPONSE)
                        responseData?.let { jsonString ->
                            val responseList: List<IdInfoRemoteResponse> = jsonString.fromJsonList()
                            Log.d("SyncDataWorker", "Work succeeded: ${responseList.toString()}")

                        }
                    }

                    WorkInfo.State.FAILED -> viewModelScope.launch { _errorMessage.emit("Work failed: $errorMessage") }
                    else -> {} // Handle other states if needed
                }
            }
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            invoiceRepository.deleteInvoice(invoice)
        }
    }

}