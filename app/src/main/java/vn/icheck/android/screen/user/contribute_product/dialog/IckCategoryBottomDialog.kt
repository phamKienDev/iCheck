package vn.icheck.android.screen.user.contribute_product.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.ick_left_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.databinding.DialogCategoryContributeBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.contribute_product.IckContributeProductViewModel
import vn.icheck.android.screen.user.contribute_product.adapter.IckCategoryAdapter
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.ick.toPx
import vn.icheck.android.util.spansAppend

class IckCategoryBottomDialog : BottomSheetDialogFragment() {


    private var _binding: DialogCategoryContributeBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: IckCategoryAdapter
    private val spannableString = SpannableStringBuilder()
    private var listText = arrayListOf<String?>()
    val ickContributeProductViewModel: IckContributeProductViewModel by activityViewModels()
    private var job: Job? = null
    private var filterJob: Job? = null
    private lateinit var categoryHeadlineAdapter: CategoryHeadlineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            (bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?)?.let { bottomSheet ->
                dialog?.context?.let { it1 -> bottomSheet.background = ViewHelper.bgWhiteRadiusTop16(it1) }
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        _binding = DialogCategoryContributeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ickContributeProductViewModel.listCategory.clear()
        ickContributeProductViewModel.listCategory.add(null)
        categoryHeadlineAdapter = CategoryHeadlineAdapter({
            try {
                clearSearch()
                if (it == 0) {
                    ickContributeProductViewModel.categoryChildrenSource.parentId = 0L
                    if (!binding.edtSearch.text.isNullOrEmpty()) {
                        binding.edtSearch.setText("")
                    } else {
                        getData()
                    }
                    ickContributeProductViewModel.listCategory.clear()
                    ickContributeProductViewModel.listCategory.add(null)
                    categoryHeadlineAdapter.notifyDataSetChanged()
                } else {
                    ickContributeProductViewModel.listCategory[it]?.id?.let { id ->
                        ickContributeProductViewModel.categoryChildrenSource.parentId = id
                        getChildren(id)
                    }
                    for (item in ickContributeProductViewModel.listCategory.lastIndex downTo it) {
                        if (item > it) {
                            ickContributeProductViewModel.listCategory.removeAt(item)
                        }
                    }
                    categoryHeadlineAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                logError(e)
            }finally {
                binding.edtSearch.setText("")
                setSearchListener()
            }
        }, ickContributeProductViewModel.listCategory)
        binding.rcvCategoryTitle.adapter = categoryHeadlineAdapter
        ViewCompat.setNestedScrollingEnabled(binding.rcvCategoryTitle, false)
        adapter = IckCategoryAdapter {
            ickContributeProductViewModel.listCategory.add(it)
            categoryHeadlineAdapter.notifyDataSetChanged()
            if (it != null) {
                ickContributeProductViewModel.currentCategory = it
                getChildren(it.id)
                clearSearch()
                setSearchListener()
            }
        }
        binding.btnClear.setOnClickListener {
            dismiss()
        }
        binding.btnDone.setOnClickListener {
            if (ickContributeProductViewModel.currentCategory != null) {
                ickContributeProductViewModel.categoryChildrenSource.final.postValue(ickContributeProductViewModel.currentCategory?.id)
            } else if (ickContributeProductViewModel.getCategory() != null) {
                ickContributeProductViewModel.categoryChildrenSource.final.postValue(ickContributeProductViewModel.getCategory())
            }
            dismiss()
        }
        binding.rcvCategory.adapter = adapter
        getData()
        setSearchListener()
    }

    private val searchTextWatcher = object : AfterTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            if (s.toString().isNotEmpty()) {
                binding.edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_gray_24dp, 0, R.drawable.ic_delete_gray_vector, 0)
                binding.edtSearch.setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        if (motionEvent.rawX > binding.edtSearch.right - 30.toPx()) {
                            binding.edtSearch.setText("")
                            return@setOnTouchListener true
                        }
                    }
                    return@setOnTouchListener false
                }
            } else {
                binding.edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_gray_24dp, 0, 0, 0)
                binding.edtSearch.setOnTouchListener(null)
            }
            when {
                filterJob == null -> {
                    filterJob = lifecycleScope.launch {
                        delay(400)
                        ickContributeProductViewModel.filterCategoryList(s.toString()).collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                    }
                }
                filterJob?.isActive == true -> {
                    filterJob?.cancel()
                    filterJob = lifecycleScope.launch {
                        delay(400)
                        ickContributeProductViewModel.filterCategoryList(s.toString()).collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                    }
                }
                else -> {
                    filterJob = lifecycleScope.launch {
                        delay(400)
                        ickContributeProductViewModel.filterCategoryList(s.toString()).collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                    }
                }
            }
        }
    }

    private fun setSearchListener() {
        binding.edtSearch.addTextChangedListener(searchTextWatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearSearch()
        ickContributeProductViewModel.categoryChildrenSource.filterString = ""
        _binding = null
        filterJob = null
        listText.clear()
        spannableString.clear()

    }

    private fun clearSearch() {
        filterJob?.cancel()
        ickContributeProductViewModel.categoryChildrenSource.filterString = ""
        _binding?.edtSearch?.removeTextChangedListener(searchTextWatcher)
        _binding?.edtSearch?.setText("")
    }

    private fun getData() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            ickContributeProductViewModel.getCategoryList().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun getChildren(id: Long?) {
        if (id != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                ickContributeProductViewModel.getCount(id)
                ickContributeProductViewModel.getChildCategoryList(id).collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }
}