package com.project.thirdhodowitcherlab

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.lang.NullPointerException


class MainActivity : AppCompatActivity() {

    private lateinit var imageContainer: ImageView
    private var uri: Uri? = null

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                uri = getTmpFileUri()
                cameraShot.launch(uri) // доступ к камере разрешен, открываем камеру
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showToast("You need to give access to camera usage to take a photo!")
            }
            else -> {
                showToast("Access to camera denied!")
            }
        }
    }

    private val cameraShot = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            uri?.let {
                imageContainer.setImageURI(it)
            }
        } else {
            // something was wrong
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        imageContainer = findViewById(R.id.imageView)

        val cameraButton: Button = findViewById(R.id.choose_image_button)
        cameraButton.setOnClickListener {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }

        val sendButton: Button = findViewById(R.id.send_photo_button)
        sendButton.setOnClickListener {
            try {
                shareImage(uri!!)
            }catch (e: NullPointerException){
                showToast("You haven't taken a photo!")
            }catch (e: Exception){
                Log.d("f", e.toString())
            }
        }
    }

    private fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", getExternalFilesDir("my_images")).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun shareImage(uri: Uri){
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "КПП УІ-191 Костючук")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("Hodovychenko.labs@gmail.com"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }else showToast("There are no apps to send an image!")
    }

}