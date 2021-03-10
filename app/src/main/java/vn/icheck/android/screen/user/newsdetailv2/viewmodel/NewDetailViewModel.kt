package vn.icheck.android.screen.user.newsdetailv2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.news.NewsInteractor
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class NewDetailViewModel : ViewModel() {
    val listener = NewsInteractor()

    var liveData = MutableLiveData<BaseModelList<ICNews>>()
    val onError = MutableLiveData<ICError>()

    fun getHeaderAlpha(totalScroll: Int, layoutHeader: Int): Float {
        return when {
            totalScroll > 0 -> {
                if (totalScroll <= layoutHeader) {
                    (1f / layoutHeader) * totalScroll
                } else {
                    1f
                }
            }
            totalScroll < 0 -> {
                if (totalScroll < -layoutHeader) {
                    (-1f / layoutHeader) * totalScroll
                } else {
                    0f
                }
            }
            else -> {
                0f
            }
        }
    }

    fun getNewsDetail(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        viewModelScope.launch {
            listener.getNewsDetail(id, object : ICNewApiListener<ICResponse<ICNews>> {
                override fun onSuccess(obj: ICResponse<ICNews>) {
                    if (obj.data != null) {

                        listener.getListNewsV2Social(object : ICNewApiListener<ICResponse<ICListResponse<ICNews>>> {

                            override fun onSuccess(list: ICResponse<ICListResponse<ICNews>>) {
                                if (!list.data?.rows.isNullOrEmpty()) {
                                    for (i in list.data?.rows!!.size - 1 downTo 0) {
                                        if (obj.data!!.id == list.data?.rows!![i].id) {
                                            list.data?.rows!!.removeAt(i)
                                        }
                                    }

                                    if (list.data?.rows!!.size - 1 > 2) {
                                        list.data?.rows!!.removeAt(3)
                                    }

                                    liveData.postValue(BaseModelList(false, list.data?.rows ?: mutableListOf(), null, obj.data))
                                }
                            }

                            override fun onError(error: ICResponseCode?) {
                                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                            }
                        })
                    }

                }

                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }
}