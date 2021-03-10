package vn.icheck.android.activities.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_group_chat.*
import vn.icheck.android.R
import vn.icheck.android.activities.base.BaseICActivity
import vn.icheck.android.fragments.AddMembersFragment
import vn.icheck.android.fragments.CreateGroupFragment
import vn.icheck.android.fragments.LoadingDialogFragment
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICChatCodeResponse
import vn.icheck.android.network.models.ICFollowing

class CreateGroupChatActivity : BaseICActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group_chat)
        INSTANCE = this
        initView()
        supportFragmentManager.beginTransaction().replace(R.id.create_group_container, AddMembersFragment()).commit()
    }

    private fun initView() {
        img_back.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        tv_create.setOnClickListener(this)
    }

    fun showContinue() {
        tv_next.visibility = View.VISIBLE
    }

    fun hideContinue() {
        tv_next.visibility = View.GONE
    }

    fun hideCreate() {
        tv_create.visibility = View.GONE
    }

    fun showCreate() {
        tv_create.visibility = View.VISIBLE
    }


    fun save(list: List<ICFollowing.Rows>) {
        listData.clear()
        listData.addAll(list)
    }

    fun setName(name: String) {
        groupName = name
    }

    fun setAvatar(url: String) {
        groupAvatar = url
    }

    fun getData(): List<ICFollowing.Rows> {
        return listData
    }

    fun setSuccess(isSuccess: Boolean) {
        success = isSuccess
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                onBackPressed()
            }
            R.id.tv_next -> {
                hideContinue()
                showCreate()
                val fragment = CreateGroupFragment()
                supportFragmentManager.beginTransaction().replace(R.id.create_group_container, fragment).commit()
            }
            R.id.tv_create -> {
                if (groupName.isNotEmpty()) {
                    createGroupRoom()
                } else {
                    Toast.makeText(this, "Không để trống trường tên", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createGroupRoom() {
        if (success) {
            val dialog = LoadingDialogFragment()
            dialog.show(supportFragmentManager, null)
            val createRoom = hashMapOf<String, Any>()
            createRoom.put("name", groupName)
            if (groupAvatar.isNotEmpty()) {
                createRoom.put("logo", groupAvatar)
            }
            val listMemberId = mutableListOf<String>()
            listData.forEach {
                listMemberId.add("i-" + it.rowObject.id.toString())
            }
            createRoom.put("members", listMemberId)
            try {
                ICNetworkClient.getChatClient()
                            .createRoom(createRoom)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : SingleObserver<ICChatCodeResponse> {

                            override fun onSuccess(t: ICChatCodeResponse) {
                                dialog.dismiss()
                                finish()
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                Log.e("debugPhone", e.toString())
                            }
                        })
            } catch (e: NullPointerException) {
                Log.e("debugPhone", e.toString())
            }
        }
    }

    companion object {
        private lateinit var INSTANCE: CreateGroupChatActivity
        private val listData = mutableListOf<ICFollowing.Rows>()
        private var groupName = ""
        private var groupAvatar = ""
        private var success = false
        fun getInstance(): CreateGroupChatActivity {
            return INSTANCE
        }
    }
}
