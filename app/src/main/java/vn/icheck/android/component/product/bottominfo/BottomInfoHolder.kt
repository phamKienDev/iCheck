package vn.icheck.android.component.product.bottominfo

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_product_bottom_info.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.component.BottomModel
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.constant.Constant

class BottomInfoHolder(parent: ViewGroup,val listener: ProductDetailListener) : BaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_bottom_info, parent, false)) {

    init {
        itemView.tvPhone.setOnClickListener {
            Constant.callPhone("0902195488")
        }
        itemView.tvEmail.setOnClickListener {
            Constant.sendEmail("cskh@icheck.vn")
        }
        itemView.tvHotline.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${"0902195488"}"))
            itemView.context.startActivity(intent)
        }
    }

    private val adapter = ContactSettingAdapter(listener)

    fun bind(bottomModel: BottomModel) {
        itemView.rcvContacSetting.layoutManager = LinearLayoutManager(itemView.context)
        itemView.rcvContacSetting.adapter = adapter
        adapter.setListData(bottomModel.list)
    }
}