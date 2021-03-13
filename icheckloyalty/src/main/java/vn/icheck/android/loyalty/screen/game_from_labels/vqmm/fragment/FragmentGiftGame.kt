package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_gift_game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ImageHelper
import vn.icheck.android.loyalty.helper.PermissionHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.animations.scaleAnimation
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.gift_voucher.GiftDetailFromAppActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class FragmentGiftGame : Fragment() {
    private val args: FragmentGiftGameArgs by navArgs()
    private val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
    private val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
    private val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
    private val luckyGameViewModel: LuckyGameViewModel by activityViewModels()

    private val requestShare = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gift_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        WidgetHelper.loadImageFitCenterUrl(img_bg_center, args.giftImage)
        WidgetHelper.loadImageUrl(imgBusiness, ImageHelper.getImageUrl(args.ownerAvatar, ImageHelper.thumbSmallSize), R.drawable.ic_business_v2)
        tv_gift_name.text = args.giftName
        tv_third.text = args.campaignName
        if (args.giftType.equals("ICOIN", true)) {
            img_xkq.setImageResource(R.drawable.btn_qlx)
        }
        initAnim()
        img_kbb.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        img_xkq.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        luckyGameViewModel.updatePlay(args.count)
    }

    private fun initAnim() {
        ObjectAnimator.ofPropertyValuesHolder(img_star_effect, scaleX, scaleY, alpha).apply {
            interpolator = OvershootInterpolator()
            duration = 2000
        }.start()
        img_kbb.setOnClickListener {
            it.startAnimation(scaleAnimation)

            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestShare)
        }
        img_xkq.setOnClickListener {
            it.startAnimation(scaleAnimation)
            lifecycleScope.launch {
                delay(240)
                if (!args.giftType.equals("ICOIN", true)) {
                    startActivity(Intent(requireContext(), GiftDetailFromAppActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.DATA_1, args.winnerId.toLong())
                    })
                } else {
                    LoyaltySdk.openActivity("point_transitions")
                }
            }
        }
        btn_cancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun shareGift() {
        lifecycleScope.launch {
            delay(240)

            val layoutParent = FrameLayout(requireContext())
            layoutParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

            val layoutContent = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_gift_game_share, null)
            layoutContent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            layoutParent.addView(layoutContent)

            val screenShot = screenShot(root_shot)
            layoutParent.addView(AppCompatImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageBitmap(screenShot)
            })

            layoutContent.findViewById<AppCompatTextView>(R.id.tvName).text = SessionManager.session.user?.name
            layoutContent.findViewById<AppCompatTextView>(R.id.tvGiftName).text = args.giftName
            layoutContent.findViewById<AppCompatTextView>(R.id.tvEventName).text = args.campaignName
            layoutContent.findViewById<AppCompatTextView>(R.id.tvBusinessName).text = args.owner
            layoutContent.findViewById<AppCompatImageButton>(R.id.imgProduct).setImageBitmap(screenShot(img_bg_center))
            layoutContent.findViewById<CircleImageView>(R.id.imgBusiness).setImageBitmap(screenShot(imgBusiness))
            layoutContent.findViewById<AppCompatTextView>(R.id.tvDescription).apply {
                val spannableString = SpannableString("Tải ngay   app để tham gia chương trình")
                spannableString.setSpan(ImageSpan(requireContext(), R.drawable.ic_icheck_small_white, ImageSpan.ALIGN_BASELINE), 9, 10, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                text = spannableString
            }

            root_shot.addView(layoutParent)

            delay(400)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "*/*"
            val mBitmap = screenShot(layoutContent)
            val path = MediaStore.Images.Media.insertImage(activity?.contentResolver, mBitmap, "Anh chup man hinh", null)
            val uri = Uri.parse(path)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Chúc mừng ${SessionManager.session.user?.name} " +
                            "đã trúng \"${args.giftName}\" khi tham gia sự kiện \"${args.campaignName}\" của nhà tài trợ \"${args.owner}\"")
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua app"))

            root_shot.removeView(layoutParent)
        }
    }

    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestShare) {
            if (PermissionHelper.checkResult(grantResults)) {
                shareGift()
            } else {
                ToastHelper.showLongWarning(requireContext(), R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}