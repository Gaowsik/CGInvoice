package com.example.cginvoice.data.repository.item

import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.local.dataSource.item.LocalItemDataSource
import com.example.cginvoice.data.source.remote.dataSource.item.RemoteItemDataSource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.item.ItemData
import com.example.cginvoice.utills.SyncStatus
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val remoteItemDataSource: RemoteItemDataSource,
    private val localItemDataSource: LocalItemDataSource,
    private val userRepository: UserRepository
) : ItemRepository {
    override suspend fun getItemList(): DBResource<List<ItemData>> {
        val response = getItemsFromDb()
        if (response is DBResource.Success) {
            return response
        } else if (response is DBResource.Error) {
            val responseRemote = getAndSaveItemListFromRemote()
            if (responseRemote is DBResource.Success) {
                return getItemsFromDb()
            }
        }
        return DBResource.Error(Exception())

    }

    suspend fun getAndSaveItemListFromRemote(): DBResource<Unit> {
        val userObjectId = userRepository.getUserObjectId()

        if (userObjectId.isNullOrBlank()) {
            return DBResource.Error(Exception("Missing user ID"))
        }
        val response = remoteItemDataSource.getItemsRemoteByUserId(userObjectId)
        if (response is APIResource.Success) {
            response.value.forEach {
                val responseInsert = insertClientInfoResponseToDB(it)
                if (responseInsert is DBResource.Error) {
                    return DBResource.Error(responseInsert.exception)
                }
            }
            return DBResource.Success(Unit)
        } else if (response is APIResource.Error) {
            return DBResource.Error(Exception(response.errorBody.toString()))
        }
        return DBResource.Error(Exception("Unknown error"))
    }

    private suspend fun getItemsFromDb() = localItemDataSource.getItems()


    override suspend fun getItemInfo(itemId: Int) = localItemDataSource.getItem(itemId)

    override suspend fun insertOrUpdateItemInfoDB(itemData: ItemData): DBResource<Unit> {
        return if (itemData.itemId != 0) {
            updateItemInfoDB(itemData)
        } else {
            insertItemDataToDB(itemData)
        }
    }

    private suspend fun insertItemDataToDB(itemData: ItemData) =
        localItemDataSource.insertItemEntity(itemData)


    private suspend fun updateItemInfoDB(itemData: ItemData) =
        localItemDataSource.updateItemEntity(itemData)

    override suspend fun deleteItemByItemId(itemId: Int) {

    }

    override suspend fun syncAllItems(items: List<ItemData>): APIResource<List<IdInfoRemoteResponse>> {
        val idInfoRemoteResponseList = emptyList<IdInfoRemoteResponse>().toMutableList()
        items.filter { it.syncStatus == SyncStatus.PENDING.status }.forEach { item ->
            val response = itemInfoSync(item)
            when (response) {
                is APIResource.Success -> {
                    Log.d("suc", "")
                    idInfoRemoteResponseList.add(response.value)
                }

                is APIResource.Error -> {
                    Log.d("suc", "")

                }

                APIResource.Loading -> {}
                is APIResource.ErrorString -> {
                    Log.d("suc", "")
                }
            }
        }
        return APIResource.Success(idInfoRemoteResponseList)
    }

    private suspend fun insertClientInfoResponseToDB(itemData: ItemData) =
        localItemDataSource.insertItemEntity(itemData)


    suspend fun itemInfoSync(itemData: ItemData): APIResource<IdInfoRemoteResponse> {

        return if (itemData.itemObjectId.isNullOrEmpty()) {
            val response = remoteItemDataSource.insertItemRemote(itemData)
            if (response is APIResource.Success) {
                updateObjectId(response.value)
            }
            return response
        } else {
            remoteItemDataSource.updateItemRemote(itemData)
        }
    }

    private suspend fun updateObjectId(it: IdInfoRemoteResponse) {
        localItemDataSource.updateItemObjectId(
            it.id,
            it.objectId
        )
        localItemDataSource.updateStatusByItemId(it.id, SyncStatus.COMPLETED.status)

    }

}

