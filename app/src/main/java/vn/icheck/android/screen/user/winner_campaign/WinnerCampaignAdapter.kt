package vn.icheck.android.screen.user.winner_campaign

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.util.kotlin.WidgetUtils

class WinnerCampaignAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<CampaignModel>(callback) {
    private val headerType = 3
    private val bottomType = 4

    fun setHeaderTheWinner(obj: CampaignModel) {
        listData.clear()

        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    fun setData(obj: List<CampaignModel>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<CampaignModel>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position].type == headerType) {
            headerType
        } else {
            bottomType
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == headerType) {
            HeaderTheWinner(parent)
        } else {
            BottomTheWinner(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HeaderTheWinner -> {
                if (!listData[position].listData.isNullOrEmpty()) {
                    holder.bind(listData[position].listData!!)
                }
            }
            is BottomTheWinner -> {
                listData[position].campaign?.let {
                    holder.bind(it)
                }
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }

    inner class HeaderTheWinner(parent: ViewGroup) : BaseViewHolder<MutableList<ICCampaign>>(LayoutInflater.from(parent.context).inflate(R.layout.item_header_the_winner, parent, false)) {
        override fun bind(obj: MutableList<ICCampaign>) {
            (itemView as ViewGroup).run {
                (getChildAt(1) as ConstraintLayout).run {
                    val viewTop1 = getChildAt(0) as View
                    val viewTop2 = getChildAt(1) as View
                    val viewTop3 = getChildAt(2) as View

                    val avatarTop1 = getChildAt(11) as CircleImageView
                    val avatarTop2 = getChildAt(6) as CircleImageView
                    val avatarTop3 = getChildAt(16) as CircleImageView

                    val nameTop1 = getChildAt(13) as AppCompatTextView
                    val nameTop2 = getChildAt(8) as AppCompatTextView
                    val nameTop3 = getChildAt(18) as AppCompatTextView

                    val coinTop1 = getChildAt(14) as AppCompatTextView
                    val coinTop2 = getChildAt(9) as AppCompatTextView
                    val coinTop3 = getChildAt(19) as AppCompatTextView

                    viewTop1.background=ViewHelper.bgWhiteCornersTop6(viewTop1.context)
                    viewTop2.background=ViewHelper.bgWhiteCornersTopLeft6(viewTop1.context)
                    viewTop3.background=ViewHelper.bgWhiteCornersTopRight6(viewTop1.context)

                    //Top 1
                    WidgetUtils.loadImageUrl(avatarTop1, obj[0].avatar, R.drawable.ic_user_svg)

                    setName(nameTop1, obj[0].name)

                    setReward(coinTop1, obj[0].valueReward)

                    //Top 2
                    if (obj.size > 1) {
                        WidgetUtils.loadImageUrl(avatarTop2, obj[1].avatar, R.drawable.ic_user_svg)

                        setName(nameTop2, obj[1].name)
                        setReward(coinTop2, obj[1].valueReward)

                        //Top 3
                        if (obj.size > 2) {
                            WidgetUtils.loadImageUrl(avatarTop3, obj[2].avatar, R.drawable.ic_user_svg)

                            setName(nameTop3, obj[2].name)
                            setReward(coinTop3, obj[2].valueReward)
                        } else {
                            setName(nameTop3, null)
                            setReward(coinTop3, null)
                        }
                    } else {
                        setName(nameTop2, null)
                        setReward(coinTop2, null)
                    }
                }
            }
        }

        private fun setReward(textView: AppCompatTextView, value: Long?) {
            if (value != null) {
                textView.text = "${TextHelper.formatMoneyPhay(value)}"
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentYellow))
                textView.setTypeface(null, Typeface.NORMAL)
            } else {
                textView.text = itemView.context.getString(R.string.dang_cap_nhat)
                textView.setTypeface(null, Typeface.ITALIC)
                textView.setTextColor(Constant.getDisableTextColor(itemView.context))
            }
        }

        private fun setName(tv: AppCompatTextView, value: String?) {
            if (value.isNullOrEmpty()) {
                tv.setTextColor(Constant.getDisableTextColor(itemView.context))
                tv.text = itemView.context.getString(R.string.dang_cap_nhat)
                tv.typeface = Typeface.createFromAsset(itemView.context.assets, "font/barlow_medium.ttf")
            } else {
                tv.text = value
                tv.setTextColor(Constant.getNormalTextColor(itemView.context))
                tv.typeface = Typeface.createFromAsset(itemView.context.assets, "font/barlow_semi_bold.ttf")
            }
        }
    }

    inner class BottomTheWinner(parent: ViewGroup) : BaseViewHolder<ICCampaign>(LayoutInflater.from(parent.context).inflate(R.layout.item_bottom_the_winner, parent, false)) {
        override fun bind(obj: ICCampaign) {
            ((itemView as LinearLayout).getChildAt(0) as AppCompatTextView).visibility = if (adapterPosition == 1) {
                View.VISIBLE
            } else {
                View.GONE
            }

            (itemView.getChildAt(1) as ConstraintLayout).run {
                WidgetUtils.loadImageUrl((getChildAt(0) as CircleImageView), obj.avatar, R.drawable.ic_user_svg)

                (getChildAt(1) as AppCompatTextView).run {
                    text = if (!obj.name.isNullOrEmpty()) {
                        obj.name
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(2) as AppCompatTextView).run {
                    text = if (!obj.phone.isNullOrEmpty()) {
                        obj.phone
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }
                (getChildAt(3) as AppCompatTextView).run {
                    text = if (!obj.receiveAt.isNullOrEmpty()) {
                        TimeHelper.convertDateTimeSvToTimeDateVnV1(obj.receiveAt)
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }
                (getChildAt(4) as AppCompatImageView).run {
                    if (obj.type == 3) {
                        WidgetUtils.loadImageUrlFitCenter(this, obj.icoinIcon, R.drawable.ic_icheck_xu)
                    } else {
                        WidgetUtils.loadImageUrl(this, obj.rewardImage)
                    }
                }
            }
        }
    }
}