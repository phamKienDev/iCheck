package vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_open_reward_box.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.network.models.ICUnBox_Gift
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.MyGiftWareHouseActivity
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box.presenter.OpenRewardBoxPresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box.view.IOpenRewardBoxView
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class OpenRewardBoxActivity : BaseActivity<OpenRewardBoxPresenter>(), IOpenRewardBoxView {

    private var position = 0
    private var mId: String? = null
    private var mNumber: Int = 0
    private var mBusiness_type: Int = 0
    private var slotUnbox: Int = 0
    private var urlImage: String = ""
    private var title: String? = null

    private var player: MediaPlayer? = null
    private var vibrate: Vibrator? = null

    companion object {
        fun start(activity: FragmentActivity) {
            ListCampaignInteractor().getListBoxReward(0, 1, object : ICApiListener<ICListResponse<ICBoxReward>> {
                override fun onSuccess(obj: ICListResponse<ICBoxReward>) {
                    if (obj.rows.isNullOrEmpty()) {
//                        ActivityUtils.startActivity<ListMissionActivity>(activity)
                    } else {
                        ActivityUtils.startActivity<OpenRewardBoxActivity, ICBoxReward>(activity, Constant.DATA_1, obj.rows[0])
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    ActivityUtils.startActivity<MyGiftWareHouseActivity>(activity)
                }
            })
        }
    }

    override val getLayoutID: Int
        get() = R.layout.activity_open_reward_box

    override val getPresenter: OpenRewardBoxPresenter
        get() = OpenRewardBoxPresenter(this)

    override fun onInitView() {
        presenter.onGetDataId(intent)
        vibrate = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnUnBox.setOnClickListener {
            val countBoxAnimation = if (mNumber > 12) {
                12
            } else {
                mNumber
            }

            if (countBoxAnimation > 0) {
                if (mBusiness_type == 1) {
                    presenter.onUnboxGift(mId, countBoxAnimation)
                } else {
                    presenter.onUnboxGift(mId, 1)
                }

                if (mBusiness_type == 1) {
                    val ctBoxGift = layoutContent.getChildAt(0) as ConstraintLayout

                    for (i in 0 until ctBoxGift.childCount) {
                        val v = ctBoxGift.getChildAt(i) as View
                        val shake = AnimationUtils.loadAnimation(this, R.anim.vibrate)

                        shake.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(p0: Animation?) {

                            }

                            override fun onAnimationEnd(p0: Animation?) {
                            }

                            override fun onAnimationStart(p0: Animation?) {
                                btnUnBox.animate().alpha(0f)
                                btnUnBox.visibility = View.GONE
                                btnUnBox.isClickable = false
                            }
                        })
                        v.startAnimation(shake)
                    }
                } else {
                    val childCount = layoutContent.childCount
                    for (i in 0 until childCount) {
                        val v = layoutContent.getChildAt(i)
                        val shake = AnimationUtils.loadAnimation(this, R.anim.vibrate)
                        shake.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(p0: Animation?) {}

                            override fun onAnimationEnd(p0: Animation?) {}

                            override fun onAnimationStart(p0: Animation?) {
                                btnUnBox.animate().alpha(0f)
                                btnUnBox.visibility = View.GONE
                                btnUnBox.isClickable = false
                            }
                        })

                        v.startAnimation(shake)
                    }
                }
            } else {
                DialogHelper.showNotification(this@OpenRewardBoxActivity, R.string.so_luong_qua_da_het_hay_kiem_them_de_mo_qua_nhe, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }

        btnNext.setOnClickListener {
            if (layoutContent.getChildAt(0).animation != null) {
                return@setOnClickListener
            }
            tvCountBoxGift.visibility = View.VISIBLE
            tvMess1.visibility = View.GONE
            tvMess2.visibility = View.GONE
            tvSubMessSuccess.visibility = View.GONE
            tvMessSuccess.visibility = View.GONE
            removeListGift()
        }
    }

    private fun initDataIcheckSeller() {
        val layoutBox = LayoutInflater.from(this).inflate(R.layout.layout_list_box_gift, layoutContent, false) as ConstraintLayout

        val count = if (mNumber > 12) {
            12
        } else {
            mNumber
        }

        for (i in 0 until count) {
            when (i) {
                0 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img1).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box1).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img1), urlImage)
                }

                1 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img2).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box2).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img2), urlImage)
                }

                2 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img3).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box3).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img3), urlImage)
                }

                3 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img4).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box4).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img4), urlImage)
                }

                4 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img5).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box5).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img5), urlImage)
                }

                5 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img6).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box6).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img6), urlImage)
                }

                6 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img7).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box7).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img7), urlImage)
                }

                7 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img8).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box8).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img8), urlImage)
                }

                8 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img9).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box9).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img9), urlImage)
                }

                9 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img10).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box10).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img10), urlImage)
                }

                10 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img11).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box11).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img11), urlImage)
                }

                11 -> {
                    layoutBox.findViewById<AppCompatImageButton>(R.id.img12).visibility = View.VISIBLE
                    layoutBox.findViewById<AppCompatImageView>(R.id.box12).setImageResource(R.drawable.ic_gift_box_icheck)
                    WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.img12), urlImage)
                }
            }
        }

        layoutContent.addView(layoutBox)
    }

    private fun initDataNormalSeller() {
        val layoutBox = LayoutInflater.from(this).inflate(R.layout.layout_list_one_box_gift, layoutContent, false) as ConstraintLayout

        layoutBox.findViewById<AppCompatImageButton>(R.id.imgBig1).visibility = View.VISIBLE
        WidgetUtils.loadImageUrlRounded4(layoutBox.findViewById<AppCompatImageButton>(R.id.imgBig1), urlImage)
        layoutBox.findViewById<AppCompatImageView>(R.id.box_big).setImageResource(R.drawable.ic_gift_box_normal_seller)
        layoutContent.addView(layoutBox)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetDataIdSuccess(item: ICBoxReward) {
        item.business_type?.let {
            mBusiness_type = it
        }

        mId = item.id
        mNumber = item.number!!.toInt()
        if (!item.logo.isNullOrEmpty()) {
            urlImage = item.logo!!
        }

        txtTitle.text = item.title
        title = item.title
        tvCountBoxGift.text = Html.fromHtml("Bạn đang có " + "<b><font color=#ffaf26>${item.number} hộp quà</font></b>" + " từ " + item.title)
        WidgetUtils.loadImageUrl(imgAvatar, item.logo)

        slotUnbox = if (mNumber > 12) {
            12
        } else {
            mNumber
        }

        if (item.business_type == 1) {
            initDataIcheckSeller()
        } else {
            initDataNormalSeller()
        }

        if (mBusiness_type == 1) {
            btnUnBox.text = "Đập quà " + slotUnbox + "/" + item.number
        } else {
            btnUnBox.text = "Đập quà " + 1 + "/" + item.number
        }

    }

    override fun onGetDataError(type: Int) {
        when (type) {
            Constant.ERROR_INTERNET -> {
                DialogHelper.showNotification(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
            Constant.ERROR_UNKNOW -> {
                DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }
    }

    override fun onUnboxGiftSuccess(obj: MutableList<ICUnBox_Gift>, numberUnboxIcheck: Int) {
        position = 0
        updateData(obj)
        setResult(Activity.RESULT_OK)
        mNumber -= obj.size

        tvCountBoxGift.text = Html.fromHtml("Bạn đang có " + "<b><font color=#ffaf26>${mNumber} hộp quà</font></b>" + " " + title)

        if (mBusiness_type == 1) {
            btnUnBox.text = if (mNumber > 12) {
                "Đập quà 12/$mNumber"
            } else {
                "Đập quà $mNumber/$mNumber"
            }
        } else {
            btnUnBox.text = if (mNumber > 1) {
                "Đập quà 1/$mNumber"
            } else {
                "Đập quà $mNumber/$mNumber"
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun playAudio() {
        val sound = SettingManager.getSoundSetting
        Log.e("sou", sound.toString())
        killMediaPlayer()

        if (sound) {
            try {
                player = MediaPlayer.create(this, R.raw.cheerful)
                player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                player?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            killMediaPlayer()
        }
    }

    private fun killMediaPlayer() {
        try {
            player?.release()
            player = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runVibrate() {
        val vibra = SettingManager.getVibrateSetting
        Log.e("vii", vibra.toString())
        if (vibra) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrate?.vibrate(500)
            }
        } else {
            vibrate?.cancel()
        }
    }

    private fun updateData(list: MutableList<ICUnBox_Gift>) {
        if (layoutContent == null) {
            return
        }
        if (mBusiness_type == 1) {
            if (list.isNotEmpty()) {
                Handler().postDelayed({
                    val layoutBox = layoutContent.getChildAt(0) as ConstraintLayout
                    when (position) {
                        0 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img1).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box1), layoutBox.findViewById(R.id.tvName1), null, null, null)
                        }

                        1 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img2).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box2), layoutBox.findViewById(R.id.tvName2), null, null, null)
                        }

                        2 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img3).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box3), layoutBox.findViewById(R.id.tvName3), null, null, null)
                        }

                        3 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img4).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box4), layoutBox.findViewById(R.id.tvName4), null, null, null)
                        }

                        4 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img5).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box5), layoutBox.findViewById(R.id.tvName5), null, null, null)
                        }

                        5 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img6).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box6), layoutBox.findViewById(R.id.tvName6), null, null, null)
                        }

                        6 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img7).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box7), layoutBox.findViewById(R.id.tvName7), null, null, null)
                        }

                        7 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img8).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box8), layoutBox.findViewById(R.id.tvName8), null, null, null)
                        }

                        8 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img9).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box9), layoutBox.findViewById(R.id.tvName9), null, null, null)
                        }

                        9 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img10).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box10), layoutBox.findViewById(R.id.tvName10), null, null, null)
                        }

                        10 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img11).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box11), layoutBox.findViewById(R.id.tvName11), null, null, null)
                        }

                        11 -> {
                            layoutBox.findViewById<AppCompatImageButton>(R.id.img12).visibility = View.GONE
                            setData(list[0], layoutBox.findViewById(R.id.box12), layoutBox.findViewById(R.id.tvName12), null, null, null)
                        }
                    }

                    position++
                    list.removeAt(0)
                    updateData(list)
                }, 500)
            }
        } else {
            if (list.isNotEmpty()) {
                val childCount = layoutContent.childCount
                for (i in 0 until childCount) {
                    Handler().postDelayed({
                        val layoutBox = layoutContent.getChildAt(0) as ConstraintLayout
                        layoutBox.findViewById<AppCompatImageButton>(R.id.imgBig1).visibility = View.GONE
                        setData(list[i], layoutBox.findViewById(R.id.box_big), null, layoutBox.findViewById(R.id.img_bg), layoutBox.findViewById(R.id.imgDotTop), layoutBox.findViewById(R.id.imgDotBottom))
                    }, 500)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(obj: ICUnBox_Gift, image: AppCompatImageView?, nameQua: AppCompatTextView?, bg: AppCompatImageView?, imgDotTop: AppCompatImageView?, imgDotBottom: AppCompatImageView?) {
        image?.setBackgroundColor(Color.TRANSPARENT)
        when (obj.type) {
            0 -> {
                if (mBusiness_type == 1) {
                    image?.setImageResource(R.drawable.ic_gift_unbox_icheck)
                } else {
                    tvCountBoxGift.visibility = View.GONE
                    tvMess1.visibility = View.VISIBLE
                    tvMess2.visibility = View.VISIBLE
                    image?.setImageResource(R.drawable.ic_gift_unbox_emty_big)
                }
            }
            1 -> {
                playAudio()
                runVibrate()
                if (obj.image.isNullOrEmpty()) {
                    if (mBusiness_type == 1) {
                        image?.setImageResource(R.drawable.ic_default_square)
                        nameQua?.text = obj.name
                    } else {
                        tvCountBoxGift.visibility = View.GONE
                        tvSubMessSuccess.visibility = View.VISIBLE
                        tvMessSuccess.visibility = View.VISIBLE
                        tvMessSuccess.text = obj.name
                        image?.setImageResource(R.drawable.ic_default_square)
                        playBackgroundAnimation(bg)
                        playDotLightAnimation(imgDotTop, imgDotBottom)
                    }
                } else {
                    image?.let {
                        if (mBusiness_type == 1) {
                            WidgetUtils.loadImageUrlRoundedFitCenter(it, obj.image, R.drawable.ic_default_square, SizeHelper.size5)
                            nameQua?.text = obj.name
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = obj.name
                            WidgetUtils.loadImageUrlRoundedFitCenter(it, obj.image, R.drawable.ic_default_square, SizeHelper.size5)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                }
            }
            2 -> {
                playAudio()
                runVibrate()
                if (obj.image.isNullOrEmpty()) {
                    if (mBusiness_type == 1) {
                        image?.setImageResource(R.drawable.ic_default_square)
                        nameQua?.text = obj.name
                    } else {
                        tvCountBoxGift.visibility = View.GONE
                        tvSubMessSuccess.visibility = View.VISIBLE
                        tvMessSuccess.visibility = View.VISIBLE
                        tvMessSuccess.text = obj.name
                        image?.setImageResource(R.drawable.ic_default_square)
                        playBackgroundAnimation(bg)
                        playDotLightAnimation(imgDotTop, imgDotBottom)
                    }
                } else {
                    image?.let {
                        if (mBusiness_type == 1) {
                            WidgetUtils.loadImageUrlRoundedFitCenter(it, obj.image, R.drawable.ic_default_square, SizeHelper.size5)
                            nameQua?.text = obj.name
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = obj.name
                            WidgetUtils.loadImageUrlRoundedFitCenter(it, obj.image, R.drawable.ic_default_square, SizeHelper.size5)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                }
            }
            3 -> {
                playAudio()
                runVibrate()
                playBackgroundAnimation(bg)
                when (obj.icoin) {
                    Constant.NAMTRAM -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_500k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_500k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.HAITRAM -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_200k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_200k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.MOTTRAM -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_100k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_100k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.NAMMUOINGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_50k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_50k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.HAIMUOINGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_20k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_20k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.MUOINGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_10k_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_10k_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.NAMNGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_5000_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_5000_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.HAINGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_2000_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_2000_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.MOTNGHIN -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_1000_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_1000_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.NAMTRAMDONG -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_500_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_500_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.HAITRAMDONG -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_200_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_200_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.MOTTRAMDONG -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_100_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_100_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.NAMMUOI -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_50_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_50_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.HAIMUOI -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_20_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_20_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                    Constant.MUOI -> {
                        if (mBusiness_type == 1) {
                            image?.setImageResource(R.drawable.ic_icoin_10_80_dp)
                        } else {
                            tvCountBoxGift.visibility = View.GONE
                            tvSubMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.visibility = View.VISIBLE
                            tvMessSuccess.text = TextHelper.formatMoney(obj.icoin) + " iCoin"
                            image?.setImageResource(R.drawable.ic_icoin_10_big_dp)
                            playBackgroundAnimation(bg)
                            playDotLightAnimation(imgDotTop, imgDotBottom)
                        }
                    }
                }
            }
            else -> {
                if (mBusiness_type == 1)
                    image?.setImageResource(R.drawable.ic_gift_unbox_icheck)
                else
                    image?.setImageResource(R.drawable.ic_gift_unbox_emty_big)
            }
        }
        btnNext.animate().alpha(1f)
        btnNext.visibility = View.VISIBLE
    }

    private fun removeListGift() {
        playAnimation(layoutContent.getChildAt(0), R.anim.right_to_left_exit, true)
    }

    private fun playAnimation(view: View, resource: Int, isRemove: Boolean) {
        val animation = AnimationUtils.loadAnimation(view.context, resource)
        animation.duration = 400

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if (isRemove) {
                    Handler(Looper.getMainLooper()).post {
                        layoutContent.removeViewAt(0)
                        if (mBusiness_type == 1) {
                            initDataIcheckSeller()
                            btnNext.animate().alpha(0f)
                            btnUnBox.animate().alpha(1f)
                            btnNext.visibility = View.GONE
                            btnUnBox.visibility = View.VISIBLE
                            btnUnBox.isClickable = true
                        } else {
                            initDataNormalSeller()
                            btnNext.animate().alpha(0f)
                            btnUnBox.animate().alpha(1f)
                            btnNext.visibility = View.GONE
                            btnUnBox.visibility = View.VISIBLE
                            btnUnBox.isClickable = true
                        }
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        view.startAnimation(animation)
    }

    private fun playBackgroundAnimation(bg: AppCompatImageView?) {
        bg?.visibility = View.VISIBLE
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 5000
        rotate.repeatCount = -1
        rotate.interpolator = LinearInterpolator()
        bg?.startAnimation(rotate)
    }

    private fun playDotLightAnimation(imgDotTop: AppCompatImageView?, imgDotBottom: AppCompatImageView?) {
        if (imgDotTop != null && imgDotBottom != null) {
            imgDotTop.visibility = View.VISIBLE
            imgDotBottom.visibility = View.VISIBLE

// Dot TOP
            val startX = imgDotTop.x
            val startY = imgDotTop.y

            val obxTop = ObjectAnimator.ofFloat(imgDotTop, "translationX", 0f, -200f)
            obxTop.repeatCount = -1
            obxTop.duration = 1300
            obxTop.interpolator = AccelerateDecelerateInterpolator()
            obxTop.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    imgDotTop.translationX = startX
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })

            val alphaTop = ObjectAnimator.ofFloat(imgDotTop, "transitionAlpha", 1f, 0f)
            alphaTop.repeatCount = -1
            alphaTop.duration = 1300

            val obyTop = ObjectAnimator.ofFloat(imgDotTop, "translationY", 0f, -200f)
            obyTop.repeatCount = -1
            obyTop.duration = 1300
            obyTop.interpolator = AccelerateDecelerateInterpolator()
            obyTop.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    imgDotTop.translationY = startY
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })

            obxTop.start()
            obyTop.start()
            alphaTop.start()

//Dot BOTTOM
            val startbottomX = imgDotBottom.x
            val startbottomY = imgDotBottom.y

            val obxBottom = ObjectAnimator.ofFloat(imgDotBottom, "translationX", 0f, 200f)
            obxBottom.repeatCount = -1
            obxBottom.duration = 1300
            obxBottom.interpolator = AccelerateDecelerateInterpolator()
            obxBottom.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    imgDotBottom.translationX = startbottomX
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })

            val alphaBottom = ObjectAnimator.ofFloat(imgDotBottom, "transitionAlpha", 1f, 0f)
            alphaBottom.repeatCount = -1
            alphaBottom.duration = 1300

            val obyBottom = ObjectAnimator.ofFloat(imgDotBottom, "translationY", 0f, 200f)
            obyBottom.repeatCount = -1
            obyBottom.duration = 1300
            obyBottom.interpolator = AccelerateDecelerateInterpolator()
            obyBottom.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    imgDotBottom.translationY = startbottomY
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })

            obxBottom.start()
            obyBottom.start()
            alphaBottom.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }
}
