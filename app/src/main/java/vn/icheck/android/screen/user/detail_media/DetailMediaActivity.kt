package vn.icheck.android.screen.user.detail_media

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import kotlinx.android.synthetic.main.activity_detail_media2.*
import kotlinx.android.synthetic.main.ic_image_holder2.view.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.DownloadHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.media_in_post.ICExoMedia
import vn.icheck.android.screen.user.media_in_post.MediaInPostAdapter
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class DetailMediaActivity : BaseActivityMVVM(), View.OnClickListener {
    private lateinit var adapter: MediaInPostAdapter

    private var positionView: Int = -1
    private val permissionWrite = 1

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

        fun start(activity: Activity, listImage: ArrayList<String?>, pos: Int = 0) {
            val listMedia = arrayListOf<ICMedia>()
            listImage.filter { !it.isNullOrEmpty() }.forEach {
                listMedia.add(ICMedia(it, Constant.IMAGE))
            }
            val json = JsonHelper.toJson(listMedia)

            val intent = Intent(activity, DetailMediaActivity::class.java)
            intent.putExtra(Constant.DATA_1, json)
            intent.putExtra(Constant.DATA_2, pos)
            ActivityUtils.startActivity(activity, intent)
        }

        fun start(context: Context, listImage: ArrayList<String?>, pos: Int = 0) {
            val listMedia = arrayListOf<ICMedia>()
            listImage.filter { !it.isNullOrEmpty() }.forEach {
                listMedia.add(ICMedia(it, Constant.IMAGE))
            }
            val json = JsonHelper.toJson(listMedia)

            val intent = Intent(context, DetailMediaActivity::class.java)
            intent.putExtra(Constant.DATA_1, json)
            intent.putExtra(Constant.DATA_2, pos)
            context.startActivity(intent)
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

            val position = intent.getIntExtra(Constant.DATA_2, -1)
            if (position != -1) {
                rcvMedia.scrollToPosition(position)
                tvSlide.text = "${position + 1}/${listExo.size}"
                listExo[position].exoPlayer?.playWhenReady = true
                positionView = position
            } else {
                tvSlide.text = "1/${listExo.size}"
                listExo[0].exoPlayer?.playWhenReady = true
                positionView = 0
            }



            rcvMedia.addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
                override fun onScroll(p0: Float, p1: Int, p2: Int, p3: RecyclerView.ViewHolder?, p4: RecyclerView.ViewHolder?) {

                }

                override fun onScrollEnd(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = true
                    tvSlide.text = "${p1 + 1}/${listExo.size}"
                    if (positionView != p1) {
                        if (adapter.getListData.get(positionView).type == Constant.IMAGE) {
                            adapter.getListData.get(positionView).resetImage = true
                            adapter.notifyItemChanged(positionView)
                        }
                    }
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

    private fun downloadMedia(){
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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgDownload -> {
                val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (PermissionHelper.checkPermission(this, permission, permissionWrite)) {
                    downloadMedia()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionWrite){
            if (PermissionHelper.checkResult(grantResults)){
                downloadMedia()
            }else{
                showSimpleErrorToast(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
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