package hellokotlin.example.com.hellowkotlinexample.imagepicker

import android.content.Intent

interface ImagePickerInterface {

    fun handleCamera(takePictureIntent: Intent)

    fun handleGallery(galleryPickerIntent: Intent)

}
