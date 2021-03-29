package vn.icheck.android.component.product.bottominfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_product_bottom_info.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.component.BottomModel
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SettingManager

class BottomInfoHolder(parent: ViewGroup,val listener: ProductDetailListener) : BaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_bottom_info, parent, false)) {
    private val adapter = ContactSettingAdapter(listener)
    private val productContact = SettingManager.productContact

    fun bind(bottomModel: BottomModel) {
        itemView.rcvContacSetting.layoutManager = LinearLayoutManager(itemView.context)
        itemView.rcvContacSetting.adapter = adapter
        adapter.setListData(bottomModel.list)

        itemView.tvAddress.text = productContact.find { it.key == "product-contact.address" }?.value ?: itemView.context.getString(R.string.dia_chi_icheck)
        itemView.tvPhone.text = itemView.context.getString(R.string.tong_dai_icheck, productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")
        itemView.tvEmail.text = itemView.context.getString(R.string.email_icheck, productContact.find { it.key == "product-contact.mail" }?.value ?: "cskh@icheck.vn")
        itemView.tvMst.text = itemView.context.getString(R.string.ma_so_thue_icheck, productContact.find { it.key == "product-contact.tax-code" }?.value ?: "0106875900")
        itemView.tvHotline.text = itemView.context.getString(R.string.ma_so_thue_icheck, productContact.find { it.key == "product-contact.hot-line" }?.value ?: "0902195488")

        itemView.tvPhone.setOnClickListener {
            Constant.callPhone(productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")
        }
        itemView.tvEmail.setOnClickListener {
            Constant.sendEmail(productContact.find { it.key == "product-contact.mail" }?.value ?: "cskh@icheck.vn")
        }
        itemView.tvHotline.setOnClickListener {
            Constant.callPhone(productContact.find { it.key == "product-contact.phone" }?.value ?: "0902195488")
        }
    }
}