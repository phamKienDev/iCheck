package vn.icheck.android.screen.user.utilities

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICTheme
import vn.icheck.android.screen.user.home_page.holder.secondfunction.HomeSecondaryFunctionAdapter
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class UtilitiesAdapter(callbank: IRecyclerViewCallback) : RecyclerViewAdapter<ICTheme>(callbank) {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICTheme>(createView(parent.context)) {
        override fun bind(obj: ICTheme) {
            (itemView as ViewGroup).run {
                if (obj.secondary_functions.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()
                    (getChildAt(1) as AppCompatTextView).run {
                        text = " ${obj.title} (${obj.secondary_functions!!.size})"
                    }

                    (getChildAt(2) as RecyclerView).run {
                        if (!obj.secondary_functions.isNullOrEmpty()) {
//                            for (i in obj.secondary_functions ?: mutableListOf()) {
//                                if (i.scheme == "icheck://utilities") {
//                                    obj.secondary_functions?.remove(i)
//                                }
//                            }

                            val secondAdapter = HomeSecondaryFunctionAdapter(obj.secondary_functions!!, false)
                            adapter = secondAdapter
                            layoutManager = CustomGridLayoutManager(context, 4)
                        }
                    }
                }
            }
        }
    }

    fun createView(context: Context): LinearLayout {
        return LinearLayout(context).also {
            it.layoutParams = ViewHelper.createLayoutParams()
            it.orientation = LinearLayout.VERTICAL
            it.setBackgroundColor(Constant.getAppBackgroundWhiteColor(it.context))

            it.addView(View(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
                it.setBackgroundColor(Color.parseColor("#EAEAEA"))
            })

            it.addView(AppCompatTextView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size10, 0, 0)
                }
                it.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(it.context))
                it.textSize = 16f
                it.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
            })

            it.addView(RecyclerView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            })

        }
    }
}