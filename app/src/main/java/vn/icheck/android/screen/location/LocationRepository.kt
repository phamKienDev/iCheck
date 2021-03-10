package vn.icheck.android.screen.location

import vn.icheck.android.model.location.CityResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.util.ick.logDebug
import javax.inject.Inject

class LocationRepository @Inject constructor(
        private val ickApi: ICKApi
) {


    suspend fun getCities():CityResponse?{
        return try {
            ickApi.getCities()
        } catch (e: Exception) {
            logDebug(e.localizedMessage)
            null
        }
    }

    suspend fun getDistricts(cityId:Int?):CityResponse?{
        return try {
            ickApi.getDistricts(cityId)
        } catch (e: Exception) {
            logDebug(e.localizedMessage)
            null
        }
    }

    suspend fun getWards(districtId:Int?):CityResponse?{
        return try {
            ickApi.getWards(districtId)
        } catch (e: Exception) {
            logDebug(e.localizedMessage)
            null
        }
    }
}