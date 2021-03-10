package vn.icheck.android.activities.product.contribute

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contribute_product.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import vn.icheck.android.R
import vn.icheck.android.activities.base.BaseICActivity
import vn.icheck.android.adapters.ContributeImageAdapter
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.base.dialog.notify.loading.LoadingDialog
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TakePhotoHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICBarcodeProductV2
import vn.icheck.android.network.models.ICInformationTitles
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.pick_image.PickImageDialog
import vn.icheck.android.util.text.HtmlImageGetter
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ContributeProductActivity : BaseICActivity(), TakePhotoHelper.TakePhotoListener {
    private val takePhotoHelper = TakePhotoHelper(this)
    private val requestCameraPermission = 3

    lateinit var contributeImageAdapter: ContributeImageAdapter
    lateinit var descriptionAdapter: DescriptionAdapter
    val listChild = mutableListOf<ContributeImageChild>()
    var currentType = 0
    var posChange = -1
    var total = 0
    private val listTitles = mutableListOf<InfoChild>()
    private val listData = mutableListOf<InfoChild>()
    private val listImg = mutableListOf<String>()
    private val listInfo = mutableListOf<InfoValue>()
    private val listDescriptionChild = mutableListOf<DescriptionChild>()
    private val uploadQueue = mutableListOf<UploadHolder>()
    private var getTitle = false
    private var categoryId: Long = 0
    private val validateForm = MutableLiveData<Int>()
    lateinit var productV2: ICBarcodeProductV1
    val listener = object : TextWatcher {
        var current = ""
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString() != current) {
                if (current.length <= s.toString().length) {
                    edt_product_price.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    val formatted = String.format("%,dđ", cleanString.toLong())
                    current = formatted
                    edt_product_price.setText(formatted)
                    edt_product_price.setSelection(formatted.length)
                    edt_product_price.addTextChangedListener(this)
                } else {
                    edt_product_price.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    if (cleanString.length > 1) {
                        val formatted = String.format("%,dđ", cleanString.substring(0, cleanString.length - 1).toLong())
                        current = formatted
                        edt_product_price.setText(formatted)
                        edt_product_price.setSelection(formatted.length)
                    } else {
                        edt_product_price.setText("")
                        current = ""
                    }
                    edt_product_price.addTextChangedListener(this)
                }
            }
        }
    }

    companion object {
        const val UPLOAD = 1
        const val CATEGORY = 2
        const val CHANGE_IMAGE = 3
        var instance: ContributeProductActivity? = null

        fun startForResult(activity: Activity, barcode: String?, productId: Long, requestCode: Int) {
            if (SessionManager.isUserLogged) {
                val contributeProductIntent = Intent(activity, ContributeProductActivity::class.java)
                contributeProductIntent.putExtra("barcode", barcode)
                contributeProductIntent.putExtra("productId", productId)
                activity.startActivityForResult(contributeProductIntent, requestCode)
            } else {
                AccountActivity.start(activity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        compositeDisposable.dispose()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute_product)
        btn_submit.isEnabled = false
        validateForm.observe(this, Observer {
            if (it == 2) {
                btn_submit.isEnabled = true
                btn_submit.setBackgroundResource(R.drawable.bg_blue_border_20)
            } else {
                btn_submit.isEnabled = false
                btn_submit.setBackgroundResource(R.drawable.bg_gray_corner_20dp)
            }
        })
        initImageAdapter()
        setClickListener()
        initActivity()

        edt_product_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch {
                    delay(200)
                    notifyFormChange()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun notifyFormChange() {
        if (edt_product_name.text.trim().isNotEmpty() && uploadQueue.size >= 2) {
            validateForm.postValue(2)
        } else {
            validateForm.postValue(1)
        }
    }

    private fun setClickListener() {
        btnBack.setOnClickListener {
            finish()
        }
        spn_dm.setOnClickListener {
            val selectCategoryIntent = Intent(this, SelectCategoryActivity::class.java)
            startActivityForResult(selectCategoryIntent, CATEGORY)
        }
        edt_product_price.addTextChangedListener(listener)
        btn_submit.setOnClickListener {
            var validate = true
            if (edt_email.text.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(edt_email.text.toString()).matches()) {
                    validate = false
                    edt_email.setError("Email không đúng định dạng!")
                }
            }
            if (uploadQueue.size >= 2 && edt_product_name.text.trim().toString().isNotEmpty() && validate) {

                lifecycleScope.launch {
                    val loadingDialog = object : LoadingDialog(this@ContributeProductActivity) {
                        var result = 0
                        override fun onDismiss() {
                            if (result == 1) {
                                ToastUtils.showShortSuccess(this@ContributeProductActivity,
                                        "Tạo sản phẩm thành công!")
                            } else {
                                ToastUtils.showShortError(this@ContributeProductActivity,
                                        "Đã xảy ra lỗi. Vui lòng thử lại sau!")
                            }
                        }
                    }
                    loadingDialog.setCancelable(false)
                    loadingDialog.show()
                    val listJob = mutableListOf<Deferred<UploadResponse>>()
                    for (item in uploadQueue) {
                        val job = async(Dispatchers.IO) {
                            val bitmap = Glide.with(this@ContributeProductActivity.applicationContext)
                                    .asBitmap()
                                    .load(item.uri)
                                    .submit()
                                    .get()
                            val bos = ByteArrayOutputStream()
                            val dst = if (bitmap.width > bitmap.height) {
                                bitmap.width
                            } else {
                                bitmap.height
                            }
                            val scale = dst / 1024
                            var dstWidth = bitmap.width
                            var dstHeight = bitmap.height
                            if (scale >= 1) {
                                dstWidth /= scale
                                dstHeight /= scale
                            }
                            Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
                                    .compress(Bitmap.CompressFormat.JPEG, 100, bos)
                            ICNetworkClient.getNewUploadClient().postImage(
                                    RequestBody.create(
                                            "image/jpeg".toMediaTypeOrNull(),
                                            bos.toByteArray()
                                    ))
                        }
                        listJob.add(job)
                    }
                    try {
                        for (item in listJob) {
                            try {
                                val response = item.await()
                                listImg.add(response.src)
                            } catch (e: Exception) {
                                logError(e)
                                withContext(Dispatchers.Main) {
                                    ToastUtils.showShortError(this@ContributeProductActivity,
                                            "Đã xảy ra lỗi. Vui lòng thử lại sau!")
                                }
                            }
                        }
                        val body = hashMapOf<String, Any>()
                        body["name"] = edt_product_name.text.trim().toString()
                        body["product_id"] = intent.getLongExtra("productId", 0)
                        body["images"] = listImg
                        if (listDescriptionChild.isNotEmpty()) {
                            val list = mutableListOf<HashMap<String, Any>>()
                            for (item in listDescriptionChild) {
                                if (item.type == DescriptionAdapter.INFO) {
                                    item as InfoChild
                                    val child = hashMapOf<String, Any>()
                                    child["type_id"] = item.infoTitle.id
                                    child["content"] = item.text!!
                                    list.add(child)
                                }
                            }
                            body["informations"] = list
                        }
                        val owner = hashMapOf<String, Any>()
                        if (edt_name_dn.text.isNotEmpty()) {
                            owner["name"] = edt_name_dn.text.toString()
                        }
                        if (edt_email.text.isNotEmpty()) {
                            owner["email"] = edt_email.text.toString()
                        }
                        if (edt_tax.text.isNotEmpty()) {
                            owner["tax"] = edt_tax.text.toString()
                        }
                        if (edt_address_dn.text.isNotEmpty()) {
                            owner["address"] = edt_address_dn.text.toString()
                        }
                        if (edt_phone_dn.text.isNotEmpty()) {
                            owner["phone"] = edt_phone_dn.text.toString()
                        }
                        if (owner.isNotEmpty()) {
                            body["owner"] = owner
                        }

                        if (categoryId != 0L) {
                            body["category_id"] = categoryId
                        }
                        body["price"] = try {
                            edt_product_price.text.toString().replace("[,.đ]".toRegex(), "").toLong()
                        } catch (e: NumberFormatException) {
                            0
                        }
                        ICNetworkClient.getSimpleApiClient().postContriKtx(body)
                        loadingDialog.result = 1
                        loadingDialog.dismiss()
                        val resultIntent = Intent()
                        resultIntent.putExtra("id", intent.getStringExtra("barcode"))
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } catch (e: Exception) {
                        loadingDialog.result = 0
                        loadingDialog.dismiss()
                        logError(e)
                    }
                }

            } else if (::productV2.isInitialized && !productV2.attachments.isNullOrEmpty() && productV2.name.trim().isNotEmpty()) {
                lifecycleScope.launch {
                    val loadingDialog = object : LoadingDialog(this@ContributeProductActivity) {
                        var result = 0
                        override fun onDismiss() {
                            if (result == 1) {
                                ToastUtils.showShortSuccess(this@ContributeProductActivity,
                                        "Tạo sản phẩm thành công!")
                            } else {
                                ToastUtils.showShortError(this@ContributeProductActivity,
                                        "Đã xảy ra lỗi. Vui lòng thử lại sau!")
                            }
                        }
                    }
                    loadingDialog.setCancelable(false)
                    loadingDialog.show()
                    try {
                        for (item in productV2.attachments) {
                            listImg.add(item.thumbnails.original)
                        }
                        val body = hashMapOf<String, Any>()
                        body["name"] = edt_product_name.text.trim().toString()
                        body["product_id"] = intent.getLongExtra("productId", 0)
                        body["images"] = listImg
                        if (listDescriptionChild.isNotEmpty()) {
                            val list = mutableListOf<HashMap<String, Any>>()
                            for (item in listDescriptionChild) {
                                if (item.type == DescriptionAdapter.INFO) {
                                    item as InfoChild
                                    val child = hashMapOf<String, Any>()
                                    child["type_id"] = item.infoTitle.id
                                    child["content"] = item.text!!
                                    list.add(child)
                                }
                            }
                            body["informations"] = list
                        }
                        val owner = hashMapOf<String, Any>()
                        owner["name"] = edt_name_dn.text.toString()
                        owner["email"] = edt_email.text.toString()
                        owner["tax"] = edt_tax.text.toString()
                        owner["address"] = edt_address_dn.text.toString()
                        owner["phone"] = edt_phone_dn.text.toString()
                        body["owner"] = owner
                        if (categoryId != 0L) {
                            body["category_id"] = categoryId
                        }
                        body["price"] = try {
                            edt_product_price.text.toString().replace("[,.đ]".toRegex(), "").toLong()
                        } catch (e: NumberFormatException) {
                            0
                        }
                        ICNetworkClient.getSimpleApiClient().postContriKtx(body)
                        loadingDialog.result = 1
                        loadingDialog.dismiss()
                        val resultIntent = Intent()
                        resultIntent.putExtra("id", intent.getStringExtra("barcode"))
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } catch (e: Exception) {
                        loadingDialog.result = 0
                        loadingDialog.dismiss()
                        logError(e)
                    }
                }
            } else {
                if (edt_product_name.text.trim().isEmpty()) {
                    edt_product_name.error = "Trường tên sản phẩm là bắt buộc!"
                    edt_product_name.requestFocus()
                }
                if (listChild.size <= 2) {
                    Toast.makeText(this, "Sản phẩm tối thiểu phải có 2 ảnh!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    fun setTextInfo(position: Int, content: String) {
        lifecycleScope.launch {
            delay(200)
            (listDescriptionChild[position] as InfoChild).text = content
        }
    }

    fun showInfoDialog() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        if (!getTitle) {
            ICNetworkClient.getApiClient().testInformationTypes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ICInformationTitles> {
                        override fun onSuccess(t: ICInformationTitles) {
                            for (item in t.rows) {
                                listTitles.add(InfoChild(item))
                            }

                            val bottom = TitleBottomDialog(listTitles)
                            bottom.show(supportFragmentManager, null)
                            getTitle = true
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.e("E", "e", e)
                        }
                    })
        } else {
            if (listTitles.isNotEmpty()) {
                val bottom = TitleBottomDialog(listTitles)
                bottom.show(supportFragmentManager, null)
            } else {
                btn_add_description.visibility = View.GONE
            }
        }
    }

    private fun initInfoAdapter() {

        listDescriptionChild.add(DescriptionChild(DescriptionAdapter.BUTTON))

        descriptionAdapter = DescriptionAdapter(listDescriptionChild)
        rcv_add_description.adapter = descriptionAdapter
        rcv_add_description.layoutManager = LinearLayoutManager(this)
        rcv_add_description.isNestedScrollingEnabled = false
        for (item in productV2.informations!!) {
            listDescriptionChild.add(InfoChild(ICInformationTitles().InfoTitle().apply {
                this.id = item.typeId
                this.title = item.title
            }).apply {
                this.selected = true
                this.text = item.content
            })
            listData.add(InfoChild(ICInformationTitles().InfoTitle().apply {
                this.id = item.typeId
                this.title = item.title
            }).apply {
                this.selected = true
                this.text = item.content
            })
        }
        descriptionAdapter.notifyDataSetChanged()
    }

    private fun initActivity() {
        instance = this
        intent.getStringExtra("barcode")?.let { barcode ->
            edt_no_edit_barcode.setText(barcode)
            btn_search_google.setOnClickListener {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(SearchManager.QUERY, barcode)
                startActivity(intent)
            }
            lifecycleScope.launch {
                try {
                    val response = ICNetworkClient.getSimpleApiClient().scanBarcode(barcode)
                    productV2 = response

                    if (response.price != 0L) {
                        edt_product_price.setText(String.format("%,dđ", response.price))
                        edt_product_price.isEnabled = false
                    }
                    if (!response.categories.isNullOrEmpty()) {
                        spn_dm.text = response.categories.last().name
                        spn_dm.isEnabled = false
                        categoryId = response.categories.last().id
                    }
                    if (response.owner != null) {
                        btn_search_google.visibility = View.GONE
                        tv_hour.visibility = View.GONE
                        edt_name_dn.visibility = View.GONE
                        edt_address_dn.visibility = View.GONE
                        edt_phone_dn.visibility = View.GONE
                        edt_email.visibility = View.GONE
                        edt_tax.visibility = View.GONE
                    }

                    if (response.informations != null) {
                        for (item in response.informations!!) {
                            listDescriptionChild.add(InfoChild(ICInformationTitles().InfoTitle().apply {
                                this.id = item.typeId
                                this.title = item.title
                            }).apply {
                                this.selected = true
                                this.text = item.content
                                editable = false
                            })
                            listData.add(InfoChild(ICInformationTitles().InfoTitle().apply {
                                this.id = item.typeId
                                this.title = item.title
                            }).apply {
                                this.selected = true
                                this.text = item.content
                                editable = false
                            })
                        }
                    }
                    if (listData.isEmpty()) {
                        listDescriptionChild.add(DescriptionChild(DescriptionAdapter.BUTTON))
                    }
                    descriptionAdapter = DescriptionAdapter(listDescriptionChild)
                    rcv_add_description.adapter = descriptionAdapter
                    rcv_add_description.layoutManager = LinearLayoutManager(this@ContributeProductActivity)
                    rcv_add_description.isNestedScrollingEnabled = false
                    if (response.name.trim().isNotEmpty() && !response.attachments.isNullOrEmpty()) {
                        edt_product_name.setText(response.name)
                        edt_product_name.isEnabled = false
                        listChild.clear()
                        for (item in response.attachments) {
                            listChild.add(ContributeImageChild(item.thumbnails.thumbnail.toString(), 3).apply {
                                canReplace = false
                            })
                        }
                        contributeImageAdapter.notifyDataSetChanged()
                        delay(300)
                        validateForm.postValue(2)
                    } else {
                        if (!response.name.isBlank()) {
                            edt_product_name.setText(response.name)
                            edt_product_name.isEnabled = false
                        }
                        if (!response.attachments.isNullOrEmpty()) {
                            listChild.clear()
                            for (item in response.attachments) {
                                listChild.add(ContributeImageChild(item.thumbnails.thumbnail.toString(), 3).apply {
                                    canReplace = false
                                })
                            }
                            contributeImageAdapter.notifyDataSetChanged()
                        }

                    }
                } catch (e: java.lang.Exception) {
                    logError(e)
                }
            }
        }
    }

    private fun initImageAdapter() {
        listChild.add(ContributeImageChild("", ContributeImageAdapter.FRONT_HOLDER))
        listChild.add(ContributeImageChild("", ContributeImageAdapter.BACK_HOLDER))
        contributeImageAdapter = ContributeImageAdapter(listChild)
        rcv_product_images.adapter = contributeImageAdapter
        rcv_product_images.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
    }


    fun addInfo(infoChild: InfoChild) {
        listDescriptionChild.add(infoChild)
        listTitles.remove(infoChild)
        if (listTitles.size == 0) {
            listDescriptionChild.removeAt(0)
        }
        listData.add(infoChild)
        descriptionAdapter.notifyDataSetChanged()
    }

    fun removeInfo(infoChild: InfoChild, position: Int) {
        listDescriptionChild.removeAt(position)
        descriptionAdapter.notifyItemRemoved(position)
        infoChild.selected = false
        listTitles.add(infoChild)
        listData.remove(infoChild)
        val f = listDescriptionChild.firstOrNull()
        f?.let {
            if (f.type != DescriptionAdapter.BUTTON) {
                listDescriptionChild.add(0, DescriptionChild(DescriptionAdapter.BUTTON))
                descriptionAdapter.notifyItemInserted(0)
            }
        }
    }

    fun changeImage(position: Int) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
        } else if (!PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
            PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, 2)
        } else {
            posChange = position
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, CHANGE_IMAGE)
        }

    }

    fun removeImage(position: Int) {
        listChild.removeAt(position)
        if (position == 1) {
            listChild.add(1, ContributeImageChild(null, ContributeImageAdapter.BACK_HOLDER))
        }
        if (position == 0) {
            listChild.add(0, ContributeImageChild(null, ContributeImageAdapter.FRONT_HOLDER))
        }
        uploadQueue.removeAt(position)
        contributeImageAdapter.notifyDataSetChanged()
        notifyFormChange()
    }

    fun pickImage(type: Int) {
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (PermissionHelper.checkPermission(this@ContributeProductActivity, permission, requestCameraPermission)) {
            takePhotoHelper.takePhoto(this@ContributeProductActivity)
        }

        currentType = type
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        } else if (!PermissionHelper.isAllowPermission(this, Manifest.permission.CAMERA)) {
//            PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, 2)
//        }
//        else if (!PermissionHelper.isAllowPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 22)
//        }else {
//            val pickImageDialog = PickImageDialog(this, UPLOAD)
//            pickImageDialog.show(supportFragmentManager, null)
//            currentType = type
//        }
    }

    fun getImageUri(bitmap: Bitmap): Uri {
        val bos = ByteArrayOutputStream()
        val dst = if (bitmap.width > bitmap.height) {
            bitmap.width
        } else {
            bitmap.height
        }
        val scale = dst / 1024
        var dstWidth = bitmap.width
        var dstHeight = bitmap.height
        if (scale >= 1) {
            dstWidth /= scale
            dstHeight /= scale
        }
        Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
                .compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "${Calendar.getInstance().timeInMillis}", null)
        return Uri.parse(path)
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (file != null) {
            handleUri(Uri.fromFile(file))
//            val uri = Uri.fromFile(file)
//
//            if (currentType == ContributeImageAdapter.FRONT_HOLDER) {
//                listChild.removeAt(0)
//                listChild.add(0, ContributeImageChild(uri.toString(),
//                        ContributeImageAdapter.FRONT_IMAGE))
//                contributeImageAdapter.notifyDataSetChanged()
//                total++
//            }
//            if (currentType == ContributeImageAdapter.BACK_HOLDER) {
//                listChild.removeAt(1)
//                listChild.add(1, ContributeImageChild(uri.toString(),
//                        ContributeImageAdapter.BACK_IMAGE))
//                contributeImageAdapter.notifyDataSetChanged()
//                total++
//            }
//            if (currentType == ContributeImageAdapter.ADD_MORE) {
//                listChild.add(listChild.size - 1, ContributeImageChild(uri.toString(),
//                        ContributeImageAdapter.BACK_IMAGE))
//                contributeImageAdapter.notifyDataSetChanged()
//            }
//
//            if (listChild.size == 2 && total >= 2) {
//                listChild.add(ContributeImageChild("",
//                        ContributeImageAdapter.ADD_MORE))
//                contributeImageAdapter.notifyDataSetChanged()
//            }
//            uploadQueue.add(UploadHolder(uri))
//            notifyFormChange()
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestCameraPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                takePhotoHelper.takePhoto(this@ContributeProductActivity)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(this, requestCode, resultCode, data)

        when (requestCode) {
            PickImageDialog.TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    imageBitmap?.also {
                        val uri = getImageUri(imageBitmap)
                        handleUri(uri)
                    } ?: kotlin.run {
                        val uri = PickImageDialog.uri
                        uri?.let {
                            handleUri(uri)
                        }
                    }
                }
            }
            UPLOAD -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    uri?.let {
                        handleUri(uri)
                    }

                }
            }
            CATEGORY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        spn_dm.text = it.getStringExtra("name")
                        categoryId = it.getLongExtra("id", 0)

                    }
                }
            }
            CHANGE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    uri?.let {
                        uploadQueue.set(posChange, UploadHolder(it))
                        if (posChange == 0) {
                            listChild.set(0, ContributeImageChild(it.toString(),
                                    ContributeImageAdapter.FRONT_IMAGE))
                        } else {
                            listChild.set(posChange, ContributeImageChild(it.toString(),
                                    ContributeImageAdapter.BACK_IMAGE))
                        }
                        contributeImageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun handleUri(uri: Uri) {
        if (currentType == ContributeImageAdapter.FRONT_HOLDER) {
            listChild.removeAt(0)
            listChild.add(0, ContributeImageChild(uri.toString(),
                    ContributeImageAdapter.FRONT_IMAGE))
            contributeImageAdapter.notifyDataSetChanged()
            total++
        }
        if (currentType == ContributeImageAdapter.BACK_HOLDER) {
            listChild.removeAt(1)
            listChild.add(1, ContributeImageChild(uri.toString(),
                    ContributeImageAdapter.BACK_IMAGE))
            contributeImageAdapter.notifyDataSetChanged()
            total++
        }
        if (currentType == ContributeImageAdapter.ADD_MORE) {
            listChild.add(listChild.size - 1, ContributeImageChild(uri.toString(),
                    ContributeImageAdapter.BACK_IMAGE))
            contributeImageAdapter.notifyDataSetChanged()
        }

        if (listChild.size == 2 && total >= 2) {
            listChild.add(ContributeImageChild("",
                    ContributeImageAdapter.ADD_MORE))
            contributeImageAdapter.notifyDataSetChanged()
        }
        uploadQueue.add(UploadHolder(uri))
        notifyFormChange()
    }

    class UploadHolder(
            val uri: Uri
    )

    class ContributeImageChild(var url: String?, val type: Int) {
        var uri: String = ""
        var size: Long = 0
        var canReplace = true
    }

    class InfoValue(val id: Int, val edtInfo: EditText)

    class InfoChild(val infoTitle: ICInformationTitles.InfoTitle) : DescriptionChild(DescriptionAdapter.INFO) {
        var selected = false
        var text: String = ""
        var editable: Boolean = true
    }

    class TitleBottomDialog(val listData: List<InfoChild>) : BottomSheetDialogFragment() {

        var titleAdapter = TitleAdapter(listData)

        companion object {
            var instance: TitleBottomDialog? = null
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
            instance = this
        }

        override fun onDestroy() {
            super.onDestroy()
            instance = null
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog?.setOnShowListener {
                val bottomSheetDialog = dialog as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
                bottomSheet?.setBackgroundResource(R.drawable.rounded_dialog)
                BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
            }
            return inflater.inflate(R.layout.dialog_title_bottom, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val rcv = view.findViewById<RecyclerView>(R.id.rcv_titles)
            rcv.adapter = titleAdapter
            rcv.layoutManager = LinearLayoutManager(context)
        }

        fun returnData(infoChild: InfoChild) {
            infoChild.selected = true
            ContributeProductActivity.instance?.addInfo(infoChild)
            dismiss()
        }

        class TitleAdapter(val listData: List<InfoChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return TitleHolder.create(parent)
            }

            override fun getItemCount(): Int {
                return listData.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as TitleHolder).bind(listData[position])
            }

            class TitleHolder(val view: View) : RecyclerView.ViewHolder(view) {

                fun bind(infoChild: InfoChild) {
                    view.findViewById<TextView>(R.id.tv_category_name).text = infoChild.infoTitle.title
                    if (!infoChild.selected) {
                        view.findViewById<ViewGroup>(R.id.vg_parent).setOnClickListener {
                            instance?.returnData(infoChild)
                        }
                    }
                }

                companion object {
                    fun create(parent: ViewGroup): TitleHolder {
                        val view = LayoutInflater.from(parent.context)
                                .inflate(R.layout.category_holder, parent, false)
                        return TitleHolder(view)
                    }
                }
            }
        }
    }
}

