package vn.icheck.android.icheckscanditv6

import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.core.capture.DataCaptureContext

object DataCaptureManager {
    var dataCaptureContext:DataCaptureContext? = null
    var barcodeCapture:BarcodeCapture? = null
}