package vn.icheck.android.screen.user.search_home.result.holder

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_review_search_result.view.*
import kotlinx.android.synthetic.main.item_review_search_result.view.imgProduct
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.helper.TextHelper.setTextNameProductInPost
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ReviewSearchHolder(parent: ViewGroup, val type: Int? = null) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_review_search_result, parent, false)) {
    fun bind(obj: ICPost) {
        setupHeader(obj)
        setupContent(obj)
        val (imgMulti, imgOne) = setupImage(obj)
        setupProduct(obj)
        setupRating(obj)

        listener(imgMulti, obj, imgOne)
    }

    private fun setupHeader(obj: ICPost) {
        if (type == null) {
            (itemView as ConstraintLayout).layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(SizeHelper.size12, SizeHelper.size12, SizeHelper.size12, 0)
            }
            itemView.setPadding(0, 0, 0, SizeHelper.size16)
            itemView.setBackgroundResource(R.drawable.bg_corners_white_4_border_05)
        } else {
            (itemView as ConstraintLayout).layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(0, SizeHelper.size1, 0, 0)
            }
            itemView.setPadding(0, 0, 0, SizeHelper.size12)
            itemView.setBackgroundColor(Color.WHITE)
        }
        if (obj.page == null) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.user?.avatar, R.drawable.ic_avatar_default_84px)
            itemView.tvNameUser.apply {
                text = obj.user?.getName
                if (obj.user?.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            itemView.imgRank.beVisible()
            itemView.imgRank.setRankUser(obj.user?.rank?.level)
        } else {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.page?.avatar, R.drawable.ic_business_v2)
            itemView.imgRank.beGone()
            itemView.tvNameUser.text = obj.page?.getName
            if (obj.page?.isVerify == true) {
                itemView.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                itemView.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    private fun setupContent(obj: ICPost) {
        itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay2(obj.createdAt)

        checkLikeReview(obj)
        itemView.tvComment.text = if (obj.commentCount > 100) {
            "100+"
        } else {
            obj.commentCount.toString()
        }

        obj.avgPoint.let {
            itemView.containerRating.setData(it, it)
        }

        if (obj.content.isNullOrEmpty()) {
            itemView.tvContent.beGone()
        } else {
            itemView.tvContent.beVisible()
            itemView.tvContent.text = if (obj.content!!.length > 120) {
                Html.fromHtml(itemView.context.getString(R.string.xxx_xem_them, obj.content!!.substring(0, 120)))
            } else {
                obj.content
            }
        }
    }

    private fun setupImage(obj: ICPost): Pair<LayoutImageInPostComponent, AppCompatImageView> {
        val imgMulti = ((itemView as ViewGroup).getChildAt(2) as ViewGroup).getChildAt(1) as LayoutImageInPostComponent
        val imgOne = itemView.imgOne
        if (obj.media.isNullOrEmpty()) {
            itemView.containerImage.visibility = View.GONE
        } else {
            itemView.containerImage.visibility = View.VISIBLE
            if (obj.media!!.size > 1) {
                imgMulti.visibility = View.VISIBLE
                imgOne.visibility = View.GONE

                val list = mutableListOf<ICImageInPost>()
                for (item in obj.media!!) {
                    list.add(ICImageInPost(item.content ?: "", Constant.IMAGE, null, null))
                }
                imgMulti.setImageInPost(list)

                imgMulti.onClickImageDetail(object : ItemClickListener<MutableList<ICImageInPost>> {
                    override fun onItemClick(position: Int, item: MutableList<ICImageInPost>?) {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_MEDIA_IN_POST, obj.also {
                            it.positionMedia=position
                        }))

                    }
                })
            } else {
                imgMulti.visibility = View.GONE
                imgOne.visibility = View.VISIBLE
                imgOne.scaleType = ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgOne, obj.media!![0].content, R.drawable.img_default_loading_icheck, R.drawable.img_default_loading_icheck, SizeHelper.size4)
            }
        }
        return Pair(imgMulti, imgOne)
    }

    private fun setupProduct(obj: ICPost) {
        if (obj.meta != null) {
            if (ICheckApplication.currentActivity() is IckProductDetailActivity) {
                itemView.containerMeta.beGone()
            } else {
                itemView.containerMeta.beVisible()
                if (!obj.meta?.product?.media.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRounded(itemView.imgProduct, obj.meta?.product?.media!![0].content, R.drawable.img_default_product_big, SizeHelper.size4)
                } else {
                    WidgetUtils.loadImageUrlRounded(itemView.imgProduct, "", R.drawable.img_default_product_big, SizeHelper.size4)
                }
                itemView.tvProduct.setTextNameProductInPost(obj.meta?.product?.name)
                itemView.tvShop.text = obj.meta?.product?.owner?.name ?: ""
            }
        } else {
            itemView.containerMeta.beGone()
        }
    }

    private fun setupRating(obj: ICPost) {
        itemView.containerRating.setOnClickListener {
            val reviewBottom = ICReviewBottom()
            reviewBottom.averagePoint = obj.avgPoint
            reviewBottom.customerCriterias = obj.customerCriteria
            reviewBottom.message = if (obj.page != null) {
                obj.page!!.getName
            } else {
                obj.user?.getName
            }

            ICheckApplication.currentActivity()?.let { act ->
                ReviewBottomSheet.show((act as AppCompatActivity).supportFragmentManager, true, reviewBottom)
            }
        }
    }

    private fun listener(imgMulti: LayoutImageInPostComponent, obj: ICPost, imgOne: AppCompatImageView) {

        imgOne.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_MEDIA_IN_POST, obj))
        }

        itemView.tvProduct.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                obj.meta?.product?.id?.let { id -> IckProductDetailActivity.start(activity, id) }
            }
        }

        itemView.imgAvatar.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    if (obj.page?.id != null)
                        ActivityUtils.startActivity<PageDetailActivity, Long>(it, Constant.DATA_1, obj.page?.id!!)
                }
            }
        }

        itemView.layoutName.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    if (obj.page?.id != null)
                        ActivityUtils.startActivity<PageDetailActivity, Long>(it, Constant.DATA_1, obj.page?.id!!)
                }
            }
        }

        itemView.tvLike.setOnClickListener{
            postLikeReview(obj)
        }

        itemView.tvShop.setOnClickListener{
            ICheckApplication.currentActivity()?.let { activity ->
                if (obj.meta?.product?.owner?.pageId != null)
                    ActivityUtils.startActivity<PageDetailActivity, Long>(activity, Constant.DATA_1, obj.meta?.product?.owner?.pageId!!)
            }
        }

        itemView.tvComment.setOnClickListener{
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, obj))
        }

        itemView.tvContent.setOnClickListener{
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, obj))
        }

        itemView.setOnClickListener{
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, obj))
        }
    }

    private fun checkLikeReview(objReview: ICPost) {
        itemView.tvLike.setCompoundDrawablesWithIntrinsicBounds(if (objReview.expressive != null) {
            R.drawable.ic_like_12px
        } else {
            R.drawable.ic_gray_like_12_px
        }, 0, 0, 0)

        itemView.tvLike.text = if (objReview.expressiveCount > 100) {
            "100+"
        } else {
            objReview.expressiveCount.toString()
        }
    }

    private fun postLikeReview(objReview: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
                ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }
            DialogHelper.showLoading(activity)
            ProductReviewInteractor().postLikeReview(objReview.id, null, object : ICNewApiListener<ICResponse<ICNotification>> {
                override fun onSuccess(obj: ICResponse<ICNotification>) {
                    DialogHelper.closeLoading(activity)
                    if (obj.data?.id != null && obj.data?.id != 0L) {
                        objReview.expressiveCount++
                        objReview.expressive = "like"
                    } else {
                        objReview.expressiveCount--
                        objReview.expressive = null
                    }
                    checkLikeReview(objReview)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    if (error?.statusCode == "S402") {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                    } else {
                        itemView.context.showSimpleErrorToast(error?.message)
                    }
                }
            })
        }
    }
}