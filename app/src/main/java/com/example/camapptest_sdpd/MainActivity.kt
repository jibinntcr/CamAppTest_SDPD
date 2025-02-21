package com.example.camapptest_sdpd

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.Settings
import android.Manifest

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100 // Unique request code for camera permission

    private val CAMERA_REQUEST_CODE = 101  // Unique code for opening the camera

    private lateinit var btnOpenCamera: Button
    private lateinit var imgCapturedPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize UI elements
        btnOpenCamera = findViewById(R.id.btnOpenCamera)
        imgCapturedPhoto = findViewById(R.id.imgCapturedPhoto)

        // Set button click listener to check permission and open camera
        btnOpenCamera.setOnClickListener {
            checkCameraPermission()
        }
    }
    /**
     * Check if the camera permission is granted.
     * If not, request permission from the user.
     */
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            // Permission already granted, open camera
            openCamera()
        }
    }

    /**
     * Handle the result of the permission request.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera()
            } else {
                // Permission denied
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()

                // Guide user to enable permission manually
                openAppSettings()
            }
        }
    }

    /**
     * Open the camera using an intent.
     */
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    /**
     * Handle the result of the camera capture.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Retrieve the captured image
            val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (photo != null) {
                imgCapturedPhoto.setImageBitmap(photo)
                imgCapturedPhoto.visibility = android.view.View.VISIBLE // Make image visible
            }
        }
    }

    /**
     * Open app settings so the user can enable the camera permission manually.
     */
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}

