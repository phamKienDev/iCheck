package vn.icheck.android.component.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_new.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newsdetailv2.NewDetailV2Activity
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class NewsAdapter(private val listData: List<ICNews>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_new,parent,false))
    }

    override fun getItemCount(): Int = if (listData.size > 5) 5 else listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICNews>(view) {

        override fun bind(obj: ICNews) {
            WidgetUtils.loadImageUrlRoundedTransformation(itemView.imgNews, obj.thumbnail?.trim(),R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4, RoundedCornersTransformation.CornerType.TOP)

            itemView.tvName.text = obj.title

//            tvContent.text = if (obj.introtext.length > 52) {
//                Html.fromHtml(tvContent.context.getString(R.string.news_introtext, obj.introtext.substring(0, 52)))
//            } else {
//                obj.introtext
//            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<NewDetailV2Activity, Long>(activity, Constant.DATA_1, obj.id)
                }
            }
        }
    }
}