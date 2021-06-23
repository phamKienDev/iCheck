package vn.icheck.android.component.my_contribution

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.constant.ACTION_PRODUCT_DETAIL
import vn.icheck.android.constant.SHOW_ATTRIBUTES
import vn.icheck.android.databinding.ItemMyContributionBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.screen.user.product_detail.product.my_contribution.MyContributionAdapter
import vn.icheck.android.util.ick.getLayoutInflater
import vn.icheck.android.ichecklibs.util.setText

class MyContributionHolder(val binding: ItemMyContributionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(myContributionViewModel: MyContributionViewModel) {
        val arr = arrayListOf<CategoryAttributesModel>()
        for (item in myContributionViewModel.arrayInfo) {
            arr.add(CategoryAttributesModel(item).apply {
                getValues()
            })
        }
        binding.textView34.setText(R.string.thong_tin_them_d, myContributionViewModel.arrayInfo.size)
        val attributesAdapter = MyContributionAdapter(listCategory = arr, maxLine = 5, maxLength = 1)
        binding.rcvMyContribution.adapter = attributesAdapter
        binding.btnShowAll.setOnClickListener {
            binding.root.context.sendBroadcast(Intent(ACTION_PRODUCT_DETAIL).apply {
                putExtra(ACTION_PRODUCT_DETAIL, SHOW_ATTRIBUTES)
            })
        }
    }

    companion object {
        fun create(parent: ViewGroup): MyContributionHolder {
            val binding = ItemMyContributionBinding.inflate(parent.getLayoutInflater(), parent, false)
            return MyContributionHolder(binding)
        }
    }
}