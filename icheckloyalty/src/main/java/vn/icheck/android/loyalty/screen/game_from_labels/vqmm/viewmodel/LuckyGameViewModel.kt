package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vn.icheck.android.loyalty.model.ListWinnerResp
import vn.icheck.android.loyalty.model.LuckyWheelInfoRep
import vn.icheck.android.loyalty.model.PlayGameResp
import vn.icheck.android.loyalty.model.ReceiveGameResp
import vn.icheck.android.loyalty.repository.VQMMRepository

class LuckyGameViewModel(private val campaignId: Long, private val count: Int?) : ViewModel() {

    private val luckyGameRepository = VQMMRepository()

    val httpException = luckyGameRepository.mException
    val gameInfoLiveData = MutableLiveData<LuckyWheelInfoRep?>()

    val playCount = MutableLiveData<Int?>()

    var currentCount = 1


    fun getListWinnerLive(): LiveData<ListWinnerResp?> {
        return liveData<ListWinnerResp?> {
            emit(luckyGameRepository.getListWinner(campaignId,10 , 0))
        }
    }

    fun updatePlay(count: Int?) {
        currentCount = count?:1
        playCount.postValue(count)
    }

    fun playGame(campaignId:Long): LiveData<PlayGameResp?> {
        return liveData<PlayGameResp?> {
            emit(luckyGameRepository.customerPlayGame(campaignId))
        }
    }

    fun fetchGameInfo() {
        viewModelScope.launch {
            gameInfoLiveData.postValue(luckyGameRepository.getGameInfoRep(campaignId))
        }
    }

    fun receiveGame(code:String) : LiveData<ReceiveGameResp?> {
        return liveData {
            emit(luckyGameRepository.getGame(campaignId, code))
        }
    }
}