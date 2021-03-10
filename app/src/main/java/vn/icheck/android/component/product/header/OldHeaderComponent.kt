package vn.icheck.android.component.product.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.yarolegovich.discretescrollview.DiscreteScrollView
import io.reactivex.Completable
import kotlinx.android.synthetic.main.ctsp_unverified.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.ImageSliderAdapter
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.util.text.ReviewPointText
import java.util.*
import java.util.concurrent.TimeUnit

class OldHeaderComponent (view: View) : BaseHolder(view){

    private var size = 0
    private lateinit var imgAdapter: ImageSliderAdapter
    private var toggle: Boolean = false

    fun bind(productHeaderModel: ProductHeaderModel){
        if (productHeaderModel.icBarcodeProductV2.price > 0) {
            view.tv_price.text = String.format("%,dđ", productHeaderModel.icBarcodeProductV2.price)
        } else {
            view.tv_price.visibility = View.INVISIBLE
        }
        if (productHeaderModel.icBarcodeProductV2.name.isNullOrEmpty()) {
            productHeaderModel.icBarcodeProductV2.disableContribution?.let {
                if (!it) {
                    view.tv_update.visibility = View.VISIBLE
                    setOnClick(R.id.tv_update, View.OnClickListener {
                        productHeaderModel.headerClickListener.contribute()
                    })
                }
            }
        }
        if (productHeaderModel.icProductReviews?.rows?.size == 0) {
            view.tv_xrv.visibility = View.GONE
        } else {
            view.tv_xrv.text = String.format("Xem %,d đánh giá", productHeaderModel.icProductReviews?.rows?.size)
            view.tv_xrv.setOnClickListener {
                productHeaderModel.headerClickListener.showAllReview(productHeaderModel)
            }
            view.tv_xrv.visibility = View.VISIBLE
        }
        productHeaderModel.icCriteria?.percentSuggest?.let {
            if (it > 0f) {
                view.tv_recommend.text = String.format("%.0f%% Sẽ giới thiệu cho bạn bè", it)
            }
        }
        productHeaderModel.icCriteria?.productEvaluation?.averagePoint?.let {
            if (it > 0f) {
                view.product_rate.visibility = View.VISIBLE
                view.tv_product_avg.visibility = View.VISIBLE
                view.product_rate.rating = it
                view.tv_product_avg.text = String.format("%.1f %s", it * 2,
                        ReviewPointText.getText(it))
            } else {
                view.product_rate.visibility = View.GONE
                view.tv_product_avg.visibility = View.GONE
            }
        }?: kotlin.run {
            view.product_rate.visibility = View.GONE
            view.tv_product_avg.visibility = View.GONE
        }
        view.img_share.setOnClickListener {
            productHeaderModel.headerClickListener.share(productHeaderModel)
        }

        if (!productHeaderModel.icBarcodeProductV2.owner?.vendorPage?.country?.name.isNullOrEmpty()) {
            view.tv_sx.text = productHeaderModel.icBarcodeProductV2.owner?.vendorPage?.country?.name
            Glide.with(view.context.applicationContext).load(getNation(productHeaderModel.icBarcodeProductV2.owner?.vendorPage?.country?.code.toString()))
                    .into(view.nation_img)
            view.tv_sx.visibility = View.VISIBLE
            view.nation_img.visibility = View.VISIBLE
        }
        else {
            view.tv_sx.visibility = View.GONE
            view.nation_img.visibility = View.GONE
        }
//            GlideUtil.loading(topContent.nation, view.nation_img)
        view.tv_product_name.text = productHeaderModel.icBarcodeProductV2.name
        productHeaderModel.icBarcodeProductV2.attachments.let {
            size = it.size
            view.slide_show_indicator.count = size

            val listImg = mutableListOf<ImageSliderAdapter.ImageChild>()
            for (item in it) {
                listImg.add(ImageSliderAdapter.ImageChild(item.thumbnails.original, ImageSliderAdapter.TYPE_HEADER))
            }
            if (listImg.isEmpty()) {
                if (!productHeaderModel.icBarcodeProductV2.verified) {
                    listImg.add(ImageSliderAdapter.ImageChild(
                            "error", ImageSliderAdapter.TYPE_NULL))
                } else {
                    listImg.add(ImageSliderAdapter.ImageChild(
                            "", ImageSliderAdapter.TYPE_HEADER))
                }
            }
            imgAdapter = ImageSliderAdapter(listImg)
            view.slide_show_indicator.count = size
            view.imgProduct.adapter = imgAdapter
            view.imgProduct.addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<ImageSliderAdapter.ImageHolder> {
                override fun onScroll(p0: Float, p1: Int, p2: Int, p3: ImageSliderAdapter.ImageHolder?, p4: ImageSliderAdapter.ImageHolder?) {
                }

                override fun onScrollEnd(p0: ImageSliderAdapter.ImageHolder, p1: Int) {
                    view.slide_show_indicator.selection = p1
                }

                override fun onScrollStart(p0: ImageSliderAdapter.ImageHolder, p1: Int) {
                }
            })

        }
        if (size > 1) {
            Completable
                    .fromAction {
                        if (view.imgProduct.childCount > 0) {
                            if (view.imgProduct.currentItem < size - 1) {
                                view.imgProduct.smoothScrollToPosition(view.imgProduct.currentItem + 1)
                            } else {
                                view.imgProduct.smoothScrollToPosition(0)
                            }
                        }
                    }
                    .delay(4, TimeUnit.SECONDS)
                    .repeat()
                    .subscribe()
        }
        when (productHeaderModel.icBarcodeProductV2.type) {
            "gs1_official_code" -> {
                view.img_ma_icheck.setImageResource(R.drawable.gs1_logo)
                view.img_ma_icheck.setOnClickListener {
                    if (!toggle) {
                        view.img_ma_icheck.setImageResource(R.drawable.gs1_detail)
                    } else {
                        view.img_ma_icheck.setImageResource(R.drawable.gs1_logo)
                    }
                    toggle = !toggle
                }
            }
            "icheck_internal_code" -> {
                view.img_ma_icheck.setImageResource(R.drawable.ma_icheck)
                view.img_ma_icheck.setOnClickListener {
                    if (!toggle) {
                        view.img_ma_icheck.setImageResource(R.drawable.icheck_detail)
                    } else {
                        view.img_ma_icheck.setImageResource(R.drawable.ma_icheck)
                    }
                    toggle = !toggle
                }
            }
            "enterprise_internal_code" -> {
                view.img_ma_icheck.setImageResource(R.drawable.mnb_logo)
                view.img_ma_icheck.setOnClickListener {
                    if (!toggle) {
                        view.img_ma_icheck.setImageResource(R.drawable.mng_detail)
                    } else {
                        view.img_ma_icheck.setImageResource(R.drawable.mnb_logo)
                    }
                    toggle = !toggle
                }
            }
            "gs1_unofficial_code" -> {
                view.img_ma_icheck.setImageResource(R.drawable.gs1_unofficial)
                view.img_ma_icheck.setOnClickListener {
                    if (!toggle) {
                        view.img_ma_icheck.setImageResource(R.drawable.gs1_unofficial_detail)
                    } else {
                        view.img_ma_icheck.setImageResource(R.drawable.gs1_unofficial)
                    }
                    toggle = !toggle
                }
            }
            else -> {
                view.img_ma_icheck.visibility = View.INVISIBLE
            }
        }
    }
    private fun getNation(nation: String): String {
        return "http://ucontent.icheck.vn/ensign/" + nation.toUpperCase(Locale.getDefault()) + ".png"
    }

    companion object{
        fun create(parent: ViewGroup):OldHeaderComponent {
            val lf = LayoutInflater.from(parent.context)
            val view = lf.inflate(R.layout.ctsp_unverified, parent, false)
            return OldHeaderComponent(view)
        }
    }
}