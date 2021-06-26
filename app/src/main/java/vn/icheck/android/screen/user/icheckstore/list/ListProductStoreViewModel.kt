package vn.icheck.android.screen.user.icheckstore.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.gift_store.GiftStoreInteractor
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICProductIdInCart
import java.math.BigDecimal

class ListProductStoreViewModel : ViewModel() {
    private val listStoreInteractor = GiftStoreInteractor()

    private val repository = GiftStoreInteractor()

    var onError = MutableLiveData<ICError>()
    var onErrorEmpty = MutableLiveData<String>()

    val onSetData = MutableLiveData<MutableList<ICStoreiCheck>>()
    val onAddData = MutableLiveData<MutableList<ICStoreiCheck>>()

    var onErrorString = MutableLiveData<String>()

    var collectionID: Long = -1
    var offset = 0

    fun getListStore(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        listStoreInteractor.getListStore(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICStoreiCheck>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICStoreiCheck>>) {
                offset += APIConstants.LIMIT

                for (item in obj.data?.rows ?: mutableListOf()) {
                    if (item.id == AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getProductById(item.id)?.id) {
                        item.addToCart = true
                    }
                }

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        onErrorEmpty.postValue("")
                    } else {
                        onSetData.postValue(obj.data?.rows)
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun addToCart(data: ICStoreiCheck) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onErrorString.postValue(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.dispose()
        repository.addToCart(data.name ?: "", data.id, data.originId ?: -1, data.imageUrl ?: "", data.price
                ?: -1, object : ICNewApiListener<ICResponse<Int>> {
            override fun onSuccess(obj: ICResponse<Int>) {
                if (obj.statusCode == "200") {
//                    InsiderHelper.tagAddToCartSuccessStoreIcheck(data)
                    AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().insertProduct(ICProductIdInCart(data.id, data.price ?: 0, 1))
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_PRICE))
                } else {
                    onErrorString.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onErrorString.postValue(error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getTotalPrice(): Long {
        return if (AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().isNullOrEmpty()) {
            0
        } else {
            AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().sumByBigDecimal { BigDecimal(it.price) }.toLong()
        }
    }

    fun getCount(): Long {
        return if (AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().isNullOrEmpty()) {
            0
        } else {
            AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().getAll().sumByBigDecimal { BigDecimal(it.count) }.toLong()
        }
    }

    private fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }
}