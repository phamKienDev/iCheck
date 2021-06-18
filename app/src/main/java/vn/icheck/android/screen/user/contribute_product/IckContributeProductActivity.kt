package vn.icheck.android.screen.user.contribute_product

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_list_product_question.*
import kotlinx.coroutines.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.ActivityIckContributeProductBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.screen.user.contribute_product.adapter.*
import vn.icheck.android.screen.user.contribute_product.dialog.IckCategoryBottomDialog
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.screen.user.contribute_product.viewmodel.ImageModel
import vn.icheck.android.screen.user.cropimage.CropImageActivity
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.toICBaseResponse
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

const val CONTRIBUTE_REQUEST = 1

@AndroidEntryPoint
class IckContributeProductActivity : BaseActivityMVVM() {

    companion object {
        fun start(context: Activity, barcode: String) {
            val i = Intent(context, IckContributeProductActivity::class.java)
            i.putExtra(ICK_BARCODE, barcode)
            context.startActivityForResult(i, CONTRIBUTION_PRODUCT)
        }

        fun start(context: Activity, barcode: String, productId: Long, title: String? = "Đóng góp sản phẩm") {
            val i = Intent(context, IckContributeProductActivity::class.java)
            i.putExtra(ICK_BARCODE, barcode)
            i.putExtra(PRODUCT_ID, productId)
            i.putExtra(TITLE, title)
            context.startActivityForResult(i, CONTRIBUTION_PRODUCT)
        }

        const val EDIT_IMAGE_1 = 131
        const val EDIT_IMAGE_2 = 132
        var instance: IckContributeProductActivity? = null
    }

    private val takeImageListener = object : TakeMediaListener {
        var currentView: View? = null
        override fun onPickMediaSucess(file: File) {
            loadImageIntoView(file)
        }

        private fun loadImageIntoView(file: File?) {
            currentView?.let {
                if (it.id == binding.imgFirst.id) {
                    binding.tvImgFirst simpleText "Chỉnh sửa"
                    ickContributeProductViewModel.setImage(file, 0)
                }
                if (it.id == binding.imgSecond.id) {
                    ickContributeProductViewModel.setImage(file, 1)
                    binding.tvImgSecond simpleText "Chỉnh sửa"
                }
                if (it is ImageView) {
                    Glide.with(it.context.applicationContext).load(file).into(it)
                    currentView = null
                }
            }
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
            ickContributeProductViewModel.addImage(file)
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
            CropImageActivity.start(this@IckContributeProductActivity, filePath, null, ratio, TakeMediaDialog.CROP_IMAGE_GALLERY)
        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            ickContributeProductViewModel.addImage(file)
            loadImageIntoView(file)
        }
    }


    var currentMediaDialog: TakeMediaDialog? = null

    //    private val takeImageDialog = TakeMediaDialog(this, takeImageListener, selectMulti = false, cropImage = true, isVideo = false, showBottom = true)
    private val takeImageDialog = TakeMediaDialog()

    val ickContributeProductViewModel: IckContributeProductViewModel by viewModels()
    val ickCategoryBottomDialog = IckCategoryBottomDialog()
//    var showKeyboard = false

