package hellokotlin.example.com.hellowkotlinexample.views

import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast

import com.squareup.picasso.Picasso

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import hellokotlin.example.com.hellowkotlinexample.R

/**
 * Created by frenzin05 on 2/15/2018.
 */

class FragmentSingleImage : Fragment(), View.OnClickListener {
    internal lateinit var activity: Activity
    private var imageCaptureFile: Uri? = null
    var imagePath: String? = null
        internal set
    internal var imgProfile: ImageView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_single_image, container, false)
        imagePath = ""
        bindviews(view)
        val bundle = arguments
        if (bundle != null) {
            val image = bundle.getString("image", "default")
            if (image != "")
                Picasso.with(getActivity()).load(bundle.getString("image", "default")).into(imgProfile)
        }
        return view
    }

    private fun bindviews(view: View) {
        imgProfile = view.findViewById(R.id.imgProfile)
        imgProfile?.setOnClickListener(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = (context as Activity?)!!
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgProfile -> showImageSelectDialog()
        }
    }

    private fun showImageSelectDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_select_image)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.findViewById<View>(R.id.tvCamera).setOnClickListener {
            cameraIntent()
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tvGallery).setOnClickListener {
            galleryIntent()
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.tvCancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun hasPermission(context: Context?, permissions: Array<String>?): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false
            }
        }
        return true
    }

//    private fun galleryIntent() {
//
//
//        val galleryIntent = Intent()
//        galleryIntent.action = Intent.ACTION_GET_CONTENT
//        galleryIntent.type = "image/*"
//        // galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        galleryIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), IMAGE_GALLERY)
//    }

    private fun galleryIntent() {
        /* Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_GALLERY);*/
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, IMAGE_GALLERY)
    }

    private fun cameraIntent() {
        if (hasPermission(activity, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                imageCaptureFile = FileProvider.getUriForFile(activity, "com.localities", outputMediaFile)
            } else {
                imageCaptureFile = Uri.fromFile(outputMediaFile)
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureFile)
            startActivityForResult(intent, IMAGE_CAPTURE)
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0) {
                var isAllPermissionGranted = true
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isAllPermissionGranted = false
                        Log.i(TAG, "I=$i")
                    }
                }

                if (isAllPermissionGranted) {
                    cameraIntent()
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imagePath = mediaFile!!.absolutePath
            setImage()
        }
        if (requestCode == IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data

            Log.e(TAG, "uri from gallery -> " + uri!!.toString())
            imagePath = getPath(activity, uri)
            Log.e(TAG, "imagePath -> " + uri.toString())
            setImage()

        }
    }

    private fun setImage() {
        try {
            val file = File(imagePath!!)
            Picasso.with(activity).load(file).into(imgProfile)
        } catch (e: Exception) {
            imagePath = null
            Toast.makeText(activity, "Can't get image", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {

        private val IMAGE_CAPTURE = 1
        private val IMAGE_GALLERY = 2
        private val TAG = "FragmentMultipleImages"
        private var mediaFile: File? = null
        private val IMAGE_DIRECTORY_NAME = "TempImages"

        /**
         * returning image / video
         */
        // External sdcard location
        // Create the storage directory if it does not exist
        // Create a media file name
        val outputMediaFile: File?
            get() {
                val mediaStorageDir = File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        IMAGE_DIRECTORY_NAME)
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("Merchant", "Oops! Failed create "
                                + IMAGE_DIRECTORY_NAME + " directory")
                        return null
                    }
                }
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(Date())

                mediaFile = File(mediaStorageDir.path + File.separator
                        + "IMG_" + timeStamp + ".jpg")

                return mediaFile
            }


        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         * @author paulburke
         */
        fun getPath(context: Context, uri: Uri): String? {

            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }// File
            // MediaStore (and general)

            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                          selectionArgs: Array<String>?): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }
            return null
        }
    }
}
