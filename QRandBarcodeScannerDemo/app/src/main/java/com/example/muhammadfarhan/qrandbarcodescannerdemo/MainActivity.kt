package com.example.muhammadfarhan.qrandbarcodescannerdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*


private const val CAPTURE_IMAGE_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var waitingDialog: AlertDialog
    private lateinit var cameraView: com.wonderkiln.camerakit.CameraView

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        cameraView = findViewById(R.id.cameraView)

        waitingDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait")
            .setCancelable(false)
            .build()


        fabCapture.setOnClickListener {
            cameraView.start()
            cameraView.captureImage()

            /* val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
             startActivityForResult(intent, CAPTURE_IMAGE_CODE)*/
        }

        cameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {

            }

            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onImage(p0: CameraKitImage?) {
                Log.e("MainActivity", "onImage Runs")
                waitingDialog.show()
                val bitmap = p0?.bitmap
                val imgBitmap = Bitmap.createScaledBitmap(bitmap, cameraView.height, cameraView.width, false)
                cameraView.stop()
                img_capture.setImageBitmap(imgBitmap)
                runDetector(imgBitmap)
            }

            override fun onError(p0: CameraKitError?) {
                Log.e("MainActivity", p0?.message)
            }
        })
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val bitmap = data?.extras?.get("data") as Bitmap
                runDetector(bitmap)
            }
        }
    }*/

    private fun runDetector(bitmap: Bitmap) {
        Log.e("MainActivity", "runDetector Runs")
        val mFirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

        val mFirebaseVisionBarcodeDetectorOptions = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_QR_CODE,
                FirebaseVisionBarcode.FORMAT_PDF417,
                FirebaseVisionBarcode.FORMAT_AZTEC,
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS
            ).build()

        val mFirebaseVisionBarcodeDetector =
            FirebaseVision.getInstance().getVisionBarcodeDetector(mFirebaseVisionBarcodeDetectorOptions)

        mFirebaseVisionBarcodeDetector.detectInImage(mFirebaseVisionImage)
            .addOnSuccessListener {
                Log.e("MainActivity", "addOnSuccessListener list size: ${it.size}")
                processResult(it)
            }
            .addOnFailureListener {
                waitingDialog.dismiss()
                Toast.makeText(this@MainActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun processResult(list: List<FirebaseVisionBarcode>) {
        Log.e("MainActivity", "processResult Runs")
        Log.e("MainActivity", "processResult list size: ${list.size}")
        list.forEach {
            Log.e("MainActivity", "forEach")
            val value = it.valueType

            when (value) {
                FirebaseVisionBarcode.TYPE_TEXT -> {
                    Log.e("MainActivity", " FirebaseVisionBarcode.TYPE_TEXT")
                    Log.e("MainActivity", " FirebaseVisionBarcode.TYPE_TEXT   ${it.rawValue}")
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    dialog.setMessage(it.rawValue)

                    dialog.setPositiveButton("OK") { p0, p1 ->
                        p0.dismiss()
                    }
                    dialog.create().show()
                }

                FirebaseVisionBarcode.TYPE_URL -> {
                    Log.e("MainActivity", " FirebaseVisionBarcode.TYPE_URL")
                    Log.e("MainActivity", " FirebaseVisionBarcode.TYPE_URL  ${it.rawValue}")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.rawValue))
                    startActivity(intent)
                }

                FirebaseVisionBarcode.TYPE_CONTACT_INFO -> {
                    Log.e("MainActivity", "  FirebaseVisionBarcode.TYPE_CONTACT_INFO ")
                    Log.e("MainActivity", "  FirebaseVisionBarcode.TYPE_CONTACT_INFO  ${it.rawValue}")
                    val info =
                        "name: ${it.contactInfo?.name?.formattedName} \n Address: ${it.contactInfo?.addresses!![0].addressLines} \n Email: ${it.contactInfo?.emails!![0].address}"

                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    dialog.setMessage(info)

                    dialog.setPositiveButton("OK") { p0, p1 ->
                        p0.dismiss()
                    }
                    dialog.create().show()
                }
                else -> {
                    Log.e("MainActivity", " ELSE")
                    Log.e("MainActivity", "  ELSE ${it.rawValue}")
                }
            }
        }
        waitingDialog.dismiss()
    }

}
