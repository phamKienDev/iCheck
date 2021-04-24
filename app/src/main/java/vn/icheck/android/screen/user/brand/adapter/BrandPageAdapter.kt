package vn.icheck.android.screen.user.brand.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class BrandPageAdapter(val listener: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICPageTrend>()

    private val itemType = 1
    private val itemLoadMore = 2

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_request
    private var isLoading = false
    private var isLoadMore = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(error: String, icon: Int) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        iconMessage = icon
        mMessageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICPageTrend>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICPageTrend>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
        } else {
            if (mMessageError != null) {
                ICViewTypes.MESSAGE_TYPE
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, "")
                } else {
                    holder.bind(iconMessage, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPageTrend>(ViewHelper.createItemBrand(parent.context)) {

        override fun bind(obj: ICPageTrend) {
            (itemView as ConstraintLayout).run {
                setOnClickListener {
                    ToastUtils.showLongWarning(itemView.context, "onClickBrandPage")
                }
                WidgetUtils.loadImageUrl((getChildAt(0) as CircleImageView), obj.avatar)

                (getChildAt(1) as AppCompatTextView).run {
                    text = if (!obj.name.isNullOrEmpty()) {
                        obj.name
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(2) as AppCompatTextView).run {
                    text = if (obj.followCount != null) {
                        if (obj.followCount < 1000) {
                            "${obj.followCount} người thích trang này"
                        } else {
                            "${(obj.followCount) / 1000}k người thích trang này"
                        }
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(3) as AppCompatTextView).run {
                    if (!obj.followers.isNullOrEmpty()) {

                        text = if (obj.likeCount != null) {
                            if (obj.followers?.get(0)?.name.isNullOrEmpty()) {
                                "${obj.likeCount} bạn khác thích trang này"
                            } else {
                                "${obj.followers?.get(0)?.name} và ${obj.likeCount} bạn khác thích trang này"
                            }
                        } else {
                            if (!obj.followers?.get(0)?.name.isNullOrEmpty()) {
                                "${obj.followers?.get(0)?.name} và ${obj.followers!!.size - 1} bạn khác thích trang này"
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }
                        }

                    } else {
                        text = itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(4) as AppCompatTextView).run {
                    text = if (!obj.objectType.isNullOrEmpty()) {
                        obj.objectType
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(5) as AppCompatTextView).let { text ->
                    checkFollow(text, obj.isFollow)

                    text.setOnClickListener {
                        initListener(text, obj)
                    }
                }
            }
        }

        private fun checkFollow(tvFollow: AppCompatTextView, isFollow: Boolean) {
            // Text follow
            tvFollow.run {
                if (isFollow) {
                    text = "Đang theo dõi"
                    background = ContextCompat.getDrawable(context, R.drawable.bg_gray_f0_corners_4)
                    setTextColor(Constant.getSecondTextColor(context))
                } else {
                    text = "Theo dõi"
                    background = ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
            }
        }

        private fun followPage(tvFollow: AppCompatTextView, pageID: Long, isFollow: Boolean, obj: ICPageTrend) {
            RelationshipHelper.postFollowPage(pageID, object : RelationshipHelper.ClickFollowPage {
                override fun onClickFollowPage() {
                    obj.isFollow = !isFollow
                    checkFollow(tvFollow, !isFollow)
                }
            })
        }

        fun initListener(tvFollow: AppCompatTextView, obj: ICPageTrend) {
            if (obj.isFollow) {
                ICheckApplication.currentActivity()?.let { activity ->
                    DialogHelper.showConfirm(activity,
                            activity.getString(R.string.bo_theo_doi_trang),
                            activity.getString(R.string.ban_chac_chan_bo_theo_doi_trang_xxx_chu, obj.name),
                            object : ConfirmDialogListener {
                                override fun onDisagree() {}

                                override fun onAgree() {
                                    followPage(tvFollow, obj.id, obj.isFollow, obj)
                                }
                            })
                }
            } else {
                followPage(tvFollow, obj.id, obj.isFollow, obj)
            }
        }
    }
}