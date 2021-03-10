package vn.icheck.android.component.post

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_post.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.base.holder.CoroutineViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.*
import vn.icheck.android.helper.*
import vn.icheck.android.helper.TextHelper.setTextNameProductInPost
import vn.icheck.android.model.posts.PostViewModel
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.commentpost.CommentPostActivity
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText
import java.io.Serializable

class PostHolder(parent: ViewGroup, val listener: IPostListener? = null) : CoroutineViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)) {
    private val interaction = PageRepository()
    private val postInteraction = PostInteractor()

    fun bind(obj: ICPost, user: ICUser?) {
        setUpPin(obj)
        setupHeader(obj)
        setupRating(obj)
        setupImage(obj)
        setupProduct(obj.meta?.product)
        setupCount(obj)
        setupComment(obj.commentCount, obj.comments?.firstOrNull())
        setupListener(obj, user)
        checkPrivacyConfig(obj)
    }

    fun bind(postViewModel: PostViewModel) {
        bind(postViewModel.postData, null)
    }

    private fun setUpPin(obj: ICPost) {
        if (obj.pinned) {
            itemView.imgPin.beVisible()
            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, SizeHelper.dpToPx(10), 0, 0)
            }
            itemView.containerPost.layoutParams = layoutParams
        } else {
            itemView.imgPin.beGone()
            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            itemView.containerPost.layoutParams = layoutParams
        }
    }

    private fun setupHeader(obj: ICPost) {
        if (obj.page != null) {
            WidgetUtils.loadImageUrl(itemView.imgLogo, obj.page?.avatar, R.drawable.img_default_business_logo_big)
            itemView.tvPageName.text = obj.page?.getName
            itemView.imgRank.beGone()
            if (obj.page!!.isVerify) {
                itemView.imgVerify.beVisible()
            } else {
                itemView.imgVerify.beGone()
            }
        } else {
            WidgetUtils.loadImageUrl(itemView.imgLogo, obj.user?.avatar, R.drawable.ic_avatar_default_84px)
            itemView.tvPageName.text = obj.user?.getName
            itemView.imgRank.beVisible()
            itemView.imgVerify.beGone()
            itemView.imgRank.setRankUser(obj.user?.rank?.level)
        }

        itemView.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay2(obj.createdAt)

        itemView.tvContent.apply {
            if (!obj.content.isNullOrEmpty()) {
                beVisible()
                text = obj.content!!.trim()
                ViewHelper.setExpandTextWithoutAction(this, 3, itemView.tvContent.context.getString(R.string.xem_them))
            } else {
                beGone()
            }
        }
    }

    private fun setupRating(obj: ICPost) {
        if (obj.customerCriteria.isNullOrEmpty()) {
            itemView.layoutRating.beGone()
        } else {
            itemView.layoutRating.beVisible()
            ReviewPointText.setText(itemView.tvRating, obj.avgPoint)
            itemView.ratingBar.rating = obj.avgPoint
        }

        itemView.imgShowRating.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is FragmentActivity) {
                    val name = if (obj.page != null) {
                        obj.page!!.getName
                    } else {
                        obj.user?.getName
                    }
                    ReviewBottomSheet.show(activity.supportFragmentManager, true, ICReviewBottom(name, obj.avgPoint, obj.customerCriteria))
                }
            }
        }
        itemView.layoutStar.setOnClickListener {
            itemView.imgShowRating.performClick()
        }
    }

    private fun setupImage(obj: ICPost?) {
        val layoutImage = ((itemView as RelativeLayout).getChildAt(0) as LinearLayout).getChildAt(3) as LayoutImageInPostComponent


        if (!obj?.media.isNullOrEmpty()) {
            layoutImage.beVisible()
            val arr = arrayListOf<ICImageInPost>()
            for (item in obj?.media ?: arrayListOf()) {
                if (item.content != null && item.type != null) {
                    arr.add(ICImageInPost(item.content!!, item.type!!, null, null))
                }
            }
            layoutImage.setImageInPost(arr)
            layoutImage.onClickImageDetail(object : ItemClickListener<MutableList<ICImageInPost>> {
                override fun onItemClick(position: Int, item: MutableList<ICImageInPost>?) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        obj?.let { post ->
                            if (activity is IckUserWallActivity) {
                                itemView.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                                    putExtra(USER_WALL_BROADCAST, EDIT_POST)
                                    putExtra(POSITION, bindingAdapterPosition)
                                    putExtra(Constant.DATA_1, position)
                                })
                            } else {
                                MediaInPostActivity.start(post, activity, position)
                            }
                        }
                    }

                }
            })
        } else {
            layoutImage.beGone()
        }
    }

    private fun setupProduct(obj: ICProduct?) {
        if (obj != null) {
            itemView.layoutProduct.beVisible()
            WidgetUtils.loadImageUrlRounded(itemView.imgProduct, obj.media?.find { it.type == "image" }?.content, R.drawable.img_default_product_big, SizeHelper.size4)
            itemView.tvProduct.setTextNameProductInPost(obj.name)
            itemView.tvShopName.text = obj.owner?.name ?: ""
            itemView.layoutProduct.setOnClickListener {
                if (!obj.barcode.isNullOrEmpty()) {

                    ICheckApplication.currentActivity()?.let { activity ->
                        IckProductDetailActivity.start(activity, obj.id)
                    }
                }
            }
        } else {
            itemView.layoutProduct.beGone()
        }
    }

    private fun setupCount(obj: ICPost) {
        checkLike(obj)
        itemView.tvViewComment.text = TextHelper.formatCount(obj.commentCount)
        itemView.tvView.text = TextHelper.formatCount(obj.viewCount)
        itemView.tvShare.text = TextHelper.formatCount(obj.shareCount)
    }

    private fun checkLike(obj: ICPost) {
        if (!obj.expressive.isNullOrEmpty()) {
            itemView.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_on_24dp, 0, 0, 0)
            itemView.tvLike.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_like_question))
        } else {
            itemView.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_off_24dp, 0, 0, 0)
            itemView.tvLike.setTextColor(ContextCompat.getColor(itemView.context, R.color.fast_survey_gray))
        }

        itemView.tvLike.text = TextHelper.formatCount(obj.expressiveCount)
    }

    private fun checkPrivacyConfig(post: ICPost) {
        if (post.page == null) {
            if (post.user?.id != SessionManager.session.user?.id) {
                when (post.user?.userPrivacyConfig?.whoCommentYourPost) {
                    Constant.EVERYONE -> {
                        itemView.containerComment.beVisible()
                    }
                    Constant.FRIEND -> {
                        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null && SessionManager.session.user?.id != null) {
                            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendIdList, post.user?.id.toString(), object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.value != null && snapshot.value is Long) {
                                        itemView.containerComment.beVisible()
                                    } else {
                                        itemView.containerComment.beGone()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    logError(error.toException())
                                }
                            })
                        }

                    }
                    else -> {
                        if (post.user?.id == SessionManager.session.user?.id) {
                            itemView.containerComment.beVisible()
                        } else {
                            itemView.containerComment.beGone()
                        }
                    }
                }
            } else {
                itemView.containerComment.beVisible()
            }
        } else {
            itemView.containerComment.beVisible()
        }
    }

    private fun setupComment(commentCount: Int, comments: ICCommentPost?) {
        if (comments != null) {
            itemView.layoutComment.beVisible()
            itemView.divider39.beVisible()

            if (comments.page != null) {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, comments.page?.avatar, R.drawable.img_default_business_logo_big)
                itemView.tvName.text = comments.page?.getName
                itemView.imgLevel.beGone()
                if (comments.page!!.isVerify) {
                    itemView.imgVerify2.beVisible()
                } else {
                    itemView.imgVerify2.beGone()
                }
            } else {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, comments.user?.avatar, R.drawable.ic_user_svg)
                itemView.imgLevel.setImageResource(Constant.getAvatarLevelIcon16(comments.user?.rank?.level))
                itemView.tvName.text = comments.user?.getName
                itemView.imgVerify2.beGone()
            }

            if (comments.content.isNullOrEmpty()) {
                itemView.tvComment.beGone()
            } else {
                itemView.tvComment.beVisible()
                itemView.tvComment.text = comments.content
            }

            if (comments.media.isNullOrEmpty()) {
                itemView.containerImage.beGone()
            } else {
                itemView.containerImage.beVisible()
                if (comments.media!!.first()!!.type == Constant.VIDEO) {
                    itemView.btnPlay.beVisible()
                } else {
                    itemView.btnPlay.beGone()
                }
                WidgetUtils.loadImageUrlRounded(itemView.imgImage, comments.media!![0]!!.content, R.drawable.img_default_loading_icheck, R.drawable.img_default_loading_icheck, SizeHelper.size4)
            }

            if (commentCount > 1) {
                itemView.tvViewMore.beVisible()
                itemView.tvViewMore.setOnClickListener {
                    itemView.tvViewComment.performClick()
                }
            } else {
                itemView.tvViewMore.beGone()
            }
        } else {
            itemView.layoutComment.beGone()
            itemView.divider39.beGone()
        }
    }

    private fun setupListener(obj: ICPost, user: ICUser?) {
        itemView.imgMore.setOnClickListener {
            showMoreOption(obj)
        }

        itemView.containerImage.setOnClickListener {
            if (!obj.comments.isNullOrEmpty()) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, obj.comments!![0].media))
            }
        }

        itemView.btnPlay.setOnClickListener {
            if (!obj.comments.isNullOrEmpty()) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, obj.comments!![0].media))
            }
        }

        itemView.tvLike.onDelayClick({
            likePost(obj)
        }, 1000)

        itemView.tvViewComment.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is IckUserWallActivity) {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_DETAIL_POST)
                        putExtra(POSITION, bindingAdapterPosition)
                        putExtra(Constant.DATA_1, obj)
                    })
                } else {
                    ActivityUtils.startActivity<DetailPostActivity, Serializable>(activity, Constant.DATA_1, obj)
                }
            }
        }

        itemView.tvShare.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                shareMore(obj)
            }
        }

        itemView.edtComment.onDelayClick({
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity !is IckUserWallActivity) {
                    CommentPostActivity.start(activity, obj, 1)
                } else {
                    itemView.edtComment.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_COMMENT_POST)
                        putExtra(POSITION, bindingAdapterPosition)
                    })
                }
            }
        })

        itemView.tvCamera.onDelayClick({
            ICheckApplication.currentActivity()?.let { activity ->
                CommentPostActivity.start(activity, obj, 2)
            }
        })

        itemView.tvEmoji.onDelayClick({
            ICheckApplication.currentActivity()?.let { activity ->
                CommentPostActivity.start(activity, obj, 3)
            }
        })

        itemView.tvContent.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is IckUserWallActivity) {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_DETAIL_POST)
                        putExtra(POSITION, bindingAdapterPosition)
                        putExtra(Constant.DATA_1, obj)
                    })
                } else {
                    ActivityUtils.startActivity<DetailPostActivity, Serializable>(activity, Constant.DATA_1, obj)

                }
            }
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is IckUserWallActivity) {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_DETAIL_POST)
                        putExtra(POSITION, bindingAdapterPosition)
                        putExtra(Constant.DATA_1, obj)
                    })
                } else {
                    ActivityUtils.startActivity<DetailPostActivity, Serializable>(activity, Constant.DATA_1, obj)
                }
            }
        }
    }

    private fun shareMore(obj: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            interaction.getShareLinkOfPost(obj.id, object : ICNewApiListener<ICResponse<String>> {
                override fun onSuccess(response: ICResponse<String>) {
                    DialogHelper.closeLoading(activity)

                    response.data?.let { shareLink ->
                        ShareCompat.IntentBuilder.from(activity)
                                .setType("text/plain")
                                .setChooserTitle(activity.getString(R.string.chia_se))
                                .setText(shareLink)
                                .startChooser()

                        itemView.tvShare.text = (itemView.tvShare.text.toString().toInt() + 1).toString()
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message
                            ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }

    private fun showMoreOption(obj: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            delayAction({
                object : PostOptionDialog(activity, obj) {
                    override fun onPin(isPin: Boolean) {
                        if (obj.pinned) {
                            DialogHelper.showConfirm(dialog.context, "Bạn chắc chắn muốn bỏ ghim bài viết này?", null, "Để sau", "Đồng ý", true, null, R.color.lightBlue, object : ConfirmDialogListener {
                                override fun onDisagree() {

                                }

                                override fun onAgree() {
                                    pinPost(post, isPin)
                                }
                            })
                        } else {
                            pinPost(post, isPin)
                        }
                    }

                    override fun onEdit() {
                        if (activity !is IckUserWallActivity) {
                            listener?.onEditPost(obj)
                        } else {
                            itemView.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                                putExtra(USER_WALL_BROADCAST, EDIT_POST)
                                putExtra(POSITION, bindingAdapterPosition)
                            })
                        }
                    }


                    override fun onFollowOrUnfollowPage(isFollow: Boolean) {
                        listener?.followAndUnFollowPage(obj)
                    }

                    override fun onDelete(id: Long) {
                        listener?.onDeletePost(id)
                    }
                }.show()
            }, 400)
        }
    }

    private fun likePost(objPost: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            postInteraction.likeOrDislikePost(objPost.id, null, object : ICNewApiListener<ICResponse<ICPost>> {
                override fun onSuccess(obj: ICResponse<ICPost>) {
                    DialogHelper.closeLoading(activity)

                    if (obj.data?.id == null || obj.data?.id == 0L) {
                        objPost.expressive = null
                        objPost.expressiveCount += -1
                    } else {
                        objPost.expressive = "like"
                        objPost.expressiveCount += 1
                    }

                    checkLike(objPost)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    if (error?.statusCode == "S402") {
                        ICheckApplication.currentActivity()?.let { activity ->
                            object : RewardLoginDialog(activity) {
                                override fun onLogin() {
                                    (activity as AppCompatActivity) simpleStartActivity IckLoginActivity::class.java
                                }

                                override fun onRegister() {
                                    (activity as AppCompatActivity).simpleStartForResultActivity(IckLoginActivity::class.java, 1)
                                }

                                override fun onDismiss() {

                                }
                            }.show()
                        }
                    } else {
                        itemView.context.showSimpleErrorToast(error?.message
                                ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }

                }
            })
        }
    }


    private fun pinPost(objPost: ICPost, isPin: Boolean) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            DialogHelper.showLoading(activity)

            interaction.pinPostOfPage(objPost.id, isPin, objPost.page?.id, object : ICNewApiListener<ICResponse<ICPost>> {
                override fun onSuccess(obj: ICResponse<ICPost>) {
                    DialogHelper.closeLoading(activity)
                    objPost.pinned = obj.data?.pinned ?: false
                    setUpPin(objPost)

                    if (objPost.pinned)
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.PIN_POST, objPost.id))
                    else
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UN_PIN_POST, objPost.id))
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message
                            ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }
}