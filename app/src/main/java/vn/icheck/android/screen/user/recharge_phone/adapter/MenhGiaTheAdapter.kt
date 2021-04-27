package vn.icheck.android.screen.user.recharge_phone.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menh_gia.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.recharge_phone.ICMenhGia

class MenhGiaTheAdapter (private var listInfor: MutableList<ICMenhGia>) : RecyclerView.Adapter<MenhGiaTheAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return listInfor.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_menh_gia, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listInfor[position], listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindData(item: ICMenhGia, listener: OnItemClickListener?) {
            itemView.tvMenhGia.text = TextHelper.formatMoneyComma(item.menhGia.toLong()) + "đ"

            if (item.select){
                itemView.layoutParent.background = ViewHelper.bgOutlinePrimary1Corners6(itemView.context)
                itemView.tvMenhGia.setTextColor(Constant.getNormalTextColor(itemView.context))
                itemView.tvMenhGia.typeface = Typeface.createFromAsset(itemView.context.assets,"font/barlow_semi_bold.ttf")
            }else{
                itemView.layoutParent.setBackgroundResource(R.drawable.bg_default_card_loyalty)
                itemView.tvMenhGia.setTextColor(Color.parseColor("#757575"))
                itemView.tvMenhGia.typeface = Typeface.createFromAsset(itemView.context.assets,"font/barlow_medium.ttf")
            }

            itemView.setOnClickListener {
                listener?.onItemClick(itemView, layoutPosition)
            }
        }
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}