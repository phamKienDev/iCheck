package vn.icheck.android.component.product_review.my_review

import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.willy.ratingbar.ScaleRatingBar
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.product_review.submit_review.PermissionBottomSheet
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText

class MyReviewHolder(parent: ViewGroup, val listener: IMyReviewListener) : RecyclerView.ViewHolder(ViewHelper.createMyReviewHolder(parent)) {

    lateinit var tvRating: AppCompatTextView
    lateinit var viewStar: ScaleRatingBar
    lateinit var tvXemChiTiet: AppCompatTextView
    lateinit var tvDesReview: AppCompatTextView
    lateinit var imageReview: LayoutImageInPostComponent
    lateinit var imgPermission: CircleImageView
    lateinit var btnPermission: AppCompatTextView

    fun bind(obj: MyReviewModel) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                (getChildAt(0) as LinearLayout).run {
                    tvRating = getChildAt(1) as AppCompatTextView
                }
                viewStar = getChildAt(1) as ScaleRatingBar
            }
            (getChildAt(1) as LinearLayout).run {
                tvXemChiTiet = getChildAt(0) as AppCompatTextView
            }

            tvDesReview = getChildAt(3) as AppCompatTextView
            imageReview = getChildAt(4) as LayoutImageInPostComponent
            (getChildAt(5) as LinearLayout).run {
                imgPermission = getChildAt(0) as CircleImageView
                btnPermission = getChildAt(1) as AppCompatTextView
            }
        }

        obj.data.myReview?.let { data ->

            tvRating.setText(ReviewPointText.getTextTotal(data.avgPoint))
            viewStar.isClickable = false
            viewStar.rating = data.avgPoint
            if (data.content.isNullOrEmpty()) {
                tvDesReview.visibility = View.GONE
            } else {
                tvDesReview.visibility = View.VISIBLE
                tvDesReview.text = if (data.content!!.length > 130) {
                    Html.fromHtml(vn.icheck.android.ichecklibs.ViewHelper.setSecondaryHtmlString(itemView.context.getString(R.string.xxx_xem_them, data.content!!.substring(0, 130))))
                } else {
                    data.content
                }
            }
            if (!data.media.isNullOrEmpty()) {
                imageReview.beVisible()
                val arr = arrayListOf<ICImageInPost>()
                for (item in data.media!!) {
                    if (item.content != null && item.type != null) {
                        arr.add(ICImageInPost(item.content!!, item.type!!, null, null))
                    }
                }
                imageReview.setImageInPost(arr)
            } else {
                imageReview.beGone()
            }

            if (SettingManager.getPostPermission() == null) {
                WidgetUtils.loadImageUrl(imgPermission, data.user?.avatar, R.drawable.ic_avatar_default_84px)
            } else {
                val error = if (SettingManager.getPostPermission()?.type == Constant.PAGE) {
                    R.drawable.ic_business_v2
                } else {
                    R.drawable.ic_avatar_default_84px
                }
                WidgetUtils.loadImageUrl(imgPermission, SettingManager.getPostPermission()?.avatar
                        ?: "", error)

            }

            btnPermission.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    PermissionBottomSheet(object : PermissionBottomSheet.PermissionListener {
                        override fun getPermission(permission: ICCommentPermission?) {
                            if (permission != null) {
                                listener.onClickReviewPermission()
                            }
                        }
                    }).show((it as AppCompatActivity).supportFragmentManager, null)
                }
            }

            imageReview.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    MediaInPostActivity.start(obj.data.myReview!!.id, it)
                }
            }

            tvXemChiTiet.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    val name = if (data.page != null) {
                        data.page!!.getName
                    } else {
                        data.user?.getName
                    }
                    ReviewBottomSheet.show((it as AppCompatActivity).supportFragmentManager, true,
                            ICReviewBottom(name, data.avgPoint, data.customerCriteria))
                }
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_POST, data.id))
                }
            }
        }
    }

}