package vn.icheck.android.component.product.enterprise

import vn.icheck.android.network.models.ICBusiness

interface EnterpriseCallback {
    fun onDeleteBookmark(position:Int,obj: ICBusiness)
}