package vn.icheck.android.screen.user.createqrcode.createlocation.presenter

import vn.icheck.android.R
import vn.icheck.android.network.feature.location.LocationInteraction
import vn.icheck.android.network.models.ICPoints
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICPointDetail
import vn.icheck.android.screen.user.createqrcode.createlocation.view.ICreateLocationQrCodeView

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateLocationQrCodePresenter(val view: ICreateLocationQrCodeView) : BaseFragmentPresenter(view) {
    private val interaction = LocationInteraction()

    private val goongKey = SettingManager.clientSetting?.goong_api_key ?: ""

    fun searchLocation(text: String) {
        if (NetworkHelper.isNotConnected(view.mContext) || text.isEmpty()) {
            interaction.dispose()
            view.onSearchLocationSuccess(mutableListOf())
            return
        }

        val key = TextHelper.unicodeToKoDau(text)
        interaction.dispose()

        interaction.searchLocation(key, 20, object : ICNewApiListener<ICResponse<ICPoints>> {
            override fun onSuccess(obj: ICResponse<ICPoints>) {
                view.onSearchLocationSuccess(obj.data?.predictions ?: mutableListOf())
            }

            override fun onError(error: ICResponseCode?) {
                view.onSearchLocationSuccess(mutableListOf())
            }
        })
    }

    fun getLocationDetail(address: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        interaction.dispose()

        interaction.getLocationDetail(address, object : ICNewApiListener<ICResponse<ICPointDetail>> {
            override fun onSuccess(obj: ICResponse<ICPointDetail>) {
                obj.data?.location?.let {location ->
                    view.onGetLocationDetailSuccess(location)
                }
            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }

    fun validData(lat: Double?, lng: Double?) {
        if (lat == null || lng == null) {
            return
        }

        val code = view.mContext!!.getString(R.string.qr_code_location_format, lat.toString(), lng.toString())
        view.onValidSuccess(code)
    }
}