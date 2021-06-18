package vn.icheck.android.component.commentpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemCommentPostBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class CommentPostHolder(val binding: ItemCommentPostBinding, val listener: ICommentPostView) : BaseViewHolder<ICCommentPost>(binding.root) {
    private val interaction = PostInteractor()

    override fun bind(obj: ICCommentPost) {
        itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
            setMargins(obj.marginStart, obj.marginTop, 0, 0)
        }

        if (obj.page != null) {
            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.page?.avatar, R.drawable.ic_business_v2)
            binding.imgLevel.beGone()
            binding.tvTitle.text = obj.page?.name

            if (obj.page!!.isVerify) {
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        } else {
            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.user?.avatar, R.drawable.ic_avatar_default_84dp, R.drawable.ic_avatar_default_84dp)
            binding.tvTitle.apply {
                text = obj.user?.getName
                if (obj.user?.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            binding.imgLevel.setImageResource(Constant.getAvatarLevelIcon16(obj.user?.rank?.level))
            binding.imgLevel.beVisible()
        }

        if (obj.content.isNullOrEmpty()) {
            binding.tvContent.visibility = View.GONE
        } else {
            binding.tvContent.visibility = View.VISIBLE
            binding.tvContent.text = obj.content.toString().trim()
        }

        if (obj.media.isNullOrEmpty()) {
            binding.imageView.visibility = View.GONE
        } else {
            if ((obj.media!![0]!!.content ?: "").contains(".mp4")) {
                binding.btnPlay.visibility = View.VISIBLE
            } else {
                binding.btnPlay.visibility = View.GONE
            }
            binding.imageView.visibility = View.VISIBLE
            WidgetUtils.loadImageUrlRounded(binding.imageView, obj.media!![0]!!.content, R.drawable.img_default_loading_icheck, R.drawable.img_default_loading_icheck, SizeHelper.size4)
        }


        binding.tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay2(obj.createdAt)
        checkLike(obj.expressive)

        binding.tvAnswer.visibility = if (!obj.isReply) {
            View.GONE
        } else {
            if (obj.involveType == Constant.QUESTION) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        binding.imgAvatar.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    obj.page!!.id?.let { idPage -> PageDetailActivity.start(it, idPage) }
                }
            }
        }

        binding.tvTitle.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (obj.page == null) {
                    IckUserWallActivity.create(obj.user?.id, it)
                } else {
                    obj.page!!.id?.let { idPage -> PageDetailActivity.start(it, idPage) }
                }
            }
        }

        binding.tvLike.setOnClickListener {
            likeComment(obj)
        }

        binding.tvAnswer.setOnClickListener {
            listener.onAnswer(obj)
        }

        binding.imageView.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, obj.media))
        }

        binding.btnPlay.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, obj.media))
        }

        itemView.setOnLongClickListener {
            showOption(obj)
            true
        }

        binding.imageView.setOnLongClickListener {
            showOption(obj)
            true
        }

        binding.btnPlay.setOnLongClickListener {
            showOption(obj)
            true
        }
    }

    private fun checkLike(isLike: String?) {
        binding.tvLike.setTextColor(if (isLike != null) {
            ContextCompat.getColor(itemView.context, R.color.red_like_question)
        } else {
            vn.icheck.android.ichecklibs.Constant.getSecondTextColor(itemView.context)
        })
    }

    private fun showOption(obj: ICCommentPost) {
//        if (!obj.isReply && obj.user?.id != SessionManager.session.user?.id && obj.content.isNullOrEmpty()) {
//            return
//        }

        ICheckApplication.currentActivity()?.let { activity ->
            object : CommentPostOptionDialog(activity) {
                override fun onAnswer() {
                    binding.tvAnswer.performClick()
                }

                override fun onEdit() {
                    listener.onEditComment(obj)
                }

                override fun onDelete() {
                    DialogHelper.showConfirm(dialog.context, "Bạn chắc chắn muốn xóa bình luận này?", null, "Để sau", "Đồng ý", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            listener.onDelete(obj)
                        }
                    })
                }
            }.show(obj)
        }
    }

    private fun likeComment(objComment: ICCommentPost) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!SessionManager.isUserLogged) {
            ICheckApplication.currentActivity()?.let {
                DialogHelper.showLoginPopup(it)
            }
            return
        }

        interaction.postLikeComment(objComment.id, null, object : ICNewApiListener<ICResponse<ICNotification>> {
            override fun onSuccess(obj: ICResponse<ICNotification>) {
                if (obj.data?.objectId == null) {
                    objComment.expressiveCount--
                    objComment.expressive = null
                } else {
                    objComment.expressiveCount++
                    objComment.expressive = "like"
                }
                checkLike(objComment.expressive)
            }

            override fun onError(error: ICResponseCode?) {
                ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    companion object {
        fun create(parent: ViewGroup, listener: ICommentPostView) = CommentPostHolder(ItemCommentPostBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
    }
}