package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemProductImageBinding
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.BannerAdapter
import vn.icheck.android.ui.layout.HeightWrappingViewPager
import java.util.concurrent.TimeUnit

class ICProductImageHolder(parent: ViewGroup, val binding: ItemProductImageBinding = ItemProductImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<List<ICMedia>>(binding.root) {
    private val adapter = BannerAdapter()

    private var disposable: Disposable? = null

    override fun bind(obj: List<ICMedia>) {
        binding.viewPager.adapter = adapter
        adapter.setListData(obj)

        binding.indicator.apply {
            if (obj.size > 1) {
                beVisible()
                setupViewPager(binding.viewPager, obj.size)
            } else {
                beGone()
            }
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                countDownToNext()
            }
        })

        countDownToNext()
    }

    private fun countDownToNext() {
        disposable?.dispose()
        disposable = null

        disposable = Observable.timer(Constant.TIME_DELAY_SLIDE_SECOND, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.viewPager?.let { viewPager ->
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