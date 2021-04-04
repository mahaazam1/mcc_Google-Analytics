package com.example.googleanalytics

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_product_details.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProductDetails : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var firebaseStorage: StorageReference? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var progressDialog: ProgressDialog
    var startHour = 0
    var startMinute = 0
    var startSecond = 0
    var endHour = 0
    var endMinute = 0
    var endSecond = 0
    var name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        db = Firebase.firestore

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        name = intent.getStringExtra("name")!!
        var price = intent.getStringExtra("price")
        var details = intent.getStringExtra("details")

        TrackScreen(name)

        text_name.text = name
        text_price.text = price
        text_details.text = details

        when (name) {
            "cake" -> {
                putImage("cake", "jpeg", "imageFood/cake.jpeg", image_product)
            }
            "juice" -> {
                putImage("juice", "jpg", "imageFood/fruit.jpg", image_product)
            }
            "chicken" -> {
                putImage("chicken", "jpg", "imageFood/chicken.jpg", image_product)
            }
            "HP laptop core i3" -> {
                putImage("hp3", "png", "imageLaptop/hp3.png", image_product)
            }
            "HP laptop core i5" -> {
                putImage("hp5", "png", "imageLaptop/hp5.png", image_product)
            }
            "HP laptop core i7" -> {
                putImage("hp7", "jpg", "imageLaptop/hp7.jpg", image_product)
            }
            "s6" -> {
                putImage("s6", "jpg", "imagePhone/s6.jpg", image_product)
            }
            "s7" -> {
                putImage("s7", "jpg", "imagePhone/s7.jpg", image_product)
            }
            "s10" -> {
                putImage("s10", "jpg", "imagePhone/s10.jpg", image_product)
            }
        }
    }

    private fun putImage(prefix: String?, suffix: String?, path: String, img: ImageView) {
        try {
            firebaseStorage = FirebaseStorage.getInstance().reference.child(path)
            val localFile = File.createTempFile(prefix, suffix)
            firebaseStorage!!.getFile(localFile).addOnSuccessListener {
                var bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                img.setImageBitmap(bitmap)
                Log.e("TAG", "Success")
                progressDialog.dismiss()
            }.addOnFailureListener {
                Log.e("TAG", "Failure")
            }

        } catch (e: Exception) {
            Log.e("TAG", "Failure")
        }
    }

    override fun onResume() {
        super.onResume()
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())

        val values = currentDate.split(":".toRegex()).toTypedArray()
        startHour = values[0].toInt()
        startMinute = values[1].toInt()
        startSecond = values[2].toInt()
    }


    override fun onPause() {
        super.onPause()
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())

        val values = currentDate.split(":".toRegex()).toTypedArray()
        endHour = values[0].toInt()
        endMinute = values[1].toInt()
        endSecond = values[2].toInt()

        var hour =   endHour - startHour
        var minute =   endMinute - startMinute
        var second =   endSecond - startSecond

        Toast.makeText(this,"$hour:$minute:$second", Toast.LENGTH_SHORT).show()

        userData(MainActivity.user_id.toString(),name,"$hour:$minute:$second")

    }

    private fun userData(userId: String, pageName : String, spendTime: String) {
        var userData = hashMapOf("userId" to userId, "pageName" to pageName, "spendTime" to spendTime)
        db!!.collection("userData").add(userData).addOnSuccessListener {
            Log.e("TAG", "add successfully")

        }.addOnFailureListener { exception ->
            Log.e("TAG", "add Failed $exception")
        }
    }

    private fun TrackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductDetails")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}