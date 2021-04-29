package vn.icheck.android.component.product.infor

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_product_information.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICProductInformations
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.information_product.InformationProductActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductInformationHolder(parent: ViewGroup) : BaseViewHolder<ICProductInformations>(LayoutInflater.from(parent.context).inflate(R.layout.item_product_information, parent, false)) {

    override fun bind(obj: ICProductInformations) {
        itemView.tvTitle.text = obj.title

        if (!obj.shortContent.isNullOrEmpty()) {
            itemView.tvContent.beVisible()
            itemView.tvContent.text = obj.shortContent
        } else {
            itemView.tvContent.beGone()
        }

        if (!obj.image.isNullOrEmpty()) {
            itemView.imgImage.beVisible()
            itemView.viewBackground.beVisible()

            WidgetUtils.loadImageUrl(itemView.imgImage, obj.image)
        } else {
            itemView.imgImage.beGone()
            itemView.viewBackground.beGone()
        }

        itemView.imgImage.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                val listImage = mutableListOf<ICMedia>()
                listImage.add(ICMedia(content = obj.image))
                DetailMediaActivity.start(it, listImage)
            }
        }

        itemView.tvViewAll.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                val intent = Intent(activity, InformationProductActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.code)
                intent.putExtra(Constant.DATA_2, obj.productID)
                intent.putExtra(Constant.DATA_3, obj.productImage)
                ActivityUtils.startActivity(activity, intent)
            }
        }
    }
}