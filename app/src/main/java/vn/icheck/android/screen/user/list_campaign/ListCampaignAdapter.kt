package vn.icheck.android.screen.user.list_campaign

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemGiftListMissionTitleBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.view.second_text.TextSecondBarlowMedium
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class ListCampaignAdapter constructor(val callback: ListCampaignCallback) : RecyclerViewCustomAdapter<ICCampaign>(callback) {
    private val itemType = 3
    private val empityType = 4
    private val titleType = 5

    var sizeInprogess = 0
    fun updateCampaign(campaign: ICCampaign) {
        for (i in 0 until listData.size) {
            if (listData[i].id == campaign.id) {
                listData[i] = campaign
                notifyItemChanged(i)
            }
        }
    }

    fun addListCampaign(list: MutableList<ICCampaign>) {
        checkLoadmore(list)
        val listInprogess = mutableListOf<ICCampaign>()
        val listEnded = mutableListOf<ICCampaign>()

        for (campaign in list) {
            if (campaign.state.toString().toDouble().toInt() == 3) {
                listEnded.add(campaign)
            } else {
                listInprogess.add(campaign)
            }
        }

        listData.addAll(sizeInprogess, listInprogess)
        listData.addAll(listData.size, listEnded)
        sizeInprogess += listInprogess.size
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position].id == null) {
            empityType
        } else if (listData[position].id == Constant.TITLE) {
            titleType
        } else {
            itemType
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> CampaignViewHolder(parent)
            empityType -> EmpityCampaign(parent)
            titleType -> TitleHolder(ItemGiftListMissionTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is CampaignViewHolder) {
            holder.bind(listData[position])

            holder.itemView.setOnClickListener {
                callback.clickGift(listData[position])
            }
        } else if (holder is TitleHolder) {
            holder.bind()
        }
    }

    inner class CampaignViewHolder(parent: ViewGroup) : BaseViewHolder<ICCampaign>(LayoutInflater.from(parent.context).inflate(R.layout.item_campaign, parent, false)) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICCampaign) {
            itemView.btnJoinCampaign.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
            itemView.tvEnded.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(itemView.context))

            WidgetUtils.loadImageUrlRounded4(itemView.imgBanner, obj.image, R.drawable.bg_error_campaign)

            WidgetUtils.loadImageUrlRounded4(itemView.imgAvatarSponsor, obj.logo)

            itemView.txtCountUserJoin.text = obj.joinMember.toString()

            when (obj.state.toString().toDouble().toInt()) {
                //Chưa bắt đầu
                0 -> {
                    itemView.findViewById<AppCompatTextView>(R.id.tv1)?.text = "Thời gian diễn ra"
                    itemView.txtCountUserJoin.beGone()
                    itemView.tv2.beGone()
                    itemView.view.beGone()
                    itemView.tvReward.beGone()
                    itemView.imgUpcoming.beVisible()
                    itemView.btnJoinCampaign.beGone()
                    itemView.tvEnded.beGone()

                    itemView.findViewById<AppCompatTextView>(R.id.txtDate)?.text = "Từ ${TimeHelper.convertDateTimeSvToDayMonthVn(obj.beginAt)} - ${TimeHelper.convertDateTimeSvToDayMonthVn(obj.endedAt)}"
                }
                //Chưa tham gia
                1 -> {
                    itemView.findViewById<AppCompatTextView>(R.id.tv1)?.text = "Thời gian"
                    itemView.txtCountUserJoin.beVisible()
                    itemView.tv2.beVisible()
                    itemView.view.beVisible()
                    itemView.tvReward.beGone()
                    itemView.btnJoinCampaign.beVisible()
                    itemView.tvEnded.beGone()
                    itemView.imgUpcoming.beGone()

                    if (obj.beginAt.isNullOrEmpty() || obj.endedAt.isNullOrEmpty()) {
                        itemView.findViewById<AppCompatTextView>(R.id.txtDate)?.text = itemView.context.getString(R.string.dang_cap_nhat)
                    } else {
                        itemView.findViewById<AppCompatTextView>(R.id.txtDate)?.text = "Đến ${TimeHelper.convertDateTimeSvToDayMonthVn(obj.endedAt)}"
                    }

                }
                //Đã tham gia
                2 -> {
                    itemView.findViewById<AppCompatTextView>(R.id.tv1)?.text = "Thời gian"
                    itemView.txtCountUserJoin.beVisible()
                    itemView.tv2.beVisible()
                    itemView.view.beVisible()
                    itemView.tvReward.beVisible()
                    itemView.imgUpcoming.beGone()
                    itemView.btnJoinCampaign.beGone()
                    itemView.tvEnded.beGone()

                    if (obj.beginAt.isNullOrEmpty() || obj.endedAt.isNullOrEmpty()) {
                        itemView.findViewById<AppCompatTextView>(R.id.txtDate)?.text = itemView.context.getString(R.string.dang_cap_nhat)
                    } else {
                        itemView.findViewById<AppCompatTextView>(R.id.txtDate)?.text = "Đến ${TimeHelper.convertDateTimeSvToDayMonthVn(obj.endedAt)}"
                    }
                    itemView.tvReward.text = "${obj.itemCount} lượt mở"
                }
                //Đã hết hạn
                else -> {
                    itemView.findViewById<AppCompatTextView>(R.id.tv1)?.text = "Thời gian"
                    itemView.txtCountUserJoin.beGone()
                    itemView.tv2.beGone()
                    itemView.view.beGone()
                    itemView.imgUpcoming.beGone()
                    itemView.btnJoinCampaign.beGone()
                    itemView.tvReward.beGone()
                    itemView.tvEnded.beVisible()
                }
            }
        }
    }

    inner class EmpityCampaign(parent: ViewGroup) : RecyclerView.ViewHolder(createView(parent.context))

    fun createView(context: Context): LinearLayout {
        return LinearLayout(context).also {
            it.layoutParams = ViewHelper.createLayoutParams()
            it.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(it.context))
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.orientation = LinearLayout.VERTICAL

            it.addView(AppCompatImageView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.topMargin = SizeHelper.dpToPx(150)
                }
                it.setBackgroundResource(R.drawable.ic_group_120dp)
            })

            it.addView(AppCompatTextView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams().also {
                    it.topMargin = SizeHelper.size28

                }
                it.setTextColor(vn.icheck.android.ichecklibs.Constant.getNormalTextColor(context))
                it.gravity = Gravity.CENTER
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                it.textSize = 16f
                it.text = context.getString(R.string.hien_tai_dang_khong_co_su_kien_nao)
            })

            it.addView(TextSecondBarlowMedium(context).also {
                it.layoutParams = ViewHelper.createLayoutParams().also {
                    it.setMargins(SizeHelper.size38, SizeHelper.size10, SizeHelper.size38, 0)
                }
                it.gravity = Gravity.CENTER
                it.textSize = 14f
                it.text = context.getString(R.string.thuong_xuyen_tham_gia_su_kien)
            })
        }
    }

    private class TitleHolder(val binding: ItemGiftListMissionTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvTitle.text = itemView.context.getString(R.string.da_ket_thuc)
        }
    }
}