package vn.icheck.android.screen.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebSettings
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_notification_firebase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.chat.icheckchat.base.view.setGoneView
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant.getHtmlData
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity

abstract class DialogNotificationFirebaseAds(context: Activity, private val image: String?, private val htmlText: String?, private val link: String?, private val schema: String?) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_notification_firebase
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        setGoneView(imageView, textView, layoutWeb)

        when {
            image != null -> {
                imageView.setVisible()

                CoroutineScope(Dispatchers.IO).launch {
                    Glide.with(ICheckApplication.getInstance())
                            .asBitmap()
                            .timeout(30000)
                            .load(image)
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                    Log.d("onLoad", "onLoadFailed: false")
                                    dismiss()
                                    return false
                                }

                                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    if (resource != null) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val maxHeight = container.height - SizeHelper.size52
                                            val ratioHeight = container.height.toDouble() /resource.height.toDouble()
                                            val ratioWidth =  container.width.toDouble() /resource.width.toDouble()
                                            when {
                                                resource.width > container.width && resource.height <= container.height -> {
                                                    // ảnh rộng quá màn hình -> max with, wrap height
                                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                }
                                                resource.height > container.height && resource.width <= container.width -> {
                                                    //  ảnh dài quá màn hình ->  max height, wrap with
                                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, maxHeight)
                                                }
                                                resource.width > resource.height && resource.width > container.width -> {
                                                    //  ảnh rộng quá màn hình && ảnh có chiều rộng lớn hơn-> max with, wrap height
                                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                }
                                                resource.height > resource.width && resource.height > container.height -> {
                                                    if (ratioWidth > ratioHeight) {
                                                        // max with
                                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                                    } else {
                                                        // max height
                                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                    }
                                                }
                                                resource.height < container.height && resource.width < container.width->{
                                                    /* ảnh có chiều rộng & chiều dài đều bé hơn màn hình
                                                    -> tính tỉ lệ chiều nào gần full màn hình sẽ lấy chiều đó là MATCH_PARENT
                                                     */
                                                    if (ratioWidth > ratioHeight) {
                                                        // max with
                                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                                    } else {
                                                        // max height
                                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT )
                                                    }
                                                }
                                            }
                                            imageView.setImageBitmap(resource)
                                        }
                                    }
                                    return false
                                }
                            })
                            .submit()
                            .get()
                }

//                WidgetHelper.loadImageUrlRounded10FitCenter(imageView, image)
//                WidgetUtils.loadImageUrlRounded10FitCenter(imageView, image, object : RequestListener<Bitmap> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                        return false
//                    }
//
//                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                        if (resource != null) {
//                            val maxHeight = container.height - SizeHelper.size52
//                            when {
//                                resource.width > container.width && resource.height <= container.height -> {
//                                    // max with, wrap height
//                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                                }
//                                resource.height > container.height && resource.width <= container.width -> {
//                                    // max height, wrap with
//                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, maxHeight)
//                                }
//                                resource.width > resource.height && resource.width > container.width -> {
//                                    // max with, wrap height
//                                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                                }
//                                resource.height > resource.width && resource.height > container.height -> {
//                                    val ratio = resource.height / container.height
//                                    if (resource.width / ratio > container.width) {
//                                        // max with
//                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                                    } else {
//                                        // max height
//                                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, maxHeight)
//                                    }
//                                }
//                            }
//                        }
//                        return false
//                    }
//                })
//
//                val viewTreeObserver = imageView.viewTreeObserver
//                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//
//
//                    override fun onGlobalLayout() {
////                        val height = imageView.measuredHeight
////                        val width = imageView.measuredWidth
////                        val widthImage = imageView.drawable.intrinsicWidth
////                        val heightImage = imageView.drawable.intrinsicHeight
////                        logDebug("$width,$height,$widthImage, $heightImage ")
////                        imageView.layoutParams = LinearLayout.LayoutParams(SizeHelper.dpToPx(widthImage), SizeHelper.dpToPx(heightImage))
//
//                        var ih=imageView.measuredHeight;//height of imageView
//                        var iw=imageView.measuredWidth;//width of imageView
//                        val iH=imageView.drawable.intrinsicHeight;//original height of underlying image
//                        val iW=imageView.drawable.intrinsicWidth;//original width of underlying image
//
//                        if (ih/iH<=iw/iW) {
//                            iw=iW*ih/iH
//                        }else{
//                            ih= iH*iw/iW
//                        };//rescaled width of image within ImageView
//                        ;//rescaled height of image within ImageView
//                        logDebug("$iw,$ih ")
//                        Handler().postDelayed({
//                            imageView.layoutParams = LinearLayout.LayoutParams(iw, ih)
//                        },200)
//                        }
//
//                })


                imageView.setOnClickListener {
                    dismiss()
                    ICheckApplication.currentActivity()?.let { activity ->
                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                    }
                }
            }
            htmlText != null -> {
                textView.setVisible()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;
                } else {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
                }
                webViewHtml.loadDataWithBaseURL(null, getHtmlData(htmlText), "text/html", "utf-8", "")
            }
            link != null -> {
                layoutWeb.setVisible()
                webView.loadUrl(link)
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()
}