package vn.icheck.android.component.view

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.feed.ICAvatarOfFriend
import vn.icheck.android.util.kotlin.WidgetUtils

class ListAvatar : LinearLayout {

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(obj: ICAvatarOfFriend, size: Int? = null, showMore: Boolean = true, error: Int = R.drawable.ic_avatar_default_84px) {

        removeAllViews()
        val mSize = size ?: 4 // set số lượng avatar tối đa được hiển thị

        layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL

        var marginStart = 0
        var elevation = 5f
        var remain = ""

        // pos0-5f , pos1-4f , pos2-3f , pos3-2f
        // Check list ko null hoặc k emty
        if (!obj.user.isNullOrEmpty()) {
            //Nếu list lớn hơn mSize thì hiển thị thêm 1 ảnh và view text
            if (obj.user?.size!! > mSize) {

                if (obj.total != null) {
                    remain = (obj.total!! - mSize).toString()
                }

                for (i in 0 until mSize) {
                    addView(CircleImageView(context).also { avatar ->
                        avatar.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size28, SizeHelper.size28, marginStart, 0, 0, 0)
                        avatar.borderColor = ContextCompat.getColor(context, R.color.gray)
                        avatar.borderWidth = SizeHelper.size0_5
                        avatar.elevation = elevation
                        WidgetUtils.loadImageUrl(avatar, obj.user!![i], error)
                    })
                    marginStart = -SizeHelper.size8
                    --elevation
                }
                addView(FrameLayout(context).also { frameLayout ->
                    frameLayout.layoutParams = ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, -SizeHelper.size8, 0, 0, 0)
                    frameLayout.elevation = elevation

                    frameLayout.addView(CircleImageView(context).also { avatar ->
                        avatar.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size28, SizeHelper.size28)
                        avatar.borderColor = ContextCompat.getColor(context, R.color.gray)
                        avatar.borderWidth = SizeHelper.size0_5
                        WidgetUtils.loadImageUrl(avatar, obj.user!![mSize], error)
                    })

                    if (showMore) {
                        frameLayout.addView(AppCompatTextView(context).also { text ->
                            text.layoutParams = ViewHelper.createLayoutParams()
                            text.gravity = Gravity.CENTER
                            text.typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                            text.background = ContextCompat.getDrawable(context, R.drawable.circle_view_opacvity_28)
                            text.setTextColor(ContextCompat.getColor(context, R.color.white))
                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                            text.text = "+$remain"
                        })
                    }
                })
            } else {
                //Nếu list nhỏ hơn 5 hoặc = 5 thì addview bình thường
                for (i in obj.user!!) {
                    addView(CircleImageView(context).also { avatar ->
                        avatar.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size28, SizeHelper.size28, marginStart, 0, 0, 0)
                        avatar.borderColor = ContextCompat.getColor(context, R.color.white)
                        avatar.borderWidth = SizeHelper.size1
                        avatar.elevation = elevation
                        WidgetUtils.loadImageUrl(avatar, i, error)
                    })
                    marginStart = -SizeHelper.size8
                    --elevation
                }
            }
        }
    }
}