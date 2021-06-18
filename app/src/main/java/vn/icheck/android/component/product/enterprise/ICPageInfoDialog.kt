package vn.icheck.android.component.product.enterprise

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.DialogPageInfoBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.base_dialog.BaseBottomSheetDialogFragment
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.util.kotlin.GlideImageGetter

class ICPageInfoDialog : BaseBottomSheetDialogFragment() {
    private lateinit var binding: DialogPageInfoBinding
    private lateinit var obj: ICWidgetData

    companion object {
        fun show(fragmentManager: FragmentManager, obj: ICWidgetData) {
            if (fragmentManager.findFragmentByTag(ICPageInfoDialog::class.java.simpleName)?.isAdded != true) {
                ICPageInfoDialog().apply {
                    setData(obj)
                    show(fragmentManager, ICPageInfoDialog::class.java.simpleName)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogPageInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        binding.tvName.text = obj.name
        binding.tvAddress.text = vn.icheck.android.ichecklibs.Constant.getAddress(obj.address, obj.district, obj.city, obj.country, null)
        binding.tvPhone.text = obj.phone
        binding.tvEmail.text = obj.email
        binding.tvWebsite.text = obj.website
        binding.tvGln.text = obj.code

        val imageGetter = GlideImageGetter(binding.tvDescription)
        binding.tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(obj.description ?: "", Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(obj.description ?: "", imageGetter, null)
        }

        binding.tvPhone.setOnClickListener {
            Constant.callPhone(obj.phone)
        }
        binding.tvEmail.setOnClickListener {
            Constant.sendEmail(obj.email)
        }
        binding.tvWebsite.setOnClickListener {
            Constant.openUrl(obj.website)
        }
    }

    private fun setupView() {
        binding.root.background = ViewHelper.bgWhiteCornersTop16(requireContext())

        vn.icheck.android.ichecklibs.Constant.getSecondTextColor(requireContext()).apply {
            binding.tvAddress.setHintTextColor(this)
            binding.tvPhone.setHintTextColor(this)
            binding.tvEmail.setHintTextColor(this)
            binding.tvWebsite.setHintTextColor(this)
            binding.tvGln.setHintTextColor(this)
            binding.tvDescription.setHintTextColor(this)
        }
    }

    fun setData(obj: ICWidgetData) {
        this.obj = obj
    }
}