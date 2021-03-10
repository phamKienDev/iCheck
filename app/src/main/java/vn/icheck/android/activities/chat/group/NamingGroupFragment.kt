package vn.icheck.android.activities.chat.group

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.util.pick_image.PickImageDialog

class NamingGroupFragment : Fragment() {

    lateinit var createGroupViewModel: CreateGroupViewModel
    lateinit var memberGroupAdapter: MemberGroupAdapter
    var snackbar: Snackbar? = null

    companion object {
        private const val UPLOAD = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_group, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createGroupViewModel = ViewModelProvider(activity!!)[CreateGroupViewModel::class.java]
        val arr = createGroupViewModel.listMember.value
        arr?.let {
            val array = arrayListOf<MemberView>()
            array.addAll(arr)
            memberGroupAdapter = MemberGroupAdapter(array, MemberGroupAdapter.TYPE_NAMING)
            rcv_members.layoutManager = LinearLayoutManager(context)
            rcv_members.adapter = memberGroupAdapter
            tv_group_numbers.text = "${it.size} thành viên"
            edt_group_name.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    lifecycleScope.launch {
                        delay(300)
                        createGroupViewModel.groupName.postValue(s.toString())
                    }
                }
            })
            createGroupViewModel.groupAvatar.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Glide.with(requireContext().applicationContext)
                        .load(it)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                snackbar?.dismiss()
                                return false
                            }
                        })
                        .into(user_avatar)

            })

            img_change_avatar.setOnClickListener {
                val pickImageDialog = PickImageDialog(activity!!, UPLOAD)
                pickImageDialog.show(activity!!.supportFragmentManager, null)
                val vg = activity!!.findViewById<CoordinatorLayout>(R.id.root)
                snackbar = Snackbar.make(vg, "Đang upload ảnh!", Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
            }
        }
    }


}