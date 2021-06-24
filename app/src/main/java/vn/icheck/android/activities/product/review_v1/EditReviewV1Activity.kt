package vn.icheck.android.activities.product.review_v1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_review_v1.*
import vn.icheck.android.R
import vn.icheck.android.activities.product.review_product_v1.ReviewProductV1Activity
import vn.icheck.android.adapters.ImageSliderAdapter.Companion.TYPE_CCCN
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.*
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.ICShare
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.ui.GlideUtil
import java.io.File

class EditReviewV1Activity : BaseActivityMVVM(), TakePhotoHelper.TakePhotoListener {

    private var criteria: ICCriteria? = null
    val listUrl = mutableListOf<String>()
    private val listImg = mutableListOf<EditReviewImgAdapter.ImageChild>()
    lateinit var imgAdapter: EditReviewImgAdapter
    val listCriteriaChild = mutableListOf<CriteriaChild>()
    var pos = -1

    private val requestCameraPermission = 3
    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private val takePhotoHelper = TakePhotoHelper(this)

    companion object {
        //        const val UPLOAD = 1
        const val REQUEST = 2
        var instance: EditReviewV1Activity? = null
    }


    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review_v1)
        instance = this
        intent.getStringExtra("img")?.let {
            Glide.with(this.applicationContext).load(it).placeholder(R.drawable.error_load_image).into(img_product)
        }
        intent.getStringExtra("name")?.let {
            tv_title.text = it
        }

        criteria = intent.getSerializableExtra("criteria") as ICCriteria
        criteria?.let {
            if (it.customerEvaluation != null) {
                if (!it.customerEvaluation!!.imageThumbs.isNullOrEmpty()) {
                    for (item in it.customerEvaluation!!.imageThumbs) {
                        listImg.add(EditReviewImgAdapter.ImageChild(item.thumbnail, TYPE_CCCN))
                        item.thumbnail?.let {
                            listUrl.add(it)
                        }
                    }
                }
                val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
                for (item in criteria!!.productCriteriaSet!!.indices) {
                    listCriteriaChild.add(CriteriaChild(
                            criteria!!.productCriteriaSet!!.get(item),
                            it.customerEvaluation!!.customerCriteria.get(item).point.toFloat(),
                            false
                    ))
                }
                rcv_rating.adapter = criteriaAdapter
                rcv_rating.layoutManager = LinearLayoutManager(this)

                edt_nrv_comment.setText(it.customerEvaluation!!.message)
                btn_nrv_send.setOnClickListener {
                    if (NetworkHelper.isNotConnected(this)) {
                        ToastUtils.showLongError(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    } else {
                        postProductReview(
                                edt_nrv_comment.text.toString(),
                                listUrl,
                                intent.getLongExtra("id", 0L)
                        )
                    }

                }
                btn_nrv_take_img.setOnClickListener {

                    if (PermissionHelper.checkPermission(this, permission, requestCameraPermission)) {
                        takePhotoHelper.takePhoto(this@EditReviewV1Activity)
                    }
                    pos = -1
                }
                btn_back.setOnClickListener {
                    onBackPressed()
                }
                imgAdapter = EditReviewImgAdapter(listImg)
                rcv_image.adapter = imgAdapter
                rcv_image.layoutManager = LinearLayoutManager(this@EditReviewV1Activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    onBackPressed()
                }
            }
            requestCameraPermission -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    takePhotoHelper.takePhoto(this@EditReviewV1Activity)
                } else {
                    ToastUtils.showLongError(this, getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
        }
    }

    fun pickImage(position: Int) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
        } else if (!PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
            PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, 2)
        } else {

            if (PermissionHelper.checkPermission(this, permission, requestCameraPermission)) {
                takePhotoHelper.takePhoto(this@EditReviewV1Activity)
            }

            pos = position
        }
    }

    fun share(message: String, averagePoint: Float) {
        val host = APIConstants.defaultHost + APIConstants.SHARELINK()
        ICNetworkClient.getShareClient()
                .testShareLink(intent.getLongExtra("id", 0L), "product")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ICShare> {
                    override fun onSuccess(t: ICShare) {
                        if (t.link.isNotEmpty()) {
                            val share = Intent()
                            share.action = Intent.ACTION_SEND
                            share.putExtra(Intent.EXTRA_SUBJECT, intent.getStringExtra("name"))
                            share.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.chia_se_danh_gia, averagePoint * 2, message, t.link))
                            share.type = "text/plain"
                            startActivity(Intent.createChooser(share, getString(R.string.chia_se_s, intent.getStringExtra("name"))))

                            //back
                            val result = Intent()
                            result.putExtra("result", ReviewProductV1Activity.OK)
                            setResult(Activity.RESULT_OK, result)
                            onBackPressed()

                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    fun postProductReview(message: String, images: List<String>?, id: Long) {
        val body = hashMapOf<String, Any?>()
        body.put("product_id", id)
        body.put("message", message)

        if (images != null) {
            body.put("images", images)
        }
        val criterid = mutableListOf<HashMap<String, Any>>()
        var validate = true
        for (i in listCriteriaChild.indices) {
            val item = hashMapOf<String, Any>()
            item.put("criteria_id", listCriteriaChild[i].productCriteriaSet.criteria.id)
            if (listCriteriaChild.get(i).point != null) {
                item.put("point", listCriteriaChild[i].point?.toInt()!!)
                if (listCriteriaChild[i].point == 0F) {
                    validate = false
                }
            } else {
                validate = false
            }
            criterid.add(item)
        }
        body.put("criteria", criterid)

        if (validate) {
            DialogHelper.showLoading(this)
            val host = APIConstants.defaultHost + APIConstants.CRITERIAREVIEWPRODUCT()
            ICNetworkClient.getApiClient().postProductReview(host, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ICProductReviews.ReviewsRow> {
                        override fun onSuccess(t: ICProductReviews.ReviewsRow) {
                            DialogHelper.closeLoading(this@EditReviewV1Activity)

                            val reviewTributeDialog = ReviewTributeV1Dialog()
                            reviewTributeDialog.apply {
                                setAction(object : ReviewTributeV1Dialog.ReviewTributeAction {
                                    override fun onDismiss() {
                                        val result = Intent()
                                        result.putExtra("result", 1)
                                        setResult(Activity.RESULT_OK, result)
                                        onBackPressed()
                                    }

                                    override fun onShare() {
                                        share(t.message, t.averagePoint)
                                    }

                                })
                            }
                            reviewTributeDialog.show(supportFragmentManager, null)
                            reviewTributeDialog.isCancelable = false
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            DialogHelper.closeLoading(this@EditReviewV1Activity)
                            val result = Intent()
                            result.putExtra("result", 2)
                            setResult(Activity.RESULT_OK, result)
                            onBackPressed()
                        }
                    })
        } else {
            ToastUtils.showShortError(this, getString(R.string.khong_duoc_de_trong_tieu_chi))
        }
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (file != null) {
            if (NetworkHelper.isNotConnected(this)) {
                ToastUtils.showLongError(this, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(this)

            ImageHelperV1.uploadImage(this, file, object : ICApiListener<UploadResponse> {
                override fun onSuccess(obj: UploadResponse) {
                    DialogHelper.closeLoading(this@EditReviewV1Activity)

                    if (pos != -1) {
                        listImg.get(pos).url = obj.src
                        listUrl[pos] = obj.src
                        imgAdapter.notifyDataSetChanged()
                        pos = -1
                    } else {
                        listImg.add(EditReviewImgAdapter.ImageChild(obj.src, TYPE_CCCN))
                        imgAdapter.notifyDataSetChanged()
                        listUrl.add(obj.src)
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    DialogHelper.closeLoading(this@EditReviewV1Activity)
                }
            })
        }
    }

    fun deleteImage(position: Int) {
        listImg.removeAt(position)
        listUrl.removeAt(position)
        imgAdapter.notifyItemRemoved(position)
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(this, requestCode, resultCode, data)
    }

    class EditReviewImgAdapter(val listImg: List<ImageChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CccnHolder.create(parent)
        }

        override fun getItemCount(): Int {
            return listImg.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CccnHolder).bind(listImg.get(position).url)
        }

        class CccnHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val image = view.findViewById<ImageView>(R.id.product_img)
            val imgDelete = view.findViewById<ImageView>(R.id.img_delete)

            fun bind(url: String?) {
                GlideUtil.loading(url, image)
                imgDelete.visibility = View.VISIBLE

                image.setOnClickListener {
                    instance?.pickImage(adapterPosition)
                }
                imgDelete.setOnClickListener {
                    instance?.deleteImage(adapterPosition)
                }

            }

            companion object {
                fun create(parent: ViewGroup): CccnHolder {
                    return CccnHolder(LayoutInflater.from(parent.context).inflate(R.layout.cccn_holder, parent, false))
                }
            }
        }

        class ImageChild(var url: String?, val type: Int)
    }


}

