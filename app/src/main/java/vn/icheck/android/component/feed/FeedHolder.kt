package vn.icheck.android.component.feed

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.avatar_user.AvatarUserComponent
import vn.icheck.android.component.image.LayoutImageInPostComponent
import vn.icheck.android.component.postofuser.ProductInFeedComponent
import vn.icheck.android.component.view.LayoutFeedAction
import vn.icheck.android.component.view.LayoutFeedComment
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICFeed
import vn.icheck.android.util.ick.rText

class FeedHolder(parent: ViewGroup) : BaseViewHolder<ICFeed>(ViewHelper.createFeed(parent.context)) {

    override fun bind(obj: ICFeed) {
        (itemView as ViewGroup).run {
            // Layout header
            (getChildAt(0) as ViewGroup).run {
                // Layout avatar
                (getChildAt(0) as AvatarUserComponent).run {
                    setData(obj.avatar?.medium, obj.user_level)

                    setOnClickListener {

                    }
                }

                // Text name
                (getChildAt(1) as AppCompatTextView).text = obj.user_name

                // Layout rating
                (getChildAt(2) as ViewGroup).run {
                    // Text rating
                    (getChildAt(0) as AppCompatTextView).run {
                        val totalRating = obj.rating * 2

                        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context)

                        when {
                            totalRating < 6 -> {
                                rText(R.string.diem_danh_gia)
                                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                            }
                            totalRating < 7 -> {
                                rText(R.string.hai_long)
                                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                            }
                            totalRating < 8 -> {
                                rText(R.string.tot)
                                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                            }
                            totalRating < 9 -> {
                                rText(R.string.tuyet_voi)
                                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.red_v2), SizeHelper.size14.toFloat())
                            }
                            else -> {
                                rText(R.string.x_tren_ca_tuyet_voi)
                                background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                            }
                        }
                    }

                    // Rating bar
                    (getChildAt(1) as AppCompatRatingBar).rating = obj.rating
                }

                // Image more
                (getChildAt(3) as AppCompatTextView).setOnClickListener {

                }
            }

            // Text content
            (getChildAt(1) as AppCompatTextView).text = obj.content

            // Layout image
            (getChildAt(2) as LayoutImageInPostComponent).run {

            }

            // Layout product
            (getChildAt(3) as ProductInFeedComponent).run {
//                setData(obj.product)

                setOnClickListener {

                }
            }

            // Layout action
            (getChildAt(4) as LayoutFeedAction).run {
                setData(obj.isLike)

                setOnLikeClick(View.OnClickListener {

                })

                setOnCommentClick(View.OnClickListener {

                })

                setOnShareClick(View.OnClickListener {

                })
            }

            // Layout comment
            (getChildAt(5) as LayoutFeedComment).run {
                setOnAvatarClick(View.OnClickListener {

                })

                setOnCommentClick(View.OnClickListener {

                })

                setOnCameraClick(View.OnClickListener {

                })

                setOnSmileClick(View.OnClickListener {

                })
            }

            // Text time
            (getChildAt(6) as AppCompatTextView).run {
                text = TimeHelper.convertDateTimeSvToCurrentDate(obj.date_time)
            }
        }

//        val product = ICThumbnail(
//                "https://image.shutterstock.com/image-photo/white-transparent-leaf-on-mirror-260nw-1029171697.jpg",
//                "https://image.shutterstock.com/image-photo/white-transparent-leaf-on-mirror-260nw-1029171697.jpg",
//                "https://image.shutterstock.com/image-photo/white-transparent-leaf-on-mirror-260nw-1029171697.jpg",
//                "https://image.shutterstock.com/image-photo/white-transparent-leaf-on-mirror-260nw-1029171697.jpg"
//        )
//        it.setData(ICProductInPost(
//                1234L,
//                "https://image.shutterstock.com/image-photo/white-transparent-leaf-on-mirror-260nw-1029171697.jpg",
//                "Product Name",
//                "Business Name",
//                mutableListOf(product, product, product),
//                ""
//
//        ))
    }
}