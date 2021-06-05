package vn.icheck.android.component.product.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.header_infor_product_detail.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class HeaderInforProductComponent(view: View, val listener: ProductDetailListener) : BaseHolder(view) {
    private var toggle: Boolean = false

    fun bind(productHeaderModel: ProductHeaderModel) {
        itemView.img_fav.apply {
            background=ViewHelper.bgTransparentStrokeLineColor0_5Corners4(itemView.img_fav.context)
            if (productHeaderModel.icBarcodeProduct.isBookMark == true) {
                setImageResource(R.drawable.ic_like_on_24dp)
            } else {
                setImageResource(R.drawable.ic_like_off_24dp)
            }
        }

        when (productHeaderModel.icBarcodeProduct.type) {
            "gs1_official_code" -> {
                itemView.img_tags.setImageResource(R.drawable.group_gs1)
                itemView.img_tags.setOnClickListener {
                    if (!toggle) {
                        itemView.img_tags.setImageResource(R.drawable.group_gs1_expanded)
                    } else {
                        itemView.img_tags.setImageResource(R.drawable.group_gs1)
                    }
                    toggle = !toggle
                }
            }
            "icheck_internal_code" -> {
                itemView.img_tags.setImageResource(R.drawable.group_icheck_blue)
                itemView.img_tags.setOnClickListener {
                    if (!toggle) {
                        itemView.img_tags.setImageResource(R.drawable.group_icheck_blue_expanded)
                    } else {
                        itemView.img_tags.setImageResource(R.drawable.group_icheck_blue)
                    }
                    toggle = !toggle
                }
            }
            "enterprise_internal_code" -> {
                itemView.img_tags.setImageResource(R.drawable.group_business)
                itemView.img_tags.setOnClickListener {
                    if (!toggle) {
                        itemView.img_tags.setImageResource(R.drawable.group_business_expanded)
                    } else {
                        itemView.img_tags.setImageResource(R.drawable.group_business)
                    }
                    toggle = !toggle
                }
            }
            "gs1_unofficial_code" -> {
                itemView.img_tags.setImageResource(R.drawable.group_not_gs1)
                itemView.img_tags.setOnClickListener {
                    if (!toggle) {
                        itemView.img_tags.setImageResource(R.drawable.group_not_gs1_expanded)
                    } else {
                        itemView.img_tags.setImageResource(R.drawable.group_not_gs1)
                    }
                    toggle = !toggle
                }
            }
            else -> {
                itemView.img_tags.visibility = View.INVISIBLE
            }
        }

        if (!productHeaderModel.icBarcodeProduct.name.isNullOrEmpty()) {
            itemView.tvNameUpdating.beGone()
            itemView.tv_product_name.beVisible()
            itemView.tv_product_name.text = productHeaderModel.icBarcodeProduct.name
        } else {
            itemView.tvNameUpdating.beVisible()
            itemView.tv_product_name.beGone()
        }

        if (productHeaderModel.icBarcodeProduct.price != null) {
            itemView.tv_price.beVisible()
            itemView.tvGiaNiemYet.beVisible()
            itemView.tvPriceUpdating.beGone()
            itemView.tv_price.text = TextHelper.formatMoneyPhay(productHeaderModel.icBarcodeProduct.price) + "đ"
            itemView.tv_price.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(itemView.context))
        } else {
            itemView.tvPriceUpdating.beVisible()
            itemView.tv_price.beGone()
            itemView.tvGiaNiemYet.beGone()
        }

        productHeaderModel.icBarcodeProduct.let {
            try {
                itemView.tv_nation.text = it.country?.name
                Glide.with(itemView.context.applicationContext).load(ImageHelper.getNation(it.country?.code!!)).into(itemView.img_flag)
                itemView.img_flag.visibility = View.VISIBLE
                itemView.tv_nation.visibility = View.VISIBLE
            } catch (e: Exception) {
                itemView.img_flag.visibility = View.GONE
                itemView.tv_nation.visibility = View.GONE
            }
        }

        if (!productHeaderModel.icBarcodeProduct.barcode.isNullOrEmpty()) {
            itemView.tvBarcode.text = productHeaderModel.icBarcodeProduct.barcode
        } else {
            itemView.tvBarcode.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.img_fav.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ADD_OR_REMOVE_BOOKMARK))
        }

        itemView.img_share.setOnClickListener {
            if (productHeaderModel.dataProductDetail?.id != null) {
                listener.shareProduct(productHeaderModel.dataProductDetail?.id!!)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, listener: ProductDetailListener): HeaderInforProductComponent {
            return HeaderInforProductComponent(LayoutInflater.from(parent.context).inflate(R.layout.header_infor_product_detail, parent, false), listener)
        }
    }
}