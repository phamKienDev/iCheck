package vn.icheck.android.screen.user.map_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICLocation
import vn.icheck.android.network.models.ICNewDetail
import vn.icheck.android.network.models.ICPageDetail

class MapHistoryViewModel : ViewModel() {
    private val pageInteractor = PageRepository()

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<ICPageDetail>()

    fun getMapPageHistory() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        val a = ICPageDetail()
        a.verified = true
        a.address = "Số nhà 18, chung cư 125A Nguyễn Ngọc Vũ, Trung Hoà, Q. Cầu Giấy, Hà Nội"
        a.phone = "0985 555 222 • Fax: 033 22 222"
        a.lat = 20.9791212
        a.lon = 105.8274916

        val b = ICLocation()
        b.lat = 21.0053041
        b.lon = 105.803866

        a.location = b

        val c = ICNewDetail()
        c.avatar = "https://icdn.dantri.com.vn/thumb_w/640/2020/05/11/7-bf-3-c-2-eabac-84765-a-3115-a-2-d-843774-b-9-1589180584188.jpeg"
        c.title = "Nguyễn Gia Hân"
        c.description = "Lạ Sneakers là thương hiệu chuyên cung cấp các sản phẩm giày VNNK chất lượng, độc đáo với giá thành hợp lý tại thị trường Hà Nội hiện nay. Với phương châm ĐỘC — ĐẸP — RẺ, Lạ Sneakers thường xuyên cập nhật các kiểu dáng giày mới nhất đá...Đọc tiếp"

        a.newDetail = c

        liveData.postValue(a)
//        pageInteractor.getMapPageHistory(object : ICNewApiListener<ICResponse<ICPageDetail>> {
//            override fun onSuccess(obj: ICResponse<ICPageDetail>) {
//                if (obj.data != null) {
//                    liveData.postValue(obj.data)
//                }
//            }
//
//            override fun onError(error: ICResponseCode?) {
//                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
//            }
//        })
    }
}