open class DescriptionChild(val type: Int)

class DescriptionAdapter(val listChild: List<DescriptionChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val BUTTON = 1
        const val INFO = 2
        const val NULL = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BUTTON -> DescriptionButton.create(parent)
            NULL -> NullHolder.create(parent)
            INFO -> InfoHolder.create(parent)
            else -> DescriptionButton.create(parent)
        }
    }

    override fun getItemCount(): Int = listChild.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            BUTTON -> (holder as DescriptionButton).bind()
            INFO -> (holder as InfoHolder).bind(listChild[position] as ContributeProductActivity.InfoChild)
        }
    }

    override fun getItemViewType(position: Int): Int = listChild[position].type

    class DescriptionButton(view: View) : BaseHolder(view) {

        fun bind() {
            setOnClick(R.id.btn_add_description, View.OnClickListener {
                ContributeProductActivity.instance?.showInfoDialog()
            })
        }

        companion object {
            fun create(parent: ViewGroup): DescriptionButton {
                return DescriptionButton(LayoutInflater.from(parent.context)
                        .inflate(R.layout.btn_add_description, parent, false))
            }
        }
    }

    class InfoHolder(view: View) : BaseHolder(view) {
        fun bind(infoChild: ContributeProductActivity.InfoChild) {
            getTv(R.id.info_title).text = infoChild.infoTitle.title
            if (infoChild.editable) {
                setOnClick(R.id.info_title, View.OnClickListener {
                    ContributeProductActivity.instance?.removeInfo(infoChild, adapterPosition)
                })
                getEdt(R.id.edt_info).addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        ContributeProductActivity.instance?.setTextInfo(adapterPosition, s.toString())
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            } else {
                getTv(R.id.info_title).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                getEdt(R.id.edt_info).postDelayed({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getEdt(R.id.edt_info).setText(Html.fromHtml(infoChild.text, Html.FROM_HTML_MODE_COMPACT, HtmlImageGetter().apply {
                            size = getEdt(R.id.edt_info).width
                        }, null))
                    } else {
                        getEdt(R.id.edt_info).setText(Html.fromHtml(infoChild.text, HtmlImageGetter().apply {
                            size = getEdt(R.id.edt_info).width
                        }, null))
                    }
                }, 300)
                getEdt(R.id.edt_info).isEnabled = false
            }
        }

        companion object {
            fun create(parent: ViewGroup): InfoHolder {
                return InfoHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.info_holder, parent, false))
            }
        }
    }
}

class NullHolder(view: View) : BaseHolder(view) {
    companion object {
        fun create(parent: ViewGroup): NullHolder {
            return NullHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.null_holder, parent, false))
        }
    }
}

