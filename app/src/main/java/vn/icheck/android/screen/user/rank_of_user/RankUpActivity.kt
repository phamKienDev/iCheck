package vn.icheck.android.screen.user.rank_of_user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityRankUpBinding
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.wall.IckUserWallViewModel
import vn.icheck.android.util.ick.beVisible

@AndroidEntryPoint
class RankUpActivity : BaseActivityMVVM() {
    lateinit var binding:ActivityRankUpBinding
    val ickUserWallViewModel by viewModels<IckUserWallViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnClose.setOnClickListener {
            finish()
        }
        ickUserWallViewModel.getUserInfo().observe(this, { userInfo ->
            SessionManager.updateUser(userInfo?.data?.createICUser())
            if (userInfo != null) {
                binding.btnClose.beVisible()
                when (userInfo.data?.rank?.level) {
                    Constant.USER_LEVEL_SILVER -> binding.imageRank.setImageResource(R.drawable.ic_rank_levelup_silver)
                    Constant.USER_LEVEL_GOLD -> binding.imageRank.setImageResource(R.drawable.ic_rank_levelup_gold)
                    Constant.USER_LEVEL_DIAMOND -> binding.imageRank.setImageResource(R.drawable.ic_rank_levelup_diamond)
                }
            } else {
                finish()
            }
        })
    }
}