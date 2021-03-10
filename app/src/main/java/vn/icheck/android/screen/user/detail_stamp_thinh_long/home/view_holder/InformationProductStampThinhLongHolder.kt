package vn.icheck.android.screen.user.detail_stamp_thinh_long.home.view_holder

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_information_product_stamp_thinh_long.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.kotlin.GlideImageGetter

class InformationProductStampThinhLongHolder(parent: ViewGroup, val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICBarcodeProductV1.Information>(LayoutInflater.from(parent.context).inflate(R.layout.item_information_product_stamp_thinh_long, parent, false)) {

    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICBarcodeProductV1.Information) {
        itemView.tvTitle.text = obj.title

        val imageGetter = GlideImageGetter(itemView.tvContent)
        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(obj.content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(obj.content, imageGetter, null)
        }
        itemView.tvContent.text = html

        itemView.tv_more.setOnClickListener {
            headerImagelistener.showAllInformationProduct(obj.title,obj.content)
        }
    }
}