package vn.icheck.android.screen.user.wall.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.R
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.databinding.FragmentPrivacySettingsBinding
import vn.icheck.android.model.ApiErrorResponse
import vn.icheck.android.model.ApiSuccessResponse
import vn.icheck.android.model.privacy.UserPrivacyModel
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.logError

class PrivacySettingFragment:Fragment(), OnSaveChangeListener {
    private var _binding:FragmentPrivacySettingsBinding? = null
    private val binding get() = _binding!!
    private val ickUserWallViewModel:IckUserWallViewModel by activityViewModels()
    private lateinit var privacySettingsAdapter: PrivacySettingsAdapter

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPrivacySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        privacySettingsAdapter = PrivacySettingsAdapter(this)
        binding.rcvPrivacy.adapter = privacySettingsAdapter
        binding.rcvPrivacy.layoutManager = LinearLayoutManager(requireContext())
        ickUserWallViewModel.getUserPrivacy().observe(viewLifecycleOwner, Observer {
            if (it is ApiSuccessResponse) {
                ickUserWallViewModel.userPrivacyResponse = it.body
                val listViewModel = arrayListOf<ICViewModel>()

                val gr = it.body.data?.data?.groupBy { item ->
                    item?.privacyId
                }
                for (item in gr?.entries!!) {
                    for (i in item.value) {
                        if (i?.selected!!) {
                            ickUserWallViewModel.privacy[item.key!!] = i.privacyElementId!!
                        }
                    }
                    listViewModel.add(UserPrivacyModel(item.value))
                }
                listViewModel.add(object : ICViewModel {
                    override fun getTag(): String {
                        return ""
                    }

                    override fun getViewType(): Int {
                        return ICViewTypes.ITEM_PRIVACY_CONFIRM
                    }
                })
                privacySettingsAdapter.updateList(listViewModel)
            } else if (it is ApiErrorResponse) {
                logError(it.error)
            }
        })
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onSave() {
        if (ickUserWallViewModel.privacy.isNotEmpty()) {
            ickUserWallViewModel.putUserPrivacy().observe(viewLifecycleOwner, Observer {
                findNavController().popBackStack()
            })
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onSelect(id:Int?, code: Int?) {
        if (id != null && code != null) {
            ickUserWallViewModel.privacy[id] = code
        }
    }
}