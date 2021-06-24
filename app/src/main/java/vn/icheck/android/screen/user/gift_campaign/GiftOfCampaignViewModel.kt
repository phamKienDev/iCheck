package vn.icheck.android.screen.user.gift_campaign

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.CAMPAIGN_ID
import vn.icheck.android.constant.LOGO
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.models.campaign.ICGiftOfCampaign

class GiftOfCampaignViewModel : ViewModel() {
    private val repository = CampainsInteractor()

    val showError = MutableLiveData<String>()
    val onError = MutableLiveData<ICError>()
    val onErrorEmpty = MutableLiveData<String>()
    val onNotEmpty = MutableLiveData<String>()

    val onSuccess = MutableLiveData<MutableList<GiftOfCampaignModel>>()

    val listData = mutableListOf<GiftOfCampaignModel>()

    val listProduct = mutableListOf<ICGiftOfCampaign>()
    val listVoucher = mutableListOf<ICGiftOfCampaign>()
    val listICoin = mutableListOf<ICGiftOfCampaign>()
    val listMorale = mutableListOf<ICGiftOfCampaign>()
    val listCampaign365 = mutableListOf<ICGiftOfCampaign>()

    var requestSuccess = 0
    var requestError = 0
    var requestEmpty = 0

    private var collectionID = ""
    var banner = ""

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

    fun getDataIntent(intent: Intent?) {
        collectionID = try {
            intent?.getStringExtra(CAMPAIGN_ID) ?: ""
        } catch (e: Exception) {
            ""
        }

        banner = try {
            intent?.getStringExtra(LOGO) ?: ""
        } catch (e: Exception) {
            ""
        }

        resetData()

        if (collectionID.isNotEmpty()) {
            getData()
        } else {
            showError.postValue("")
        }
    }

    private fun getCampaignRewards(rewardType: Int) {
        repository.dispose()

        repository.getRewardCampaign(collectionID, rewardType, object : ICNewApiListener<ICResponse<ICListResponse<ICGiftOfCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICGiftOfCampaign>>) {
                requestSuccess++
                when (rewardType) {
                    1 -> {
                        if (obj.data?.rows.isNullOrEmpty()) {
                            requestEmpty++
                        } else {
                            listICoin.addAll(obj.data?.rows ?: mutableListOf())
                        }
                        getCampaignRewards(2)
                    }
                    2 -> {
                        if (obj.data?.rows.isNullOrEmpty()) {
                            requestEmpty++
                        } else {
                            listProduct.addAll(obj.data?.rows ?: mutableListOf())
                        }
                        getCampaignRewards(3)
                    }
                    3 -> {
                        if (obj.data?.rows.isNullOrEmpty()) {
                            requestEmpty++
                        } else {
                            listVoucher.addAll(obj.data?.rows ?: mutableListOf())
                        }
                        getCampaignRewards(4)
                    }
                    4 -> {
                        if (obj.data?.rows.isNullOrEmpty()) {
                            requestEmpty++
                        } else {
                            listMorale.addAll(obj.data?.rows ?: mutableListOf())
                        }
                        getCampaignRewards(5)
                    }
                    5 -> {
                        if (obj.data?.rows.isNullOrEmpty()) {
                            requestEmpty++
                        } else {
                            listCampaign365.addAll(obj.data?.rows ?: mutableListOf())
                        }
                    }
                }
                if (requestSuccess == 5) {
                    listData.clear()

                    listData.apply {
                        if (!listProduct.isNullOrEmpty()) {
                            add(GiftOfCampaignModel(ICViewTypes.PRODUCT_CAMPAIGN_TYPE, listProduct, getString(R.string.qua_hien_vat)))
                        }
                        if (!listVoucher.isNullOrEmpty()) {
                            add(GiftOfCampaignModel(ICViewTypes.VOUCHER_CAMPAIGN_TYPE, listVoucher, getString(R.string.qua_dich_vu)))
                        }
                        if (!listICoin.isNullOrEmpty()) {
                            add(GiftOfCampaignModel(ICViewTypes.ICOIN_CAMPAIGN_TYPE, listICoin, getString(R.string.qua_thuong_xu)))
                        }
                        if (!listMorale.isNullOrEmpty()) {
                            add(GiftOfCampaignModel(ICViewTypes.MORALE_CAMPAIGN_TYPE, listMorale, getString(R.string.qua_tinh_than)))
                        }
                        if (!listCampaign365.isNullOrEmpty()) {
                            add(GiftOfCampaignModel(ICViewTypes.CAMPAIGN_365_TYPE, listCampaign365, getString(R.string.ma_du_thuong)))
                        }
                    }

                    if (requestEmpty != 5) {
                        onNotEmpty.postValue("")
                        onSuccess.postValue(listData)
                    } else {
                        onErrorEmpty.postValue(getString(R.string.qua_tang_dang_cap_nhat))
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                requestError++
            }
        })
    }

    fun getData() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        getCampaignRewards(1)

        if (requestError == 5) {
            onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            return
        }
    }

    fun resetData() {
        listProduct.clear()
        listVoucher.clear()
        listICoin.clear()
        listMorale.clear()
        listCampaign365.clear()
        listData.clear()

        requestSuccess = 0
        requestError = 0
        requestEmpty = 0
        getData()
    }
}