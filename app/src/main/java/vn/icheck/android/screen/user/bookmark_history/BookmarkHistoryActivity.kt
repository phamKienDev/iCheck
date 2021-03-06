package vn.icheck.android.screen.user.bookmark_history

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.R
import vn.icheck.android.databinding.ActivityBookmarkHistoryBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*

@AndroidEntryPoint
class BookmarkHistoryActivity : BaseActivityMVVM() {
    private val bookmarkHistoryViewModel by viewModels<BookmarkHistoryViewModel>()
    private lateinit var binding: ActivityBookmarkHistoryBinding
    private val bookmarkHistoryAdapter = BookmarkHistoryAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        TrackingAllHelper.trackBookmarkViewed()
        DialogHelper.showLoading(this)
        binding.header.txtTitle.setText(R.string.san_pham_yeu_thich)
        binding.header.imgBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            bookmarkHistoryViewModel.getBookmarks().collectLatest {
                bookmarkHistoryAdapter.submitData(it)
            }
        }
        binding.rcvBookmark.adapter = bookmarkHistoryAdapter
        bookmarkHistoryViewModel.countLiveData.observe(this) {
            DialogHelper.closeLoading(this)
            binding.rootRefresh.isRefreshing = false
            if (it == 0) {
                if (binding.edtSearch.text?.trim().toString().isNotEmpty()) {
                    binding.rcvBookmark.beGone()
                    binding.edtSearch.beVisible()
                    binding.tvNoBookmark.beVisible()
                    binding.tvNoBookmark.setText(R.string.khong_tim_thay_san_pham_1)
                } else {
                    binding.rcvBookmark.beGone()
                    binding.edtSearch.beGone()
                    binding.tvNoBookmark.beVisible()
                }
            } else {
                binding.rcvBookmark.beVisible()
                binding.edtSearch.beVisible()
                binding.tvNoBookmark.beGone()
            }
        }
        binding.edtSearch.addTextChangedListener {
            if (job == null) {
                job = lifecycleScope.launch {
                    delay(400)
                    bookmarkHistoryViewModel.filter(it?.trim().toString()).collectLatest {
                        bookmarkHistoryAdapter.submitData(it)
                    }
                }
            } else if (job?.isActive == true) {
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(400)
                    bookmarkHistoryViewModel.filter(it?.trim().toString()).collectLatest {
                        bookmarkHistoryAdapter.submitData(it)
                    }
                }
            }
        }
        binding.rootRefresh.setOnRefreshListener {
            if (job == null) {
                job = lifecycleScope.launch {
                    delay(400)
                    bookmarkHistoryViewModel.filter(  binding.edtSearch?.text?.trim().toString()).collectLatest {
                        bookmarkHistoryAdapter.submitData(it)
                    }
                }
            } else if (job?.isActive == true) {
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(400)
                    bookmarkHistoryViewModel.filter(binding.edtSearch?.text?.trim().toString()).collectLatest {
                        bookmarkHistoryAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun setupView() {
        binding.edtSearch.setHintTextColor(ColorManager.getDisableTextColor(this))
        binding.edtSearch.setTextColor(ColorManager.getNormalTextColor(this))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(data: HashMap<String, Any?>) {
        when {
            data["checked"] == true -> {
                bookmarkHistoryViewModel.bookmark(data["id"] as Long? ?: 0L).observe(this, {
                    if (it.statusCode != "200") {
                        showShortErrorToast(it.message)
                    }
                })
            }
            data["checked"] == false -> {
                bookmarkHistoryViewModel.deleteBookmark(data["id"] as Long? ?: 0L).observe(this, {
                    if (it.statusCode != "200") {
                        showShortErrorToast(it.message)
                    }
                })
            }
            data["goHome"] == true -> {
                finish()
            }
            else -> {
                IckProductDetailActivity.start(this, data["id"] as Long? ?: 0L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}