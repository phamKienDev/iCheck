package vn.icheck.android.screen.user.option_edit_information_public

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_privacy_public.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.wall.ICUserPublicInfor
import vn.icheck.android.util.ick.convertBirthDay

class ListPrivacyPublicAdapter(val view: IEditInforPublicView) : RecyclerView.Adapter<ListPrivacyPublicAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICUserPublicInfor>()

    fun setListData(list: MutableList<ICUserPublicInfor>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setStateClick(it: Int) {
        listData[it].selected = !listData[it].selected!!
        notifyItemChanged(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_privacy_public, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICUserPublicInfor) {
            itemView.tvTitle.setCompoundDrawablesWithIntrinsicBounds(ViewHelper.setImageColorPrimary(R.drawable.ic_list_blue_12dp,itemView.context),0,0,0)
            itemView.switchKey.trackDrawable= ViewHelper.btnSwitchGrayUncheckedGreenCheckedWidth50Height30(itemView.context)

            when (item.privacyElementCode) {
                "GENDER" -> {
                    setData(item)
                    itemView.tvValue.text = item.user?.gender
                }

                "BIRTHDAY" -> {
                    setData(item)
                    if (!item.user?.bd.isNullOrEmpty()) {
                        itemView.tvValue.text = item.user?.bd?.convertBirthDay("dd/MM/yyyy")
                    }
                }

                "CITY" -> {
                    setData(item)
                    itemView.tvValue.text = item.user?.city?.name
                }

                "PHONE" -> {
                    setData(item)
                    itemView.tvValue.text = item.user?.phone
                }

                "EMAIL" -> {
                    setData(item)
                    itemView.tvValue.text = item.user?.email
                }
            }
        }

        private fun setData(item: ICUserPublicInfor) {
            itemView.tvTitle.text = item.privacyElementName
            itemView.switchKey.isChecked = item.selected!!

            itemView.switchKey.setOnCheckedChangeListener { buttonView, isChecked ->
                val isCheck = !item.selected!!
                view.onClick(item, isCheck, position)
            }
        }
    }
}