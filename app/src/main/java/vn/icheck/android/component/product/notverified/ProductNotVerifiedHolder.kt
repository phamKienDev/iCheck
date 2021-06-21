package vn.icheck.android.component.product.notverified

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.screen.user.product_detail.product.dialog.ContactBusinessDialog
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.visibleOrGone

class ProductNotVerifiedHolder(parent: ViewGroup) : BaseViewHolder<ProductNotVerifiedModel>(createView(parent.context)) {

    override fun bind(obj: ProductNotVerifiedModel) {
        // Layout parent
        (itemView as ViewGroup).run {

            // Text content
            (getChildAt(0) as AppCompatTextView).run {
                if (!obj.data.verifyMessage.isNullOrEmpty()) {
                    text = obj.data.verifyMessage
                } else {
                    setText(R.string.default_product_not_verified)
                }
            }

            // Layout bottom
            (getChildAt(1) as ViewGroup).run {
                if (obj.page?.owner == null) {
                    getChildAt(0).visibility = View.GONE
                    getChildAt(1).visibility = View.GONE
                } else {
                    if (!obj.page.owner?.phone.isNullOrEmpty() && !obj.page.owner?.email.isNullOrEmpty()) {
                        getChildAt(0).visibility = View.VISIBLE
                        getChildAt(1).visibility = View.VISIBLE
                    }
                }

                // Text contact
                getChildAt(0).apply {
                    visibleOrGone(obj.page?.owner?.verified == false && (!obj.page.owner?.phone.isNullOrEmpty() || !obj.page.owner?.email.isNullOrEmpty()))

                    setOnClickListener {
                        obj.page?.let { productDetail ->
                            ContactBusinessDialog(itemView.context).show(null, productDetail.owner?.phone, productDetail.owner?.email)
                        }
                    }
                }

                // Text view more
                getChildAt(2).setOnClickListener {
                    if (!obj.data.supportUrl.isNullOrEmpty()) {
                        ICheckApplication.currentActivity()?.let { activity ->
                            WebViewActivity.start(activity, obj.data.supportUrl)
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size10, 0,  0)
                layoutParent.orientation = LinearLayout.VERTICAL

                // Text content
                layoutParent.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(),
                        null,
                        ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                        Color.WHITE,
                        14f).also {
                    it.setBackgroundColor(Constant.getAccentRedColor(context))
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_group_error_card, 0, 0, 0)
                }.also {
                    it.setPadding(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)
                    it.compoundDrawablePadding = SizeHelper.size10
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_white_24dp, 0, 0, 0)
                })

                // Layout bottom
                layoutParent.addView(LinearLayout(context).also { layoutBottom ->
                    layoutBottom.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size40)
                    layoutBottom.orientation = LinearLayout.HORIZONTAL
                    layoutBottom.setBackgroundColor(Constant.getAppBackgroundWhiteColor(layoutBottom.context))
                    layoutBottom.setPadding(SizeHelper.size12, 0, SizeHelper.size12, 0)

                    val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context)

                    // Layout start
                    layoutBottom.addView(FrameLayout(context).also { layoutStart ->
                        layoutStart.layoutParams = ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                        layoutStart.setBackgroundResource(ViewHelper.outValue.resourceId)

                        // Text contact
                        layoutStart.addView(ViewHelper.createText(context,
                                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                                    params.gravity = Gravity.CENTER
                                },
                                null,
                                ViewHelper.createTypeface(context, R.font.barlow_medium),
                                primaryColor,
                                12f).also {
                            it.compoundDrawablePadding = SizeHelper.size6
                            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_phone_blue_18dp, 0, 0, 0)
                            it.setPadding(SizeHelper.size4, 0, SizeHelper.size4, 0)
                            it.gravity = Gravity.CENTER_VERTICAL
                            it.setText(R.string.lien_he_doanh_nghiep)
                        })
                    })

                    // View divider
                    layoutBottom.addView(View(context).also {
                        it.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size1, LinearLayout.LayoutParams.MATCH_PARENT).also { params ->
                            params.setMargins(0, SizeHelper.size14, 0, SizeHelper.size14)
                        }
                        it.setBackgroundColor(Constant.getLineColor(context))
                    })

                    // Layout end
                    layoutBottom.addView(FrameLayout(context).also { layoutStart ->
                        layoutStart.layoutParams = ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                        layoutStart.setBackgroundResource(ViewHelper.outValue.resourceId)

                        // Text view more
                        layoutStart.addView(ViewHelper.createText(context,
                                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                                    params.gravity = Gravity.CENTER
                                },
                                null,
                                ViewHelper.createTypeface(context, R.font.barlow_medium),
                                primaryColor,
                                12f).also {
                            it.compoundDrawablePadding = SizeHelper.size6
                            it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_help_blue_18dp, 0, 0, 0)
                            it.setPadding(SizeHelper.size4, 0, SizeHelper.size4, 0)
                            it.gravity = Gravity.CENTER_VERTICAL
                            it.setText(R.string.tim_hieu_them)
                        })
                    })
                })
            }
        }
    }
}