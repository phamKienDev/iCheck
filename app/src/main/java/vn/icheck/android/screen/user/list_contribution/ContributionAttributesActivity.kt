package vn.icheck.android.screen.user.list_contribution

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.databinding.ActivityContributionAttributesBinding
import vn.icheck.android.network.model.category.CategoryAttributesItem
import vn.icheck.android.screen.user.contribute_product.IckContributeProductViewModel
import vn.icheck.android.screen.user.contribute_product.adapter.CategoryAttributesAdapter
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.screen.user.product_detail.product.my_contribution.MyContributionAdapter
import vn.icheck.android.util.ick.loadRoundedImage
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.simpleText
import java.io.File

@AndroidEntryPoint
class ContributionAttributesActivity : BaseActivityMVVM() {

    val ickContributeProductViewModel: IckContributeProductViewModel by viewModels()
    lateinit var categoryAttributesAdapter: MyContributionAdapter

    lateinit var binding:ActivityContributionAttributesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContributionAttributesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        categoryAttributesAdapter = MyContributionAdapter(listCategory = ickContributeProductViewModel.categoryAttributes)
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.imgProduct.loadRoundedImage(intent.getStringExtra("image"), R.drawable.img_product_shop_default, 4)
        binding.rcvAttributes.adapter = categoryAttributesAdapter
        intent.getParcelableArrayListExtra<CategoryAttributesItem>("listInfo")?.let {
            for (item in it) {
                ickContributeProductViewModel.categoryAttributes.add(CategoryAttributesModel(item).apply {
                    getValues()
                })
            }
            categoryAttributesAdapter.notifyDataSetChanged()
        }
//        ickContributeProductViewModel.getContribution(intent.getLongExtra("productId", 0L)).observe(this, Observer {
//
//            it?.let { res ->
//
//                try {
//                    val rp = Gson().fromJson<HashMap<String, Any?>>(res.string(), object : TypeToken<HashMap<String, Any?>>() {}.type)
//                    rp.values.removeAll(sequenceOf(null))
//
//                    ickContributeProductViewModel.requestBody.putAll(rp.get("data") as Map<String, Any?>)
//                    ickContributeProductViewModel.myContributionId = (ickContributeProductViewModel.requestBody.get("id") as Double?)?.toLong()
//                    if (ickContributeProductViewModel.requestBody.get("categoryId") is Double?) {
//                        ickContributeProductViewModel.getCategoryById((ickContributeProductViewModel.requestBody.get("categoryId") as Double).toLong())
//                                .observe(this, Observer { category ->
//                                    ickContributeProductViewModel.currentCategory = category?.data?.rows?.firstOrNull()
//                                    if (ickContributeProductViewModel.currentCategory?.id != null) {
//                                        ickContributeProductViewModel.getCategoryAttributes(ickContributeProductViewModel.currentCategory?.id!!).observe(this, Observer { data ->
//                                            if (data?.data != null) {
//                                                val att = ickContributeProductViewModel.requestBody["attributes"] as ArrayList<LinkedTreeMap<String, Any?>>
//                                                ickContributeProductViewModel.categoryAttributes.clear()
//                                                for (item in data.data) {
//                                                    val filt = att.firstOrNull { map ->
//                                                        map.containsValue("${item.code}")
//                                                    }
//                                                    if (filt != null) {
//                                                        ickContributeProductViewModel.categoryAttributes.add(CategoryAttributesModel(item).apply {
//                                                            isEditable = false
//                                                            this.values = filt.get("value")
//                                                        })
//                                                    }
//
//                                                }
//                                                categoryAttributesAdapter.notifyDataSetChanged()
//                                            }
//                                        })
//                                    }
//                                })
//                    }
//                } catch (e: Exception) {
//                    logError(e)
//                }
//            }
//        })
    }
}