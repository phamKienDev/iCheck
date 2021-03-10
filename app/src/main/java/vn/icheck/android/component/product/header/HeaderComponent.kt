package vn.icheck.android.component.product.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.product_header_new.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.ImageSliderAdapter
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.util.text.ICheckTextUtils
import vn.icheck.android.util.text.ReviewPointText
import java.util.*

class HeaderComponent(view: View) : BaseHolder(view) {

    fun bind(productHeaderModel: ProductHeaderModel) {
        val listImg = mutableListOf<ImageSliderAdapter.ImageChild>()
        for (item in productHeaderModel.icBarcodeProductV2.attachments) {
            listImg.add(ImageSliderAdapter.ImageChild(item.thumbnails.original, ImageSliderAdapter.TYPE_HEADER))
        }
        val imgAdapter = ImageSliderAdapter(listImg)
        view.images_rcv.adapter = imgAdapter
        view.tv_product_name.text = productHeaderModel.icBarcodeProductV2.name
        ICheckTextUtils.setPrice(view.tv_price, productHeaderModel.icBarcodeProductV2.price)
        productHeaderModel.icCriteria?.productEvaluation?.averagePoint?.let {
            view.product_rate.rating = it
            view.product_rate.visibility = View.VISIBLE
            view.tv_score.visibility = View.VISIBLE
            view.tv_score.text = String.format("%.1f %s", it * 2, ReviewPointText.getText(it))
        } ?: kotlin.run {
            view.product_rate.visibility = View.GONE
            view.tv_score.visibility = View.GONE
        }
        productHeaderModel.icCriteria?.percentSuggest?.let {
            view.tv_suggest.text = String.format("%.0f%% Sẽ giới thiệu cho bạn bè", productHeaderModel.icCriteria?.percentSuggest)
        }
        productHeaderModel.icCriteria?.totalReviews?.let {
            if (it > 0) {
                view.tv_all_reviews.visibility = View.VISIBLE
                view.tv_all_reviews.text = String.format("Xem %,d đánh giá", it)
            } else {
                view.tv_all_reviews.visibility = View.GONE
            }
        } ?: kotlin.run {
            view.tv_all_reviews.visibility = View.GONE
        }
        productHeaderModel.icBarcodeProductV2.owner?.let {
            try {
                view.tv_nation.text = it.country.name
                Glide.with(view.context.applicationContext).load(getNation(it.country.code))
                        .into(view.img_flag)
                view.img_flag.visibility = View.VISIBLE
                view.tv_nation.visibility = View.VISIBLE
            } catch (e: Exception) {
                view.img_flag.visibility = View.INVISIBLE
                view.tv_nation.visibility = View.INVISIBLE
            }
        } ?: kotlin.run {
            view.img_flag.visibility = View.INVISIBLE
            view.tv_nation.visibility = View.INVISIBLE
        }
    }

    private fun getNation(nation: String): String {
        return "http://ucontent.icheck.vn/ensign/" + nation.toUpperCase(Locale.getDefault()) + ".png"
    }

    companion object {
        fun create(parent: ViewGroup): HeaderComponent {
            return HeaderComponent(LayoutInflater.from(parent.context).inflate(R.layout.product_header_new, parent, false))
        }
    }
}