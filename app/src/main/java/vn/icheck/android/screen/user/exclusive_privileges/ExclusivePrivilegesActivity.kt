package vn.icheck.android.screen.user.exclusive_privileges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM

class ExclusivePrivilegesActivity : BaseActivityMVVM() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exclusive_privileges)
    }
}