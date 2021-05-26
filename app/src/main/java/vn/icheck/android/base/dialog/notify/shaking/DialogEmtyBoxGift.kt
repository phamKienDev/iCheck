package vn.icheck.android.base.dialog.notify.shaking

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.view.normal_text.TextNormal
import vn.icheck.android.network.models.ICMission
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

abstract class DialogEmtyBoxGift(context: Context, private val image: Int, private val title: String, private val idCampaign: String, private val missions: MutableList<ICMission>, private val isCancelable: Boolean) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_emty_box_gift

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        findViewById<AppCompatImageView>(R.id.image)?.run {
            setImageResource(image)
        }

        val tvTitle = findViewById<AppCompatTextView>(R.id.tvTitle)
        val tvDescription = findViewById<AppCompatTextView>(R.id.tvDescription)
        val btnAction = findViewById<AppCompatTextView>(R.id.btnAction)

        findViewById<LinearLayout>(R.id.containerMission)?.run {
            background=vn.icheck.android.ichecklibs.ViewHelper.bgBorderDotted10LineColorRadius10(context)
            if (missions.isNullOrEmpty()) {
                tvTitle.text = "Bạn không đủ lượt để mở quà"
                tvDescription.beGone()
                beGone()
                btnAction.beGone()

            } else {
                tvTitle.text = title
                tvDescription.beVisible()
                tvDescription.text = "Hoàn thành các thử thách sau để nhận thêm lượt mở quà mới nào:"
                beVisible()
                btnAction.beVisible()

                for (i in 0 until missions.size) {
                    addView(TextNormal(context).also {
                        it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { params ->
                            params.setMargins(SizeHelper.size12, if (i == 0) {
                                SizeHelper.size12
                            } else {
                                SizeHelper.size16
                            }, SizeHelper.size12, 0)
                        }
                        it.gravity = Gravity.CENTER_VERTICAL
                        it.setTextColor(ContextCompat.getColor(context, R.color.colorNormalText))
                        it.typeface = ViewHelper.createTypeface(context, R.font.barlow_medium)
                        it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_blue_12px, 0, 0, 0)
                        it.text = missions[i].missionName
                    })
                }
            }
        }

        btnAction.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                dismiss()
                ICheckApplication.currentActivity()?.let {
                    ListMissionActivity.show(it, idCampaign)
                }
            }
        }

        findViewById<AppCompatTextView>(R.id.btnMoreEvent)?.run {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                dismiss()
                onMoreEvent()
            }
        }

        findViewById<AppCompatImageView>(R.id.btnClose)?.run {
            setOnClickListener {
                dismiss()
                onClose()
            }
        }
    }

    protected abstract fun onClose()
    protected abstract fun onMoreEvent()
}