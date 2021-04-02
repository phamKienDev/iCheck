package vn.icheck.android.component.take_media

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class TakeMediaAdapter(val listData: MutableList<TakeMediaDialog.ICIMageFile>, val selectMulti: Boolean = false, val isVideo: Boolean, val disableTakeImage:Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cameraType = 1
    private val imageType = 2

    private val listSelected = mutableListOf<TakeMediaDialog.ICIMageFile>()
    private var clickPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == cameraType) {
            CameraHolder(parent)
        } else {
            ImageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageHolder) {
            holder.bind(listData[position])
        } else if (holder is CameraHolder) {
            holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!disableTakeImage) {
            if (position == 0) {
                cameraType
            } else {
                imageType
            }
        } else {
            imageType
        }
    }

    fun getListSelected(): MutableList<TakeMediaDialog.ICIMageFile> {
        return listSelected
    }


    inner class ImageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createImageHolder(parent)) {
        lateinit var image: AppCompatImageView
        lateinit var bgCover: View
        lateinit var imgChecked: View
        lateinit var tvCount: AppCompatTextView
        lateinit var tvDuration: AppCompatTextView

        fun bind(obj: TakeMediaDialog.ICIMageFile) {
            (itemView as ViewGroup).run {
                image = getChildAt(0) as AppCompatImageView
                bgCover = getChildAt(1) as View
                imgChecked = getChildAt(2) as View
                tvCount = getChildAt(3) as AppCompatTextView
                tvDuration = getChildAt(4) as AppCompatTextView

                WidgetUtils.loadImageFileNotRounded(image, obj.src)

                if (obj.type == 3) {
                    if (obj.duration > 0) {
                        tvDuration.beVisible()
                        tvDuration.text = TimeHelper.convertMillisecondToTime(obj.duration)
                    } else {
                        tvDuration.beGone()
                    }
                } else {
                    tvDuration.beGone()
                }

                bgCover.visibility = if (obj.selected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }


                if (!selectMulti) {
                    imgChecked.visibility = if (obj.selected) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                } else {
                    imgChecked.visibility = View.INVISIBLE
                    if (listSelected.isNotEmpty()) {
                        for (i in 0 until listSelected.size) {
                            if (listSelected[i] == obj) {
                                obj.position = i
                            }
                        }
                    }
                    if (obj.position != -1 && obj.selected) {
                        tvCount.text = (obj.position + 1).toString()
                    } else {
                        tvCount.text = ""
                    }
                }


                itemView.setOnClickListener {
                    if (!selectMulti) {
                        listSelected.clear()
                        if (obj.selected) {
                            obj.selected = false
                        } else {
                            if (clickPosition != -1) {
                                listData[clickPosition].selected = false
                            }
                            obj.selected = true
                            listSelected.add(obj)
                        }
                        clickPosition = adapterPosition
                    } else {
                        if (obj.selected) {
                            listSelected.remove(obj)
                            obj.selected = false
                        } else {
                            obj.selected = true
                            listSelected.add(obj)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun createImageHolder(parent: ViewGroup): View {
        val layoutParent = ConstraintLayout(parent.context)
        layoutParent.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
            it.setMargins(SizeHelper.size1, SizeHelper.size1, SizeHelper.size1, SizeHelper.size1)
        }
        layoutParent.id = R.id.layoutParent

        val image = AppCompatImageView(parent.context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(0, 0)
            it.id = R.id.imgGift
            it.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        layoutParent.addView(image)

        val view = AppCompatImageView(parent.context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(0, 0)
            it.id = R.id.bgCover
            it.setImageResource(R.drawable.bg_choose_image_dialog)
        }
        layoutParent.addView(view)

        val checkbox = AppCompatImageView(parent.context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            it.id = R.id.imgChecked
            it.setImageResource(R.drawable.ic_square_checked_light_blue_24dp)
        }
        layoutParent.addView(checkbox)

        val tvCount = AppCompatTextView(parent.context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(0, SizeHelper.size2, SizeHelper.size8, 0)
            }
            it.setTextColor(Color.WHITE)
            it.id = R.id.tvCount
            it.typeface = Typeface.createFromAsset(parent.context.assets, "font/barlow_semi_bold.ttf")
        }
        layoutParent.addView(tvCount)

        val tvDuration = AppCompatTextView(parent.context).also {
            it.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(0, 0, SizeHelper.size4, SizeHelper.size6)
            }
            it.id = R.id.tvTimeDuration
            it.typeface = Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
            it.textSize = 11f
            it.setPadding(SizeHelper.size3)
            it.setTextColor(Color.WHITE)
            it.background = ContextCompat.getDrawable(parent.context, R.drawable.bg_black_45_corner_10)
        }
        layoutParent.addView(tvDuration)

        val contrainSet = ConstraintSet()
        contrainSet.clone(layoutParent)

        contrainSet.connect(image.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        contrainSet.connect(image.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        contrainSet.connect(image.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        contrainSet.connect(image.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)

        contrainSet.connect(view.id, ConstraintSet.START, layoutParent.id, ConstraintSet.START)
        contrainSet.connect(view.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        contrainSet.connect(view.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        contrainSet.connect(view.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)

        contrainSet.connect(tvDuration.id, ConstraintSet.BOTTOM, layoutParent.id, ConstraintSet.BOTTOM)
        contrainSet.connect(tvDuration.id, ConstraintSet.END, layoutParent.id, ConstraintSet.END)

        contrainSet.connect(checkbox.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        contrainSet.connect(checkbox.id, ConstraintSet.RIGHT, layoutParent.id, ConstraintSet.RIGHT)

        contrainSet.connect(tvCount.id, ConstraintSet.TOP, layoutParent.id, ConstraintSet.TOP)
        contrainSet.connect(tvCount.id, ConstraintSet.RIGHT, layoutParent.id, ConstraintSet.RIGHT)

        contrainSet.setDimensionRatio(image.id, "W, 1:1")
        contrainSet.applyTo(layoutParent)

        return layoutParent
    }

    inner class CameraHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(createCameraHolder(parent)) {
        fun bind() {
            (itemView as ViewGroup).run {
                setOnClickListener {
                    TakeMediaDialog.INSTANCE?.startCamera()
                }

                (getChildAt(1) as AppCompatTextView).run {
                    if (isVideo) {
                        setText(R.string.chup_anh_video)
                    } else {
                        setText(R.string.chup_anh)
                    }
                }
            }
        }
    }

    fun createCameraHolder(parent: ViewGroup): View {
        val layoutParent = LinearLayout(parent.context)
        layoutParent.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.gravity = Gravity.CENTER
        layoutParent.setBackgroundColor(ContextCompat.getColor(parent.context, R.color.darkGray6))

        layoutParent.addView(AppCompatImageView(parent.context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            it.background = ContextCompat.getDrawable(parent.context, R.drawable.ic_camera_off_24px)
        })

        layoutParent.addView(AppCompatTextView(parent.context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.topMargin = SizeHelper.size2
            }
            it.text = parent.context.getString(R.string.chup_anh)
            it.setTextColor(ContextCompat.getColor(parent.context, R.color.darkGray2))
            it.typeface = Typeface.createFromAsset(parent.context.assets, "font/barlow_medium.ttf")
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        })

        return layoutParent
    }
}