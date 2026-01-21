package com.example.cginvoice.presentaion.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.item.ItemRepository
import com.example.cginvoice.domain.model.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaved = MutableSharedFlow<Boolean>()
    val isSaved = _isSaved.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _currentItemData = MutableStateFlow<ItemData>(ItemData())
    val currentItemData = _currentItemData.asStateFlow()


    fun updateField(field: (ItemData) -> ItemData) {
        _currentItemData.value = field(_currentItemData.value)
    }

    fun updateItemDataDB() {
        viewModelScope.launch {
            setLoading(true)
            val updatedItem = _currentItemData.first()
            updatedItem.let {
                val response = itemRepository.insertOrUpdateItemInfoDB(it)
                when (response) {
                    is DBResource.Error -> {
                        setLoading(false)
                        _errorMessage.emit(response.exception.message.toString())
                    }

                    DBResource.Loading -> TODO()
                    is DBResource.Success -> {
                        setLoading(false)

                        _isSaved.emit(true)
                    }
                }
            }
        }
    }

    fun addItemToCurrentState(itemData: ItemData) {
        _currentItemData.value = itemData.copy(itemObjectId = null)
    }


    fun loadItem(itemId: Int) {
        if (itemId != -1) {
            getItemByItemId(itemId)
        }
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun getItemByItemId(itemId: Int) {
        viewModelScope.launch {
            setLoading(true)
            val response = itemRepository.getItemInfo(itemId)
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
                    setCurrentItemData(response.value)

                }
            }

        }
    }


    fun setCurrentItemData(currentItemData: ItemData) {
        _currentItemData.value = currentItemData
    }


}