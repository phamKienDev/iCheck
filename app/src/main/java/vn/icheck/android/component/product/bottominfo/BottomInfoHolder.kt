package vn.icheck.android.component.product.bottominfo

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_product_bottom_info.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.component.BottomModel
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SettingManager

class BottomInfoHolder(parent: ViewGroup,val listener: ProductDetailListener) : BaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_bottom_info, parent, false)) {
    private val adapter = ContactSettingAdapter(listener)
    private val productContact = SettingManager.productContact

    fun bind(bottomModel: BottomModel) {
        itemView.view.background=ViewHelper.bgWhiteCornersTop16(itemView.context)
        itemView.rcvContacSetting.layoutManager = LinearLayoutManager(itemView.context)
        itemView.rcvContacSetting.adapter = adapter
        adapter.setListData(bottomModel.list)

        ViewHelper.setImageColorPrimary(R.drawable.ic_list_blue_12dp,itemView.context).apply {
            itemView.tvPhone.setCompoundDrawablesWithIntrinsicBounds(this,0,0,0)
            itemView.tvEmail.setCompoundDrawablesWithIntrinsicBounds(this,0,0,0)
            itemView.tvMst.setCompoundDrawablesWithIntrinsicBounds(this,0,0,0)
        }

        itemView.tvAddress.text = productContact.find { it.key == "product-contact.address" }?.value ?: itemView.context.getString(R.string.dia_chi_icheck)
        itemView.tvMst.text = itemView.context.getString(R.string.ma_so_thue_icheck, productContact.find { it.key == "product-contact.tax-code" }?.value ?: "0106875900")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemView.tvPhone.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.tong_dai_icheck, productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")), Html.FROM_HTML_MODE_COMPACT)
            itemView.tvEmail.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.email_icheck, productContact.find { it.key == "product-contact.mail" }?.value ?: "cskh@icheck.vn")), Html.FROM_HTML_MODE_COMPACT)
            itemView.tvHotline.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.hotline_dang_ky, productContact.find { it.key == "product-contact.hot-line" }?.value ?: "0902195488")), Html.FROM_HTML_MODE_COMPACT)
        } else {
            itemView.tvPhone.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.tong_dai_icheck, productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")))
            itemView.tvEmail.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.email_icheck, productContact.find { it.key == "product-contact.mail" }?.value ?: "cskh@icheck.vn")))
            itemView.tvHotline.text = Html.fromHtml(ViewHelper.setPrimaryHtmlString(itemView.context.getString(R.string.hotline_dang_ky, productContact.find { it.key == "product-contact.hot-line" }?.value ?: "0902195488")))
        }

        itemView.tvPhone.setOnClickListener {
            Constant.callPhone(productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")
        }
        itemView.tvEmail.setOnClickListener {
            Constant.sendEmail(productContact.find { it.key == "product-contact.mail" }?.value ?: "cskh@icheck.vn")
        }
        itemView.tvHotline.setOnClickListener {
            Constant.callPhone(productContact.find { it.key == "product-contact.hot-line" }?.value ?: "0902195488")
        }
    }
}