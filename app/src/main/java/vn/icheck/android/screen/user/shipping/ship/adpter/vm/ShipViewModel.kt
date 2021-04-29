package vn.icheck.android.screen.user.shipping.ship.adpter.vm

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.JsonElement
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.network.model.cart.CartResponse
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.network.model.cart.PurchasedOrderResponse
import vn.icheck.android.network.model.detail_order.DetailOrderResponse
import vn.icheck.android.network.model.location.CityItem
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.campaign.DetailRewardResponse
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.logError


class ShipViewModel @ViewModelInject constructor(val ickApi: ICKApi, @Assisted savedStateHandle: SavedStateHandle) : ViewModel() {

    val currentFragmentPosLiveData = MutableLiveData<Int>()
    private val addressRequest = hashMapOf<String, Any?>()
    private var currentCity: CityItem? = null
    private var currentDistrict: CityItem? = null
    private var currentWard: CityItem? = null
    var arrayAddress = arrayListOf<ShipAddressResponse?>(null)
    var detailRewardResponse: DetailRewardResponse? = null
    val arrayCart = arrayListOf<ItemCartItem>()
    var shop = hashMapOf<String, Any?>()
    var detailOrderId = 0L

    private var updateAddress: Long? = null
    private var chosenAddress: Long? = null
    var currentPos = 1
    val rebuyLiveData = MutableLiveData<Long>()

    fun noItemSelected() = arrayCart.firstOrNull { it.isSelected } == null

    fun setChosen(id: Long) {
        chosenAddress = id
    }

    fun getChosen(): Long {
        return chosenAddress ?: -1L
    }

    fun getCurrentCity(): CityItem? {
        return currentCity
    }

    fun clearCurrentCity() {
        currentCity = null
    }

    fun clearDistrict() {
        currentDistrict = null
    }

    fun clearWard() {
        currentWard = null
    }

    fun getDistrict(): CityItem? {
        return currentDistrict
    }

    fun setLastName(name: String) {
        addressRequest.put("lastName", name)
    }

    fun setFirstName(name: String) {
        addressRequest.put("firstName", name)
    }

    fun setAddress(address: String) {
        addressRequest.put("address", address)
    }

    fun setPhone(phone: String) {
        addressRequest.put("phone", phone)
    }

    fun setUpdate(addressId: Long) {
        updateAddress = addressId
        moveToCreate()
    }

    fun getAddressUpdateId(): Long {
        return updateAddress ?: -1L
    }

    fun removeUpdate() {
        updateAddress = null
    }

    fun isUpdate() = updateAddress != null

    fun setCity(city: CityItem) {
        currentCity = city
        currentDistrict = null
        addressRequest.remove("district")
        addressRequest.remove("ward")
        val hashCity = hashMapOf<String, Any?>()
        hashCity["name"] = city.name
        hashCity["id"] = city.id
        addressRequest.put("city", hashCity)
    }

    fun setDistrict(city: CityItem) {
        currentDistrict = city
        val hashCity = hashMapOf<String, Any?>()
        hashCity["name"] = city.name
        hashCity["id"] = city.id
        addressRequest.remove("ward")
        addressRequest.put("district", hashCity)
    }

    fun setWard(city: CityItem) {
        currentWard = city
        val hashCity = hashMapOf<String, Any?>()
        hashCity["name"] = city.name
        hashCity["id"] = city.id
        addressRequest.put("ward", hashCity)
    }

    fun getShipAddress(): LiveData<ICResponse<ICListResponse<ShipAddressResponse>>> {
        return liveData {
            emit(ickApi.getAddress())
        }
    }

    fun createShipAddress(): LiveData<ICResponse<*>> {
        return liveData {
            if (updateAddress != null) {
                emit(ickApi.updateAddress(addressRequest, updateAddress!!))
            } else {
                emit(ickApi.createAddress(addressRequest))
            }
        }
    }

    fun confirmShip(): LiveData<ICResponse<*>> {
        return liveData {
            try {
                val requestBody = hashMapOf<String, Any?>()
                requestBody["id"] = detailRewardResponse?.data?.id
                val value = ickApi.confirmShipGift(requestBody)
                emit(value)
            } catch (e: Exception) {
            }
        }
    }

