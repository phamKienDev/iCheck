package vn.icheck.android.component.news

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newslistv2.NewsListV2Activity
import vn.icheck.android.util.kotlin.ActivityUtils

class NewsHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICNews>>(ViewHelper.createRecyclerViewWithTitleHolder(parent.context)) {

    override fun bind(obj: MutableList<ICNews>) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).run {
                    setText("Tin hay ngày mới")
                }

                (getChildAt(1) as AppCompatTextView).run {
                    setOnClickListener {
                        ICheckApplication.currentActivity()?.let { act ->
                            ActivityUtils.startActivity<NewsListV2Activity>(act)
                        }
                    }
                }
            }

            (getChildAt(1) as RecyclerView).run {
                onFlingListener = null
                layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = NewsAdapter(obj)
            }
        }
    }
}