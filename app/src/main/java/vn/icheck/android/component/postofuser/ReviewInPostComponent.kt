package vn.icheck.android.component.postofuser

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.component.avatar_user.AvatarUserComponent
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.rating_star.RatingStarComponent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.post.ICImageInPost
import vn.icheck.android.network.models.post.ICProductInPost

class ReviewInPostComponent : LinearLayout {
    constructor(context: Context) : super(context) {
        setUpView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        setUpView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpView()
    }

    fun setUpView() {
        orientation = VERTICAL
        background =vn.icheck.android.ichecklibs.ViewHelper.bgWhiteStrokeLineColor1(context)
        setPadding(SizeHelper.size4, SizeHelper.size4, SizeHelper.size4, SizeHelper.size4)

        addView(LinearLayout(context).also { headerLayout ->
            headerLayout.layoutParams = ViewHelper.createLayoutParams()
            headerLayout.orientation = HORIZONTAL

            headerLayout.addView(AvatarUserComponent(context, SizeHelper.size30, SizeHelper.size12).also {
                it.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT)
            })

            headerLayout.addView(LinearLayout(context).also { infoHeaderLayout ->
                infoHeaderLayout.layoutParams = ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size8, SizeHelper.size6, 0, 0)
                }
                infoHeaderLayout.orientation = VERTICAL

                infoHeaderLayout.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, 0, 1f),
                        null,
                        ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                        ContextCompat.getColor(context, R.color.colorNormalText),
                        16f,
                        1).also {
                    it.gravity = Gravity.CENTER_VERTICAL
                })

                infoHeaderLayout.addView(RatingStarComponent(context))
            })
        })

        addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
                null,
                Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf"),
                ContextCompat.getColor(context, R.color.colorNormalText),
                14f,
                3).also {
            it.lineHeight = SizeHelper.size20
        })

        addView(LayoutImageInPostComponent(context).also {
            it.layoutParams=ViewHelper.createLayoutParams().also {layoutParams->
                layoutParams.setMargins(0,0,0,SizeHelper.size4)
            }
        })
        addView(ProductInPostComponent(context))
    }

    fun setData(review: ICProductReviews.ReviewsRow) {

        (getChildAt(0) as ViewGroup).run {
            (getChildAt(0) as AvatarUserComponent).run {
                setData("", "")
            }

            (getChildAt(1) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).run {
                    text = if (review.owner.name != null) {
                        review.owner.name
                    } else {
                        context.getString(R.string.dang_cap_nhat)
                    }
                }

                (getChildAt(1) as RatingStarComponent).run {
                    setData(9f, 9f)
                }
            }
        }

        (getChildAt(1) as AppCompatTextView).run {
            text = if (review.message.length > 135) {
                Html.fromHtml(context.getString(R.string.sponsor_feed_introtext, review.message.substring(0, 135)))
            } else {
                review.message
            }
        }

//        val media = ICImageInPost("https://picsum.photos/300/300", "image", null, 0)

        val media1 = ICImageInPost("https://firebasestorage.googleapis.com/v0/b/flickering-heat-5334.appspot.com/o/demo1.mp4?alt=media&token=f6d82bb0-f61f-45bc-ab13-16970c7432c4", "video", 3, 2)
        val list = mutableListOf<ICImageInPost>()
        list.add(media1)
        (getChildAt(2) as LayoutImageInPostComponent).run {
                setImageInPost(list)
        }

        val product = ICProductInPost(1, "ádfasdf", "ádfdsaf", "adsfsadfsadf", null, "")
        (getChildAt(3) as ProductInPostComponent).run {
            setData(product)
        }
    }
}