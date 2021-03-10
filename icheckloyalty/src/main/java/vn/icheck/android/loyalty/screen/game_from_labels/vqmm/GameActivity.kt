package vn.icheck.android.loyalty.screen.game_from_labels.vqmm

import android.media.MediaPlayer
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.model.RowsItem
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.GameListViewModel

class GameActivity : BaseActivityGame() {

    lateinit var clickMp: MediaPlayer
    lateinit var successMp: MediaPlayer
    private val gameListViewModel: GameListViewModel by viewModels()

    override val getLayoutID: Int
        get() = R.layout.activity_game

    override fun onInitView() {
        clickMp = MediaPlayer.create(this, R.raw.click)
        clickMp.isLooping = true
        successMp = MediaPlayer.create(this, R.raw.success)

    }

    fun playOnClick() {
        clickMp.start()
    }

    fun playOnSucess() {
        clickMp.stop()
        successMp.start()
    }

    fun inGame() {
        gameListViewModel.inGame = true
    }

    fun outGame() {
        gameListViewModel.inGame = false
    }

    override fun onBackPressed() {
//        if (gameListViewModel.inGame) {
//            finish()
//        } else {
            super.onBackPressed()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clickMp.release()
        successMp.release()
    }
}