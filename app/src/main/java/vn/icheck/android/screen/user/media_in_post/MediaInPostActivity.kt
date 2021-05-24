package vn.icheck.android.screen.user.media_in_post

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yarolegovich.discretescrollview.DiscreteScrollView
import kotlinx.android.synthetic.main.activity_detail_media.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.review.ReviewBottomSheet
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.ViewHelper.delayTimeoutClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.screen.user.commentpost.CommentPostActivity
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class MediaInPostActivity : BaseActivityMVVM(), View.OnClickListener {
    private lateinit var adapter: MediaInPostAdapter
    private lateinit var viewModel: MediaInPostViewModel

    private var positionView: Int = -1

    private lateinit var downloadManager: DownloadManager
    private var downloadHelper: DownloadHelper? = null
    private var downloadId: Long = -1

    private var postScreen: String? = null

    private val requestWriteStorage = 1

    companion object {
        /*
        Constant.DATA_1: object post
        Constant.DATA_2: postId
        Constant.DATA_3: postion media trong list
        Constant.DATA_4: check có vào từ màn PostDetaik?
        Constant.DATA_5: truyền ảnh vào đề tìm vị trí trong list media
         */

        fun start(post: ICPost, activity: Activity, position: Int? = null, requestCode: Int = -1) {
            val intent = Intent(activity, MediaInPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, post)
            intent.putExtra(Constant.DATA_3, position)
            if (requestCode == -1) {
                ActivityUtils.startActivity(activity, intent)
            } else {
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            }
        }

        fun start(post: ICPost, fragment: Fragment, position: Int? = null, requestCode: Int = -1) {
            val intent = Intent(fragment.context, MediaInPostActivity::class.java)
            intent.putExtra(Constant.DATA_1, post)
            intent.putExtra(Constant.DATA_3, position)
            fragment.startActivityForResult(intent, requestCode)
        }


        fun start(postId: Long, activity: Activity, position: Int? = null, requestCode: Int = -1) {
            val intent = Intent(activity, MediaInPostActivity::class.java)
            intent.putExtra(Constant.DATA_2, postId)
            intent.putExtra(Constant.DATA_3, position)

            if (requestCode == -1) {
                ActivityUtils.startActivity(activity, intent)
            } else {
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            }
        }

        fun start(postId: Long, activity: Activity, image: String?, requestCode: Int = -1) {
            val intent = Intent(activity, MediaInPostActivity::class.java)
            intent.putExtra(Constant.DATA_2, postId)
            intent.putExtra(Constant.DATA_5, image)

            if (requestCode == -1) {
                ActivityUtils.startActivity(activity, intent)
            } else {
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_media)
        viewModel = ViewModelProvider(this).get(MediaInPostViewModel::class.java)
        initView()
        listenerData()
    }

    private fun initView() {
        SessionManager.session.user?.let {
            WidgetUtils.loadImageUrl(imgAvatarSend, it.avatar)
        }
        postScreen = intent.getStringExtra(Constant.DATA_4)
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadHelper =
            DownloadHelper(downloadManager, this, object : DownloadHelper.DownloadHelperCallback {
                override fun downloadSuccess() {
                    imgDownload.setImageResource(R.drawable.ic_download_24_white)
                    imgDownload.isEnabled = true
                    DialogHelper.showDialogSuccessBlack(
                        this@MediaInPostActivity,
                        "Tải xuống thành công"
                    )
                }

                override fun downloadError() {
                    imgDownload.setImageResource(R.drawable.ic_download_24_white)
                    imgDownload.isEnabled = true
                    DialogHelper.showDialogErrorBlack(
                        this@MediaInPostActivity,
                        "Tải xuống thất bại"
                    )
                    downloadHelper?.cancelDownload(downloadId)

                }
            })
        downloadHelper?.register()

        adapter = MediaInPostAdapter(false)
        rcvMedia.adapter = adapter

        WidgetUtils.setClickListener(
            this, imgBack,
            imgDownload,
            layoutHeader,
            tvViewComment,
            containerComment,
            imgAvatarSend,
            imgDown,
            tvLike,
            tvShare,
            imgAvatar,
            tvName,
            containerRating,
            tvContent
        )
    }

    private fun listenerData() {
        viewModel.getData(intent)

        viewModel.onPostData.observe(this, {
            if (it.page != null) {
                WidgetUtils.loadImageUrl(imgAvatar, it.page!!.avatar, R.drawable.ic_business_v2)
                tvName.text = it.page!!.name
                if (it.page!!.isVerify) {
                    tvName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_verified_16px,
                        0
                    )
                } else {
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

                imgRank.beInvisible()
            } else {
                WidgetUtils.loadImageUrl(
                    imgAvatar,
                    it.user!!.avatar,
                    R.drawable.ic_avatar_default_84px
                )
                imgRank.beVisible()
                imgRank.setRankUser(it.user?.rank?.level)
                tvName.apply {
                    text = it.user?.getName
                    if (it.user?.kycStatus == 2) {
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_verified_user_16dp,
                            0
                        )
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
            }
            if (!it.customerCriteria.isNullOrEmpty() && it.avgPoint != 0f) {
                containerRating.beVisible()
                containerRating.setData(it.avgPoint, it.avgPoint)
            } else {
                containerRating.beGone()
            }
            tvTime.text = TimeHelper.convertDateTimeSvToCurrentDay2(it.createdAt)
            if (!it.content.isNullOrEmpty()) {
                tvContent.text = it.content!!.trim()
                ViewHelper.setExpandTextWithoutAction(
                    tvContent,
                    2,
                    getString(R.string.xem_chi_tiet),
                    "#FFB800"
                )
            }
            tvLike.setCompoundDrawablesWithIntrinsicBounds(
                if (it.expressive == null) {
                    R.drawable.ic_like_white_off_24px
                } else {
                    R.drawable.ic_like_on_24dp
                }, 0, 0, 0
            )
            tvLike.text = it.expressiveCount.toString()
            tvViewComment.text = it.commentCount.toString()
            tvView.text = it.viewCount.toString()
            tvShare.text = it.shareCount.toString()
            checkPrivacyConfig()
        })

        viewModel.onMediaData.observe(this, {
            adapter.setData(it)

            val positionIntent = intent.getIntExtra(Constant.DATA_3, -1)
            val image = intent.getStringExtra(Constant.DATA_5)
            if (positionIntent != -1) {
                rcvMedia.scrollToPosition(positionIntent)
                tvSlide.text = "${positionIntent + 1}/${it.size}"
                it[positionIntent].exoPlayer?.playWhenReady = true
                positionView = positionIntent
            } else if (!image.isNullOrEmpty()) {
                val index = it.indexOfFirst { it.src == image }
                rcvMedia.scrollToPosition(index)
                tvSlide.text = "${index + 1}/${it.size}"
                it[index].exoPlayer?.playWhenReady = true
                positionView = index
            } else {
                tvSlide.text = "1/${it.size}"
                it[0].exoPlayer?.playWhenReady = true
                positionView = 0
            }

            rcvMedia.addScrollStateChangeListener(object :
                DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
                override fun onScroll(
                    p0: Float,
                    p1: Int,
                    p2: Int,
                    p3: RecyclerView.ViewHolder?,
                    p4: RecyclerView.ViewHolder?
                ) {

                }

                override fun onScrollEnd(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = true
                    tvSlide.text = "${p1 + 1}/${it.size}"
                    positionView = p1
                }

                override fun onScrollStart(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = false
                }
            })
        })

        viewModel.onSharePost.observe(this, {
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(this.getString(R.string.chia_se))
                .setText(it)
                .startChooser()
            tvShare.text = (tvShare.text.toString().toInt() + 1).toString()
        })

        viewModel.onError.observe(this, {
            showShortError(it)
            finish()
        })

        viewModel.onShowMessage.observe(this, {
            showShortError(it)
        })

        viewModel.onStatusCode.observe(this, {
            if (it == ICMessageEvent.Type.ON_SHOW_LOADING) {
                DialogHelper.showLoading(this)
            } else {
                DialogHelper.closeLoading(this)
            }
        })
    }

    private fun checkPrivacyConfig() {
        if (viewModel.postDetail?.page == null) {
            if (viewModel.postDetail?.user?.id != SessionManager.session.user?.id) {
                if(viewModel.postDetail?.user?.userPrivacyConfig?.whoCommentYourPost==null){
                    layoutComment.beVisible()
                }else{
                    when (viewModel.postDetail?.user?.userPrivacyConfig?.whoCommentYourPost) {
                        Constant.EVERYONE -> {
                            layoutComment.beVisible()
                        }
                        Constant.FRIEND -> {
                            if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null && SessionManager.session.user?.id != null) {
                                //người khác gửi kết bạn cho mình
                                ICheckApplication.getInstance().mFirebase.registerRelationship(
                                    Constant.myFriendIdList,
                                    viewModel.postDetail?.user?.id.toString(),
                                    object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value != null && snapshot.value is Long) {
                                                layoutComment.beVisible()
                                            } else {
                                                layoutComment.beGone()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            logError(error.toException())
                                        }
                                    })
                            }

                        }
                        else -> {
                            if (viewModel.postDetail?.user?.id == SessionManager.session.user?.id) {
                                layoutComment.beVisible()
                            } else {
                                layoutComment.beGone()
                            }
                        }
                    }
                }
            } else {
                layoutComment.beVisible()
            }
        } else {
            layoutComment.beVisible()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.imgDownload -> {
                if (PermissionHelper.checkPermission(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestWriteStorage
                    )
                ) {
                    downloadMedia()
                }
            }
            R.id.layoutHeader -> {
                setResult(Activity.RESULT_OK)
                onBackPressed()
            }
            R.id.tvViewComment -> {
                tvViewComment.delayTimeoutClick(1500)
                viewModel.postDetail?.let {
                    CommentPostActivity.start(this, it)
                }
            }
            R.id.tvLike -> {
                viewModel.onLikeReview()
            }
            R.id.tvShare -> {
                viewModel.onSharePost()
            }
            R.id.containerComment -> {
                containerComment.delayTimeoutClick(1500)
                viewModel.postDetail?.let {
                    CommentPostActivity.start(this, it, 1)
                }
            }
            R.id.tvName -> {
                if (viewModel.postDetail?.page != null) {
                    PageDetailActivity.start(this, viewModel.postDetail!!.page!!.id)
                } else {
                    IckUserWallActivity.create(viewModel.postDetail?.user?.id, this)
                }
            }
            R.id.imgAvatar -> {
                if (viewModel.postDetail?.page != null) {
                    PageDetailActivity.start(this, viewModel.postDetail!!.page!!.id)
                } else {
                    IckUserWallActivity.create(viewModel.postDetail?.user?.id, this)
                }
            }
            R.id.containerRating -> {
                if (viewModel.postDetail != null) {
                    val name = if (viewModel.postDetail?.page != null) {
                        viewModel.postDetail!!.page!!.getName
                    } else {
                        viewModel.postDetail!!.user?.getName
                    }

                    ReviewBottomSheet.show(
                        supportFragmentManager,
                        true,
                        ICReviewBottom(
                            name,
                            viewModel.postDetail!!.avgPoint,
                            viewModel.postDetail!!.customerCriteria
                        )
                    )
                }
            }
            R.id.tvContent -> {
                if (postScreen != null) {
                    onBackPressed()
                } else {
                    viewModel.postDetail?.id?.let { DetailPostActivity.start(this, it) }
                }
            }
        }
    }

    private fun downloadMedia() {
        if (!adapter.getListData.isNullOrEmpty()) {
            if (adapter.getListData[positionView].mediaError || NetworkHelper.isNotConnected(this)) {
                DialogHelper.showDialogErrorBlack(this@MediaInPostActivity, "Tải xuống thất bại")
            } else {
                imgDownload.setImageResource(R.drawable.ic_download_24_gray)
                imgDownload.isEnabled = false
                downloadId = downloadHelper?.startDownload(adapter.getListData[positionView].src!!)
                    ?: -1
                if (downloadId == -1L) {
                    imgDownload.setImageResource(R.drawable.ic_download_24_white)
                    imgDownload.isEnabled = true
                    DialogHelper.showDialogErrorBlack(
                        this@MediaInPostActivity,
                        "Tải xuống thất bại"
                    )
                    downloadHelper?.cancelDownload(downloadId)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::adapter.isInitialized && positionView != -1 && !adapter.listData.isNullOrEmpty())
            adapter.listData[positionView].exoPlayer?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized && positionView != -1 && !adapter.listData.isNullOrEmpty())
            adapter.listData[positionView].exoPlayer?.playWhenReady = true
    }


    override fun onBackPressed() {
        setResult(RESULT_OK, Intent().apply {
            putExtra(Constant.DATA_1, viewModel.postDetail)
        })
        EventBus.getDefault().post(
            ICMessageEvent(
                ICMessageEvent.Type.RESULT_MEDIA_POST_ACTIVITY,
                viewModel.postDetail
            )
        )
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestWriteStorage) {
            if (PermissionHelper.checkResult(grantResults)) {
                downloadMedia()
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.RESULT_COMMENT_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    viewModel.getData(Intent().apply { putExtra(Constant.DATA_1, event.data) })
                }
            }
            ICMessageEvent.Type.RESULT_DETAIL_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    viewModel.getData(Intent().apply { putExtra(Constant.DATA_1, event.data) })
                }
            }
            ICMessageEvent.Type.DELETE_DETAIL_POST -> {
                if (event.data != null && event.data is Long) {
                    if (event.data == viewModel.postDetail?.id) {
                        DialogHelper.showDialogErrorBlack(
                            this,
                            getString(R.string.bai_viet_khong_con_ton_tai),
                            null,
                            2000
                        )
                        Handler().postDelayed({
                            finish()
                        }, 2200)
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