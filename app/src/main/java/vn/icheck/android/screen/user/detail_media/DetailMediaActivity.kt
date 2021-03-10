package vn.icheck.android.screen.user.detail_media

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import kotlinx.android.synthetic.main.activity_detail_media2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.DownloadHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.media_in_post.ICExoMedia
import vn.icheck.android.screen.user.media_in_post.MediaInPostAdapter
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class DetailMediaActivity : BaseActivityMVVM(), View.OnClickListener {
    private lateinit var adapter: MediaInPostAdapter

    private var positionView: Int = -1

    private lateinit var downloadManager: DownloadManager
    private var downloadHelper: DownloadHelper? = null
    private var downloadId: Long = -1

    companion object {
        fun start(activity: Activity, media: List<ICMedia>) {
            val intent = Intent(activity, DetailMediaActivity::class.java)
            val json = JsonHelper.toJson(media)
            intent.putExtra(Constant.DATA_1, json)
            ActivityUtils.startActivity(activity, intent)
        }

        fun start(activity: Activity, image: String, type: String = Constant.IMAGE) {
            val listMedia = arrayListOf<ICMedia>()
            listMedia.add(ICMedia(image, type))
            val json = JsonHelper.toJson(listMedia)

            val intent = Intent(activity, DetailMediaActivity::class.java)
            intent.putExtra(Constant.DATA_1, json)
            ActivityUtils.startActivity(activity, intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_media2)

        initView()
        initRecyclerView()
    }

    private fun initRecyclerView() {
       val listData = JsonHelper.parseListAttachment(intent.getStringExtra(Constant.DATA_1))

        if (listData.isNullOrEmpty()) {
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            finish()
        } else {
            val listExo = mutableListOf<ICExoMedia>()
            for (i in listData) {
                listExo.add(ICExoMedia(i.content, i.type).also {
                    it.checkTypeMedia()
                })
            }

            adapter.setData(listExo)
            tvSlide.text = "1/${listExo.size}"
            listExo[0].exoPlayer?.playWhenReady = true
            positionView = 0

            rcvMedia.addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
                override fun onScroll(p0: Float, p1: Int, p2: Int, p3: RecyclerView.ViewHolder?, p4: RecyclerView.ViewHolder?) {

                }

                override fun onScrollEnd(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = true
                    tvSlide.text = "${p1 + 1}/${listExo.size}"
                    positionView = p1
                }

                override fun onScrollStart(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = false
                }
            })
        }
    }

    private fun initView() {
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadHelper = DownloadHelper(downloadManager, this, object : DownloadHelper.DownloadHelperCallback {
            override fun downloadSuccess() {
                imgDownload.setImageResource(R.drawable.ic_download_24_white)
                imgDownload.isEnabled = false
                DialogHelper.showDialogSuccessBlack(this@DetailMediaActivity, "Tải xuống thành công")

            }

            override fun downloadError() {
                imgDownload.setImageResource(R.drawable.ic_download_24_white)
                imgDownload.isEnabled = false
                DialogHelper.showDialogErrorBlack(this@DetailMediaActivity, "Tải xuống thất bại")
                downloadHelper?.cancelDownload(downloadId)

            }
        })
        downloadHelper?.register()

        adapter = MediaInPostAdapter(true)
        rcvMedia.adapter = adapter

        WidgetUtils.setClickListener(this, imgBack, imgDownload)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgDownload -> {
                if (!adapter.getListData.isNullOrEmpty()) {
                    if (adapter.getListData[positionView].mediaError || NetworkHelper.isNotConnected(this)) {
                        DialogHelper.showDialogErrorBlack(this@DetailMediaActivity, "Tải xuống thất bại")
                    } else {
                        imgDownload.setImageResource(R.drawable.ic_download_24_gray)
                        imgDownload.isEnabled = false
                        downloadId = downloadHelper?.startDownload(adapter.getListData[positionView].src)
                                ?: -1
                    }
                }
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.IS_SCROLL_MEDIA -> {
                rcvMedia.post {
                    if (event.data != null) {
                        rcvMedia.suppressLayout(false)
                    } else {
                        rcvMedia.suppressLayout(true)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in adapter.getListData) {
            i.release()
        }
        downloadHelper?.destroyActivity()
    }
}