package vn.icheck.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.databinding.ActivityTestBinding
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.screen.account.icklogin.fragment.IckUserInfoFragment

@AndroidEntryPoint
class TestActivity : BaseActivityMVVM() {
    lateinit var binding:ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.name.setDrawbleNextEndText("a very long text just to test username ", R.drawable.ic_verified_16px)
    }
}