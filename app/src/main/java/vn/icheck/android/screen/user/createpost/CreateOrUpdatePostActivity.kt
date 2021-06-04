package vn.icheck.android.screen.user.createpost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_create_or_update_post.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper.setTextNameProductInPost
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICPrivacy
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductDetail
import vn.icheck.android.screen.scan.V6ScanditActivity
import vn.icheck.android.screen.user.createpost.dialog.SelectPostPrivacyDialog
import vn.icheck.android.screen.user.createpost.viewmodel.CreateOrUpdatePostViewModel
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.kotlin.WidgetUtils.loadImageFromVideoFile
import java.io.File

/**
 * Created by VuLCL on 8/6/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateOrUpdatePostActivity : BaseActivityMVVM(), TakeMediaHelper.TakeCameraListener {
    private lateinit var viewModel: CreateOrUpdatePostViewModel

    private val takeMediaHelper = TakeMediaHelper(this,this, true)

    private val permissionCamera = 1
    private val permissionWallpaper = 2
    private val requestScanProduct = 1

    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            addImage(file)
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
            for (i in 0 until file.size) {
                addImage(file[i])
            }
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            file?.let {
                addImage(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_post)

        setupToolbar()
        setupView()
        setupViewModel()
        setupListener()
    }

    private fun setupToolbar() {
        layoutToolbar.setPadding(0, SizeHelper.size16, 0, 0)

        imgBack.setImageResource(R.drawable.ic_cancel_light_blue_24dp)
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        tvViewMore.background=vn.icheck.android.ichecklibs.ViewHelper.btnWhiteStrokeSecondary1Corners4(this)
        if (intent?.getLongExtra(Constant.DATA_2, -1) != -1L) {

            WidgetUtils.loadImageUrl(imgAvatar, intent.getStringExtra(Constant.DATA_4), R.drawable.ic_business_v2)
            edtContent.hint = "Hãy chia sẻ những thông tin hữu ích nào!"
            tvType.beGone()
            imgStatus.beGone()
            tvName.text = intent.getStringExtra(Constant.DATA_3)
            if (intent?.getBooleanExtra(Constant.DATA_5, false) == true) {
                tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        } else {
            SessionManager.session.user?.let { user ->
                imgStatus.setRankUser(user.rank?.level)
                WidgetUtils.loadImageUrl(imgAvatar, user.avatar, R.drawable.ic_avatar_default_84px)
                tvName.apply {
                    text = user.getName
                    if (user.kycStatus == 2) {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_18dp, 0)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
                edtContent.hint = "Bạn đã sử dụng sản phẩm nào? Hãy chia sẻ cảm nhận nhé!"
                tvType.beVisible()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CreateOrUpdatePostViewModel::class.java)

        viewModel.onSetTitle.observe(this, {
            txtTitle.setText(it)
            if (intent.getBooleanExtra(Constant.DATA_3, false)) {
                imgWallpaper.performClick()
                intent.putExtra(Constant.DATA_3, false)
            }
        })

        viewModel.onSetPrivacy.observe(this, {
            tvType.text = it.privacyElementName
            tvType.tag = it.privacyElementId
        })

        viewModel.onSetPost.observe(this, {
            edtContent.setText(it.content)

            it.meta?.product?.let { product ->
                addProduct(product)
            }

            if (!it.media.isNullOrEmpty()) {
                for (item in it.media!!) {
                    addImage(item.content!!)
                }
            }
        })

        viewModel.onCreatedPost.observe(this, {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(Constant.DATA_1, it)
            })
            if (intent?.getLongExtra(Constant.DATA_1, -1) == -1L) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.RESULT_CREATE_POST, it))
            } else {
                DialogHelper.showDialogSuccessBlack(this, this.getString(R.string.ban_da_chinh_sua_bai_viet_thanh_cong))
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.RESULT_EDIT_POST, it))
                Intent().apply {
                    putExtra(Constant.DATA_1, it)
                    setResult(RESULT_OK, this)
                }
            }
            finish()
        })

        viewModel.onErrorGetData.observe(this, {
            DialogHelper.showNotification(this@CreateOrUpdatePostActivity, it, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        })

        viewModel.onShowMessage.observe(this, {
            showLongError(it)
        })

        viewModel.onStatus.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.getData(intent)
    }

    private fun setupListener() {
        tvType.setOnClickListener {
            object : SelectPostPrivacyDialog(this, viewModel.getListPrivacy) {
                override fun onDone(privacy: ICPrivacy) {
                    tvType.text = privacy.privacyElementName
                    tvType.tag = privacy.privacyElementId
                    viewModel.updatePrivacy(privacy.privacyElementId)
                }
            }.show()
        }

        imgCamera.setOnClickListener {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PermissionHelper.checkPermission(this, permission, permissionCamera)) {
                takeMediaHelper.startTakeMedia()
            }
        }

        imgWallpaper.setOnClickListener {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PermissionHelper.checkPermission(this, permission, permissionWallpaper)) {
                pickWallpaper()
            }
        }

        imgScan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                }
            } else {
                V6ScanditActivity.scanOnly(this, requestScanProduct)
            }

        }

        btnSend.setOnClickListener {
            delayAction({
                val productID = if (layoutProduct.isNotEmpty()) {
                    layoutProduct.getChildAt(0).tag as Long?
                } else {
                    null
                }

                val listImage = mutableListOf<String>()
                if (layoutImage.isNotEmpty()) {
                    for (i in 0 until layoutImage.childCount) {
                        if (layoutImage.getChildAt(i).tag != null && layoutImage.getChildAt(i).tag is String) {
                            listImage.add(layoutImage.getChildAt(i).tag as String)
                        }
                    }
                }

                viewModel.createOrUpdate(tvType.tag as Long?, edtContent.text.toString(), productID, listImage)
            })
        }

        tvViewMore.setOnClickListener {
            if (tvViewMore.text == getString(R.string.thu_gon)) {
                layoutImage.apply {
                    for (i in childCount - 2 downTo 5) {
                        getChildAt(i).beGone()
                    }
                }
                tvViewMore.text = getString(R.string.xem_tat_ca_x_anh, layoutImage.childCount - 1)
            } else {
                layoutImage.apply {
                    for (i in childCount - 2 downTo 5) {
                        getChildAt(i).beVisible()
                    }
                }
                tvViewMore.text = getString(R.string.thu_gon)
            }
        }
    }

    private fun pickWallpaper() {
        TakeMediaDialog.show(supportFragmentManager, this, takeMediaListener, selectMulti = true, isVideo = true)
    }

    private fun addProduct(product: ICProductDetail) {
        layoutProduct.removeAllViews()
        layoutProduct.addView(FrameLayout(this).also { parent ->
            parent.layoutParams = ViewHelper.createLayoutParams().also { params ->
                params.topMargin = SizeHelper.size10
            }
            parent.tag = product.id

            val productLayout = LayoutInflater.from(this).inflate(R.layout.item_short_product, parent, false) as ViewGroup
            productLayout.setPadding(SizeHelper.size4, SizeHelper.size6, SizeHelper.size26, SizeHelper.size6)
            productLayout.background = ViewHelper.createShapeDrawable(vn.icheck.android.ichecklibs.Constant.getAppBackgroundGrayColor(this), SizeHelper.size4.toFloat())
            WidgetUtils.loadImageUrl(productLayout.getChildAt(0) as AppCompatImageView, product.media?.find { it.type == "image" }?.content)
            (productLayout.getChildAt(1) as AppCompatTextView).setTextNameProductInPost(product.basicInfo?.name)
            (productLayout.getChildAt(2) as AppCompatTextView).text = product.owner?.name
            parent.addView(productLayout)

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.gravity = Gravity.END
                }
                image.setImageResource(R.drawable.ic_delete_gray_24px)

                image.setOnClickListener {
                    layoutProduct.removeView(parent)
                }
            })
        })
    }

    private fun addProduct(product: ICProduct) {
        layoutProduct.removeAllViews()
        layoutProduct.addView(FrameLayout(this).also { parent ->
            parent.layoutParams = ViewHelper.createLayoutParams().also { params ->
                params.topMargin = SizeHelper.size10
            }
            parent.tag = product.id

            val productLayout = LayoutInflater.from(this).inflate(R.layout.item_short_product, parent, false) as ViewGroup
            productLayout.background = ViewHelper.createShapeDrawable(vn.icheck.android.ichecklibs.Constant.getAppBackgroundGrayColor(this), SizeHelper.size4.toFloat())
            WidgetUtils.loadImageUrl(productLayout.getChildAt(0) as AppCompatImageView, product.media?.find { it.type == "image" }?.content)
            (productLayout.getChildAt(1) as AppCompatTextView).setTextNameProductInPost(product.name)
            (productLayout.getChildAt(2) as AppCompatTextView).text = product.owner?.name
            parent.addView(productLayout)

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.gravity = Gravity.END
                }
                image.setImageResource(R.drawable.ic_delete_gray_24px)

                image.setOnClickListener {
                    layoutProduct.removeView(parent)
                }
            })
        })
    }

    private fun addImage(file: File) {
        val image = FrameLayout(this).also { parent ->
            parent.layoutParams = ViewHelper.createLayoutParams().also { params ->
                params.topMargin = SizeHelper.size10
            }
            parent.tag = file.absolutePath

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                image.scaleType = ImageView.ScaleType.FIT_CENTER
                image.adjustViewBounds = true

                WidgetUtils.loadImageFile(image, file)
                image.loadImageFromVideoFile(file, null)
            })

            if (file.absolutePath.contains(".mp4")) {
                parent.addView(AppCompatImageView(this).also { image ->
                    image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    image.setBackgroundColor(ContextCompat.getColor(this,R.color.black_20))
                })

                parent.addView(AppCompatImageView(this).also { play ->
                    play.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                        params.gravity = Gravity.CENTER
                    }
                    play.setImageResource(R.drawable.ic_play_40dp)
                    play.setOnClickListener {
                        DetailMediaActivity.start(this, file.absolutePath, Constant.VIDEO)
                    }
                })
            }

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.gravity = Gravity.END
                }
                image.setImageResource(R.drawable.ic_delete_gray_24px)

                image.setOnClickListener {
                    deleteImage(parent)
                }
            })
        }

        addImage(image)
    }

    private fun addImage(url: String) {
        val image = FrameLayout(this).also { parent ->
            parent.layoutParams = ViewHelper.createLayoutParams().also { params ->
                params.topMargin = SizeHelper.size10
            }
            parent.tag = url

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                image.scaleType = ImageView.ScaleType.FIT_CENTER
                image.adjustViewBounds = true

                WidgetUtils.loadImageUrl(image, url)
            })

            parent.addView(AppCompatImageView(this).also { image ->
                image.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.gravity = Gravity.END
                }
                image.setImageResource(R.drawable.ic_delete_gray_24px)

                image.setOnClickListener {
                    deleteImage(parent)
                }
            })
        }

        addImage(image)
    }

    private fun addImage(image: FrameLayout) {
        layoutImage.apply {
            if (childCount >= 6) {
                if (tvViewMore.isVisible) {
                    if (tvViewMore.text != getString(R.string.thu_gon)) {
                        image.beGone()
                        tvViewMore.text = getString(R.string.xem_tat_ca_x_anh, layoutImage.childCount)
                    }
                } else {
                    image.beGone()
                    tvViewMore.beVisible()
                    tvViewMore.text = getString(R.string.xem_tat_ca_x_anh, layoutImage.childCount)
                }
            }
            addView(image, layoutImage.childCount - 1)
        }
    }

    private fun deleteImage(image: FrameLayout) {
        layoutImage.apply {
            removeView(image)

            if (childCount <= 6) {
                tvViewMore.beGone()

                layoutImage.apply {
                    for (i in childCount - 2 downTo 0) {
                        getChildAt(i).beVisible()
                    }
                }
            } else {
                for (i in 0 until 5) {
                    getChildAt(i).beVisible()
                }

                if (tvViewMore.text != getString(R.string.thu_gon)) {
                    tvViewMore.text = getString(R.string.xem_tat_ca_x_anh, layoutImage.childCount - 1)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (layoutImage.childCount > 1 || edtContent.text.toString().isNotEmpty() || layoutProduct.childCount > 0) {
            if (viewModel.postDetail?.id == null) {
                DialogHelper.showConfirm(this, "Bạn muốn bỏ bài viết này?", null, "Tiếp tục chỉnh sửa", "Bỏ bài viết", true, null, R.color.colorAccentRed, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        finish()
                    }
                })
            } else {
                DialogHelper.showConfirm(this, "Tiếp tục chỉnh sửa bài viết?", null, "Để sau", "Tiếp tục", true, object : ConfirmDialogListener {
                    override fun onDisagree() {
                        finish()
                    }

                    override fun onAgree() {

                    }
                })
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionHelper.checkResult(grantResults)) {
            when (requestCode) {
                permissionCamera -> {
                    takeMediaHelper.startTakeMedia()
                }
                permissionWallpaper -> {
                    pickWallpaper()
                }
            }
        } else {
            showLongWarning(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeMediaHelper.onActivityResult(requestCode, resultCode)

        if (requestCode == requestScanProduct) {
            if (resultCode == Activity.RESULT_OK) {
                (data?.getSerializableExtra(Constant.DATA_1) as ICProductDetail?)?.let { product ->
                    if (product.status == "ok" && product.state == "active") {
                        addProduct(product)
                    } else {
                        showShortErrorToast("Không tìm thấy sản phẩm")
                    }
                }
            }
        }
    }

    override fun onTakeMediaSuccess(file: File?) {
        file?.let {
            addImage(it)
        }
    }
}
