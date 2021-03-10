package vn.icheck.android.component.banner

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vn.icheck.android.component.view.IndicatorLineHorizontal
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICAdsNew
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.ui.view.HeightWrappingViewPager
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import java.util.concurrent.TimeUnit

class SlideBannerV2Holder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createSlideBannerHolder(parent.context)) {
    private var disposable: Disposable? = null
    private lateinit var listener: IBannerV2Listener

    fun bind(obj: ICAdsNew, listener: IBannerV2Listener) {
        this.listener = listener

        (itemView as ViewGroup).run {
            (getChildAt(0) as HeightWrappingViewPager).run {
                removeAllViews()

                adapter = SlideBannerV2Adapter(obj.data, listener)
                measure(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                currentItem = 0

                addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    }

                    override fun onPageSelected(position: Int) {
                        countDownToNext()
                    }
                })
            }

            if (!obj.data.isNullOrEmpty()){
                if (obj.data.size > 2){
                    (getChildAt(1) as IndicatorLineHorizontal).beVisible()
                } else {
                    (getChildAt(1) as IndicatorLineHorizontal).beInvisible()
                }
                (getChildAt(1) as IndicatorLineHorizontal).run {
                    bind(obj.data.size)
                }
            }
        }

        countDownToNext()
    }

    private fun countDownToNext() {
        disposable?.dispose()
        disposable = null

        disposable = Observable.timer(Constant.TIME_DELAY_SLIDE_SECOND, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    ((itemView as ViewGroup).getChildAt(0) as HeightWrappingViewPager?)?.let { viewPager ->
                        val totalCount = viewPager.adapter?.count ?: 0

                        viewPager.currentItem = if (viewPager.currentItem < totalCount - 1) {
                            viewPager.currentItem + 1
                        } else {
                            0
                        }
                    }
                }
    }
}