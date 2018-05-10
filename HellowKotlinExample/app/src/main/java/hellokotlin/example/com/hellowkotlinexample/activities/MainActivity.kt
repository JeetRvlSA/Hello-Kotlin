package hellokotlin.example.com.hellowkotlinexample.activities

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import hellokotlin.example.com.hellowkotlinexample.views.FragmentSingleImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.widget.FrameLayout
import hellokotlin.example.com.hellowkotlinexample.R
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var imageview: FrameLayout? = null

    var fragmentSingleImage: FragmentSingleImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var enteredText = edtEnterNAme.text

        var list = ArrayList<String>()

        for (index in 0..8) {
            list.add("index is $index")
        }


        btnSubmit.setOnClickListener {

            /* var bundle = Bundle()
             bundle.putString("key", "value1234")

             var intent = Intent(this, SecondActivity::class.java)
             intent.putExtras(bundle)
             startActivity(intent)*/

            Log.e("ImagePAth", "path is " + fragmentSingleImage?.imagePath)

        }

        imageview = imageView as FrameLayout
/*
        imageview!!.setOnClickListener {
            showPictureDialog()
        }*/
        /*var addImagePicker = ImagePicker(this, this)

        imageView.setOnClickListener {
            addImagePicker.createImageChooser()
        }*/
        fragmentSingleImage = FragmentSingleImage()
        supportFragmentManager.beginTransaction().replace(R.id.imageView, fragmentSingleImage).commit()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, 12)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 13)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == 12) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Log.e("Gallery path ", "path is $path")
                    Toast.makeText(this@MainActivity, "Image Saved! $path", Toast.LENGTH_LONG).show()
//                    imageview!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_LONG).show()
                }

            }

        } else if (requestCode == 13) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
//            imageview!!.setImageBitmap(thumbnail)
            val path = saveImage(thumbnail)
            Log.e("Camera path ", "path is $path")
            Toast.makeText(this@MainActivity, "Image Saved! $path", Toast.LENGTH_LONG).show()
        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }

}

