package vn.icheck.android.component.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemNewBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.visibleOrGone
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newsdetailv2.NewDetailV2Activity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class NewsAdapter(private val listData: List<ICNews>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun getItemCount(): Int = if (listData.size > 5) 5 else listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemNewBinding = ItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICNews>(binding.root) {

        override fun bind(obj: ICNews) {
            WidgetUtils.loadImageUrlRoundedTransformation(binding.imgNews, obj.thumbnail?.trim(),R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)

            binding.tvStatus.visibleOrGone(!obj.pageIds.isNullOrEmpty())

            binding.tvName.text = obj.title

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<NewDetailV2Activity, Long>(activity, Constant.DATA_1, obj.id)
                }
            }
        }
    }
}