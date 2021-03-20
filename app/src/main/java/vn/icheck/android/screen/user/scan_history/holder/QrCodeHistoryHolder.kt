package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_qrcode_history_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class QrCodeHistoryHolder(parent: ViewGroup, val listener: IScanHistoryView) : BaseViewHolder<ICItemHistory>(LayoutInflater.from(parent.context).inflate(R.layout.layout_qrcode_history_holder, parent, false)) {

    override fun bind(obj: ICItemHistory) {
        itemView.tvName.text = obj.actionData?.qrBarcode?.trim()

        if (obj.actionData?.qrBarcode?.startsWith("BEGIN:VEVENT", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_event)
        } else if (obj.actionData?.qrBarcode?.startsWith("WIFI", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_wifi)
        } else if (obj.actionData?.qrBarcode?.startsWith("BEGIN:VCARD", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_contact)
        } else if (obj.actionData?.qrBarcode?.startsWith("BEGIN:geo") == true || obj.actionData?.qrBarcode?.startsWith("GEO") == true || obj.actionData?.qrBarcode?.startsWith("geo") == true) {
            itemView.imgAva.setImageResource(R.drawable.history_location)
        } else if (obj.actionData?.qrBarcode?.startsWith("tel", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_phone)
        } else if (obj.actionData?.qrBarcode?.startsWith("MATMSG:TO", true) == true || obj.actionData?.qrBarcode?.startsWith("mailto:email", true) == true || obj.actionData?.qrBarcode?.startsWith("MAILTO:", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_email)
        } else if (obj.actionData?.qrBarcode?.startsWith("smsto", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_sms)
        } else if (obj.actionData?.qrBarcode?.startsWith("http", true) == true || obj.actionData?.qrBarcode?.startsWith("https", true) == true || obj.actionData?.qrBarcode?.startsWith("URL", true) == true) {
            itemView.imgAva.setImageResource(R.drawable.history_link)
        } else {
            itemView.imgAva.setImageResource(R.drawable.history_textnote)
        }

        itemView.onDelayClick({
            if (!obj.actionData?.qrBarcode.isNullOrEmpty()) {
                listener.onClickQrType(obj.actionData?.qrBarcode)
            }
        }, 1500)
    }
}