package vn.icheck.android.screen.bottomsheet

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICPrizePage
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class BottomSheetPageAdapter (val listData: MutableList<ICPrizePage>, val width: Int, val height: Int, val textSizeName: Float, val textSizeDate: Float, val left: Int) : RecyclerView.Adapter<BottomSheetPageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPrizePage>(ViewHelper.createItemPrizePageHolder(parent.context, width, height, textSizeName, textSizeDate, left)){
        override fun bind(obj: ICPrizePage) {
            (itemView as LinearLayout).run {
                WidgetUtils.loadImageUrl((getChildAt(0) as CircleImageView), obj.avatar)

                (getChildAt(1) as LinearLayout).run {

                    (getChildAt(0) as AppCompatTextView).run {
                        if (!obj.name.isNullOrEmpty()){
                            text = obj.name
                        }
                    }

                    (getChildAt(1) as AppCompatTextView).run {
                        if (!obj.createdAt.isNullOrEmpty()){
                            text = obj.createdAt
                        }
                    }
                }

                setOnClickListener {
                    ToastUtils.showLongWarning(itemView.context, "onClickPrizePage")
                }
            }
        }
    }
}