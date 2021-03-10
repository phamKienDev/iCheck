package vn.icheck.android.screen.user.list_product_question.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.screen.user.commentpost.EmojiAdapter
import vn.icheck.android.util.kotlin.WidgetUtils

class ListEmojiAdapter(val type: Int, val listener: ItemClickListener<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<Any>()

    val parenType = 1
    var selectedPosition = 0

    fun setParentData(list: MutableList<StickerPackages>) {
        selectedPosition = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setChildData(list: MutableList<Stickers>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == parenType) {
            ParentEmojiHolder(parent)
        } else {
            ChildEmojiHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChildEmojiHolder -> {
                holder.bind(listData[position] as Stickers)
            }
            is ParentEmojiHolder -> {
                holder.bind(listData[position] as StickerPackages)
            }
        }
    }

    inner class ParentEmojiHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createParentView(parent.context)) {

        fun bind(obj: StickerPackages) {
            WidgetUtils.loadImageUrl((itemView as AppCompatImageView), obj.thumbnail)

            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition
                    listener.onItemClick(adapterPosition, obj)
                }
            }
        }
    }

    fun createParentView(context: Context): AppCompatImageView {
        return AppCompatImageView(context).also {
            it.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size40, SizeHelper.size40).also {
                it.setMargins(0, SizeHelper.size2, SizeHelper.size10, SizeHelper.size2)
            }
        }
    }


    inner class ChildEmojiHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createChildView(parent.context)) {

        fun bind(obj: Stickers) {
            WidgetUtils.loadImageUrl((itemView as ViewGroup).getChildAt(0) as AppCompatImageView, obj.image)

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, obj)
            }
        }
    }

    fun createChildView(context: Context): View {
        val layoutParent = ConstraintLayout(context)
        layoutParent.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        layoutParent.id = R.id.layoutParent

        val image = AppCompatImageView(context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(0, 0).also {
                it.setMargins(SizeHelper.size5, SizeHelper.size5, SizeHelper.size5, SizeHelper.size5)
            }
            it.id = R.id.imgGift
        }
        layoutParent.addView(image)

        val imageSet = ConstraintSet()
        imageSet.clone(layoutParent)
        imageSet.connect(image.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        imageSet.connect(image.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        imageSet.connect(image.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)
        imageSet.connect(image.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)

        imageSet.setDimensionRatio(image.id, "W, 1:1")
        imageSet.applyTo(layoutParent)

        return layoutParent
    }

}