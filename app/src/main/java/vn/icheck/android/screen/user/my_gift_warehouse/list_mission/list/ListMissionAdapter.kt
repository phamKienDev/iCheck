package vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemGiftListMissionBinding
import vn.icheck.android.databinding.ItemGiftListMissionTitleBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICMission
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.util.kotlin.WidgetUtils

class ListMissionAdapter(val listData: MutableList<ICMission>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemType = 1

    fun setData(list: MutableList<ICMission>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateMission(mission: ICMissionDetail) {
        var positionMission = -1
        var positionTitle = -1
        var missionChange: ICMission? = null

        for (i in 0 until listData.size) {
            if (listData[i].id.isNotEmpty()) {
                if (listData[i].id == mission.id) {
                    listData[i].currentEvent = mission.currentEvent
                    listData[i].finishState = mission.finishState
                    listData[i].state = mission.state!!
                    notifyItemChanged(i)

                    missionChange = listData[i]
                    positionMission = i
                }
            } else {
                positionTitle = i
            }
        }

        if (mission.finishState == 2 && positionMission < positionTitle) {
            listData.removeAt(positionMission)
            if (positionTitle == -1) {
                listData.add(ICMission())
                missionChange?.let { listData.add(it) }
            } else {
                missionChange?.let { listData.add(positionTitle, it) }
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = listData.size

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].id.isNotEmpty()) {
            itemType
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemType) {
            ItemHolder(ItemGiftListMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            TitleHolder(ItemGiftListMissionTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemHolder -> {
                holder.bind(listData[position])
            }
        }
    }

    private class ItemHolder(val binding: ItemGiftListMissionBinding) : BaseViewHolder<ICMission>(binding.root) {

        override fun bind(obj: ICMission) {

            if (obj.image.isNullOrEmpty()) {
                binding.imgIcon.setImageResource(if (obj.finishState == 3) {
                    Constant.getMissionFailedIcon(obj.event)
                } else {
                    Constant.getMissionInprogressIcon(obj.event)
                })
            } else {
                WidgetUtils.loadImageUrl(binding.imgIcon, obj.image)
            }

            binding.tvCount.text = obj.boxAmount.toString()
            binding.tvName.text = obj.missionName
            binding.tvStatus.text = ("${obj.currentEvent}/${obj.totalEvent}")
            binding.progressBar.max = obj.totalEvent
            binding.progressBar.progress = obj.currentEvent

            when (obj.finishState) {
                3 -> {
                    binding.tvProgress.text = itemView.context.getString(R.string.da_ket_thuc)
                    binding.progressBar.progressDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.progress_gray_d8)
                }
                0 -> {
                    binding.tvProgress.text = itemView.context.getString(R.string.chua_dien_ra)
                    binding.progressBar.progressDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.progress_gray_d8)
                }
                else -> {
                    binding.tvProgress.text = (itemView.context.getString(R.string.tien_do_xxx, ((obj.currentEvent.toDouble() / obj.totalEvent.toDouble()) * 100).toInt()) + "%")
                    binding.progressBar.progressDrawable = ViewHelper.progressbarAccentYellowMission(itemView.context)
                }
            }

            binding.imgStatus.setImageResource(if (obj.currentEvent < obj.totalEvent) {
                R.drawable.ic_checkbox_single_off_24dp
            } else {
                R.drawable.ic_checkbox_single_on_24dp
            })

            binding.root.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_MISSION, obj.id))
                }
            }
        }
    }

    private class TitleHolder(binding: ItemGiftListMissionTitleBinding) : RecyclerView.ViewHolder(binding.root)
}