package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_qr_product_history_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class QrProductHistoryHolder(parent: ViewGroup, val listener: IScanHistoryView) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_qr_product_history_holder, parent, false)) {
    fun bind(item: ICItemHistory) {
        if (item.actionData != null) {
            if (!item.actionData?.imageUrl.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgAvaProduct, item.actionData?.imageUrl, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size8)
            } else {
                WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgAvaProduct, item.actionData?.imageUrl, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product, SizeHelper.size8)
            }

            if (!item.actionData?.productName.isNullOrEmpty()) {
                itemView.tvNameProduct.beVisible()
                itemView.tvTenSpUpdating.beInvisible()
                itemView.tvNameProduct.text = item.actionData?.productName
            } else {
                itemView.tvTenSpUpdating.beVisible()
                itemView.tvNameProduct.beInvisible()
            }

            itemView.tvQrcode.text = item.actionData?.qrBarcode ?: itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.onDelayClick({
            if (!item.actionData?.qrBarcode.isNullOrEmpty()) {
//                if (item.actionData?.qrBarcode?.startsWith("https://qcheck.vn", false) == true) {
//                    listener.onValidStamp(item.actionData?.qrBarcode)
//                } else if (item.actionData?.qrBarcode?.startsWith("https://qcheck-dev.vn", false) == true) {
//                    listener.onValidStamp(item.actionData?.qrBarcode)
//                } else if (item.actionData?.qrBarcode?.startsWith("http://qcheck.vn", false) == true) {
//                    listener.onValidStamp(item.actionData?.qrBarcode)
//                } else if (item.actionData?.qrBarcode?.startsWith("http://qcheck-dev.vn", false) == true) {
//                    listener.onValidStamp(item.actionData?.qrBarcode)
//                } else {
                listener.onValidStamp(item.actionData?.qrBarcode)
//                }
            }
        },1500)

    }
}