package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

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
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_loading_game.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.helper.SizeHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.model.HttpErrorResponse
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.animations.ProgressBarAnimation
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModelFactory
import kotlin.math.sqrt

class FragmentLoadingGame : Fragment() {

    private val args: FragmentLoadingGameArgs by navArgs()
    private val luckyGameViewModel: LuckyGameViewModel by viewModels {
        LuckyGameViewModelFactory(this, args.campaignId, 1)
    }
    val arrayImage = arrayListOf<String>()
    val arrayGiftName = arrayListOf<String>()
    var campaignName = ""
    var shopName = ""
    var avatarShop = ""
    var playCount: Int? = 1
    var hasChanceCode = true
    var success = false
    var desc = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loading_game_progress.isEnabled = false
        loading_game_progress.isClickable = false

        luckyGameViewModel.gameInfoLiveData.observe(viewLifecycleOwner, Observer { response ->
            campaignName = response?.data?.campaign?.name.toString()
            shopName = response?.data?.campaign?.owner?.name.toString()
            avatarShop = response?.data?.campaign?.owner?.logo?.medium.toString()
            desc = response?.data?.campaign?.description.toString()
            playCount = response?.data?.play?.toInt()
            luckyGameViewModel.updatePlay(playCount)
            lifecycleScope.launch {
                val newArr = arrayListOf<Deferred<Any>>()
                response?.data?.campaign?.box?.boxGames?.let { boxGames ->

                    for (index in boxGames.indices) {
                        try {
                            arrayImage.add(boxGames[index]?.gift?.image?.small!!)
                            newArr.add(async(Dispatchers.IO) {
                                val requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
                                Glide.with(requireActivity().applicationContext)
                                        .asBitmap()
                                        .load(boxGames[index]?.gift?.image?.small)
                                        .format(DecodeFormat.PREFER_RGB_565)
                                        .apply(requestOptions)
                                        .submit()

                            })
                            arrayGiftName.add(boxGames[index]?.gift?.name!!)
                        } catch (e: Exception) {

                        }
                    }
                    try {
                        newArr.awaitAll()
                    } catch (e: Exception) {
                        ToastHelper.showShortError(requireContext(), "Đã xảy ra lỗi vui lòng thử lại sau")
                        findNavController().popBackStack()
                    }
                }
            }
            try {
                hasChanceCode = response?.data?.campaign?.hasChanceCode!!
            } catch (e: Exception) {

            }
            if (arrayImage.size > 0) {
                success = true
            }
        })
        luckyGameViewModel.httpException.observe(viewLifecycleOwner, Observer {
            try {
                val s = (it as HttpException).response()?.errorBody()?.string()
                val gson = Gson().fromJson(s, HttpErrorResponse::class.java)
                ToastHelper.showLongError(requireContext(), gson.message)
            } catch (e: Exception) {

            }
        })
        luckyGameViewModel.fetchGameInfo()
        val layerDrawable: LayerDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(R.drawable.progress_loading_game_drawable, null) as LayerDrawable
        } else {
            resources.getDrawable(R.drawable.progress_loading_game_drawable) as LayerDrawable
        }
        val radius = SizeHelper.convertDpToPixel(40f, requireContext())
        val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shapeDrawable = ShapeDrawable(RoundRectShape(radii, null, null))
        shapeDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {

            override fun resize(width: Int, height: Int): Shader {
                val metric = DisplayMetrics()
                activity?.windowManager?.defaultDisplay?.getMetrics(metric)
                val height = metric.heightPixels / metric.ydpi
                val width = metric.widthPixels / metric.xdpi
                val diagonalInches = sqrt((width * width + height * height).toDouble())

                val bitmap = if (diagonalInches >= 6.5) {
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_pro_v2_tablet)
                } else {
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_prog_loading)
                }
                return BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            }
        }
        val drawable: Drawable = ClipDrawable(shapeDrawable, Gravity.START, ClipDrawable.HORIZONTAL)
        layerDrawable.setDrawableByLayerId(android.R.id.progress, drawable)
        loading_game_progress.progressDrawable = layerDrawable

        val anim = ProgressBarAnimation(loading_game_progress, tv_percent, 0f, 100f)
        anim.duration = 3000
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (success) {
                    val action = FragmentLoadingGameDirections.actionFragmentLoadingGameToFragmentLuckyWheelGame2(
                            args.campaignId,
                            arrayImage.toTypedArray(),
                            arrayGiftName.toTypedArray(),
                            campaignName, shopName, avatarShop, playCount!!,
                            hasChanceCode,
                            args.owner,
                            desc,
                            args.data
                    )
                    findNavController().navigate(action)
                } else {
                    findNavController().popBackStack()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        anim.interpolator = DecelerateInterpolator()
        loading_game_progress.startAnimation(anim)
    }
}