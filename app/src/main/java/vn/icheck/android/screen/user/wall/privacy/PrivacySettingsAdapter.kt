package vn.icheck.android.screen.user.wall.privacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.databinding.ItemConfirmPrivacyBinding
import vn.icheck.android.databinding.ItemPrivacySettingBinding
import vn.icheck.android.network.model.privacy.UserPrivacyModel
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.model.privacy.UserPrivacyModel
import vn.icheck.android.util.ick.logError

class PrivacySettingsAdapter(val onSaveChangeListener: OnSaveChangeListener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = arrayListOf<ICViewModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.ITEM_PRIVACY_CONFIRM -> {
                ItemButton(ItemConfirmPrivacyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            ICViewTypes.ITEM_PRIVACY_DEFAULT -> {
                ItemPrivacyDefault(ItemPrivacySettingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }

            else -> {
                NullHolder.create(parent)
            }
        }
    }

    fun updateList(list: List<ICViewModel>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ICViewTypes.ITEM_PRIVACY_DEFAULT) {
            val userPrivacyModel = listData[position] as UserPrivacyModel
            (holder as ItemPrivacyDefault).bind(userPrivacyModel)
            holder.binding.privacyGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    holder.binding.btnFriends.id -> {
                        val first = userPrivacyModel.listData.first()
                        onSaveChangeListener.onSelect(first?.privacyId, first?.privacyElementId)
                    }
                    holder.binding.btnEveryone.id -> {
                        val second = userPrivacyModel.listData[1]
                        onSaveChangeListener.onSelect(second?.privacyId, second?.privacyElementId)
                    }
                    holder.binding.btnPrivate.id -> {
                        val third = userPrivacyModel.listData[2]
                        onSaveChangeListener.onSelect(third?.privacyId, third?.privacyElementId)
                    }
                }
            }
        }
        else if (holder.itemViewType == ICViewTypes.ITEM_PRIVACY_CONFIRM) {
            (holder as ItemButton).binding.btnContinue.apply {
                background = ViewHelper.bgPrimaryCorners4(context)
                setOnClickListener {
                    onSaveChangeListener.onSave()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].getViewType()
    }
}

class ItemPrivacyDefault(val binding:ItemPrivacySettingBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(userPrivacyModel: UserPrivacyModel) {
        val arr = arrayListOf<RadioButton>(binding.btnFriends, binding.btnEveryone, binding.btnPrivate)
        val firstOrNull = userPrivacyModel.listData.firstOrNull()
        binding.tvGroupTitle.text = " ${firstOrNull?.privacyId}. ${firstOrNull?.privacyName}"
        if (userPrivacyModel.listData.size == 2) {
            binding.btnPrivate.visibility = View.GONE
        } else {
            binding.btnPrivate.visibility = View.VISIBLE
        }
        if (userPrivacyModel.listData.size <= arr.size) {
            try {
                for (item in userPrivacyModel.listData) {
                    arr[userPrivacyModel.listData.indexOf(item)].text = item?.privacyElementName
                    arr[userPrivacyModel.listData.indexOf(item)].isChecked = item?.selected!!
                }
            } catch (e: Exception) {
                logError(e)
            }
        }

    }
}

class ItemButton(val binding: ItemConfirmPrivacyBinding):RecyclerView.ViewHolder(binding.root)

interface OnSaveChangeListener{
    fun onSave()
    fun onSelect( id:Int?,code:Int?)
}