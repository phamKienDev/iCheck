package vn.icheck.android.component.tendency.holder

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class TopTendencyBusinessHolder(parent: ViewGroup) : BaseViewHolder<ICPageTrend>(ViewHelper.createItemBusinessTopTendency(parent.context)) {
    override fun bind(obj: ICPageTrend) {
        val parent = itemView as LinearLayout
        val img = parent.getChildAt(0) as AppCompatImageView
        val constraintLayout = parent.getChildAt(1) as ConstraintLayout
        val imgAvatar = constraintLayout.getChildAt(0) as CircleImageView
        val tvName = constraintLayout.getChildAt(1) as AppCompatTextView
        val tvFollow = constraintLayout.getChildAt(2) as AppCompatTextView
        val btnFollow = constraintLayout.getChildAt(3) as AppCompatTextView

        WidgetUtils.loadImageUrlRounded(img, obj.cover, SizeHelper.size4)
        WidgetUtils.loadImageUrl(imgAvatar, obj.avatar)

        checkFollow(btnFollow, obj.isFollow)

        if (!obj.name.isNullOrEmpty()) {
            tvName.text = obj.name
        } else {
            tvName.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.follower != null && obj.follower != 0) {
            if (obj.follower!! > 1000) {
                tvFollow.setText(R.string.k_nguoi_theo_doi, ((obj.follower)!! / 1000f).toString().replace(".0", ""))
            } else {
                obj.follower?.let {
                    tvFollow.setText(R.string.d_nguoi_theo_doi, it)
                }
            }
        }

        btnFollow.setOnClickListener {
            initListener(btnFollow, obj)
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ActivityUtils.startActivity<PageDetailActivity, Long>(activity, Constant.DATA_1, obj.id)
            }
        }

//        when (type) {
//            "Doanh nghiệp" -> {
//                WidgetUtils.loadImageUrlRounded(img, obj.cover, SizeHelper.size4)
//                WidgetUtils.loadImageUrl(imgAvatar, obj.avatar)
//
//                checkFollow(btnFollow, obj.isFollow)
//
//                if (!obj.name.isNullOrEmpty()) {
//                    tvName.text = obj.name
//                } else {
//                    tvName.text = itemView.context.getString(R.string.dang_cap_nhat)
//                }
//
//                if (obj.follower != null && obj.follower != 0) {
//                    if (obj.follower!! > 1000) {
//                        tvFollow.text = "${((obj.follower)!! / 1000f).toString().replace(".0", "")}k người theo dõi"
//                    } else {
//                        tvFollow.text = "${obj.follower} người theo dõi"
//                    }
//                }
//
//                itemView.setOnClickListener {
//                    ICheckApplication.currentActivity()?.let { act ->
//                        ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, 1)
//                    }
//                }
//
//                btnFollow.setOnClickListener {
//                    initListener(btnFollow, obj)
//                }
//            }
//            "Chuyên gia" -> {
//
//            }
//        }
    }

    private fun checkFollow(tvFollow: AppCompatTextView, isFollow: Boolean) {
        // Text follow
        tvFollow.run {
            if (isFollow) {
                setText(R.string.dang_theo_doi)
                background = vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(itemView.context)
                setTextColor(vn.icheck.android.ichecklibs.ColorManager.getSecondTextColor(itemView.context))
            } else {
                setText(R.string.theo_doi)
                background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
                setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
        }
    }

    private fun followPage(tvFollow: AppCompatTextView, page: ICPageTrend) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!SessionManager.isLoggedAnyType) {
                DialogHelper.showLoginPopup(activity)
                return
            }

            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().followPage(page.id, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    page.isFollow = !page.isFollow
                    checkFollow(tvFollow, page.isFollow)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, error?.message ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    fun initListener(tvFollow: AppCompatTextView, obj: ICPageTrend) {
        if (obj.isFollow) {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.showConfirm(activity,
                        activity.getString(R.string.bo_theo_doi_trang),
                        activity.getString(R.string.ban_chac_chan_bo_theo_doi_trang_s_chu, obj.name),
                        object : ConfirmDialogListener {
                            override fun onDisagree() {}

                            override fun onAgree() {
                                followPage(tvFollow, obj)
                            }
                        })
            }
        } else {
            followPage(tvFollow,obj)
        }
    }
}