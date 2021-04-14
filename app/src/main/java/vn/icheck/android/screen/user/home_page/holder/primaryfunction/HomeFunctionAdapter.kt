package vn.icheck.android.screen.user.home_page.holder.primaryfunction

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.network.models.ICThemeFunction
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class HomeFunctionAdapter(private val listData: MutableList<ICThemeFunction> = mutableListOf()) : RecyclerView.Adapter<HomeFunctionAdapter.ViewHolder>() {

    fun setData(list: MutableList<ICThemeFunction>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICThemeFunction>(LayoutInflater.from(parent.context).inflate(R.layout.item_primary_functions_holder, parent, false)) {

        override fun bind(obj: ICThemeFunction) {
            (itemView as ConstraintLayout).run {
                (getChildAt(0) as AppCompatImageView).run {
                    if (obj.background_source != 0) {
                        setImageResource(obj.background_source)
                    } else {
                        if (!obj.source.isNullOrEmpty()) {
                            if (obj.source!!.startsWith("http")) {
                                WidgetUtils.loadImageUrlFitCenter(this, obj.source, R.drawable.ic_default_square, R.drawable.ic_default_square)
                            } else {
                                val drawable = Drawable.createFromPath(FileHelper.getImagePath(FileHelper.getPath(context), obj.source))
                                if (drawable != null) {
                                    setImageDrawable(drawable)
                                } else {
                                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_square)
                                    setImageBitmap(bitmap)
                                }
                            }
                        } else {
                            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_square)
                            setImageBitmap(bitmap)
                        }
                    }
                }

                (getChildAt(1) as AppCompatTextView).run {
                    text =obj.label ?: ""
                }

                (getChildAt(2) as AppCompatImageView).run {
                    visibility = if (obj.is_hot == true) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        if ((obj.scheme ?: "").contains("icheck://campaigns")) {
                            TrackingAllHelper.tagCampaignListClicked()
                        } else if ((obj.scheme ?: "").contains("icheck://campaign")) {
                            val campaignId = Uri.parse(obj.scheme).getQueryParameter("id")
                            if (!campaignId.isNullOrEmpty())
                                TrackingAllHelper.tagCampaignHomescreenButtonClicked(campaignId)
                        }
                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, obj.scheme)
                    }
                }
            }
        }
    }
}