package vn.icheck.android.component.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_news_list_v2.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newsdetailv2.NewDetailV2Activity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.TestTimeUtil

class NewsListV2Holder(view: View) : BaseViewHolder<ICNews>(view) {

    override fun bind(obj: ICNews) {
        val millisecond = TimeHelper.convertDateTimeSvToMillisecond(obj.createdAt) ?: 0
        val time = System.currentTimeMillis() - millisecond

        if (time < 86400000) {
            itemView.txtTime.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(itemView.context, R.drawable.ic_tag_new_news_32px), null)
        } else {
            itemView.txtTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        if (!obj.title.isNullOrEmpty()) {
            itemView.txtTitle.text = obj.title
        } else {
            itemView.txtTitle.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (!obj.introtext.isNullOrEmpty() || !obj.introtext2.isNullOrEmpty()) {
            itemView.txtDescription.text = obj.introtext ?: obj.introtext2
        } else {
            itemView.txtDescription.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        WidgetUtils.loadImageUrlRounded4(itemView.imgNews, obj.thumbnail?.trim(),R.drawable.img_default_loading_icheck)

        if (!obj.createdAt.isNullOrEmpty()) {
            itemView.txtTime.text = TestTimeUtil(obj.createdAt!!).getTimeDateNews()
        } else {
            itemView.txtTime.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
                ActivityUtils.startActivity<NewDetailV2Activity, Long>(act, Constant.DATA_1, obj.id)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): NewsListV2Holder {
            return NewsListV2Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_news_list_v2, parent, false))
        }
    }
}