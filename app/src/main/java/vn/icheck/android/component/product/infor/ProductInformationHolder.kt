package vn.icheck.android.component.product.infor

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_information.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemProductInformationBinding
import vn.icheck.android.network.models.ICInfo
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICProductInformations
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.MoreInformationProductActivity
import vn.icheck.android.screen.user.information_product.InformationProductActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductInformationHolder(parent: ViewGroup, val binding: ItemProductInformationBinding =
        ItemProductInformationBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(obj: ICProductInformations) {
        binding.tvTitle.text = obj.title

        if (!obj.shortContent.isNullOrEmpty()) {
            binding.tvContent.beVisible()
            binding.tvContent.text = Html.fromHtml(obj.shortContent)
        } else {
            binding.tvContent.beGone()
        }

        if (!obj.image.isNullOrEmpty()) {
            binding.imgImage.beVisible()
            binding.viewBackground.beVisible()

            WidgetUtils.loadImageUrl(binding.imgImage, obj.image)
        } else {
            binding.imgImage.beGone()
            binding.viewBackground.beGone()
        }

        binding.imgImage.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                val listImage = mutableListOf<ICMedia>()
                listImage.add(ICMedia(content = obj.image))
                DetailMediaActivity.start(it, listImage)
            }
        }

        binding.tvViewAll.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                val intent = Intent(activity, InformationProductActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.code)
                intent.putExtra(Constant.DATA_2, obj.productID)
                intent.putExtra(Constant.DATA_3, obj.productImage)
                ActivityUtils.startActivity(activity, intent)
            }
        }
    }

    fun bind(obj: ICInfo) {
        binding.tvTitle.text = obj.title

        if (!obj.content.isNullOrEmpty()) {
            binding.tvContent.beVisible()
            binding.tvContent.text = Html.fromHtml(obj.content)
        } else {
            binding.tvContent.beGone()
        }

        binding.imgImage.beGone()
        binding.viewBackground.beGone()

        binding.tvViewAll.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                val intent = Intent(activity, MoreInformationProductActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.id)
                ActivityUtils.startActivity(activity, intent)
            }
        }
    }
}