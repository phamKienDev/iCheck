package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_lucky_wheel.*
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_1
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_2
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_3
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_4
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_5
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_6
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_7
import kotlinx.android.synthetic.main.lucky_wheel_eight.view.img_8
import kotlinx.android.synthetic.main.lucky_wheel_six.view.*
import kotlinx.android.synthetic.main.lucky_wheel_ten.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.dialog.DialogErrorLoyalty
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.PlayGameResp
import vn.icheck.android.loyalty.dialog.DialogGuidePlayGame
import vn.icheck.android.loyalty.dialog.DialogOotGame
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.GameActivity
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.animations.*
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModelFactory
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class FragmentLuckyWheelGame : Fragment() {
    private val args: FragmentLuckyWheelGameArgs by navArgs()
    private val luckyGameViewModel: LuckyGameViewModel by activityViewModels {
        LuckyGameViewModelFactory(requireActivity(), args.campaignId, args.playCount)
    }
    private var spinning = false
    private var update = false

    private lateinit var rotateWheelAnimations: Animation
    private var listImageView = arrayListOf<ImageView>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lucky_wheel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!update) {
            luckyGameViewModel.updatePlay(args.playCount)
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnim()
        initWheel()
        initViews()
        Glide.with(requireContext().applicationContext)
                .load(args.data.campaign?.header_image_rotation?.original)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(img_banner)

        Glide.with(requireContext().applicationContext)
                .load(args.data.campaign?.background_rotation?.original)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .error(R.drawable.ic_background_game)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(img_bg_game)

        luckyGameViewModel.getListWinnerLive().observe(viewLifecycleOwner, { listWinner ->
            val text = StringBuilder()
            if (listWinner != null) {
                try {
                    for (item in listWinner.data?.rows!!) {
                        var name = item?.name
                        if (name.isNullOrEmpty()) {
                            name = rText(R.string.nguoi_choi)
                        }
                        text.append(rText(R.string.s_da_trung_s, name, item?.winnerGifts?.firstOrNull()?.gift?.name))
                    }
                } catch (e: Exception) {

                }
            }
            marquee_text.setText(text.toString())
            marquee_text.isSelected = true
        })
        luckyGameViewModel.playCount.observe(viewLifecycleOwner, {
            setTotalPlay(it)
        })
        when (args.bitmapMap.size) {
            8 -> {
                initEightWheelGame()
            }
            6 -> {
                initSixWheelGame()
            }
            10 -> {
                initTenWheelGame()
            }
        }
        (requireActivity() as GameActivity).inGame()
    }

    private fun initImage(size: Int) {
        when (args.bitmapMap.size) {
            6 -> {
                for (index in args.bitmapMap.indices) {
                    setImage(index, size / 5f)
                }
            }
            8 -> {
                for (index in args.bitmapMap.indices) {
                    if (index < listImageView.size) {
                        setImage(index, size * 0.14f)
                    }
                }
            }
            10 -> {
                for (index in args.bitmapMap.indices) {
                    setImage(index, size * .14f)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as GameActivity).outGame()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        Handler(Looper.getMainLooper()).post {
            when (event.type) {
                ICMessageEvent.Type.NMDT -> {
                    if (args.hasChanceCode) {
                        Handler().postDelayed({
                            val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToNmdtDialogFragment(args.campaignId, luckyGameViewModel.currentCount)
                            findNavController().navigate(action)
                        }, 400)
                    } else {
                        Handler().postDelayed({
                            LoyaltySdk.openActivity("scan?typeLoyalty=mini_game&campaignId=${args.campaignId}&nameCampaign=${args.campaignName}&nameShop=${args.shopName}&avatarShop=${args.avatarShop}&currentCount=${luckyGameViewModel.currentCount}")
//                            val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToScanForGameFragment(luckyGameViewModel.currentCount, args.campaignId, args.campaignName, args.shopName, args.avatarShop)
//                            findNavController().navigate(action)
                        }, 400)
                    }
                }
                ICMessageEvent.Type.UPDATE_COUNT_GAME -> {
                    update = true

                    if (event.data != null){
                        val count = event.data as Long?
                        luckyGameViewModel.updatePlay(count?.toInt())
                    }

                }
                ICMessageEvent.Type.SCAN_GAME -> {
                    Handler().postDelayed({
                        LoyaltySdk.openActivity("scan?typeLoyalty=mini_game&campaignId=${args.campaignId}&nameCampaign=${args.campaignName}&nameShop=${args.shopName}&avatarShop=${args.avatarShop}&currentCount=${luckyGameViewModel.currentCount}")
//                        val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToScanForGameFragment(luckyGameViewModel.currentCount, args.campaignId, args.campaignName, args.shopName, args.avatarShop)
//                        findNavController().navigate(action)
                    }, 400)
                }
            }
        }
    }

    private fun setImage(index: Int, size: Float) {
        if (index < listImageView.size) {
            Glide.with(requireContext().applicationContext)
                    .load(args.bitmapMap[index])
                    .centerCrop()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .override(size.toInt(), size.toInt())
                    .into(listImageView[index])
        }
    }

    private fun setTotalPlay(count: Int?) {
        if (count != null) {
            tv_total_turn.rText(R.string.ban_co_d_luot_quay, count)
        }
    }

    private fun initTenWheelGame() {
        luckyWheel.visibility = View.GONE
        luckyWheelEight.visibility = View.GONE
        luckyWheelTen.visibility = View.VISIBLE
        img_vanh.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                img_vanh.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val lp = luckyWheelTen.layoutParams
                val width = img_vanh.width
                Glide.with(requireContext().applicationContext)
                        .load(R.drawable.center)
                        .override(width / 4, width / 4)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_center_wheel)
                lp.height = width
                initCursor(width, img_vanh.height)
                luckyWheelTen.layoutParams = lp
                initImage(width)

                val lp1 = luckyWheelTen.img_1.layoutParams as ConstraintLayout.LayoutParams
                lp1.setMargins(0, (0.12f * width).toInt(), 0, 0)
                luckyWheelTen.img_1.layoutParams = lp1

                val lp2 = luckyWheelTen.img_2.layoutParams as ConstraintLayout.LayoutParams
                lp2.setMargins(0, (0.18f * width).toInt(), (0.25f * width).toInt(), 0)
                luckyWheelTen.img_2.layoutParams = lp2

                val lp3 = luckyWheelTen.img_3.layoutParams as ConstraintLayout.LayoutParams
                lp3.setMargins(0, (0.33f * width).toInt(), (0.13f * width).toInt(), 0)
                luckyWheelTen.img_3.layoutParams = lp3

                val lp4 = luckyWheelTen.img_4.layoutParams as ConstraintLayout.LayoutParams
                lp4.setMargins(0, 0, (0.13f * width).toInt(), (0.33f * width).toInt())
                luckyWheelTen.img_4.layoutParams = lp4

                val lp5 = luckyWheelTen.img_5.layoutParams as ConstraintLayout.LayoutParams
                lp5.setMargins(0, 0, (0.25f * width).toInt(), (0.18f * width).toInt())
                luckyWheelTen.img_5.layoutParams = lp5

                val lp6 = luckyWheelTen.img_6.layoutParams as ConstraintLayout.LayoutParams
                lp6.setMargins(0, 0, 0, (0.12f * width).toInt())
                luckyWheelTen.img_6.layoutParams = lp6

                val lp7 = luckyWheelTen.img_7.layoutParams as ConstraintLayout.LayoutParams
                lp7.setMargins((0.25f * width).toInt(), 0, 0, (0.18f * width).toInt())
                luckyWheelTen.img_7.layoutParams = lp7

                val lp8 = luckyWheelTen.img_8.layoutParams as ConstraintLayout.LayoutParams
                lp8.setMargins((0.13f * width).toInt(), 0, 0, (0.33f * width).toInt())
                luckyWheelTen.img_8.layoutParams = lp8

                val lp9 = luckyWheelTen.img_9.layoutParams as ConstraintLayout.LayoutParams
                lp9.setMargins((0.13f * width).toInt(), (0.33f * width).toInt(), 0, 0)
                luckyWheelTen.img_9.layoutParams = lp9

                val lp10 = luckyWheelTen.img_10.layoutParams as ConstraintLayout.LayoutParams
                lp10.setMargins((0.25f * width).toInt(), (0.18f * width).toInt(), 0, 0)
                luckyWheelTen.img_10.layoutParams = lp10

            }
        })

        listImageView.clear()
        listImageView.add(luckyWheelTen.img_1)
        listImageView.add(luckyWheelTen.img_2)
        listImageView.add(luckyWheelTen.img_3)
        listImageView.add(luckyWheelTen.img_4)
        listImageView.add(luckyWheelTen.img_5)
        listImageView.add(luckyWheelTen.img_6)
        listImageView.add(luckyWheelTen.img_7)
        listImageView.add(luckyWheelTen.img_8)
        listImageView.add(luckyWheelTen.img_9)
        listImageView.add(luckyWheelTen.img_10)
    }

    private fun initEightWheelGame() {
        luckyWheel.visibility = View.GONE
        luckyWheelEight.visibility = View.VISIBLE
        luckyWheelTen.visibility = View.GONE
        img_vanh.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                img_vanh.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val lp = luckyWheelEight.layoutParams
                val width = img_vanh.width
                lp.height = width
                initCursor(width, img_vanh.height)
                luckyWheelEight.layoutParams = lp
                initImage(width)

                val lp1 = luckyWheelEight.img_1.layoutParams as ConstraintLayout.LayoutParams
                lp1.setMargins(0, (0.16f * width).toInt(), 0, 0)
                luckyWheelEight.img_1.layoutParams = lp1

                val lp2 = luckyWheelEight.img_2.layoutParams as ConstraintLayout.LayoutParams
                lp2.setMargins(0, (0.24f * width).toInt(), (0.25f * width).toInt(), 0)
                luckyWheelEight.img_2.layoutParams = lp2

                val lp3 = luckyWheelEight.img_3.layoutParams as ConstraintLayout.LayoutParams
                lp3.setMargins(0, 0, (0.16f * width).toInt(), 0)
                luckyWheelEight.img_3.layoutParams = lp3

                val lp4 = luckyWheelEight.img_4.layoutParams as ConstraintLayout.LayoutParams
                lp4.setMargins(0, 0, (0.24f * width).toInt(), (0.24f * width).toInt())
                luckyWheelEight.img_4.layoutParams = lp4

                val lp5 = luckyWheelEight.img_5.layoutParams as ConstraintLayout.LayoutParams
                lp5.setMargins(0, 0, 0, (0.16f * width).toInt())
                luckyWheelEight.img_5.layoutParams = lp5

                val lp6 = luckyWheelEight.img_6.layoutParams as ConstraintLayout.LayoutParams
                lp6.setMargins((0.24f * width).toInt(), 0, 0, (0.24f * width).toInt())
                luckyWheelEight.img_6.layoutParams = lp6

                val lp7 = luckyWheelEight.img_7.layoutParams as ConstraintLayout.LayoutParams
                lp7.setMargins((0.16f * width).toInt(), 0, 0, 0)
                luckyWheelEight.img_7.layoutParams = lp7

                val lp8 = luckyWheelEight.img_8.layoutParams as ConstraintLayout.LayoutParams
                lp8.setMargins((0.24f * width).toInt(), (0.24f * width).toInt(), 0, 0)
                luckyWheelEight.img_8.layoutParams = lp8

            }
        })
        listImageView.clear()
        listImageView.add(luckyWheelEight.img_1)
        listImageView.add(luckyWheelEight.img_2)
        listImageView.add(luckyWheelEight.img_3)
        listImageView.add(luckyWheelEight.img_4)
        listImageView.add(luckyWheelEight.img_5)
        listImageView.add(luckyWheelEight.img_6)
        listImageView.add(luckyWheelEight.img_7)
        listImageView.add(luckyWheelEight.img_8)
    }

    private fun initSixWheelGame() {
        luckyWheel.visibility = View.VISIBLE
        luckyWheelEight.visibility = View.GONE
        luckyWheelTen.visibility = View.GONE
        img_vanh.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                img_vanh.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = img_vanh.width

                Glide.with(requireContext().applicationContext)
                        .load(R.drawable.center)
                        .override(width / 4, width / 4)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_center_wheel)

                val lp = luckyWheelTen.layoutParams
                lp.height = width
                initCursor(width, img_vanh.height)
                luckyWheel.layoutParams = lp
                initImage(width)

                val lp1 = luckyWheel.img_one.layoutParams as ConstraintLayout.LayoutParams
                lp1.setMargins(0, (width * 0.1).toInt(), 0, 0)
                luckyWheel.img_one.layoutParams = lp1

                val lp2 = luckyWheel.img_second.layoutParams as ConstraintLayout.LayoutParams
                lp2.setMargins(0, ((width / 2 - width * 0.25f)).toInt(), (width * 0.14).toInt(), 0)
                luckyWheel.img_second.layoutParams = lp2

                val lp3 = luckyWheel.img_third.layoutParams as ConstraintLayout.LayoutParams
                lp3.setMargins(0, 0, (width * 0.14).toInt(), ((width / 2 - width * 0.25f)).toInt())
                luckyWheel.img_third.layoutParams = lp3

                val lp4 = luckyWheel.img_four.layoutParams as ConstraintLayout.LayoutParams
                lp4.setMargins(0, 0, 0, (width * 0.1).toInt())
                luckyWheel.img_four.layoutParams = lp4

                val lp5 = luckyWheel.img_five.layoutParams as ConstraintLayout.LayoutParams
                lp5.setMargins((width * 0.14).toInt(), 0, 0, ((width / 2 - width * 0.25f)).toInt())
                luckyWheel.img_five.layoutParams = lp5

                val lp6 = luckyWheel.img_six.layoutParams as ConstraintLayout.LayoutParams
                lp6.setMargins((width * 0.14).toInt(), ((width / 2 - width * 0.25f)).toInt(), 0, 0)
                luckyWheel.img_six.layoutParams = lp6
            }
        })
        listImageView.clear()
        listImageView.add(luckyWheel.img_one)
        listImageView.add(luckyWheel.img_second)
        listImageView.add(luckyWheel.img_third)
        listImageView.add(luckyWheel.img_four)
        listImageView.add(luckyWheel.img_five)
        listImageView.add(luckyWheel.img_six)
    }

    fun initCursor(size: Int, height: Int) {
        Glide.with(requireContext().applicationContext)
                .load(R.drawable.ic_cursor_gold)
                .format(DecodeFormat.PREFER_RGB_565)
                .override(size / 6, size / 4)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(img_cursor)
        val lp = img_cursor.layoutParams as ConstraintLayout.LayoutParams
        lp.setMargins(0, 0, 0, (height - size) / 2 + size - size / 8)
        img_cursor.layoutParams = lp
        val tvlp = tv_total_turn.layoutParams as ConstraintLayout.LayoutParams
        tvlp.setMargins(0, 0, 0, (height - size) / 6)
        tv_total_turn.layoutParams = tvlp
    }

    /**
     * Set animation to background and star and center image
     */
    private fun startAnim() {
        img_bg_game.startAnimation(fadeAnimation)
        img_star.startAnimation(starAnimation)
        img_vanh_off.startAnimation(revealAnim)
        img_center_wheel.startAnimation(centerScaleAnimation)
        img_tv_quay.startAnimation(centerScaleAnimation)
    }

    private fun stopAnim() {
        img_bg_game.clearAnimation()
        img_star.clearAnimation()
        img_vanh_off.clearAnimation()
        img_center_wheel.clearAnimation()
        img_center_wheel.startAnimation(centerScaleAnimation)
    }

    fun showSpin() {
        img_center_wheel.visibility = View.VISIBLE
        img_tv_quay.visibility = View.VISIBLE
    }

    fun hideSpin() {
        img_center_wheel.clearAnimation()
        img_center_wheel.visibility = View.INVISIBLE
        img_tv_quay.visibility = View.INVISIBLE
    }

    /**
     * Init wheel
     */
    private fun initWheel() {
        img_tv_quay.setOnClickListener {
            if (NetworkHelper.isNotConnected(context)) {
                object : DialogErrorLoyalty(requireContext(), R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)) {

                }.show()
                return@setOnClickListener
            }
            if (!spinning) {
                if (luckyGameViewModel.currentCount > 0) {
                    playClickSound()
                    spinning = true
                    startEaseInAnim(args.bitmapMap.size)
                    luckyGameViewModel.playGame(args.campaignId).observe(viewLifecycleOwner, Observer { response ->
                        stopEaseInAnim(args.bitmapMap.size)
                        if (response != null) {
                            luckyGameViewModel.updatePlay(response.data?.play)
                            if (response.data?.gift != null) {
                                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_GAME))
                                spinToGift(response)
                            } else {
                                spinToNoGift()
                            }
                        }
                    })
                } else {
                    luckyGameViewModel.playGame(args.campaignId).observe(viewLifecycleOwner, Observer { response ->
                        try {
                            if (response?.data?.gift != null) {
                                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_GAME))
                                spinning = true
                                playClickSound()
                                luckyGameViewModel.updatePlay(response.data.play)
                                spinToGift(response)
                            } else {
                                if (args.hasChanceCode) {
                                    object : DialogOotGame(requireContext(),
                                            rText(R.string.tiec_qua_ban_khong_co_luot_quay_nao),
                                            rText(R.string.nhap_ma_de_nhan_them_luot_quay_va_co_co_hoi_trung_hang_ngan_giai_thuong_hap_dan_nao),
                                            R.drawable.ic_vqmm_het_luot) {
                                        override fun onClick() {
                                            val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToNmdtDialogFragment(args.campaignId, luckyGameViewModel.currentCount)
                                            findNavController().navigate(action)
                                        }
                                    }.show()
                                }else{
                                    object : DialogOotGame(requireContext(),
                                            rText(R.string.tiec_qua_ban_khong_co_luot_quay_nao),
                                        rText(R.string.quet_tem_de_nhan_them_luot_quay_va_co_co_hoi_trung_hang_ngan_giai_thuong_hap_dan_nao),
                                            R.drawable.ic_vqmm_het_luot) {
                                        override fun onClick() {
                                            object : DialogGuidePlayGame(requireContext()) {
                                                override fun onClick() {
//                                                    val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToScanForGameFragment(luckyGameViewModel.currentCount, args.campaignId, args.campaignName, args.shopName, args.avatarShop)
//                                                    findNavController().navigate(action)
                                                    LoyaltySdk.openActivity("scan?typeLoyalty=mini_game&campaignId=${args.campaignId}&nameCampaign=${args.campaignName}&nameShop=${args.shopName}&avatarShop=${args.avatarShop}&currentCount=${luckyGameViewModel.currentCount}")
                                                }
                                            }.show()
                                        }
                                    }.show()
                                }
                            }
                        } catch (e: Exception) {
                            object : DialogErrorLoyalty(requireContext(), R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)) {

                            }.show()
                        }
                    })
                }
            }
        }
    }

    private fun initViews() {

        imgBack.setOnClickListener {
            if (!spinning) {
                activity?.finish()
            }
        }
        btnThemLuot.setOnClickListener {
            if (!spinning) {
                it.startAnimation(scaleAnimation)
                lifecycleScope.launch {
                    delay(240)
                    if (SharedLoyaltyHelper(requireContext()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)) {
                        val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToNmdtDialogFragment(args.campaignId, luckyGameViewModel.currentCount)
                        findNavController().navigate(action)
                    } else {
                        object : DialogGuidePlayGame(requireContext()) {
                            override fun onClick() {
//                                val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToScanForGameFragment(luckyGameViewModel.currentCount, args.campaignId, args.campaignName, args.shopName, args.avatarShop)
//                                findNavController().navigate(action)
                                LoyaltySdk.openActivity("scan?typeLoyalty=mini_game&campaignId=${args.campaignId}&nameCampaign=${args.campaignName}&nameShop=${args.shopName}&avatarShop=${args.avatarShop}&currentCount=${luckyGameViewModel.currentCount}")
                            }
                        }.show()
                    }
                }
            }
        }
        btnDSQua.setOnClickListener {
            if (!spinning) {
                it.startAnimation(scaleAnimation)
                lifecycleScope.launch {
                    delay(240)
                    val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToListOfGiftReceived(args.campaignId, luckyGameViewModel.currentCount)
                    findNavController().navigate(action)
                }
            }
        }
        btnTheWinner.setOnClickListener {
            if (!spinning) {
                it.startAnimation(scaleAnimation)
                lifecycleScope.launch {
                    delay(240)
                    val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToFragmentListUserWin(args.campaignId, args.campaignName, luckyGameViewModel.currentCount)
                    findNavController().navigate(action)
                }
            }
        }
        btnQA.setOnClickListener {
            if (!spinning) {
                it.startAnimation(scaleAnimation)
                lifecycleScope.launch {
                    delay(240)
                    val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToHelpGameFragment(args.description, luckyGameViewModel.currentCount)
                    findNavController().navigate(action)
                }
            }
        }
        btnHistory.setOnClickListener {
            if (!spinning) {
                val action = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToHistoryGameFragment(args.campaignId, luckyGameViewModel.currentCount)
                findNavController().navigate(action)
            }
        }
    }

    private fun playClickSound() {
        try {
            (activity as GameActivity).playOnClick()
        } catch (e: Exception) {

        }
    }

    private fun spinToNoGift() {
        rotateWheelAnimation.fillBefore = true
        rotateWheelAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                spinning = false
                img_vanh_off.clearAnimation()
                img_vanh_off.startAnimation(revealAnim)
            }

            override fun onAnimationStart(animation: Animation?) {
                img_vanh_off.clearAnimation()
                img_vanh_off.startAnimation(lightBubbleAnim)
                findNavController().navigate(R.id.action_fragmentLuckyWheelGame_to_noGiftDialogFragment)
            }
        })
        luckyWheel.startAnimation(rotateWheelAnimation)
    }

    private fun spinToGift(response: PlayGameResp) {
        if (::rotateWheelAnimations.isInitialized) {
            rotateWheelAnimations.fillBefore = true
        }
        var pos = 0
        for (item in args.arrGiftName) {
            if (item == response.data?.gift?.name) {
                pos = args.arrGiftName.indexOf(item)
                break
            }
        }
        listImageView[pos].startAnimation(AlphaAnimation(1f, 0.5f).apply {
            this.repeatCount = 5
            duration = 100
        })
        initSpinAnim(pos, args.bitmapMap.size)
        setSpinAnimListener(response, pos)
        startSpinAnim(args.bitmapMap.size)
    }

    fun startEaseInAnim(size: Int) {
        when (size) {
            6 -> {
                luckyWheel.startAnimation(easeInAnimation)
            }
            8 -> {
                luckyWheelEight.startAnimation(easeInAnimation)
            }
            10 -> {
                luckyWheelTen.startAnimation(easeInAnimation)
            }
        }
    }

    fun stopEaseInAnim(size: Int) {
        when (size) {
            6 -> {
                luckyWheel.clearAnimation()
            }
            8 -> {
                luckyWheelEight.clearAnimation()
            }
            10 -> {
                luckyWheelTen.clearAnimation()
            }
        }
    }

    fun startSpinAnim(size: Int) {
        when (size) {
            6 -> {
                luckyWheel.startAnimation(rotateWheelAnimations)
            }
            8 -> {
                luckyWheelEight.startAnimation(rotateWheelAnimations)
            }
            10 -> {
                luckyWheelTen.startAnimation(rotateWheelAnimations)
            }
        }
    }

    fun initSpinAnim(pos: Int, size: Int) {
        rotateWheelAnimations = RotateAnimation(
                0f,
                360f * 10 + (360f - pos * 360f / size),
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        ).apply {
            duration = 5000
            interpolator = DecelerateInterpolator()
            fillAfter = true
            repeatMode = Animation.INFINITE
        }
    }

    private fun setSpinAnimListener(response: PlayGameResp, pos: Int) {
        rotateWheelAnimations.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                img_vanh_off.clearAnimation()
                img_vanh_off.startAnimation(revealAnim)
                finalSpinAnim(response, pos)
            }

            override fun onAnimationStart(animation: Animation?) {
                img_vanh_off.clearAnimation()
                img_vanh_off.startAnimation(lightBubbleAnim)
            }
        })
    }

    private fun finalSpinAnim(response: PlayGameResp, pos: Int) {
        val alphaAnim = AlphaAnimation(.5f, 1f).apply {
            duration = 45
            interpolator = BounceInterpolator()
            repeatMode = Animation.REVERSE
            repeatCount = 15
        }
        alphaAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                spinning = false
                try {
                    val direction = FragmentLuckyWheelGameDirections.actionFragmentLuckyWheelGameToFragmentGiftGame(args.campaignName, response.data?.gift?.name, response.data?.gift?.image?.original, args.owner, response.data?.play!!,
                            args.data.campaign?.owner?.logo.toString(), response.data.gift?.type!!, response.data.winner?.id.toString())
                    findNavController().navigate(direction)
                } catch (e: Exception) {

                }
            }

            override fun onAnimationStart(animation: Animation?) {
                try {
                    (activity as GameActivity).playOnSucess()
                } catch (e: Exception) {

                }
            }
        })
        listImageView[pos].startAnimation(alphaAnim)
    }
}