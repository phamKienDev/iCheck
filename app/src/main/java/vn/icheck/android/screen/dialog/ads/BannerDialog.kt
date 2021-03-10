package vn.icheck.android.screen.dialog.ads

import android.content.Context
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.dialog_banner.*
import retrofit2.Response
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class BannerDialog(context: Context, val ads: ICAds) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_banner

    override val getIsCancelable: Boolean
        get() = true

    override fun onInitView() {
        setupView()
        setupListener()
    }

    private fun setupView() {
        val size = try {
            ads.banner_size?.split("x")
        } catch (e: Exception) {
            null
        }

        if (size != null && size.size > 1) {
            val cs = ConstraintSet()
            cs.clone(layoutContent)
            cs.setDimensionRatio(imgBanner.id, "${size[0]}:${size[1]}")
            cs.applyTo(layoutContent)
        }

        WidgetUtils.loadImageUrlRounded10FitCenter(imgBanner, ads.banner_thumbnails?.original, R.drawable.ic_default_square, R.drawable.ic_default_square)

        imgBanner.setOnClickListener {
            hideAds(ads.id)
            onClicked(ads)
            dismiss()
        }
    }

    private fun setupListener() {
        tvClose.setOnClickListener {
            dismiss()
        }
    }

    private fun hideAds(id: Long) {
        AdsRepository().hideAds(id, object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {

            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }

    protected abstract fun onClicked(obj: ICAds)
}