    lateinit var listImageAdapter: ListImageAdapter
    private lateinit var binding: ActivityIckContributeProductBinding
    lateinit var categoryAttributesAdapter: CategoryAttributesAdapter
    private val broadcastReceiver = object : BroadcastReceiver() {
        var parent = 0
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == CONTRIBUTIONS_ACTION) {
                intent.getStringExtra(CONTRIBUTIONS_ACTION)?.let {
                    when (it) {
                        TAKE_IMAGE -> {
                            val position = intent.getIntExtra(TAKE_IMAGE, 0)
                            val imageDialog = TakeMediaDialog()
                            imageDialog.setListener(this@IckContributeProductActivity, object : TakeMediaListener {
                                override fun onPickMediaSucess(file: File) {
                                    ickContributeProductViewModel.categoryAttributes[position].values = file
                                    categoryAttributesAdapter.notifyItemChanged(position)
                                }

                                override fun onPickMuliMediaSucess(file: MutableList<File>) {

                                }

                                override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

                                }

                                override fun onDismiss() {
                                }

                                override fun onTakeMediaSuccess(file: File?) {
                                    if (file != null) {
                                        ickContributeProductViewModel.categoryAttributes[position].values = file
                                        categoryAttributesAdapter.notifyItemChanged(position)
                                    }
                                }
                            }, isVideo = false)
//                            imageDialog.show(supportFragmentManager, null)
                            request(imageDialog)
                        }
                        ADD_IMAGE -> {
                            val position = intent.getIntExtra(ADD_IMAGE, 0)
                            val imageDialog2 = TakeMediaDialog()
                            imageDialog2.setListener(this@IckContributeProductActivity, object : TakeMediaListener {
                                override fun onPickMediaSucess(file: File) {
                                    ickContributeProductViewModel.listImageModel.add(ImageModel(file))
                                    listImageAdapter.notifyDataSetChanged()
                                }

                                override fun onPickMuliMediaSucess(file: MutableList<File>) {
                                    for (item in file) {
                                        ickContributeProductViewModel.listImageModel.add(ImageModel(item))
                                    }
                                    listImageAdapter.notifyDataSetChanged()
                                }

                                override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
                                }

                                override fun onDismiss() {
                                }

                                override fun onTakeMediaSuccess(file: File?) {
                                    ickContributeProductViewModel.listImageModel.add(ImageModel(file))
                                    listImageAdapter.notifyDataSetChanged()
                                }
                            }, selectMulti = true, isVideo = false)
//                            imageDialog.show(supportFragmentManager, null)
                            request(imageDialog2)
                        }
                        REMOVE_IMAGE -> {
                            try {
                                val position = intent.getIntExtra(REMOVE_IMAGE, 0)
                                if (ickContributeProductViewModel.listImageModel.size > 1) {
                                    ickContributeProductViewModel.listImageModel.removeAt(position)
                                    listImageAdapter.notifyDataSetChanged()
                                }

                            } catch (e: Exception) {
                                logError(e)
                            }
                        }
                        ADD_IMAGE_HOLDER -> {
                            val position = intent.getIntExtra(ADD_IMAGE_HOLDER, 0)
                            parent = intent.getIntExtra(PARENT_POSITION, 0)
                            try {
                                val categoryAttributesModel = ickContributeProductViewModel.categoryAttributes[parent]
                                val imageDialog2 = TakeMediaDialog()
                                imageDialog2.setListener(this@IckContributeProductActivity, object : TakeMediaListener {
                                    override fun onPickMediaSucess(file: File) {

                                        if (categoryAttributesModel.categoryItem.type == "image-single") {
                                            categoryAttributesModel.addSingleImage(file)
                                        } else {
                                            categoryAttributesModel.addImage(file)
                                        }
                                        categoryAttributesAdapter.notifyItemChanged(parent)
                                    }

                                    override fun onPickMuliMediaSucess(file: MutableList<File>) {
                                        ickContributeProductViewModel.categoryAttributes[parent].addAllImage(file)
                                        categoryAttributesAdapter.notifyItemChanged(parent)
                                    }

                                    override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

                                    }

                                    override fun onDismiss() {
                                    }

                                    override fun onTakeMediaSuccess(file: File?) {
                                        if (categoryAttributesModel.categoryItem.type == "image-single") {
                                            categoryAttributesModel.addSingleImage(file)
                                        } else {
                                            categoryAttributesModel.addImage(file)
                                        }
                                        //                                    ickContributeProductViewModel.categoryAttributes[parent].addImage(file)
                                        categoryAttributesAdapter.notifyItemChanged(parent)
                                    }
                                }, selectMulti = categoryAttributesModel.categoryItem.type != "image-single", isVideo = false)
//                            imageDialog.show(supportFragmentManager, null)
                                request(imageDialog2)
                            } catch (e: Exception) {
                            }
                        }
                        REMOVE_IMAGE_HOLDER -> {
                            val position = intent.getIntExtra(REMOVE_IMAGE_HOLDER, 0)
                            val parent = intent.getIntExtra(PARENT_POSITION, 0)
                            if (parent < ickContributeProductViewModel.categoryAttributes.size) {
                                if (ickContributeProductViewModel.categoryAttributes[parent].categoryItem.type == "image-single") {
                                    ickContributeProductViewModel.categoryAttributes[parent].removeAll()
                                    ickContributeProductViewModel.categoryAttributes[parent].addImage(null)
                                } else {
                                    ickContributeProductViewModel.categoryAttributes[parent].removeAt(position)
                                }
                                categoryAttributesAdapter.notifyItemChanged(parent)
                            }
                        }
                        PUT_ATTRIBUTES -> {
                            val position = intent.getIntExtra(ATTRIBUTES_POSITION, 0)
                            try {
                                if (position < ickContributeProductViewModel.categoryAttributes.size) {
                                    when (ickContributeProductViewModel.categoryAttributes[position].categoryItem.type) {
                                        "number" -> {
                                            ickContributeProductViewModel.categoryAttributes[position].values = intent.getStringExtra(PUT_ATTRIBUTES)
                                        }
                                        "integer" -> {
                                            ickContributeProductViewModel.categoryAttributes[position].values = intent.getLongExtra(PUT_ATTRIBUTES, 0L)
                                        }
                                        "boolean" -> {
                                            ickContributeProductViewModel.categoryAttributes[position].values = intent.getBooleanExtra(PUT_ATTRIBUTES, false)
                                        }
                                        "group", "htmleditor", "textarea", "text" -> {
                                            val stringExtra = intent.getStringExtra(PUT_ATTRIBUTES)
                                            if (!stringExtra.isNullOrEmpty()) {
                                                ickContributeProductViewModel.categoryAttributes[position].values = stringExtra
                                            }
                                        }
                                        "select" -> {
                                            ickContributeProductViewModel.categoryAttributes[position].values = intent.getIntExtra(PUT_ATTRIBUTES, 0)
                                        }
                                        "date" -> {
                                            ickContributeProductViewModel.categoryAttributes[position].values = intent.getStringExtra(PUT_ATTRIBUTES)
                                            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                            ickContributeProductViewModel.categoryAttributes[position].calendar?.time = df.parse(intent.getStringExtra(PUT_ATTRIBUTES))
                                        }
                                        "multiselect" -> {
                                            try {
                                                if (ickContributeProductViewModel.categoryAttributes[position].values is ArrayList<*>) {
                                                    val id = intent.getIntExtra(PUT_ATTRIBUTES, 0)
                                                    val arr = (ickContributeProductViewModel.categoryAttributes[position].values as ArrayList<Double>)
                                                    if (id > 0) {
                                                        val filter = arr.firstOrNull { item ->
                                                            item.toInt() == id
                                                        }
                                                        if (filter == null) {
                                                            arr.add(id.toDouble())
                                                        }
                                                    } else {
                                                        val filter = arr.firstOrNull { item ->
                                                            item.toInt() == abs(id)
                                                        }
                                                        if (filter != null) {
                                                            arr.remove(abs(id).toDouble())
                                                        }
                                                    }

                                                }
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {

                            }

                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIckContributeProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        instance = this
        listImageAdapter = ListImageAdapter(ickContributeProductViewModel.listImageModel)
        binding.rcvImages.adapter = listImageAdapter

        takeImageDialog.setListener(this, takeImageListener, selectMulti = false, cropImage = true, isVideo = false)

        binding.textView4.text = intent.getStringExtra(TITLE) ?: "Đóng góp sản phẩm"

        ickContributeProductViewModel.setBarcode(intent.getStringExtra(ICK_BARCODE))
        ickContributeProductViewModel.listImages.observe(this, Observer { fileList ->
            ickContributeProductViewModel.listImageModel.clear()
            if (fileList.size >= 2) {
                binding.rcvImages.visibility = View.VISIBLE
                binding.tvDescription.visibility = View.GONE
                ickContributeProductViewModel.listImageModel.add(ImageModel(null))
                for (item in fileList.indices) {
                    if (item > 1) {
                        ickContributeProductViewModel.listImageModel.add(ImageModel(fileList[item]))
                    }
//                when (item) {
//                    0 -> {
//                        binding.imgFirst loadSimpleFile fileList[item]
//                    }
//                    1 -> {
//                        binding.imgSecond loadSimpleFile fileList[item]
//                    }
//                    else -> {
//                        ickContributeProductViewModel.listImageModel.add(0, ImageModel(fileList[item]))
//                    }
//                }
                }
                listImageAdapter.notifyDataSetChanged()
            } else {
                binding.rcvImages.beGone()
                binding.tvDescription.beVisible()
            }


        })
        initViews()
        categoryAttributesAdapter = CategoryAttributesAdapter(ickContributeProductViewModel.categoryAttributes)
        binding.groupAttributes.adapter = categoryAttributesAdapter
        binding.groupAttributes.layoutManager = CustomLinearLayoutManager(this)

        ickContributeProductViewModel.categoryChildrenSource.final.observe(this, Observer {
            ickContributeProductViewModel.setCategory(it)
            ickCategoryBottomDialog.dismiss()
            binding.edtCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_gray_24px, 0)
            binding.edtCategory simpleText ickContributeProductViewModel.currentCategory?.name
            ickContributeProductViewModel.getCategoryAttributes(it).observe(this, Observer { data ->
                if (data?.data != null) {
                    ickContributeProductViewModel.categoryAttributes.clear()
                    for (item in data.data ?: arrayListOf()) {
                        ickContributeProductViewModel.categoryAttributes.add(CategoryAttributesModel(item))
                    }
                    ickContributeProductViewModel.categoryAttributes.filter { model ->
                        model.categoryItem.type == "date"
                    }.forEach { model ->
                        model.fragmentManager = supportFragmentManager
                        model.calendar = Calendar.getInstance()
                    }
                    ickContributeProductViewModel.categoryAttributes.filter { model ->
                        model.categoryItem.type == "image"
                    }.forEach { model ->
                        model.addAddImage()
                    }
                    ickContributeProductViewModel.categoryAttributes.filter { model ->
                        model.categoryItem.type == "image-single"
                    }.forEach { model ->
                        model.addAddImage()
                    }
                    ickContributeProductViewModel.categoryAttributes.filter { model ->
                        model.categoryItem.type == "multiselect"
                    }.forEach { model ->
                        model.values = arrayListOf<Int>()
                    }

                    categoryAttributesAdapter.notifyDataSetChanged()
                }
            })
        })
        val filter = IntentFilter(CONTRIBUTIONS_ACTION)
        registerReceiver(broadcastReceiver, filter)
        ickContributeProductViewModel.getMyContribution(intent.getStringExtra(ICK_BARCODE))
                .observe(this, Observer {
                    it?.let { res ->
                        lifecycleScope.launch {
                            val rp = Gson().fromJson<HashMap<String, Any?>>(res.string(), object : TypeToken<HashMap<String, Any?>>() {}.type)
                            try {
                                rp.values.removeAll(sequenceOf(null))
                                if (rp.get("data") != null) {
                                    var firstJob: Job? = null
                                    var secondJob: Job? = null
                                    ickContributeProductViewModel.myContribute = 1
                                    binding.textView4 simpleText "Chỉnh sửa đóng góp"
                                    DialogHelper.showLoading(this@IckContributeProductActivity)
                                    ickContributeProductViewModel.requestBody.putAll(rp.get("data") as Map<String, Any?>)
                                    if (ickContributeProductViewModel.requestBody.get("id") != null) {
                                        ickContributeProductViewModel.myContributionId = (ickContributeProductViewModel.requestBody.get("id") as Double?)?.toLong()
                                    }
                                    if (ickContributeProductViewModel.requestBody.get("name") as String? != null) {
                                        binding.edtNameProduct simpleText ickContributeProductViewModel.requestBody.get("name") as String?
                                    }
                                    if (ickContributeProductViewModel.requestBody.get("price").toString().toLongOrNull() != null) {
                                        binding.edtPrice simpleText ickContributeProductViewModel.requestBody.get("price").toString()
                                    }
                                    if (ickContributeProductViewModel.requestBody["images"] as ArrayList<String>? != null) {
                                        firstJob = async {
                                            val arr = ArrayList<File>()
                                            val arrAsync = arrayListOf<Deferred<Any>>()
                                            val arrayList = ickContributeProductViewModel.requestBody["images"] as ArrayList<String>
                                            for (item in arrayList) {
                                                arrAsync.add(async(Dispatchers.IO) {
                                                    try {
                                                        val bm = Glide.with(this@IckContributeProductActivity.applicationContext)
                                                                .asBitmap()
                                                                .load(item)
                                                                .submit()
                                                                .get()
                                                        val fileName = item.split("/")
                                                        val f = File(getExternalFilesDir(".jpg"), fileName.last())
                                                        val os = FileOutputStream(f)
                                                        bm.compress(Bitmap.CompressFormat.PNG, 100, os)
                                                        arr.add(f)
                                                    } catch (e: Exception) {
                                                        dismissLoadingScreen()
                                                        withContext(Dispatchers.Main) {
                                                            showShortErrorToast("Đã xảy ra lỗi vui lòng thử lại sau")
                                                        }
                                                        logError(e)
                                                    }

                                                })
                                            }
                                            arrAsync.awaitAll()
                                            if (isActive) {
                                                val mapArr = arrayList.map { item ->
                                                    item.split("/").last()
                                                }
                                                arr.sortBy { file ->
                                                    mapArr.indexOf(file.name)
                                                }
                                                when (arr.size) {
                                                    0 -> {

                                                    }
                                                    1 -> {
                                                        binding.imgFirst.loadSimpleFile(arr[0])
                                                        binding.tvImgFirst simpleText "Chỉnh sửa"
                                                    }

                                                    else -> {
                                                        binding.imgFirst.loadSimpleFile(arr[0])
                                                        binding.imgSecond.loadSimpleFile(arr[1])
                                                        binding.tvImgFirst simpleText "Chỉnh sửa"
                                                        binding.tvImgSecond simpleText "Chỉnh sửa"
                                                    }
                                                }
                                                ickContributeProductViewModel.addAllImage(arr)
                                            }
                                        }
                                        firstJob?.start()
                                    }
                                    if (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>? != null) {
                                        binding.edtNamePage simpleText (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>).get("name") as String?
                                        binding.edtAddressPage simpleText (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>).get("address") as String?
                                        binding.edtPhonePage simpleText (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>).get("phone") as String?
                                        binding.edtEmail simpleText (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>).get("email") as String?
                                        binding.edtTax simpleText (ickContributeProductViewModel.requestBody.get("unverifiedOwner") as Map<*, *>).get("tax") as String?
                                    }
                                    if (ickContributeProductViewModel.requestBody.get("categoryId") != null && ickContributeProductViewModel.requestBody.get("categoryId") is Double?) {
                                        ickContributeProductViewModel.getCategoryById((ickContributeProductViewModel.requestBody.get("categoryId") as Double).toLong())
                                                .observe(this@IckContributeProductActivity, Observer { category ->
                                                    lifecycleScope.launch {
                                                        ickContributeProductViewModel.currentCategory = category?.data?.rows?.firstOrNull()
                                                        binding.edtCategory simpleText ickContributeProductViewModel.currentCategory?.name
                                                        if (ickContributeProductViewModel.currentCategory?.id != null) {
                                                            ickContributeProductViewModel.getCategoryAttributes(ickContributeProductViewModel.currentCategory?.id!!).observe(this@IckContributeProductActivity, Observer { data ->
                                                                lifecycleScope.launch {
                                                                    try {
                                                                        if (data?.data != null) {
                                                                            /**
                                                                             * Lấy danh mục về. Check nếu tồn tại danh mục này trong phần thông tin đã đóng góp thì fill value vào
                                                                             */
                                                                            val att = ickContributeProductViewModel.requestBody["attributes"] as ArrayList<LinkedTreeMap<String, Any?>>
                                                                            ickContributeProductViewModel.categoryAttributes.clear()
                                                                            for (item in data.data ?: arrayListOf()) {
                                                                                ickContributeProductViewModel.categoryAttributes.add(CategoryAttributesModel(item).apply {
                                                                                    val filt = att.firstOrNull { map ->
                                                                                        map.containsValue("${item.code}")
                                                                                    }
                                                                                    if (filt != null) {
                                                                                        this.values = filt.get("value")
                                                                                        if (item.type == "image-single" || item.type == "image") {
                                                                                            if (item.type == "image") {
                                                                                                addImage(null)
                                                                                            }
                                                                                            if (this.values is ArrayList<*>) {
                                                                                                 secondJob = async {
                                                                                                    val arr = arrayListOf<File>()
                                                                                                    val arrAsync = arrayListOf<Deferred<Any>>()
                                                                                                    val arrayList = this@apply.values as ArrayList<String>
                                                                                                    for (image in arrayList) {
                                                                                                        arrAsync.add(async(Dispatchers.IO) {
                                                                                                            try {
                                                                                                                val bm = Glide.with(this@IckContributeProductActivity.applicationContext)
                                                                                                                        .asBitmap()
                                                                                                                        .load(image)
                                                                                                                        .submit()
                                                                                                                        .get()
                                                                                                                val fileName = image.split("/")
                                                                                                                val f = File(getExternalFilesDir(".jpg"), fileName.last())
                                                                                                                val os = FileOutputStream(f)
                                                                                                                bm.compress(Bitmap.CompressFormat.PNG, 100, os)
                                                                                                                arr.add(f)
                                                                                                            } catch (e: Exception) {
                                                                                                                dismissLoadingScreen()
                                                                                                                withContext(Dispatchers.Main) {
                                                                                                                    showShortErrorToast("Đã xảy ra lỗi vui lòng thử lại sau")
                                                                                                                }
                                                                                                                logError(e)
                                                                                                            }
                                                                                                        })
                                                                                                    }
                                                                                                    arrAsync.awaitAll()
                                                                                                    if (isActive) {
                                                                                                        addAllImage(arr)
                                                                                                        categoryAttributesAdapter.notifyDataSetChanged()
                                                                                                    }
                                                                                                }
                                                                                                secondJob?.start()
                                                                                            }
                                                                                        } else if (item.type == "date") {
                                                                                            fragmentManager = supportFragmentManager
                                                                                            calendar = Calendar.getInstance()
                                                                                        }

                                                                                    } else if (item.type == "image-single") {
                                                                                        addImage(null)
                                                                                    } else if (item.type == "image") {
                                                                                        addImage(null)
                                                                                    } else if (item.type == "date") {
                                                                                        fragmentManager = supportFragmentManager
                                                                                        calendar = Calendar.getInstance()
                                                                                    } else if (item.type == "multiselect") {
                                                                                        values = arrayListOf<Int>()
                                                                                    }
                                                                                })
                                                                            }

                                                                            categoryAttributesAdapter.notifyDataSetChanged()
                                                                            DialogHelper.closeLoading(this@IckContributeProductActivity)
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        DialogHelper.closeLoading(this@IckContributeProductActivity)
                                                                        logError(e)
                                                                    }
                                                                }
                                                            })
                                                        }
                                                    }
                                                })
                                    } else {
                                        DialogHelper.closeLoading(this@IckContributeProductActivity)
                                    }
                                    val data = rp.get("data") as Map<String, Any?>
                                    if (data["hidden"] != null) {
                                        if (data["hidden"] as Boolean) {
                                            val msg = if (!(data["reason"] as String?).isNullOrEmpty()) "Đóng góp trước đó của bạn đã bị Huỷ duyệt với lý do: \"" + data["reason"].toString() +"\". Bạn có muốn thực hiện đóng góp thông tin lại cho sản phẩm này?" else "Đóng góp trước đó của bạn đã bị Huỷ duyệt bởi người quản trị. Bạn có muốn thực hiện đóng góp thông tin lại cho sản phẩm này?"
                                            DialogHelper.showConfirm(this@IckContributeProductActivity, "Thông báo",
                                                    msg,
                                                    "Hủy",
                                                    "Đóng góp lại",
                                                    false,
                                                    object : ConfirmDialogListener {
                                                        override fun onDisagree() {
                                                            finish()
                                                        }

                                                        override fun onAgree() {
                                                            try {
                                                                firstJob?.cancel()
                                                                secondJob?.cancel()
                                                                ickContributeProductViewModel.requestBody.clear()
                                                                categoryAttributesAdapter.notifyDataSetChanged()

                                                                binding.edtNameProduct.setText("")
                                                                binding.edtPrice.setText("")
                                                                binding.edtAddressPage.setText("")
                                                                binding.edtEmail.setText("")
                                                                binding.edtNamePage.setText("")
                                                                binding.edtPhonePage.setText("")
                                                                binding.edtTax.setText("")
                                                                binding.edtCategory.setText("")
                                                                binding.edtCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24dp, 0)
                                                                ickContributeProductViewModel.categoryAttributes.clear()
                                                                categoryAttributesAdapter.notifyDataSetChanged()
                                                                binding.tvImgFirst simpleText "+ Ảnh mặt trước"
                                                                binding.imgFirst.setImageResource(R.drawable.ic_front_image_holder)
                                                                binding.imgSecond.setImageResource(R.drawable.ic_back_image_holder)
                                                                binding.tvImgSecond simpleText "+ Ảnh mặt sau"
                                                                lifecycleScope.launch {
                                                                    delay(200)
                                                                    ickContributeProductViewModel.listImageModel.clear()
                                                                    ickContributeProductViewModel.arrayListImage.clear()
                                                                    ickContributeProductViewModel.postSize()
                                                                    ickContributeProductViewModel.requestBody.remove("categoryId")
                                                                    ickContributeProductViewModel.requestBody.remove("name")
                                                                    ickContributeProductViewModel.currentCategory = null
                                                                }
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    })
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                DialogHelper.closeLoading(this@IckContributeProductActivity)
                                logError(e)
                            }

                        }
                    }
                })
    }

    private fun setupView() {
        ViewHelper.bgWhiteStrokeLineColor1Corners4(this).apply {
            binding.edtNameProduct.background=this
            binding.edtPrice.background=this
            binding.edtNamePage.background=this
            binding.edtAddressPage.background=this
            binding.edtPhonePage.background=this
            binding.edtEmail.background=this
            binding.edtTax.background=this
        }
    }

    private fun initViews() {
        binding.btnContinue.background = ViewHelper.bgPrimaryCorners4(this)
        binding.edtCategory.setHintTextColor(vn.icheck.android.ichecklibs.Constant.getDisableTextColor(this))

        val barcode = intent.getStringExtra(ICK_BARCODE)
        binding.edtBarcode.background=ViewHelper.bgGrayF0StrokeLineColor1Corners4(this)
        binding.edtBarcode.setText(barcode)

        binding.imgFirst.setOnClickListener {
            delayAction({
                takeImageListener.currentView = binding.imgFirst
                request(takeImageDialog)
            }, 500)
        }
        binding.imgSecond.setOnClickListener {
            delayAction({
                takeImageListener.currentView = binding.imgSecond
                request(takeImageDialog)
            }, 500)
//            request(takeImageDialog)
//            takeImageListener.currentView = binding.imgSecond
        }
        binding.rootScroll.setOnTouchListener(object : View.OnTouchListener {
            var startClickTime = 0L
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {

                if (event?.getAction() == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis();
                } else if (event?.getAction() == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                        // Touch was a simple tap. Do whatever.
                    } else {
                        // Touch was a not a simple tap.
                        try {
                            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(binding.rootScroll.windowToken, 0)
                            binding.textView4.post {
                                binding.textView4.requestFocus()
                                binding.textView4.performClick()
                            }
                        } catch (e: Exception) {
                        }
                    }

                }
                return false
            }
        })
//        binding.groupAttributes.viewTreeObserver.addOnScrollChangedListener {
//            try {
//
//                val imm:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(binding.rootScroll.windowToken, 0)
//                binding.textView4.post {
//                    binding.textView4.requestFocus()
//                    binding.textView4.performClick()
//                }
//            } catch (e: Exception) {
//            }
//
//        }
//        binding.rootScroll.viewTreeObserver.addOnScrollChangedListener {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(binding.rootScroll.windowToken, 0)
//        }
        binding.tvImgFirst.setOnClickListener {
            delayAction({
                if (ickContributeProductViewModel.arrayListImage.size >= 1) {
                    startActivityForResult(Intent(this, CropImageActivity::class.java).apply {
                        putExtra(Constant.DATA_1, ickContributeProductViewModel.arrayListImage.firstOrNull()?.absolutePath)
                    }, EDIT_IMAGE_1)
                }
            })

        }

        binding.tvImgSecond.setOnClickListener {
            delayAction({
                if (ickContributeProductViewModel.arrayListImage.size >= 2) {
                    startActivityForResult(Intent(this, CropImageActivity::class.java).apply {
                        putExtra(Constant.DATA_1, ickContributeProductViewModel.arrayListImage[1]?.absolutePath)
                    }, EDIT_IMAGE_2)
                }
            })
        }
        binding.edtNameProduct.setImeActionDone({
            binding.edtNameProduct.clearFocus()
        })
        binding.edtNameProduct.addTextChangedListener {
            ickContributeProductViewModel.setName(it?.trim().toString())
        }
//        binding.edtPrice.setImeActionDone({
//            binding.edtPrice.clearFocus()
//        })
        binding.edtPrice.addPriceTextWatcher()
        binding.edtCategory.setOnClickListener {
            delayAction({
                ickContributeProductViewModel.categoryChildrenSource.parentId = 0L
                if (!ickCategoryBottomDialog.isVisible) {
                    ickCategoryBottomDialog.show(supportFragmentManager, null)
                } else {
                    ickCategoryBottomDialog.dismiss()
                    ickCategoryBottomDialog.show(supportFragmentManager, null)
                }
            }, 500)

        }
        binding.edtCategory.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (ickContributeProductViewModel.categoryAttributes.isNotEmpty()) {
                            binding.edtCategory.setText("")
                            binding.edtCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24dp, 0)
                            ickContributeProductViewModel.categoryAttributes.clear()
                            categoryAttributesAdapter.notifyDataSetChanged()
                        }
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
        binding.btnContinue.setOnClickListener {
            showLoadingTimeOut(5000)
            if (!binding.edtPrice.text?.trim().isNullOrEmpty()) {
                ickContributeProductViewModel.setPrice(binding.edtPrice.text?.trim()?.toString()?.replace("đ", "")?.toLong()
                        ?: -1)
            } else {
                ickContributeProductViewModel.setPrice(-1)
            }
            if (ickContributeProductViewModel.arrayListImage.size >= 2 && binding.edtNameProduct.text!!.trim().trim().isNotEmpty()) {

                ickContributeProductViewModel.finalStep()?.observe(this, Observer { listInfos ->
                    val filter = listInfos.firstOrNull {
                        it.state != WorkInfo.State.SUCCEEDED
                    }
                    if (filter == null) {
                        listInfos.firstOrNull { workInfo ->
                            workInfo.outputData.getString("key") == "front"
                        }?.let { workInfo ->
                            ickContributeProductViewModel.addSrc(workInfo.outputData.getString(ICK_IMAGE_UPLOADED_SRC))
                        }
                        listInfos.firstOrNull { workInfo ->
                            workInfo.outputData.getString("key") == "back"
                        }?.let { workInfo ->
                            ickContributeProductViewModel.addSrc(workInfo.outputData.getString(ICK_IMAGE_UPLOADED_SRC))
                        }
                        for (item in listInfos) {
                            if (item.state == WorkInfo.State.SUCCEEDED && item.outputData.getString("key") != "front" && item.outputData.getString("key") != "back") {
                                ickContributeProductViewModel.addSrc(item.outputData.getString(ICK_IMAGE_UPLOADED_SRC))
                            }
                        }
                        var hasAttrImage = false
                        for (item in ickContributeProductViewModel.categoryAttributes) {
                            if (item.hasImages()) {
                                hasAttrImage = true
                            }
                        }
                        if (hasAttrImage) {
                            ickContributeProductViewModel.uploadAttributesImages()?.observe(this, Observer { listAttrImgs ->
                                val filterAttr = listAttrImgs.firstOrNull {
                                    it.state != WorkInfo.State.SUCCEEDED
                                }
                                if (filterAttr == null) {
                                    val hash = listAttrImgs.groupBy {
                                        it.outputData.getString("key")
                                    }.map {
                                        it.key to it.value.map { info ->
                                            info.outputData.getString(ICK_IMAGE_UPLOADED_SRC)
                                        }
                                    }
//                                    val arrI = arrayListOf<String?>()
//                                    val arrK = arrayListOf<String?>()
//                                    for (item in listAttrImgs) {
//                                        arrI.add(item.outputData.getString(ICK_IMAGE_UPLOADED_SRC))
//                                        arrK.add(item.outputData.getString("key"))
//                                    }
//                                    arrK.distinct()
                                    for (item in hash) {
                                        ickContributeProductViewModel.categoryAttributes.first {
                                            it.categoryItem.code == item.first
                                        }.values = item.second
                                        for (i in item.second) {
                                            ickContributeProductViewModel.removeSrc(i)
                                        }
                                    }
                                    ickContributeProductViewModel.contributeProduct().observe(this, Observer {
                                        dismissLoadingScreen()
                                        if (it?.data != null) {
                                            Intent().apply {
                                                putExtra(Constant.DATA_1, it.data!!.productId)
                                                putExtra(Constant.DATA_2, ickContributeProductViewModel.myContribute)
                                                setResult(RESULT_OK, this)
                                            }
                                            finish()
                                        }
                                    })
                                }

                            })
                        } else {
                            ickContributeProductViewModel.contributeProduct().observe(this, Observer {

                                dismissLoadingScreen()
                                if (it?.data != null) {
                                    Intent().apply {
                                        putExtra(Constant.DATA_1, it.data!!.productId)
                                        putExtra(Constant.DATA_2, ickContributeProductViewModel.myContribute)
                                        setResult(RESULT_OK, this)
                                    }
                                    finish()
                                }
                            })
                        }
                    }
                })
            } else {
                if (ickContributeProductViewModel.arrayListImage.size < 2) {
                    showShortError("Sản phẩm cần tối thiểu 2 ảnh!")
                } else if (binding.edtNameProduct.text!!.trim().isEmpty()) {
                    showShortError("Sản phẩm phải có tên!")
                }
                dismissLoadingScreen()
            }
        }
        ickContributeProductViewModel.mErr.observe(this, Observer {
            dismissLoadingScreen()
            showShortErrorToast(it.toICBaseResponse()?.message)
        })
        ickContributeProductViewModel.mException.observe(this) {
            dismissLoadingScreen()
            showShortErrorToast(it.message)
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.edtNamePage.addTextChangedListener {
            ickContributeProductViewModel.setOwnerName(it?.trim().toString())
        }
        binding.edtAddressPage.addTextChangedListener {
            ickContributeProductViewModel.setAddress(it?.trim().toString())
        }
        binding.edtEmail.addTextChangedListener {
            ickContributeProductViewModel.setEmail(it?.trim().toString())
        }
        binding.edtTax.addTextChangedListener {
            ickContributeProductViewModel.setTax(it?.trim().toString())
        }
        binding.edtPhonePage.addTextChangedListener {
            ickContributeProductViewModel.setPhone(it?.trim().toString())
        }
        binding.tvSearchGg.setOnClickListener {
            startActivity(Intent(Intent.ACTION_WEB_SEARCH))
        }
    }

    fun request(dialog: TakeMediaDialog) {
        delayAfterAction({
            if (ContextCompat.checkSelfPermission(
                            this@IckContributeProductActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            this@IckContributeProductActivity,
                            Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            CONTRIBUTE_REQUEST
                    )
                }
            } else {
                try {
                    currentMediaDialog = dialog
                    if (currentMediaDialog?.isAdded == false) {
                        currentMediaDialog?.show(supportFragmentManager, null)
                    } else {
                        currentMediaDialog?.dismiss()
                        currentMediaDialog?.show(supportFragmentManager, null)
                    }
                } catch (e: Exception) {
                    logError(e)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TakeMediaDialog.CROP_IMAGE_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    data?.getStringExtra(Constant.DATA_1)?.let { url ->
                        takeImageListener.onPickMediaSucess(File(url))
                        currentMediaDialog?.dismiss()
                    }
                }
            }
            EDIT_IMAGE_1 -> {
                if (resultCode == RESULT_OK) {
                    data?.getStringExtra(Constant.DATA_1)?.let { result ->
                        ickContributeProductViewModel.setImage(File(result), 0)
                        binding.imgFirst.setImageBitmap(BitmapFactory.decodeFile(result))
                    }
                }
            }
            EDIT_IMAGE_2 -> {
                if (resultCode == RESULT_OK) {
                    data?.getStringExtra(Constant.DATA_1)?.let { result ->
                        ickContributeProductViewModel.setImage(File(result), 1)
                        binding.imgSecond.setImageBitmap(BitmapFactory.decodeFile(result))
                    }
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        unregistrar = KeyboardVisibilityEvent.registerEventListener(this, object : KeyboardVisibilityEventListener {
//            override fun onVisibilityChanged(isOpen: Boolean) {
//                if (!isOpen) {
//                    currentFocus?.apply {
//                        clearFocus()
//                        isSelected = false
//                    }
//                    binding.groupAttributes.apply {
//                        clearFocus()
//                        isSelected = false
//                    }
//
//                    categoryAttributesAdapter.notifyDataSetChanged()
//                }
//            }
//        })
//    }

    inline fun delayAfterAction(crossinline action: () -> Unit, timeout:Long = 200L) {
        job = if (job?.isActive == true) {
            job?.cancel()
            lifecycleScope.launch {
                action()
                delay(timeout)
            }
        } else {
            lifecycleScope.launch {
                action()
                delay(timeout)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentMediaDialog = null
        instance = null
    }

//    override fun onPause() {
//        super.onPause()
//        unregistrar?.unregister()
//        unregistrar = null
//    }
}