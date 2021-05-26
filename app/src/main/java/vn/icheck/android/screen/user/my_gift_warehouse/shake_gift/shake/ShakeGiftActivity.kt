package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_shake_gift.*
import kotlinx.android.synthetic.main.activity_shake_gift.imgBack
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake.sdk.ShakeDetector
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake.viewmodel.ShakeGiftViewModel
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake_box_success.ShakeBoxSuccessActivity
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ShakeGiftActivity : BaseActivityMVVM() {

    private val mShakeDetector = ShakeDetector()

    private var isSetShared = false

    private lateinit var viewModel: ShakeGiftViewModel

    private val requestResultCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_gift)
        viewModel = ViewModelProvider(this).get(ShakeGiftViewModel::class.java)
        viewModel.getDataIntent(intent)
        initView()
        StatusBarUtils.setOverStatusBarLight(this)
        listener()
        initSensorManager()
        listenerGetData()
    }

    private fun initView() {
        progressBar.setOnTouchListener { view, motionEvent -> true }

        val layerDrawable: LayerDrawable =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resources.getDrawable(R.drawable.progress_shake_gift, null) as LayerDrawable
            } else {
                resources.getDrawable(R.drawable.progress_shake_gift) as LayerDrawable
            }
        val radius = DimensionUtil.convertDpToPixel(40f, this)
        val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shapeDrawable = ShapeDrawable(RoundRectShape(radii, null, null))
        shapeDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {

            override fun resize(width: Int, height: Int): Shader {
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_progress_box_v2)
                return BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            }
        }
        val drawable: Drawable = ClipDrawable(shapeDrawable, Gravity.START, ClipDrawable.HORIZONTAL)
        layerDrawable.setDrawableByLayerId(android.R.id.progress, drawable)
        progressBar.progressDrawable = layerDrawable
    }

    @SuppressLint("SetTextI18n")
    private fun initSensorManager() {
        val listTitle = arrayOf(
            R.string.message_shake_1,
            R.string.message_shake_2,
            R.string.message_shake_3,
            R.string.message_shake_4
        )
        mShakeDetector.initSensor(this)
        mShakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShaking(total: Int, progress: Int) {
                // trạng thái lắc
                if (!isSetShared) {
                    isSetShared = true
                }

                progressBar.max = total
                progressBar.progress = progress
                if (progress >= 100) {
                    tv_progress.text = "100 %"
                } else {
                    tv_progress.text = "$progress %"
                }
            }

            override fun onShake(count: Int) {
                mShakeDetector.unRegisterSensor()
                // lắc xong thì call api và chuyển sang màn quà success
                viewModel.openGiftBox()
            }

            override fun onCancel() {
                isSetShared = false

                runOnUiThread {
                    ObjectAnimator.ofInt(progressBar, "progress", 0).setDuration(800).apply {
                        interpolator = AccelerateDecelerateInterpolator()
                        start()
                    }
                    progressBar.progress = 0
                    tv_progress.text = ""
                    listTitle.shuffle()
                    tvMessageShake.setText(listTitle[0])
                }
            }
        })
    }

    private fun listenerGetData() {
        viewModel.liveDataObject.observe(this, {
            if (!it?.dynamicImageUrl.isNullOrEmpty()) {
                WidgetUtils.loadGiftUrl(imgGift, it?.dynamicImageUrl!!)
            }
        })

        viewModel.data.observe(this, Observer {
            val intent = Intent(this@ShakeGiftActivity, ShakeBoxSuccessActivity::class.java)
            intent.putExtra(Constant.DATA_1, viewModel.campaign)
            intent.putExtra(Constant.DATA_2, it)
            startActivityForResult(intent, requestResultCount)
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(
                        this,
                        R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau,
                        R.string.huy_bo,
                        R.string.thu_lai,
                        object : ConfirmDialogListener {
                            override fun onDisagree() {
                                onBackPressed()
                            }

                            override fun onAgree() {
                                viewModel.openGiftBox()
                            }
                        })
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, Observer {
            when (it.type) {
                ICMessageEvent.Type.ERROR_EMPTY -> {
                    DialogHelper.showNotification(
                        this,
                        null,
                        R.string.co_loi_xay_ra_vui_long_thu_lai,
                        false,
                        object : NotificationDialogListener {
                            override fun onDone() {
                                onBackPressed()
                            }
                        })
                }

                ICMessageEvent.Type.ERROR_SERVER -> {
                    var message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    if (it.data != null) {
                        if (!(it.data as ICResponseCode).message.isNullOrEmpty()) {
                            message = it.data.message.toString()
                        }
                        if (it.data.statusCode == "S441") {
                            ListShakeGridBoxActivity.numberGiftUser = 0
                        }
                    }
                    DialogHelper.showNotification(
                        this,
                        message,
                        false,
                        object : NotificationDialogListener {
                            override fun onDone() {
                                onBackPressed()
                            }
                        })
                }
            }
        })
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mShakeDetector.registerSensor()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestResultCount) {
                Intent().apply {
                    putExtra(Constant.DATA_2, data?.getIntExtra(Constant.DATA_2, 0))
                    putExtra(Constant.DATA_3, data?.getStringExtra(Constant.DATA_3)) // id campaign
                    putExtra(Constant.DATA_4, data?.getStringExtra(Constant.DATA_4)) // rewardType
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }
        }
    }

    override fun onBackPressed() {
        Intent().apply {
            setResult(RESULT_OK, this)
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        mShakeDetector.unRegisterSensor()
    }

    override fun onDestroy() {
        super.onDestroy()
        mShakeDetector.unRegisterSensor()
    }
}