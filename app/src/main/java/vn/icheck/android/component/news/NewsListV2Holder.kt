package vn.icheck.android.component.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemNewsListV2Binding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.visibleOrGone
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newsdetailv2.NewDetailV2Activity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.TestTimeUtil

class NewsListV2Holder(parent: ViewGroup, val binding: ItemNewsListV2Binding = ItemNewsListV2Binding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICNews>(binding.root) {

    override fun bind(obj: ICNews) {
        val millisecond = TimeHelper.convertDateTimeSvToMillisecond(obj.createdAt) ?: 0

        WidgetUtils.loadImageUrlRounded4(binding.imgNews, obj.thumbnail?.trim(), R.drawable.img_default_loading_icheck)

        if (System.currentTimeMillis() - millisecond < 86400000) {
            binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(itemView.context, R.drawable.ic_new_36dp), null, null, null)
        } else {
            binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        binding.tvTitle.text = if (!obj.title.isNullOrEmpty()) {
            obj.title
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }
        binding.tvTime.apply {
            text = if (!obj.createdAt.isNullOrEmpty()) {
                TestTimeUtil(obj.createdAt!!).getTimeDateNews(false)
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }
        }

        binding.txtDescription.text = if (!obj.introtext.isNullOrEmpty() || !obj.introtext2.isNullOrEmpty()) {
            obj.introtext ?: obj.introtext2
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        binding.tvStatus.visibleOrGone(!obj.pageIds.isNullOrEmpty())

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
                ActivityUtils.startActivity<NewDetailV2Activity, Long>(act, Constant.DATA_1, obj.id)
            }
        }
    }
}