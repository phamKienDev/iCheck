package vn.icheck.android.screen.user.product_detail.product.wrongcontribution

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.component.report.ReasonReportAdapter
import vn.icheck.android.component.view.ButtonLightBlueCorners4
import vn.icheck.android.component.view.ButtonWhiteOutlinePrimaryBlueCorners4
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.screen.user.contribute_product.IckContributeProductActivity
import vn.icheck.android.ui.SafeFlexboxLayoutManager

class ReportWrongContributionSuccessDialog(val context: Context, isCancelable: Boolean = true, val productId: Long = -1, val isContributed:Boolean = false) : BaseBottomSheetDialog(context, isCancelable) {

    fun show(listReason: MutableList<ICReportForm>, type: String = "", title: String? = null, barcode: String? = null) {
        val contentView = LinearLayout(context).also { layoutParent ->
            layoutParent.layoutParams = ViewHelper.createLayoutParams()
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.background = ViewHelper.createShapeDrawable(Color.WHITE, 0, Color.TRANSPARENT,
                    SizeHelper.size20.toFloat(), SizeHelper.size20.toFloat(), 0f, 0f)
            layoutParent.setPadding(SizeHelper.size12, SizeHelper.size32, SizeHelper.size12, SizeHelper.size20)
            layoutParent.gravity = Gravity.CENTER_HORIZONTAL

            layoutParent.addView(AppCompatImageView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.bottomMargin = SizeHelper.size2
                }
                it.scaleType = ImageView.ScaleType.FIT_CENTER
                it.setImageResource(R.drawable.ic_feed_report_success)
            })

            layoutParent.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(SizeHelper.size42, SizeHelper.size16, SizeHelper.size42, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColor(context, R.color.colorNormalText),
                    16f).also {
                it.text = title
                        ?: context.getString(R.string.report_wrong_contribution_success_title)
                it.gravity = Gravity.CENTER_HORIZONTAL
            })

            layoutParent.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(SizeHelper.size42, SizeHelper.size6, SizeHelper.size42, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_medium),
                    Constant.getSecondTextColor(context),
                    14f).also {
                it.setText(R.string.report_wrong_contribution_success_message)
                it.gravity = Gravity.CENTER_HORIZONTAL
            })

            layoutParent.addView(View(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size0_5).also { params ->
                    params.topMargin = SizeHelper.size10
                }
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
            })

            layoutParent.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams().also { params ->
                        params.topMargin = SizeHelper.size16
                    },
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_medium),
                    Constant.getSecondTextColor(context),
                    14f).also {
                if (type!="order") {
                    it.setText(R.string.noi_dung_bao_cao)
                }else{
                    it.setText(R.string.noi_dung_bao_loi)
                }
            })

            layoutParent.addView(RecyclerView(context).also { recyclerView ->
                recyclerView.layoutParams = ViewHelper.createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size4
                }
                recyclerView.layoutManager = SafeFlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
                recyclerView.adapter = ReasonReportAdapter(listReason)

                val verticalDecoration = FlexboxItemDecoration(context)
                verticalDecoration.setDrawable(GradientDrawable().apply { setSize(SizeHelper.size6, SizeHelper.size8) })
                verticalDecoration.setOrientation(FlexboxItemDecoration.BOTH)
                recyclerView.addItemDecoration(verticalDecoration)
            })

            when (type) {
                "contributor" -> {
                    layoutParent.addView(LinearLayout(context).also { params ->
                        params.layoutParams = ViewHelper.createLayoutParams().also {
                            it.topMargin = SizeHelper.size20
                        }
                        params.orientation = LinearLayout.VERTICAL

                        params.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams().also {
                            it.setMargins(SizeHelper.size16, 0, SizeHelper.size16, 0)
                        }, null, ViewHelper.createTypeface(context, R.font.barlow_medium), vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context), 14f).also {
                            it.text = "Nếu bạn biết thông tin chính xác về sản phẩm này, \n hãy đóng góp thêm nhé!"
                            it.gravity = Gravity.CENTER
                        })

                        params.addView(LinearLayout(context).also { linear ->
                            linear.layoutParams = ViewHelper.createLayoutParams().also {
                                it.setMargins(SizeHelper.size12, SizeHelper.size26, SizeHelper.size12, 0)
                            }
                            linear.orientation = LinearLayout.HORIZONTAL

                            linear.addView(ButtonWhiteOutlinePrimaryBlueCorners4(context).also {
                                it.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size120, SizeHelper.size36)
                                it.gravity = Gravity.CENTER
                                it.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                                it.setText(R.string.dong)

                                it.setOnClickListener {
                                    dialog.dismiss()
                                }
                            })

                            linear.addView(ButtonLightBlueCorners4(context).also {
                                it.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size36, 1f).also { pr ->
                                    pr.leftMargin = SizeHelper.size10
                                }
                                it.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                                it.text = "Đóng góp sản phẩm"

                                it.setOnClickListener {
                                    dialog.dismiss()
                                    ICheckApplication.currentActivity()?.let {
                                        barcode?.let { barcode -> IckContributeProductActivity.start(it, barcode) }
                                    }
                                }
                            })
                        })
                    })
                }
                else -> {
                    layoutParent.addView(ButtonLightBlueCorners4(context).also {
                        it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size36).also { params ->
                            params.topMargin = SizeHelper.size20
                        }
                        it.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                        it.setText(R.string.xong)

                        it.setOnClickListener {
                            dialog.dismiss()
                        }
                    })
                }
            }
        }

        dialog.setContentView(contentView)
        dialog.show()

        val bottemSheetInternal = dialog.findViewById<View>(R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottemSheetInternal!!).state = BottomSheetBehavior.STATE_EXPANDED
    }
}