package com.example.googleanalytics

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductActivity : AppCompatActivity(),AdapterProduct.OnClickItem {
    var db: FirebaseFirestore? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var arrayProduct:ArrayList<ModelCategory>
    var category:ModelCategory? = null
    lateinit var progressDialog: ProgressDialog
    var startHour = 0
    var startMinute = 0
    var startSecond = 0
    var endHour = 0
    var endMinute = 0
    var endSecond = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        arrayProduct = ArrayList()
        category = intent.getParcelableExtra("category")!!
        var products = category!!.products
        var adapterProduct = AdapterProduct(this,arrayProduct,this)

        TrackScreen(category!!.name!!)


        db!!.collection("categories").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    for (product in products!!){
                        if(category!!.id == document.id){
                            var id = document.id
                            var name = document.getString("nameC")
                            var a = document.get("products") as ArrayList<Map<String,String?>>?


                            arrayProduct.add(ModelCategory(id,name,a))
                        }
                    }
                }
                recyclerProduct.adapter = adapterProduct
                recyclerProduct.layoutManager = LinearLayoutManager(this)
                recyclerProduct.setHasFixedSize(true)
                progressDialog.dismiss()
            }

    }

    override fun onClick(position: Int) {
        var i = Intent(this,ProductDetails::class.java)
        i.putExtra("name",arrayProduct[position].products?.get(position)?.get("nameP"))
        i.putExtra("price",arrayProduct[position].products?.get(position)?.get("price"))
        i.putExtra("details",arrayProduct[position].products?.get(position)?.get("details"))

        var name = arrayProduct[position].products?.get(position)?.get("nameP")

        TrackScreen(name!!)
        startActivity(i)
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

        userData(MainActivity.user_id.toString(),category!!.name!!,"$hour:$minute:$second")

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
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

}