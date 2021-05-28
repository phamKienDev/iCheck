package vn.icheck.android.screen.user.newsdetailv2

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_new_detail_v2.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.news.NewsAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.newsdetailv2.adapter.NewDetailBusinessAdapter
import vn.icheck.android.screen.user.newsdetailv2.viewmodel.NewDetailViewModel
import vn.icheck.android.screen.user.newslistv2.NewsListV2Activity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.TestTimeUtil

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class NewDetailV2Activity : BaseActivityMVVM() {
    lateinit var viewModel: NewDetailViewModel

    var id = -1L

    var html = ""

    var imgCover = ""

    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_detail_v2)

        initView()
        setupToolbar()
        setupScrollToolbar()
        getDataSuccess()
        getDataError()
    }

    fun initView() {
        StatusBarUtils.setOverStatusBarDark(this@NewDetailV2Activity)

        viewModel = ViewModelProvider(this)[NewDetailViewModel::class.java]

        getDataIntent()
    }

    fun getDataIntent() {
        id = try {
            intent?.getLongExtra(Constant.DATA_1, -1L) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        try {
            title = intent?.getStringExtra(Constant.DATA_1) ?: ""
            imgCover = intent?.getStringExtra(Constant.DATA_2) ?: ""
            html = intent?.getStringExtra(Constant.DATA_3) ?: ""
        } catch (e: Exception) {
            title = ""
            imgCover = ""
            html = ""
        }

        if (id != -1L) {
            constraintLayout.visibility = View.VISIBLE
            viewModel.getNewsDetail(id)

        } else if (!html.isNullOrEmpty()) {

            constraintLayout.visibility = View.GONE
            loadDataHTML()

        } else {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }

    fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgAction.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupScrollToolbar() {
        var backgroundHeight = 0

        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->

            if (backgroundHeight <= 0) {
                backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
            }

            val visibility = viewModel.getHeaderAlpha(nestedScrollView.computeVerticalScrollOffset(), backgroundHeight)
            toolbarAlpha.alpha = visibility
            viewShadow.alpha = visibility
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    private fun getDataSuccess() {
        viewModel.liveData.observe(this, {
            WidgetUtils.loadImageUrl(imgBanner, it.obj?.thumbnail?.trim())

            if (!it.obj?.title.isNullOrEmpty()) {
                txtTitle.text = it.obj?.title

                tvTitle.text = it.obj?.title
            }

            if (!it.obj?.ctaLabel.isNullOrEmpty()) {
                linearLayout.visibility = View.VISIBLE
                viewShadowBottom.visibility = View.VISIBLE
                btnCTA.text = it.obj?.ctaLabel
            } else {
                linearLayout.visibility = View.GONE
                viewShadowBottom.visibility = View.GONE
            }

            btnCTA.setOnClickListener { view ->
                FirebaseDynamicLinksActivity.startDestinationUrl(this@NewDetailV2Activity, it.obj?.ctaUrl)
            }

            webViewUrl.settings.javaScriptEnabled = true
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            webViewUrl.settings.domStorageEnabled = true
            webViewUrl.settings.allowFileAccessFromFileURLs = true
            webViewUrl.settings.allowUniversalAccessFromFileURLs = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            } else {
                webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            }

            if (!it.obj?.fulltext.isNullOrEmpty()) {
                webViewUrl.loadDataWithBaseURL(null, Constant.getHtmlData(it.obj?.fulltext!!), "text/html", "utf-8", "")
            }

            webViewUrl.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    constraintLayout.visibility = View.GONE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.loadUrl(request.url.toString())
                    }
                    return false
                }
            }

            if (!it.obj?.articleCategory?.name.isNullOrEmpty()) {
                tvType.setVisible()
                view.setVisible()

                tvType.apply {
                    text = "#${it.obj?.articleCategory?.name}"

                    setOnClickListener { _ ->
                        ActivityUtils.startActivity<NewsListV2Activity, Long>(this@NewDetailV2Activity, Constant.ID, it.obj?.articleCategory?.id
                                ?: -1)
                    }
                }

            } else {
                tvType.setGone()
                view.setGone()
            }

            if (!it.obj?.createdAt.isNullOrEmpty()) {

                val millisecond = TimeHelper.convertDateTimeSvToMillisecond(it.obj?.createdAt) ?: 0

                tvDate.text = TestTimeUtil(it.obj?.createdAt!!).getTimeDateNews()

                if (System.currentTimeMillis() - millisecond < 86400000) {
                    layoutDate.setVisible()
                } else {
                    layoutDate.setGone()
                }
            }

            if (!it.obj?.pages.isNullOrEmpty()) {
                layoutBusiness.setVisible()

                recyclerView.layoutManager = GridLayoutManager(this@NewDetailV2Activity, 6)
                recyclerViewBusiness.adapter = NewDetailBusinessAdapter(it.obj?.pages!!)
            } else {
                layoutBusiness.setGone()
            }

            if (!it.listData.isNullOrEmpty()) {
                constraintLayout.visibility = View.VISIBLE

                tvTitleNew.text = if (it.obj?.articleCategory?.name.isNullOrEmpty()) {
                    "Tin tức liên quan"
                } else {
                    it.obj?.articleCategory?.name
                }

                txtViewAll.setOnClickListener { _ ->
                    ActivityUtils.startActivity<NewsListV2Activity, Long>(this, Constant.ID, it.obj?.articleCategory?.id
                            ?: -1)
                }

                val adapter = NewsAdapter(it.listData)
                recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                recyclerView.adapter = adapter
            } else {
                constraintLayout.visibility = View.GONE
            }

        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadDataHTML() {
        if (!title.isNullOrEmpty()) {
            txtTitle.text = title

            tvTitle.text = title
        }

        WidgetUtils.loadImageUrl(imgBanner, imgCover.trim())

        webViewUrl.settings.javaScriptEnabled = true
        webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webViewUrl.settings.domStorageEnabled = true
        webViewUrl.settings.allowFileAccessFromFileURLs = true
        webViewUrl.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        } else {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }

        if (!html.isNullOrEmpty()) {
            webViewUrl.loadDataWithBaseURL(null, Constant.getHtmlData(html), "text/html", "utf-8", "")
        }

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                constraintLayout.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                }
                return false
            }
        }
    }

    private fun getDataError() {
        viewModel.onError.observe(this, Observer {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        id = -1L
        html = ""
        imgCover = ""
        title = ""
    }
}
