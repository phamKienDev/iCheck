package vn.icheck.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import vn.icheck.android.databinding.ActivityTestBinding
import vn.icheck.android.screen.account.icklogin.fragment.IckUserInfoFragment

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {
    lateinit var binding:ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = IckUserInfoFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment, null).commit()
    }
}