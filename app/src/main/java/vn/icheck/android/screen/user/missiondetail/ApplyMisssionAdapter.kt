package vn.icheck.android.screen.user.missiondetail

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICCompany
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class ApplyMisssionAdapter : RecyclerViewAdapter<Any>() {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<Any>(creteView(parent.context)) {
        override fun bind(obj: Any) {
            val appCompatImageView = (itemView as LinearLayout).getChildAt(0) as AppCompatImageView
            val circleImageView = itemView.getChildAt(1) as CircleImageView
            val textView = itemView.getChildAt(2) as AppCompatTextView

            when (obj) {
                is ICProduct -> {
                    appCompatImageView.beVisible()
                    circleImageView.beGone()

                    WidgetUtils.loadImageUrlRounded4(appCompatImageView, obj.image,R.drawable.img_default_product_big)
                    textView.text = obj.name
                }
                is ICCategory -> {
                    appCompatImageView.beVisible()
                    circleImageView.beGone()

                    WidgetUtils.loadImageUrlRounded4(appCompatImageView, obj.image)
                    textView.text = obj.name
                }
                is ICCompany -> {
                    appCompatImageView.beGone()
                    circleImageView.beVisible()

                    WidgetUtils.loadImageUrl(circleImageView, obj.image, R.drawable.ic_business_v2)
                    textView.text = obj.name
                }
            }
        }
    }

    fun creteView(context: Context): LinearLayout {
        return LinearLayout(context).apply {
            layoutParams = ViewHelper.createLayoutParams().apply {
                topMargin = SizeHelper.size12
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            addView(AppCompatImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(SizeHelper.size50, SizeHelper.size50)
                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, ContextCompat.getColor(context, R.color.gray), SizeHelper.size4.toFloat())
                setPadding(SizeHelper.size1, SizeHelper.size1, SizeHelper.size1, SizeHelper.size1)
            })

            addView(CircleImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(SizeHelper.size50, SizeHelper.size50)
                circleBackgroundColor = ContextCompat.getColor(context, R.color.gray)
                setPadding(SizeHelper.size1, SizeHelper.size1, SizeHelper.size1, SizeHelper.size1)
            })

            addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(SizeHelper.size12, 0, 0, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_medium),
                    ContextCompat.getColor(context, R.color.colorNormalText),
                    14f,
                    2).apply {
            })
        }
    }
}