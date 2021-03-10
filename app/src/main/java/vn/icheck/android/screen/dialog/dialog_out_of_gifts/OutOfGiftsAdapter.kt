package vn.icheck.android.screen.dialog.dialog_out_of_gifts

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICGiftReceived

class OutOfGiftsAdapter(val listData: MutableList<ICGiftReceived>) : RecyclerView.Adapter<OutOfGiftsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (listData.size < 3) {
            listData.size
        } else {
            3
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICGiftReceived>(createView(parent.context)) {

        companion object {
            fun createView(context: Context): View {
                return AppCompatTextView(context).also {
                    it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { params ->
                        params.setMargins(SizeHelper.size9, SizeHelper.size12, SizeHelper.size9, 0)
                    }
                    it.isSingleLine = true
                    it.ellipsize = TextUtils.TruncateAt.END
                    it.includeFontPadding = false
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_blue_12px, 0, 0, 0)
                    it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    it.setTextColor(ContextCompat.getColor(context, R.color.black))
                    it.compoundDrawablePadding = SizeHelper.size2
                }
            }
        }

        override fun bind(obj: ICGiftReceived) {
            (itemView as AppCompatTextView).run {
                text = if (obj.title.isNullOrEmpty()) {
                    itemView.context.getString(R.string.dang_cap_nhat)
                } else {
                    obj.title
                }
            }
        }
    }
}