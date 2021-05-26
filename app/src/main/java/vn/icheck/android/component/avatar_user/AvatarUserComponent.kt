package vn.icheck.android.component.avatar_user

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class AvatarUserComponent : LinearLayout {
    var avatarSize = SizeHelper.size40
    var rankSize = SizeHelper.size16

    constructor(context: Context, avatarSize: Int, rankSize: Int) : super(context) {
        this.avatarSize = avatarSize
        this.rankSize = rankSize
        setUpView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpView(attrs)
    }

    private fun setUpView(attrs: AttributeSet? = null) {
        orientation = VERTICAL
        gravity = Gravity.END
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            elevation = 4f
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarUserComponent, 0, 0)

        addView(AppCompatImageView(context).also {
            if (typedArray.hasValue(R.styleable.AvatarUserComponent_rankSize)) {
                val size = typedArray.getDimensionPixelSize(R.styleable.AvatarUserComponent_rankSize, 0)
                it.layoutParams = ViewHelper.createLayoutParams(size, size)
                rankSize = size
            } else {
                it.layoutParams = ViewHelper.createLayoutParams(rankSize, rankSize)
            }
        })

        addView(CircleImageView(context).also { imgAvatar ->
            if (typedArray.hasValue(R.styleable.AvatarUserComponent_avatarSize)) {
                val size = typedArray.getDimensionPixelSize(R.styleable.AvatarUserComponent_avatarSize, 0)
                imgAvatar.layoutParams = ViewHelper.createLayoutParams(size, size, 0, -(rankSize / 2), 0, 0)
                imgAvatar.setBorderWidth(SizeHelper.size0_5)
                imgAvatar.borderColor=ContextCompat.getColor(context,R.color.colorLineView)
            } else {
                imgAvatar.layoutParams = ViewHelper.createLayoutParams(avatarSize, avatarSize, 0, -(rankSize / 2), 0, 0)
            }
        })

    }

    fun setData(avatar: String?, level: String?) {
        (getChildAt(0) as AppCompatImageView).run {
            when (level) {
                "silver" -> {
                    setImageResource(R.drawable.ic_avatar_rank_silver_16dp)
                }
                "gold" -> {
                    setImageResource(R.drawable.ic_avatar_rank_gold_16dp)

                }
                "dimond" -> {
                    setImageResource(R.drawable.ic_avatar_rank_diamond_16dp)
                }
                else -> {
                    setImageResource(R.drawable.ic_avatar_rank_standard_16dp)
                }
            }
        }

        (getChildAt(1) as CircleImageView).run {
            WidgetUtils.loadImageUrl(this, avatar, R.drawable.ic_circle_avatar_default)
        }


    }

    fun setData(avatar: String?, level: Int?, errorHolder: Int, page: Boolean = false) {
        (getChildAt(0) as AppCompatImageView).run {
            if (page) {
                visibility = View.INVISIBLE
            } else {
                visibility = View.VISIBLE
                if (level == null) {
                    setImageResource(0)
                } else {
                    when (level) {
                        Constant.USER_LEVEL_STANDARD -> {
                            setImageResource(R.drawable.ic_avatar_rank_standard_16dp)
                        }
                        Constant.USER_LEVEL_SILVER -> {
                            setImageResource(R.drawable.ic_avatar_rank_silver_16dp)
                        }
                        Constant.USER_LEVEL_GOLD -> {
                            setImageResource(R.drawable.ic_avatar_rank_gold_16dp)
                        }
                        Constant.USER_LEVEL_DIAMOND -> {
                            setImageResource(R.drawable.ic_avatar_rank_diamond_16dp)
                        }
                        else -> {
                            setImageResource(0)
                        }
                    }
                }

            }

            (getChildAt(1) as CircleImageView).run {
                WidgetUtils.loadImageUrl(this, avatar, errorHolder)
            }
        }
    }

}