package vn.icheck.android.component.product.chat

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICBarcodeProductV2

class ChatProductModel(val icBarcodeProductV2: ICBarcodeProductV2) : ICViewModel {
    override fun getTag(): String {
        return ""
    }

    override fun getViewType(): Int {
        return ICViewTypes.TYPE_CHAT_PAGE
    }
}