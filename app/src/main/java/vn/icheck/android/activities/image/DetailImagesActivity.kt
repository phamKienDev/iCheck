package vn.icheck.android.activities.image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_image_holder.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.activities.base.BaseICActivity
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.POSITION
import vn.icheck.android.databinding.ActivityDetailImagesBinding
import vn.icheck.android.databinding.SingleImageBinding
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.util.ick.loadSimpleImage
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.ui.GlideUtil

class DetailImagesActivity : AppCompatActivity() {

    lateinit var imageSliderAdapter: ImageSliderAdapter
    val listData = mutableListOf<String>()
    lateinit var rcvMain: RecyclerView
    lateinit var binding: ActivityDetailImagesBinding
    lateinit var singleImageBinding: SingleImageBinding
    var pos:Int = 0

    companion object {
        private const val DATA = "data"
        var instance: DetailImagesActivity? = null

        fun start(listUrl: ArrayList<String?>, context: Context) {
            val startIntent = Intent(context, DetailImagesActivity::class.java)
            startIntent.putExtra(DATA, listUrl)
            startIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(startIntent)
        }

        fun start(listUrl: ArrayList<String?>, activity: Activity, pos: Int = 0) {
            val startIntent = Intent(activity, DetailImagesActivity::class.java)
            startIntent.putExtra(DATA, listUrl)
            startIntent.putExtra(POSITION, pos)
            startIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            ActivityUtils.startActivity(activity, startIntent)
        }
        var isZoomed = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pos = intent.getIntExtra(POSITION, 0)
        instance = this
        listData.addAll(intent.getStringArrayListExtra(DATA))

        if (listData.size > 1) {
            binding = ActivityDetailImagesBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val rcvThumbs = binding.rcvThumbs
            rcvMain = binding.rcvMain
            imageSliderAdapter = ImageSliderAdapter(listData)
            rcvMain.adapter = imageSliderAdapter
            binding.btnBack.setOnClickListener {
                finish()
            }
//            rcvThumbs.adapter = ThumbSliderAdapter(listData)
//            rcvThumbs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rcvMain.post {
                binding.rcvMain.scrollToPosition(pos)
            }
        } else {
            singleImageBinding = SingleImageBinding.inflate(layoutInflater)
            setContentView(singleImageBinding.root)
            val img = singleImageBinding.imgGift
            GlideUtil.loading(listData.firstOrNull(), img)
            singleImageBinding.btnBack.setOnClickListener {
                finish()
            }
        }
        EventBus.getDefault().register(this)
    }

    fun onThumbChildClick(position: Int) {
        rcvMain.smoothScrollToPosition(position)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(data: HashMap<String, Any?>) {
        rcvMain.post {
            rcvMain.suppressLayout(data.get("isZoomed") as Boolean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    class ImageSliderAdapter(val listData: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return DetailImageHolder.create(parent)
        }

        override fun getItemCount(): Int = listData.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as DetailImageHolder).bind(listData[position])
        }

        class DetailImageHolder(view: View) : BaseHolder(view) {

            fun bind(url: String) {
                GlideUtil.loading(url, getImg(R.id.img_detail))
//                if (URLUtil.isValidUrl(url)) {
//                    GlideUtil.loading(url, getImg(R.id.img_detail))
//                } else {
//                    GlideUtil.loading(ImageHelper.getImageUrl(url, ImageHelper.originalSize), getImg(R.id.img_detail))
//                }
                itemView.img_detail.setOnTouchImageViewListener {
                    EventBus.getDefault().post(hashMapOf("isZoomed" to itemView.img_detail.isZoomed))
                }
            }

            companion object {
                fun create(parent: ViewGroup): DetailImageHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.detail_image_holder, parent, false)
                    return DetailImageHolder(view)
                }
            }
        }
    }

    class ThumbSliderAdapter(val listData: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ThumbImageHolder.create(parent)
        }

        override fun getItemCount(): Int = listData.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ThumbImageHolder).bind(listData[position])
        }

        class ThumbImageHolder(view: View) : BaseHolder(view) {

            fun bind(url: String) {
                GlideUtil.loading(url, getImg(R.id.img_detail))
//                if (URLUtil.isValidUrl(url)) {
//                    GlideUtil.loading(url, getImg(R.id.img_detail))
//                } else {
//                    GlideUtil.loading(ImageHelper.getImageUrl(url, ImageHelper.thumbSmallSize), getImg(R.id.img_detail))
//                }
                setOnClick(R.id.img_detail, View.OnClickListener {
                    instance?.onThumbChildClick(adapterPosition)
                })
            }

            companion object {
                fun create(parent: ViewGroup): ThumbImageHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.thumb_image_holder, parent, false)
                    return ThumbImageHolder(view)
                }
            }
        }
    }
}
