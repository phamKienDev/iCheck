package vn.icheck.android.fragments.bookmark.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.bookmark.ICBookmarkProduct

class ProductBmVmd : ViewModel() {

    var icBookmarkProduct = MutableLiveData<ICBookmarkProduct>()
    var bookmark: ICBookmarkProduct? = null
    val complete = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            try {
                val respose =
                        ICNetworkClient.getSimpleApiClient()
                                .getBookmarkProductNoLatLng()
                icBookmarkProduct.postValue(respose)
                bookmark = respose
                complete.postValue(1)
            } catch (e: Exception) {
            }
        }
    }

    fun getBookmark() {
        try {
//            viewModelScope.launch {
//                val gpsTracker = GPSTracker(ICheckApplication.getInstance())
//                if (gpsTracker.canGetLocation()) {
//                    val respose =
//                            ICNetworkClient.getSimpleApiClient()
//                                    .getBookmarkProduct(gpsTracker.latitude, gpsTracker.longitude)
//                    icBookmarkProduct.postValue(respose)
//                    bookmark = respose
//                    complete.postValue(1)
//                } else {
//                    val respose =
//                            ICNetworkClient.getSimpleApiClient()
//                                    .getBookmarkProductNoLatLng()
//                    icBookmarkProduct.postValue(respose)
//                    bookmark = respose
//                    complete.postValue(1)
//                }
//            }
        } catch (e: Exception) {
        }
    }
}