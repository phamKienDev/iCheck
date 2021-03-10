package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.step_build_product

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_step_build_product_stamp_child.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.callback.ItemClickSmallAnimationListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectInfo
import vn.icheck.android.util.kotlin.WidgetUtils

class StepBuildProductStampAdapter(val listener: ItemClickListener<ICObjectInfo>, val listenerSmall: ItemClickSmallAnimationListener<ICObjectInfo>) : RecyclerView.Adapter<StepBuildProductStampAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICObjectInfo>()

    private var selectedPosition = 0

    fun setData(list: MutableList<ICObjectInfo>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    val getSelectedPosition: Int
        get() {
            if (selectedPosition > listData.size) {
                selectedPosition = 0
            }
            return selectedPosition
        }

    val getSelectedObj: ICObjectInfo
        get() {
            return listData[selectedPosition]
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICObjectInfo>(LayoutInflater.from(parent.context).inflate(R.layout.item_step_build_product_stamp_child, parent, false)) {

        override fun bind(obj: ICObjectInfo) {
            itemView.viewLine.visibility = if (adapterPosition == 0) {
                View.GONE
            } else {
                View.VISIBLE
            }

            itemView.imgIcon.apply {
                WidgetUtils.loadImageUrl(this, obj.image, 0)

                if (selectedPosition == adapterPosition) {
                    setSelect(true)
                } else {
                    setSelect(false)
                }

                setOnClickListener {
                    if (selectedPosition != adapterPosition) {
                        listener.onItemClick(adapterPosition, obj)
                        listenerSmall.onItemClickSmall(selectedPosition, obj)
                        setSelect(true)
                        selectedPosition = adapterPosition
                    }
                }
            }
        }

        fun setSelect(isSelected: Boolean) {
            val start: Int
            val end: Int

            if (isSelected) {
                itemView.imgBackground.visibility = View.VISIBLE
                itemView.tvChecked.visibility = View.VISIBLE
                start = itemView.imgIcon.width
                end = SizeHelper.size52
                itemView.imgIcon.setPadding(SizeHelper.size5, SizeHelper.size8, SizeHelper.size5, SizeHelper.size4)
//                android:paddingStart="@dimen/size_3"
//                android:paddingTop="@dimen/size_4"
//                android:paddingEnd="@dimen/size_3"
//                android:paddingBottom="@dimen/size_4"
            } else {
                itemView.imgBackground.visibility = View.GONE
                itemView.tvChecked.visibility = View.GONE
                start = itemView.imgIcon.width
                end = SizeHelper.size30
                itemView.imgIcon.setPadding(0,0,0,0)
            }

            WidgetUtils.changeValueAnimation(start, end, 200, ValueAnimator.AnimatorUpdateListener {
                val layoutParams = itemView.imgIcon.layoutParams as ConstraintLayout.LayoutParams
                if (isSelected){
                    layoutParams.height = SizeHelper.size67
                } else {
                    layoutParams.height = SizeHelper.size38
                }
                layoutParams.width = it.animatedValue as Int
                itemView.imgIcon.layoutParams = layoutParams
            })
        }
    }
}