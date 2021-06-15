package vn.icheck.android.component.post

import android.content.Context
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_post_option.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.feed.FeedReportSuccessDialog
import vn.icheck.android.component.privacy_post.PrivacyPostDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICRelatedPage
import vn.icheck.android.network.models.ICUserPost
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionDialog
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils

abstract class PostOptionDialog(context: Context, val post: ICPost) : BaseBottomSheetDialog(context, R.layout.dialog_post_option, true) {
    private var isFollow = false
    private var isReview = false

    fun show() {
        if (post.customerCriteria.isNullOrEmpty()) {
            isReview = false
            dialog.tvReportTitle rText R.string.bao_cao_bai_viet
            dialog.tvEditTitle rText R.string.chinh_sua_bai_viet
            dialog.tvEditContent rText R.string.feed_option_edit
            dialog.tvDeleteContent rText R.string.post_delete_option_content
            dialog.tvPrivacyContent rText R.string.post_privacy_option_content
            dialog.tvReportContent rText R.string.bao_cao_bai_viet_content
        } else {
            isReview = true
            dialog.layoutDelete.beGone()
            dialog.tvReportTitle rText R.string.feed_option_report_title
            dialog.tvEditTitle rText R.string.chinh_sua_bai_danh_gia
            dialog.tvEditContent rText R.string.review_option_edit
            dialog.tvPrivacyContent rText R.string.review_privacy_option_content
            dialog.tvReportContent rText R.string.bao_cao_bai_danh_gia_content
        }

        if (post.page != null) {
            checkOwnerPage(post.page!!)
            dialog.layoutPrivacy.beGone()
        } else {
            dialog.layoutPin.beGone()
            if (post.user?.id == SessionManager.session.user?.id) {
                dialog.layoutFollow.beGone()
                dialog.layoutReport.beGone()
            } else {
                dialog.layoutEdit.beGone()
                dialog.layoutDelete.beGone()
                dialog.layoutPrivacy.beGone()
            }
            post.user?.let { checkFollowUser(it) }
        }

        if (post.disableNotify) {
            dialog.imgNotify.setImageResource(R.drawable.ic_turn_on_notification_40dp)
            if (isReview) {
                dialog.tvNotifyTitle rText R.string.bat_thong_bao_cho_bai_danh_gia
                dialog.tvNotifyContent rText R.string.review_turn_on_notification_option_content
            } else {
                dialog.tvNotifyTitle rText R.string.post_turn_on_notification_option_title
                dialog.tvNotifyContent rText R.string.post_turn_on_notification_option_content
            }
        } else {
            dialog.imgNotify.setImageResource(R.drawable.ic_turn_off_notification_40dp)
            if (isReview) {
                dialog.tvNotifyTitle rText R.string.tat_thong_bao_cho_bai_danh_gia
                dialog.tvNotifyContent rText R.string.review_turn_off_notification_option_content
            } else {
                dialog.tvNotifyTitle rText R.string.post_turn_off_notification_option_title
                dialog.tvNotifyContent rText R.string.post_turn_off_notification_option_content
            }
        }

        onListener()
        dialog.show()
    }