    fun checkout(): LiveData<ICResponse<PurchasedOrderResponse>> {
        return liveData {
            try {
                val requestBody = hashMapOf<String, Any?>()
                requestBody["type"] = "mission"
                requestBody["shippingAddressId"] = chosenAddress
                requestBody["itemId"] = detailRewardResponse?.data?.id

                val value = ickApi.checkoutOrder(requestBody)
                emit(value)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun getFee(): LiveData<ICResponse<JsonElement>> {
        return liveData {
            try {
                val value = if (detailRewardResponse == null) {
                    ickApi.getFee()
                } else {
                    ickApi.getFee("mission")
                }
                emit(value)
            } catch (e: Exception) {
            }
        }
    }

    fun getCart(): LiveData<ICResponse<List<CartResponse>>> {
        return liveData {
            try {
                val response = ickApi.getCart()
                shop["id"] = response.data?.firstOrNull()?.shop?.id
                shop["name"] = response.data?.firstOrNull()?.shop?.name
                shop["avatar"] = response.data?.firstOrNull()?.shop?.avatar
                shop["cover"] = response.data?.firstOrNull()?.shop?.cover
                emit(response)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun rebuy(order: DetailOrderResponse) {
        viewModelScope.launch {
            if (!order.orderItem.isNullOrEmpty()) {
                val arrAsync = arrayListOf<Deferred<Any?>>()
                for (item in order.orderItem ?: arrayListOf()) {
                    arrAsync.add(async {
                        val requestBody = hashMapOf<String, Any?>()
//                        val shopHash = hashMapOf<String, Any?>()
//                        shopHash["id"] = order.shop?.id
//                        shopHash["name"] = order.shop?.name
//                        shopHash["avatar"] = order.shop?.avatar
//                        shopHash["cover"] = order.shop?.cover
//                        requestBody["shop"] = shopHash
                        val itemRequest = hashMapOf<String, Any?>()
                        itemRequest["id"] = item?.productInfo?.id
                        itemRequest["originId"] = item?.productInfo?.originId
                        itemRequest["name"] = item?.productInfo?.name
                        itemRequest["imageUrl"] = item?.productInfo?.imageUrl

                        requestBody["price"] = item?.price
                        requestBody["originPrice"] = item?.price
                        requestBody["product"] = itemRequest
                        requestBody["quantity"] = item?.quantity
                        ickApi.addProductIntoCart(requestBody)
                    })
                }
                arrAsync.awaitAll()
                rebuyLiveData.postValue(order.id ?: 0L)
            }
        }
    }

    fun addItemIntoCart(position: Int): LiveData<ICResponse<*>> {
        return liveData {

            val requestBody = hashMapOf<String, Any?>()
//            requestBody["shop"] = shop

            val item = arrayCart[position]
            val itemRequest = hashMapOf<String, Any?>()
            itemRequest["id"] = item.product?.id
            itemRequest["originId"] = item.product?.originId
            itemRequest["name"] = item.product?.name
            itemRequest["imageUrl"] = item.product?.imageUrl

            requestBody["price"] = item.price
            requestBody["originPrice"] = item.price
            requestBody["product"] = itemRequest
            requestBody["quantity"] = 1
            emit(ickApi.addProductIntoCart(requestBody))
        }
    }

    fun removeItemOutOfCart(position: Int): LiveData<ICResponse<*>> {
        return liveData {

            val requestBody = hashMapOf<String, Any?>()
//            requestBody["shop"] = shop

            val item = arrayCart[position]
            val itemRequest = hashMapOf<String, Any?>()
            itemRequest["id"] = item.product?.id
            itemRequest["originId"] = item.product?.originId
            itemRequest["name"] = item.product?.name
            itemRequest["imageUrl"] = item.product?.imageUrl

            requestBody["price"] = item.price
            requestBody["originPrice"] = item.price
            requestBody["quantity"] = 1
            requestBody["product"] = itemRequest
            emit(ickApi.moveProductOutOfCart(requestBody))
        }
    }

    fun deleteItemFromCart(position: Int): LiveData<ICResponse<*>> {
        return liveData {

            val requestBody = hashMapOf<String, Any?>()
//            requestBody["shop"] = shop

            val item = arrayCart[position]
            val itemRequest = hashMapOf<String, Any?>()
            itemRequest["originId"] = item.product?.originId
            itemRequest["id"] = item.product?.id
            itemRequest["name"] = item.product?.name
            itemRequest["imageUrl"] = item.product?.imageUrl

            requestBody["price"] = item.price
            requestBody["originPrice"] = item.price
            requestBody["product"] = itemRequest
            requestBody["quantity"] = 1
            emit(ickApi.deleteItemCart(requestBody))
        }
    }

    fun purchase(note: String): LiveData<ICResponse<PurchasedOrderResponse>> {
        return liveData {
            try {
                val requestBody = hashMapOf<String, Any?>()
                requestBody["type"] = "icheck"
                requestBody["shippingAddressId"] = chosenAddress
                if (note.isNotEmpty()) {
                    requestBody["note"] = note
                }
                val filter = arrayCart.filter {
                    it.isSelected
                }
                val arrItem = arrayListOf<HashMap<String, Any?>>()

                for (item in filter) {
                    val itemRequest = hashMapOf<String, Any?>()
                    itemRequest["productId"] = item.product?.id
//                    itemRequest["name"] = item.product?.name
//                    itemRequest["imageUrl"] = item.product?.imageUrl
                    itemRequest["price"] = item.price
                    itemRequest["quantity"] = item.quantity
                    itemRequest["shopId"] = shop["id"]
                    arrItem.add(itemRequest)
                }
                requestBody["products"] = arrItem
                val value = ickApi.purchase(requestBody)
                if (value.statusCode == "200") {
                    arrayAddress.firstOrNull { address ->
                        address?.id == getChosen()
                    }?.let {
                        TrackingAllHelper.tagIcheckItemBuySuccess(arrayCart)
                        if (detailRewardResponse?.data != null) {
                            TrackingAllHelper.tagGiftDeliverySuccess(
                                    campaign_id = detailRewardResponse?.data?.campaignId,
                                    giftbox_product_name = detailRewardResponse?.data?.name
                            )
                        }
//                        TekoHelper.tagCheckoutSuccess(value.data, arrayCart, it)
                    }
                }
                emit(value)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun getDetailOrder(): LiveData<ICResponse<DetailOrderResponse>> {
        return liveData {
            emit(ickApi.getDetailOrder(detailOrderId))
        }
    }

    fun cancelOrder(): LiveData<ICResponse<*>> {
        return liveData {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["status"] = 6
            emit(ickApi.updateOrderStatus(detailOrderId, requestBody))
        }
    }

    fun moveToCreate() {
        currentFragmentPosLiveData.postValue(3)
        currentPos = 3
    }

    fun moveToChoose() {
        currentFragmentPosLiveData.postValue(2)
        currentPos = 2
    }

    fun moveToConfirm() {
        currentFragmentPosLiveData.postValue(4)
        currentPos = 4
    }

    fun moveToCart() {
        currentFragmentPosLiveData.postValue(1)
        currentPos = 1
    }
}