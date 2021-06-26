package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemStampContactBinding
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICStampContact

class ICStampContactAdapter(val listData: MutableList<ICStampContact> = mutableListOf()) : RecyclerView.Adapter<ICStampContactAdapter.ViewHolder>() {

    fun setListData(list: MutableList<ICStampContact>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemStampContactBinding =
            ItemStampContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
            BaseViewHolder<ICStampContact>(binding.root) {

        override fun bind(obj: ICStampContact) {
            binding.tvTitle.text = obj.title

            binding.tvHotline.apply {
                text = context.getString(R.string.hotline_v2_s, obj.hotline ?: " ")
                background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlineSecondary1Corners45(itemView.context)
                setOnClickListener {
                    Constant.callPhone(obj.hotline)
                }
            }

            binding.tvEmail.apply {
                text = context.getString(R.string.email_v2_s, obj.email ?: " ")
                background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlineSecondary1Corners45(itemView.context)
                setOnClickListener {
                    Constant.sendEmail(obj.email)
                }
            }
        }
    }
}