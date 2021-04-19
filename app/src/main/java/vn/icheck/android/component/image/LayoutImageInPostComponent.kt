package vn.icheck.android.component.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class LayoutImageInPostComponent : ConstraintLayout {

    private lateinit var imgOne: AppCompatImageView
    private lateinit var imgTwo: AppCompatImageView
    private lateinit var imgThree: AppCompatImageView

    private lateinit var imagePlay: AppCompatImageView
    private lateinit var imagePlay1: AppCompatImageView
    private lateinit var imagePlay2: AppCompatImageView

    private lateinit var imgBG: AppCompatImageView
    private lateinit var imgBG1: AppCompatImageView
    private lateinit var imgBG2: AppCompatImageView

    private lateinit var viewImage: View
    private lateinit var text: AppCompatTextView

    private lateinit var listData: MutableList<ICImageInPost>

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }


    /**
     * Nếu mọi người dùng thằng này thì nhớ check list != null or Empty trước khi truyền vào giúp em với nhé
     */
    fun setImageInPost(list: MutableList<ICImageInPost>) {
        id = R.id.constraintLayout

        removeAllViews()

        imgOne = AppCompatImageView(context)
        imgTwo = AppCompatImageView(context)
        imgThree = AppCompatImageView(context)

        imagePlay = AppCompatImageView(context)
        imagePlay1 = AppCompatImageView(context)
        imagePlay2 = AppCompatImageView(context)

        imgBG = AppCompatImageView(context)
        imgBG1 = AppCompatImageView(context)
        imgBG2 = AppCompatImageView(context)

        viewImage = View(context)
        text = AppCompatTextView(context)

        listData = list

        showImageCropCenter(list)
    }

    fun onClickImageDetail(listener: ItemClickListener<MutableList<ICImageInPost>>) {
        imgOne.setOnClickListener {
            listener.onItemClick(0, listData)
        }

        imgTwo.setOnClickListener {
            listener.onItemClick(1, listData)
        }

        imgThree.setOnClickListener {
            listener.onItemClick(2, listData)
        }
    }

    /**
     * Create View
     */
    private fun showImageCropCenter(list: MutableList<ICImageInPost>) {
        when (list.size) {
            1 -> {
                imgOne.id = R.id.imageView
                imgOne.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgOne.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgOne, list[0].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)
                addView(imgOne)


                if (list[0].type == "video") {
                    addView(setImageOpacity50(imgBG, R.id.imgBG))
                    addView(setUpPlay(imagePlay, R.id.imagePlay))

                    val imageSet = ConstraintSet()
                    imageSet.clone(this)
                    imageSet.connect(imgOne.id, ConstraintSet.START, id, ConstraintSet.START)
                    imageSet.connect(imgOne.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                    imageSet.connect(imgOne.id, ConstraintSet.END, id, ConstraintSet.END)
                    imageSet.connect(imgOne.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)

                    imageSet.connect(imgBG.id, ConstraintSet.START, imgOne.id, ConstraintSet.START)
                    imageSet.connect(imgBG.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                    imageSet.connect(imgBG.id, ConstraintSet.END, imgOne.id, ConstraintSet.END)
                    imageSet.connect(imgBG.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                    imageSet.connect(imagePlay.id, ConstraintSet.START, imgBG.id, ConstraintSet.START)
                    imageSet.connect(imagePlay.id, ConstraintSet.TOP, imgBG.id, ConstraintSet.TOP)
                    imageSet.connect(imagePlay.id, ConstraintSet.END, imgBG.id, ConstraintSet.END)
                    imageSet.connect(imagePlay.id, ConstraintSet.BOTTOM, imgBG.id, ConstraintSet.BOTTOM)

                    imageSet.setDimensionRatio(imgOne.id, "H, 1:1")
                    imageSet.applyTo(this)
                } else {
                    val imageSet = ConstraintSet()
                    imageSet.clone(this)
                    imageSet.connect(imgOne.id, ConstraintSet.START, id, ConstraintSet.START)
                    imageSet.connect(imgOne.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                    imageSet.connect(imgOne.id, ConstraintSet.END, id, ConstraintSet.END)
                    imageSet.connect(imgOne.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)

                    imageSet.setDimensionRatio(imgOne.id, "H, 1:1")
                    imageSet.applyTo(this)
                }

            }
            2 -> {
                imgOne.id = R.id.imgOne
                imgOne.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgOne.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgOne, list[0].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                imgTwo.id = R.id.imgTwo
                imgTwo.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(SizeHelper.size5, 0, 0, 0)
                }
                imgTwo.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgTwo, list[1].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                addView(imgOne)
                addView(imgTwo)

                if (list[0].type == "video") {
                    addView(setImageOpacity50(imgBG, R.id.imgBG))
                    addView(setUpPlay(imagePlay, R.id.imagePlay))
                }

                if (list[1].type == "video") {
                    addView(setImageOpacity50(imgBG1, R.id.imgBG1))
                    addView(setUpPlay(imagePlay1, R.id.imagePlay1))
                }

                val imageSet = ConstraintSet()
                imageSet.clone(this)

                imageSet.connect(imgOne.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imgOne.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imgOne.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)
                imageSet.connect(imgOne.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)

                imageSet.connect(imgBG1.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgBG1.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.TOP)
                imageSet.connect(imgBG1.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imgBG1.id, ConstraintSet.BOTTOM, imgTwo.id, ConstraintSet.BOTTOM)

                imageSet.connect(imagePlay.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imagePlay.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imagePlay.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)
                imageSet.connect(imagePlay.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)

                imageSet.connect(imagePlay1.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imagePlay1.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imagePlay1.id, ConstraintSet.END, id, ConstraintSet.END)
                imageSet.connect(imagePlay1.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.setDimensionRatio(imgOne.id, "H, 1:1")
                imageSet.setDimensionRatio(imgTwo.id, "H, 1:1")

                imageSet.connect(imgTwo.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imgTwo.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgTwo.id, ConstraintSet.END, id, ConstraintSet.END)
                imageSet.connect(imgTwo.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgBG.id, ConstraintSet.START, imgOne.id, ConstraintSet.START)
                imageSet.connect(imgBG.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgBG.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)
                imageSet.connect(imgBG.id, ConstraintSet.END, imgOne.id, ConstraintSet.END)
                imageSet.applyTo(this)
            }
            3 -> {
                imgOne.id = R.id.imgOne
                imgOne.layoutParams = LayoutParams(0, SizeHelper.dpToPx(230)).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgOne.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgOne, list[0].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                imgTwo.id = R.id.imgTwo
                imgTwo.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(SizeHelper.size5, 0, 0, 0)
                }
                imgTwo.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgTwo, list[1].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                imgThree.id = R.id.imgThree
                imgThree.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgThree.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgThree, list[2].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                addView(imgOne)
                addView(imgTwo)
                addView(imgThree)

                if (list[0].type == "video") {
                    addView(setImageOpacity50(imgBG, R.id.imgBG))
                    addView(setUpPlay(imagePlay, R.id.imagePlay))
                }

                if (list[1].type == "video") {
                    addView(setImageOpacity50(imgBG1, R.id.imgBG1))
                    addView(setUpPlay(imagePlay1, R.id.imagePlay1))
                }

                if (list[2].type == "video") {
                    addView(setImageOpacity50(imgBG2, R.id.imgBG2))
                    addView(setUpPlay(imagePlay2, R.id.imagePlay2))
                }

                val imageSet = ConstraintSet()
                imageSet.clone(this)
                imageSet.connect(imgOne.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imgOne.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imgOne.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgOne.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgTwo.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imgTwo.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgTwo.id, ConstraintSet.END, id, ConstraintSet.END)

                imageSet.connect(imgThree.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imgThree.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgThree.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imgThree.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(imagePlay.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imagePlay.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imagePlay.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imagePlay.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)

                imageSet.connect(imagePlay1.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imagePlay1.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imagePlay1.id, ConstraintSet.BOTTOM, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imagePlay1.id, ConstraintSet.END, id, ConstraintSet.END)

                imageSet.connect(imagePlay2.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imagePlay2.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imagePlay2.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imagePlay2.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.constrainPercentWidth(imgOne.id, 0.65F)
                imageSet.setDimensionRatio(imgTwo.id, "H, 1:1")
//                imageSet.setDimensionRatio(imgThree.id, "H, 1:1")

                imageSet.connect(imgBG.id, ConstraintSet.START, imgOne.id, ConstraintSet.START)
                imageSet.connect(imgBG.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgBG.id, ConstraintSet.END, imgOne.id, ConstraintSet.END)
                imageSet.connect(imgBG.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgBG1.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgBG1.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.TOP)
                imageSet.connect(imgBG1.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imgBG1.id, ConstraintSet.BOTTOM, imgTwo.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgBG2.id, ConstraintSet.TOP, imgThree.id, ConstraintSet.TOP)
                imageSet.connect(imgBG2.id, ConstraintSet.START, imgThree.id, ConstraintSet.START)
                imageSet.connect(imgBG2.id, ConstraintSet.END, imgThree.id, ConstraintSet.END)
                imageSet.connect(imgBG2.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)

                imageSet.applyTo(this)
            }
            else -> {
                imgOne.id = R.id.imgOne
                imgOne.layoutParams = LayoutParams(0, SizeHelper.dpToPx(230)).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgOne.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgOne, list[0].src, R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck,SizeHelper.size4)

                imgTwo.id = R.id.imgTwo
                imgTwo.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(SizeHelper.size5, 0, 0, 0)
                }
                imgTwo.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgTwo, list[1].src,R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                imgThree.id = R.id.imgThree
                imgThree.layoutParams = LayoutParams(0, 0).also {
                    it.setMargins(0, SizeHelper.size5, 0, 0)
                }
                imgThree.scaleType=ImageView.ScaleType.FIT_XY
                WidgetUtils.loadImageUrlRounded(imgThree, list[2].src,R.drawable.img_default_loading_icheck,R.drawable.img_default_loading_icheck, SizeHelper.size4)

                createViewAndTextVertical(list)

                addView(imgOne)
                if (list[0].type == "video") {
                    addView(setImageOpacity50(imgBG, R.id.imgBG))
                    addView(setUpPlay(imagePlay, R.id.imagePlay))
                }

                addView(imgTwo)
                if (list[1].type == "video") {
                    addView(setImageOpacity50(imgBG1, R.id.imgBG1))
                    addView(setUpPlay(imagePlay1, R.id.imagePlay1))
                }
                addView(imgThree)

                if (list[2].type == "video") {
                    addView(setImageOpacity50(imgBG2, R.id.imgBG2))
                    addView(setUpPlay(imagePlay2, R.id.imagePlay2))
                }

                addView(viewImage)
                addView(text)

                val imageSet = ConstraintSet()
                imageSet.clone(this)
                imageSet.connect(imgOne.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imgOne.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imgOne.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgOne.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgTwo.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imgTwo.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgTwo.id, ConstraintSet.END, id, ConstraintSet.END)

                imageSet.connect(imgThree.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imgThree.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgThree.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imgThree.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(imagePlay.id, ConstraintSet.START, id, ConstraintSet.START)
                imageSet.connect(imagePlay.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                imageSet.connect(imagePlay.id, ConstraintSet.END, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imagePlay.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)

                imageSet.connect(imagePlay1.id, ConstraintSet.START, imgOne.id, ConstraintSet.END)
                imageSet.connect(imagePlay1.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imagePlay1.id, ConstraintSet.BOTTOM, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imagePlay1.id, ConstraintSet.END, id, ConstraintSet.END)

                imageSet.connect(imagePlay2.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(imagePlay2.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imagePlay2.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imagePlay2.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(viewImage.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(viewImage.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(viewImage.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(viewImage.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(text.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.BOTTOM)
                imageSet.connect(text.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(text.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(text.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.constrainPercentWidth(imgOne.id, 0.65F)
                imageSet.setDimensionRatio(imgTwo.id, "H, 1:1")
                imageSet.setDimensionRatio(imgThree.id, "H, 1:1")
                imageSet.setDimensionRatio(viewImage.id, "H, 1:1")
                imageSet.setDimensionRatio(text.id, "H, 1:1")

                imageSet.connect(imgBG.id, ConstraintSet.START, imgOne.id, ConstraintSet.START)
                imageSet.connect(imgBG.id, ConstraintSet.TOP, imgOne.id, ConstraintSet.TOP)
                imageSet.connect(imgBG.id, ConstraintSet.END, imgOne.id, ConstraintSet.END)
                imageSet.connect(imgBG.id, ConstraintSet.BOTTOM, imgOne.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgBG1.id, ConstraintSet.START, imgTwo.id, ConstraintSet.START)
                imageSet.connect(imgBG1.id, ConstraintSet.TOP, imgTwo.id, ConstraintSet.TOP)
                imageSet.connect(imgBG1.id, ConstraintSet.END, imgTwo.id, ConstraintSet.END)
                imageSet.connect(imgBG1.id, ConstraintSet.BOTTOM, imgTwo.id, ConstraintSet.BOTTOM)

                imageSet.connect(imgBG2.id, ConstraintSet.TOP, imgThree.id, ConstraintSet.TOP)
                imageSet.connect(imgBG2.id, ConstraintSet.START, imgThree.id, ConstraintSet.START)
                imageSet.connect(imgBG2.id, ConstraintSet.END, imgThree.id, ConstraintSet.END)
                imageSet.connect(imgBG2.id, ConstraintSet.BOTTOM, imgThree.id, ConstraintSet.BOTTOM)
                imageSet.applyTo(this)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createViewAndTextVertical(list: MutableList<ICImageInPost>) {
        viewImage.id = R.id.viewImage
        viewImage.layoutParams = LayoutParams(0, 0).also {
            it.setMargins(0, SizeHelper.size5, 0, 0)
        }

        viewImage.setBackgroundResource(R.drawable.bg_black_30_corner_4)

        text.id = R.id.textView
        text.layoutParams = LayoutParams(0, 0).also {
            it.setMargins(0, SizeHelper.size5, 0, 0)
        }
        text.text = "+${list.size - 2} ảnh"
        text.setTextColor(Color.parseColor("#FFFFFF"))
        text.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
        text.includeFontPadding = false
        text.gravity = Gravity.CENTER
        text.textSize = 18F
    }

    private fun setUpPlay(play: AppCompatImageView, id: Int): AppCompatImageView {
        play.id = id
        play.layoutParams = LayoutParams(SizeHelper.size40, SizeHelper.size40)

        play.setImageResource(R.drawable.ic_play_40dp)

        return play
    }

    private fun setImageOpacity50(image: AppCompatImageView, id: Int): AppCompatImageView {
        image.id = id
        image.layoutParams = LayoutParams(0, 0)

        image.setImageResource(R.drawable.bg_black_20_corners_4)

        return image
    }
    /**
     * End Create View
     */
}