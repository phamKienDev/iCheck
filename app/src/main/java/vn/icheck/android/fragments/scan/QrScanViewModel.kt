package vn.icheck.android.fragments.scan

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.scandit.barcodepicker.BarcodePicker
import com.scandit.barcodepicker.ScanSession
import com.scandit.recognition.Barcode
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.ICheckApplication
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkAPI
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.detail_stamp_v6_1.ICCheckValidStamp
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICQrScan

class QrScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QrScanRepository
    var icCheckValidStamp: ICCheckValidStamp? = null
    var code = ""
    /**
     * Post 1 when is barcode
     * Post 2 when get stamp success
     * Post 3 when error stamp
     */
    var stateCode = MutableLiveData<Int>()

    init {
        val qrScanDao = AppDatabase.getDatabase(application).qrScanDao()
        repository = QrScanRepository(qrScanDao)
    }

    fun didScan(barcodePicker: BarcodePicker, context: Context, scanSession: ScanSession) {
        code = scanSession.allRecognizedCodes
                ?.get(scanSession.allRecognizedCodes.lastIndex)!!.data
        if (code.matches("^[0-9]*$".toRegex()) && code.length == 12) {
            code = "0$code"
        }
        val symbology = scanSession.allRecognizedCodes
                ?.get(scanSession.allRecognizedCodes.lastIndex)?.symbology
        val qrArray = arrayListOf(Barcode.SYMBOLOGY_QR,
                Barcode.SYMBOLOGY_DATA_MATRIX,
                Barcode.SYMBOLOGY_MICRO_PDF417,
                Barcode.SYMBOLOGY_MICRO_QR)
        viewModelScope.launch {
            val listJob = mutableListOf<Deferred<Any?>>()
            listJob.add(async {
                val vibrate = SettingManager.getVibrateSetting

                if (vibrate) {
                    barcodePicker.overlayView.setVibrateEnabled(true)
                } else {
                    barcodePicker.overlayView.setVibrateEnabled(false)
                }
            })
            if (!qrArray.contains(symbology)) {
                stateCode.postValue(1)
            } else {
                listJob.add(async {
//                    val httpLoggingInterceptor = HttpLoggingInterceptor()
                    val retrofit = Retrofit.Builder()
                            .baseUrl(APIConstants.DETAIL_STAMP_HOST)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(OkHttpClient.Builder()
//                                    .addInterceptor(httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY })
                                    .build())
                            .build()
                    val json = JsonObject()
                    json.addProperty("code", code)
                    val icNetworkClient = retrofit.create(ICNetworkAPI::class.java)
                    val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    try {
                        icCheckValidStamp = icNetworkClient.checkValidStamp(requestBody)
                        stateCode.postValue(2)
                    } catch (e: Exception) {
                        stateCode.postValue(3)
                    }
                })
            }
            for (item in listJob) {
                item.await()
            }
        }
    }

    fun insert(qrScan: ICQrScan) = viewModelScope.launch {
        repository.insert(qrScan)
    }

    fun search(id: String): LiveData<ICQrScan?> {
        return repository.search(id)
    }

    fun update(qrScan: ICQrScan) = viewModelScope.launch {
        repository.update(qrScan)
    }
}