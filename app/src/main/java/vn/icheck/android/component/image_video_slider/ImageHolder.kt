package vn.icheck.android.component.image_video_slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import kotlinx.android.synthetic.main.ic_image_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.screen.user.page_details.PageDetailActivity

class ImageHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(mediaLogic: MediaLogic, listMedia: List<MediaLogic>, onClick: (() -> Unit)?) {
        val circularProgressDrawable = CircularProgressDrawable(view.context)
        circularProgressDrawable.strokeWidth = 8f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        ICheckApplication.currentActivity()?.let {
            if (it is PageDetailActivity) {
                if (mediaLogic.bgDefault==null) {
                    view.src_image.scaleType = ImageView.ScaleType.CENTER_CROP
                }else{
                    view.src_image.scaleType = ImageView.ScaleType.FIT_XY
                }
            } else {
                view.src_image.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
        if (mediaLogic.bgDefault == null) {
            Glide.with(view.context.applicationContext)
                    .load(mediaLogic.src)
                    .transform(FitCenter())
                    .error(mediaLogic.idHolder ?: R.drawable.bg_error_emty_attachment)
                    .placeholder(circularProgressDrawable)
                    .into(view.src_image)
        } else {
            view.src_image.setBackgroundResource(mediaLogic.bgDefault!!)
        }
        view.root.setOnClickListener {
            onClick?.invoke()
            val list = mutableListOf<ICMedia>()
            for (media in listMedia) {
                if (!media.src.isNullOrEmpty()) {
                    list.add(ICMedia(media.src, if (media.type == ICMediaType.TYPE_IMAGE) {
                        Constant.IMAGE
                    } else {
                        Constant.VIDEO
                    }))
                }
            }

            if (!list.isNullOrEmpty()) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_FULL_MEDIA, list))
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): ImageHolder {
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.ic_image_holder, parent, false)
            return ImageHolder(v)
        }
    }
}