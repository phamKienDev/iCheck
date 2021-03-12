package vn.icheck.android.screen.scan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.scandit.datacapture.barcode.capture.*
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.data.SymbologyDescription
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.internal.sdk.utils.jsonFromObject
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.CameraSettings
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import vn.icheck.android.R
import vn.icheck.android.databinding.IckScanCustomViewBinding
import vn.icheck.android.util.ick.getDeviceHeight
import vn.icheck.android.util.ick.getDeviceWidth
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.showSimpleSuccessToast

class V6ScanditActivity : AppCompatActivity(), BarcodeCaptureListener {
    lateinit var dataCaptureContext: DataCaptureContext
    lateinit var barcodeCapture: BarcodeCapture
    lateinit var cameraSettings: CameraSettings
    var camera: Camera? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataCaptureContext = DataCaptureContext.Companion.forLicenseKey(getString(R.string.scandit_v6_key_dev))
        val settings = BarcodeCaptureSettings().apply {
            enableSymbology(Symbology.CODE128, true)
            enableSymbology(Symbology.CODE39, true)
            enableSymbology(Symbology.QR, true)
            enableSymbology(Symbology.EAN8, true)
            enableSymbology(Symbology.UPCE, true)
            enableSymbology(Symbology.EAN13_UPCA, true)
        }
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings)

        barcodeCapture.addListener(this)
        cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        camera = Camera.getDefaultCamera(cameraSettings)
        dataCaptureContext.setFrameSource(camera)
        if (camera != null) {
            camera?.switchToDesiredState(FrameSourceState.ON);
        }


        val dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)

        dataCaptureView.addView(IckScanCustomViewBinding.inflate(layoutInflater, dataCaptureView, false).root, getDeviceWidth(), getDeviceHeight())
        setContentView(dataCaptureView)

    }

    override fun onBarcodeScanned(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {
        if (session.newlyRecognizedBarcodes.isEmpty()) return
        val barcode = session.newlyRecognizedBarcodes[0]
        barcodeCapture.isEnabled = false
        runOnUiThread {
            val symbology = SymbologyDescription.create(barcode.symbology).readableName
            val result = "Scanned: " + barcode.data + " (" + symbology + ")"
            showSimpleSuccessToast(result)
        }
    }
}