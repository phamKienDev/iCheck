package vn.icheck.android.component.product.enterprise

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICBarcodeProductV2
import vn.icheck.android.network.models.ICBusiness

class EnterpriseModel(val typeEnterprise: String?,
                      val business: ICBusiness,
                      val productV2:ICBarcodeProductV2?,
                      val icon: Int?,
                      val enterpriseCallback: EnterpriseCallback) : ICViewModel {

    var subTitle: String? = null
    var exclusiveDistributor = false
    var moreDistributor = false
    var showIcon = true
    var clickable = true
    // Nếu doanh nghiệp sở hữu chưa xác thực thì true
    var unverfiedOwner = false
    var default = Theme("")
    var vt = ICViewTypes.ENTERPRISE_TYPE
    override fun getTag(): String {
        return ICViewTags.ENTERPRISE_COMPONENT
    }

    override fun getViewType(): Int {
        return vt
    }
}

class Theme(color:String){

}