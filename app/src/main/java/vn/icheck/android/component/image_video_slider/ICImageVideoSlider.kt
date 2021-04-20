package vn.icheck.android.component.image_video_slider

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yarolegovich.discretescrollview.DiscreteScrollView
import kotlinx.android.synthetic.main.ick_product_detail_attachments.view.*
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.ACTION_PRODUCT_DETAIL
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.POSITION
import vn.icheck.android.constant.SHOW_ATTACHMENTS
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.logError

class ICImageVideoSlider(val view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var icImageVideoAdapter: ICImageVideoAdapter
    var posImage = 0

    fun bind(icImageVideoSliderModel: ICImageVideoSliderModel) {
        if (icImageVideoSliderModel.getPageId != null) {
            view.indicator.beGone()
        } else {
            view.indicator.beVisible()
            view.indicator.removeAllViews()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                initIndicator(icImageVideoSliderModel.listSrc.size)
            }
        }

        icImageVideoSliderModel.coroutineScope.coroutineContext.cancelChildren()
//        val listVideo = icImageVideoSliderModel.listSrc.filter {
//            it.type == ICMediaType.TYPE_VIDEO
//        }
//        val arr = arrayListOf<MediaSource>()
//        if (!listVideo.isNullOrEmpty()) {
//            for (item in listVideo) {
//                item.progressiveMediaSource?.let { arr.add(it) }
//            }
//        }
//        val con = ConcatenatingMediaSource(*arr.toTypedArray())
//        icImageVideoSliderModel.exoPlayer.prepare(con, false, false)
        icImageVideoAdapter = ICImageVideoAdapter(icImageVideoSliderModel.listSrc) {
            view.context.sendBroadcast(Intent(ACTION_PRODUCT_DETAIL).apply {
                putExtra(ACTION_PRODUCT_DETAIL, SHOW_ATTACHMENTS)
                putExtra(POSITION, posImage)
            })
        }
        if (ICheckApplication.currentActivity() is PageDetailActivity) {
            view.rcv_slider.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(200))
        } else {
            view.rcv_slider.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(350))
        }
        view.rcv_slider.adapter = icImageVideoAdapter
//        if (icImageVideoSliderModel.listSrc.isEmpty()) {
//            view.img_no_img?.beVisible()
//            view.tv_no_img?.beVisible()
//        } else {
//            view.img_no_img?.beGone()
//            view.tv_no_img?.beGone()
//        }
//        view.rcv_slider.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        try {
            view.rcv_slider.addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
                override fun onScroll(p0: Float, p1: Int, p2: Int, p3: RecyclerView.ViewHolder?, p4: RecyclerView.ViewHolder?) {
                }

                override fun onScrollEnd(p0: RecyclerView.ViewHolder, p1: Int) {
                    try {
                        val newSelect = view.indicator.getChildAt(p1)
                        newSelect.setBackgroundResource(R.drawable.bg_indicator_selected)
                        posImage = p1
                    } catch (e: Exception) {
                    }
                }

                override fun onScrollStart(p0: RecyclerView.ViewHolder, p1: Int) {
                    try {
                        val oldSelect = view.indicator.getChildAt(p1)
                        oldSelect.setBackgroundResource(R.drawable.bg_indicator_not_select)
                    } catch (e: Exception) {
                    }
                }
            })
//            coroutineScope.cancel()
            icImageVideoSliderModel.coroutineScope.launch {
                while (true) {
                    delay(3000)
                    if (view.rcv_slider.childCount > 0) {
                        if (view.rcv_slider.currentItem < icImageVideoSliderModel.listSrc.size - 1) {
                            view.rcv_slider.smoothScrollToPosition(view.rcv_slider.currentItem + 1)
                        } else {
                            view.rcv_slider.smoothScrollToPosition(0)
                        }
                    }
                }
            }
//            if (view.rcv_slider.itemDecorationCount == 0) {
//                view.rcv_slider.addItemDecoration(LinePagerIndicatorDecoration())
//            }
        } catch (e: Exception) {
            logError(e)
        }

        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myOwnerPageIdList, (icImageVideoSliderModel.getPageId
                    ?: -1).toString(), object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null && snapshot.value is Long) {
                        view.imgEditCover.beVisible()
                    } else {
                        view.imgEditCover.beGone()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } else {
            view.imgEditCover.beGone()
        }

        view.imgEditCover.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.TAKE_IMAGE_DIALOG, ICViewTypes.IMAGE_VIDEO_SLIDER))
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun initIndicator(size: Int) {
        val lf = LayoutInflater.from(view.context)
        for (i in 0 until size) {
            val v = lf.inflate(R.layout.indicator_not_select, view.indicator, false)
            val lm = v.layoutParams as LinearLayout.LayoutParams
            lm.weight = DimensionUtil.convertDpToPixel(1f, view.context)
            lm.marginStart = DimensionUtil.convertDpToPixel(12f, view.context).toInt()
            if (i == size - 1) {
                lm.marginEnd = DimensionUtil.convertDpToPixel(12f, view.context).toInt()
            }
            if (i == 0) {
                v.setBackgroundResource(R.drawable.bg_indicator_selected)
            }
            v.layoutParams = lm
            view.indicator.addView(v)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ICImageVideoSlider {
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.image_video_slider_component, parent, false)
            return ICImageVideoSlider(v)
        }

        fun createAttachments(parent: ViewGroup, recycledViewPool: RecyclerView.RecycledViewPool?, height: Int = SizeHelper.dpToPx(350)): ICImageVideoSlider {
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.ick_product_detail_attachments, parent, false)
            if (recycledViewPool != null) {
                v.findViewById<DiscreteScrollView>(R.id.rcv_slider).apply {
                    layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, height)
                    this.setRecycledViewPool(recycledViewPool)
                }
            }
            return ICImageVideoSlider(v)
        }
    }

}