    private fun checkOwnerPage(page: ICRelatedPage) {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myOwnerPageIdList, page.id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        dialog.layoutFollow.beGone()
                        dialog.layoutReport.beGone()
                        checkPin(post)
                    } else {
                        dialog.layoutPin.beGone()
                        dialog.layoutEdit.beGone()
                        dialog.layoutDelete.beGone()
                        checkFollowPage(page)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            dialog.layoutPin.beGone()
            dialog.layoutEdit.beGone()
            dialog.layoutDelete.beGone()
            if (!SessionManager.isUserLogged)
                checkFollowPage(page)
        }
    }

    private fun checkFollowPage(page: ICRelatedPage) {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingPageIdList, page.id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        setTextFollow(page.getName, true)
                    } else {
                        setTextFollow(page.getName, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            setTextFollow(page.getName, false)
        }
    }

    private fun checkFollowUser(user: ICUserPost) {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFollowingUserIdList, user.id.toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        setTextFollow(user.getName, true)
                    } else {
                        setTextFollow(user.getName, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            setTextFollow(user.getName, false)
        }
    }

    private fun checkPin(obj: ICPost) {
        if (obj.pinned) {
            dialog.imgPin.setImageResource(R.drawable.ic_un_pin_40dp)
            if (isReview) {
                dialog.tvPinTitle rText R.string.review_un_pin_option_title
                dialog.tvPinContent rText R.string.review_un_pin_option_content
            } else {
                dialog.tvPinTitle rText R.string.post_un_pin_option_title
                dialog.tvPinContent rText R.string.post_un_pin_option_content
            }
        } else {
            dialog.imgPin.setImageResource(R.drawable.ic_pin_post_40dp)
            if (isReview) {
                dialog.tvPinTitle rText R.string.review_pin_option_title
                dialog.tvPinContent rText R.string.review_pin_option_content
            } else {
                dialog.tvPinTitle rText R.string.post_pin_option_title
                dialog.tvPinContent rText R.string.post_pin_option_content
            }
        }
    }

    private fun setTextFollow(name: String, follow: Boolean) {
        if (follow) {
            isFollow = true
            if (post.page != null) {
                dialog.tvFollowTitle rText R.string.bo_theo_doi_trang_nay
                if (isReview) {
                    dialog.tvFollowContent.text = Html.fromHtml(dialog.context.rText(R.string.review_option_off_follow_page_message, name))
                } else {
                    dialog.tvFollowContent.text = Html.fromHtml(dialog.context.rText(R.string.feed_option_off_follow_page_message, name))
                }
            } else {
                dialog.tvFollowTitle.text = Html.fromHtml(dialog.context.rText(R.string.feed_option_off_follow_title, name))
                if (isReview) {
                    dialog.tvFollowContent rText R.string.review_option_off_follow_user_message
                } else {
                    dialog.tvFollowContent rText R.string.feed_option_off_follow_user_message
                }
            }
        } else {
            isFollow = false
            if (post.page != null) {
                dialog.tvFollowTitle rText R.string.theo_doi_trang_nay
                if (isReview) {
                    dialog.tvFollowContent.text = Html.fromHtml(dialog.context.rText(R.string.review_option_on_follow_page_message, name))
                } else {
                    dialog.tvFollowContent.text = Html.fromHtml(dialog.context.rText(R.string.feed_option_on_follow_page_message, name))
                }
            } else {
                dialog.tvFollowTitle.text = Html.fromHtml(dialog.context.rText(R.string.feed_option_on_follow_title, name))
                if (isReview) {
                    dialog.tvFollowContent rText R.string.review_option_on_follow_message
                } else {
                    dialog.tvFollowContent rText R.string.feed_option_on_follow_message
                }
            }
        }
    }

    private fun onListener() {
        dialog.layoutNotify.setOnClickListener {
            dialog.dismiss()
            if (post.disableNotify) {
                subcribeNotification()
            } else {
                unsubcribeNotification()
            }
        }

        dialog.layoutPin.setOnClickListener {
            dialog.dismiss()
            if (SessionManager.isUserLogged) {
                onPin(!post.pinned)
            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
            }
        }

        dialog.layoutPrivacy.setOnClickListener {
            dialog.dismiss()
            ICheckApplication.currentActivity()?.let {
                PrivacyPostDialog(post.id).show((it as AppCompatActivity).supportFragmentManager, null)
            }
        }

        dialog.layoutEdit.setOnClickListener {
            dialog.dismiss()
            onEdit()
        }

        dialog.layoutReport.setOnClickListener {
            dialog.dismiss()
            if (SessionManager.isUserLogged) {
                showReport(post)
            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
            }
        }
        dialog.layoutFollow.setOnClickListener {
            dialog.dismiss()
            if (SessionManager.isUserLogged) {
                if (post.page != null) {
                    if (ICheckApplication.currentActivity() is PageDetailActivity) {
                        onFollowOrUnfollowPage(isFollow)
                    } else {
                        followOrUnfollowPage()
                    }
                } else {
                    followOrUnfollowUser()
                }
            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
            }
        }

        dialog.layoutDelete.setOnClickListener {
            dialog.dismiss()
            DialogHelper.showConfirm(dialog.context, dialog.context rText R.string.ban_chac_chan_muon_xoa_bai_viet_nay, null, dialog.context rText R.string.de_sau, dialog.context rText R.string.dong_y, true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    onDelete(post.id)
                }
            })
        }
    }

    private fun showReport(objPost: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (activity is AppCompatActivity) {
                if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                    ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    return
                }

                DialogHelper.showLoading(activity)

                PageRepository().getListReportPost(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
                    override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                        DialogHelper.closeLoading(activity)

                        obj.data?.rows?.let { list ->
                            ReportWrongContributionDialog(list, R.string.bao_cao_bai_viet).apply {
                                setListener(object : ReportWrongContributionDialog.DialogClickListener {
                                    override fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>) {
                                        dismiss()
                                        reportPost(objPost, listReason, message, listMessage)
                                    }
                                })

                                show(activity.supportFragmentManager, tag)
                            }
                        }
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        val message = error?.message ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        ToastUtils.showLongError(activity, message)
                    }
                })
            }
        }
    }

    private fun reportPost(objPost: ICPost, listReason: MutableList<Int>, message: String, listMessage: List<String>) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().reportPost(objPost.id, listReason, message, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                    DialogHelper.closeLoading(activity)

                    if (activity is FragmentActivity) {
                        val list = mutableListOf<ICReportForm>()

                        for (item in listMessage) {
                            list.add(ICReportForm(0, item))
                        }
                        if (message.isNotEmpty())
                            list.add(ICReportForm(0, message))

                        FeedReportSuccessDialog(activity).show(list)
                    } else {
                        ToastUtils.showLongSuccess(activity, R.string.bao_cao_bai_viet_thanh_cong)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, error?.message ?: activity rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun followOrUnfollowPage() {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showShortError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            post.page?.id?.let { pageId ->
                DialogHelper.showLoading(activity)
                if (isFollow) {
                    PageRepository().unFollowPage(pageId, object : ICNewApiListener<ICResponse<Boolean>> {
                        override fun onSuccess(obj: ICResponse<Boolean>) {
                            DialogHelper.closeLoading(activity)
                            activity.showShortSuccessToast(activity rText R.string.ban_da_huy_theo_doi_trang_nay)
                        }

                        override fun onError(error: ICResponseCode?) {
                            DialogHelper.closeLoading(activity)
                            ToastUtils.showShortError(ICheckApplication.getInstance(), activity rText R.string.co_loi_xay_ra_vui_long_thu_lai)

                        }
                    })
                } else {
                    PageRepository().followPage(pageId, object : ICNewApiListener<ICResponse<Boolean>> {
                        override fun onSuccess(obj: ICResponse<Boolean>) {
                            DialogHelper.closeLoading(activity)
                            activity.showShortSuccessToast(activity rText R.string.ban_da_theo_doi_trang_nay)
                        }

                        override fun onError(error: ICResponseCode?) {
                            DialogHelper.closeLoading(activity)
                            ToastUtils.showShortError(ICheckApplication.getInstance(), activity rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                        }
                    })
                }
            }
        }
    }

    private fun followOrUnfollowUser() {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showShortError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }
            /*
            * status=2 : bỏ theo dõi
            * status=1: theo dõi
            * */
            val status = if (isFollow) {
                2
            } else {
                1
            }

            DialogHelper.showLoading(activity)
            RelationshipInteractor().followUser(post.user!!.id, status, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    obj.data?.let {
                        if (status == 2) {
                            activity.showShortSuccessToast(activity.rText(R.string.ban_da_bo_theo_doi_s, post.user?.getName))
                        } else {
                            activity.showShortSuccessToast(activity.rText(R.string.ban_da_theo_doi_s, post.user?.getName))
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showShortError(ICheckApplication.getInstance(), activity rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun subcribeNotification() {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().subcribeNotification(post.id, "post", object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    post.disableNotify = false
                    dialog.context.showShortSuccessToast(dialog.context rText R.string.ban_da_bat_thong_bao_bai_viet_nay)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message ?: activity rText R.string.co_loi_xay_ra_vui_long_thu_lai
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }

    private fun unsubcribeNotification() {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            PageRepository().unsubcribeNotification(post.id, "post", object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    post.disableNotify = true
                    dialog.context.showShortSuccessToast(dialog.context rText R.string.ban_da_tat_thong_bao_bai_viet_nay)

                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message ?: activity rText R.string.co_loi_xay_ra_vui_long_thu_lai
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }

    protected abstract fun onPin(isPin: Boolean)
    protected abstract fun onEdit()
    protected abstract fun onFollowOrUnfollowPage(isFollow: Boolean)
    protected abstract fun onDelete(id: Long)
}