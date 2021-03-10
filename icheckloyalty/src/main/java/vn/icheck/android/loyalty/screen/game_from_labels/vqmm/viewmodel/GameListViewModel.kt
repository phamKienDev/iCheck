package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import vn.icheck.android.loyalty.model.GameListRep
import vn.icheck.android.loyalty.model.RowsItem
import vn.icheck.android.loyalty.repository.VQMMRepository

class GameListViewModel : ViewModel() {
    private val listGameRepository = VQMMRepository()
    var campaignId = -1L
    var owner = ""
    var banner = ""
    var state = ""
    var campaignName = ""
    var data: RowsItem? = null
    var landing: String? = null
    var titleButton: String? = null
    var schema: String? = null

    var inGame = false

    fun getListGames(): LiveData<GameListRep?> {
        return liveData<GameListRep?> {
            emit(listGameRepository.getListGame())
        }
    }
}