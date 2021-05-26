package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import vn.icheck.android.databinding.ItemCampaignV61Binding
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.util.kotlin.WidgetUtils

class ICCampaignV61Holder(parent: ViewGroup, val binding: ItemCampaignV61Binding =
        ItemCampaignV61Binding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        RecyclerView.ViewHolder(binding.root) {

    private var textWatcher: TextWatcher? = null

    fun bind(obj: ICKLoyalty, bannerClicked: (obj: ICKLoyalty) -> Unit, checkCode: (obj: ICKLoyalty) -> Unit) {
        WidgetUtils.loadImageUrl(binding.bannerLoyalty, obj.image?.original)
        binding.loyaltyDescription.text = obj.name

        if (textWatcher == null) {
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    obj.content = binding.edittextCode.text.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        }

        binding.edittextCode.apply {
            removeTextChangedListener(textWatcher)
            setText(obj.content)
            setSelection(text.toString().length)
            addTextChangedListener(textWatcher)
        }

        binding.btnCheckCode.setOnClickListener {
            binding.btnCheckCode.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                binding.btnCheckCode.isEnabled = true
            }, 1000)

            checkCode(obj)
        }

        TabLayout(itemView.context)

        binding.bannerLoyalty.setOnClickListener {
            bannerClicked(obj)
        }
    }
}