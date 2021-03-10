package vn.icheck.android.screen.dialog.ads

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.dialog_banner_survey.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.callback.LoadImageListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class BannerSurveyDialog(context: Context, private val ads: ICAds) : BaseDialog(context, R.style.DialogTheme) {
    private lateinit var layoutCenter: ConstraintLayout
    private lateinit var imgBanner: AppCompatImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtClose: AppCompatImageButton

    override val getLayoutID: Int
        get() = R.layout.dialog_banner_survey

    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        layoutCenter = findViewById(R.id.layoutCenter)
        imgBanner = findViewById(R.id.imgBanner)
        progressBar = findViewById(R.id.progressBar)
        txtClose = findViewById(R.id.tvClose)

        val size = try {
            ads.banner_size?.split("x")
        } catch (e: Exception) {
            null
        }

        if (size != null && size.size > 1) {
            val cs = ConstraintSet()
            cs.clone(layoutCenter)
            cs.setDimensionRatio(R.id.imgBanner, "${size[0]}:${size[1]}")
            cs.applyTo(layoutCenter)
        }

        WidgetUtils.loadImageUrlRoundedListener(imgBanner, ImageHelper.getImageUrl(ads.banner_id, ads.banner_thumbnails?.original, ImageHelper.originalSize), R.drawable.ic_default_horizontal, SizeHelper.size6, object : LoadImageListener {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
            }

            override fun onFailed() {
                progressBar.visibility = View.GONE
            }
        })

        imgBanner.setOnClickListener {
            dismiss()
            onBannerClicked(ads)
        }

        txtClose.setOnClickListener {
            DialogHelper.showConfirm(context, context.getString(R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu), object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    dismiss()
                }
            })
        }
        tv_close.setOnClickListener {
            DialogHelper.showConfirm(context, context.getString(R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu), object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    dismiss()
                }
            })
        }
    }

    abstract fun onBannerClicked(ads: ICAds)
}