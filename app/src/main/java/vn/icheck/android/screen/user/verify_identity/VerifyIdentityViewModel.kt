package vn.icheck.android.screen.user.verify_identity

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.model.kyc.KycResponse
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICPostKyc
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.util.ick.logDebug
import java.io.File

class VerifyIdentityViewModel : ViewModel() {
    val supportWebData = MutableLiveData<MutableList<ICClientSetting>>()
    val onKycStatus = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent>()
    val onSuccess = MutableLiveData<Int>()
    val kycResponseLiveData = MutableLiveData<List<KycResponse>>()
    val kycResponseList = arrayListOf<KycResponse>()

    var frontImage: File? = null
    var afterImage: File? = null
    var typeCard: String? = null
    var position = -1
    var listImage = mutableListOf<String>()
    var kycStatus = 0

    fun getData(intent: Intent) {
        kycStatus = try {
            intent.getIntExtra(Constant.DATA_1, 0)
        } catch (e: Exception) {
            0
        }
        onKycStatus.postValue(kycStatus)
    }

    fun getKyc() {
        UserInteractor().getUserKyc(object : ICNewApiListener<ICResponse<ListResponse<KycResponse>>> {
            override fun onSuccess(obj: ICResponse<ListResponse<KycResponse>>) {
                kycResponseList.clear()
                kycResponseList.addAll(obj.data?.rows ?: arrayListOf())
                if (kycResponseList.isNotEmpty()) {
                    kycResponseLiveData.postValue(kycResponseList)
                }
            }

            override fun onError(error: ICResponseCode?) {
                position = -1
                listImage.clear()
                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                        if (error?.message.isNullOrEmpty()) {
                            getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        } else {
                            error!!.message
                        }))
            }
        })
    }

    fun postKyc() {
        position++
        if (position >= 2) {
            if (listImage.size >= 2) {
                val document = mutableListOf<ICPostKyc.KycDocuments>().also {
                    it.add(ICPostKyc.KycDocuments(if (typeCard == getString(R.string.can_cuoc_cong_dan)) {
                        2
                    } else {
                        1
                    }, typeCard, listImage))
                }
                val kyc = ICPostKyc(1, document)

//                UserInteractor().postKyc(kyc, object : ICNewApiListener<ICResponse<String>> {
//                    override fun onSuccess(obj: ICResponse<String>) {
//                        onSuccess.postValue(1)
//                    }
//
//                    override fun onError(error: ICResponseCode?) {
//                        position = -1
//                        listImage.clear()
//                        statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
//                                if (error?.message.isNullOrEmpty()) {
//                                    getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
//                                } else {
//                                    error!!.message
//                                }))
//                    }
//                })
                val requestBody = hashMapOf<String, Any?>()
                val documents = arrayListOf<HashMap<String, Any?>>()
                documents.add(hashMapOf(
                        "type" to if (typeCard == getString(R.string.can_cuoc_cong_dan)) {
                            2
                        } else {
                            1
                        },
                        "document" to arrayListOf(listImage.firstOrNull()),
                        "documentName" to typeCard
                ))
                documents.add(hashMapOf(
                        "type" to if (typeCard == getString(R.string.can_cuoc_cong_dan)) {
                            2
                        } else {
                            1
                        },
                        "document" to arrayListOf(listImage.get(1)),
                        "documentName" to typeCard
                ))
                requestBody["kycDocuments"] = documents
                requestBody["requestKycLevel"] = 1
                val url = APIConstants.socialHost + "social/api/users/kyc-request"
                ICNetworkClient.getNewSocialApi().createUserKyc(url, requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<ResponseBody>{
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onNext(t: ResponseBody) {
                                onSuccess.postValue(1)
                            }

                            override fun onError(e: Throwable) {
                                position = -1
                                listImage.clear()
                                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                                        getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                            }

                            override fun onComplete() {

                            }
                        })
            } else {
                position = -1
                listImage.clear()
                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        } else {
            val file = if (position == 0) {
                frontImage
            } else {
                afterImage
            }

            file?.let {
                ImageHelper.uploadMedia(it, object : ICApiListener<UploadResponse> {
                    override fun onSuccess(obj: UploadResponse) {
                        listImage.add(obj.src)
                        postKyc()
                    }

                    override fun onError(error: ICBaseResponse?) {
                        position = -1
                        listImage.clear()
                    }
                })
            }
        }
    }


    fun getSetting() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, null))
            return
        }
        statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING, null))


        SettingHelper.getSystemSetting("kyc-benefit", "kyc-support", object : ISettingListener {
            override fun onRequestError(error: String) {
                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, null))
            }

            override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                list?.let {
                    statusCode.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                    supportWebData.postValue(it)
                }
            }
        })
    }

}