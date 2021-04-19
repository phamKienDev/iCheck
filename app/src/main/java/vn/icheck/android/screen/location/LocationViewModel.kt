package vn.icheck.android.screen.location

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import okhttp3.ResponseBody
import vn.icheck.android.network.model.location.CityResponse

class LocationViewModel @ViewModelInject constructor(
        private val locationRepository: LocationRepository,
        @Assisted  private val savedStateHandle: SavedStateHandle
):ViewModel() {

    var response:CityResponse? = null

    fun getCities():LiveData<CityResponse?>{
        return liveData {
            response = locationRepository.getCities()
            emit(response)
        }
    }

    fun getDistricts(cityId:Int?) :LiveData<CityResponse?>{
        return liveData {
            response = locationRepository.getDistricts(cityId)
            emit(response)
        }
    }

    fun getWards(wardId:Int?) :LiveData<CityResponse?>{
        return liveData {
            response = locationRepository.getWards(wardId)
            emit(response)
        }
    }

    override fun onCleared() {
        super.onCleared()
        response = null
    }
}