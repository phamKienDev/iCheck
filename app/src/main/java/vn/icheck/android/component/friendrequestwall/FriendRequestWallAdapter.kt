package vn.icheck.android.component.friendrequestwall

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_friend_request_wall.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendRequestWallAdapter : RecyclerView.Adapter<FriendRequestWallAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICSearchUser>()

    private var onUpdateTitleListener: View.OnClickListener? = null
    private var onRemoveListener: View.OnClickListener? = null

    fun setData(list: MutableList<ICSearchUser>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setUpdateTitleListener(listener: View.OnClickListener) {
        this.onUpdateTitleListener = listener
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.onRemoveListener = listener
    }

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])

        holder.setOnRemoveListener(View.OnClickListener {
            listData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(0, itemCount)
            onUpdateTitleListener?.onClick(null)

            if (listData.isEmpty()) {
                onRemoveListener?.onClick(null)
            }
        })
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICSearchUser>(LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request_wall, parent, false)) {
        private val interaction = RelationshipInteractor()

        private var listener: View.OnClickListener? = null

        override fun bind(obj: ICSearchUser) {
            WidgetUtils.loadImageUrlRounded(itemView.imgAvatar, obj.avatar, R.drawable.ic_user_svg, SizeHelper.size4)

            itemView.tvName.apply {
                text = obj.getName
                if (obj.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            itemView.btnAgree.setOnClickListener {
                acceptFriendRequest(obj)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    IckUserWallActivity.create(obj.id, activity)
                }
            }
        }

        fun setOnRemoveListener(listener: View.OnClickListener) {
            this.listener = listener
        }

        private fun acceptFriendRequest(objFriend: ICSearchUser) {
            ICheckApplication.currentActivity()?.let { activity ->
                if (NetworkHelper.isNotConnected(activity)) {
                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    return
                }

                DialogHelper.showLoading(activity)

                interaction.updateFriendInvitation(objFriend.id, Constant.FRIEND_REQUEST_ACCEPTED, object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        DialogHelper.closeLoading(activity)
                        Handler().postDelayed({
                            val message = if (!objFriend.lastName.isNullOrEmpty()) {
                                activity.getString(R.string.ban_da_tro_thanh_ban_be_voi_xxx, Constant.getName(objFriend.lastName, objFriend.firstName))
                            } else {
                                activity.getString(R.string.cac_ban_da_tro_thanh_ban_be)
                            }
                            itemView.context.showShortSuccessToast(message)
                            listener?.onClick(null)
                        }, 200)
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        itemView.context.showShortErrorToast(error?.message ?: itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                })
            }
        }
    }
}