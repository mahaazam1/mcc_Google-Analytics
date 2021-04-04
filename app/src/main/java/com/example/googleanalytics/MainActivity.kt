package com.example.googleanalytics

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),AdapterCategory.OnClickItem {
    var db: FirebaseFirestore? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var adapterCategory:AdapterCategory
    var arrayCategory= ArrayList<ModelCategory>()
    lateinit var progressDialog: ProgressDialog
    var startHour = 0
    var startMinute = 0
    var startSecond = 0
    var endHour = 0
    var endMinute = 0
    var endSecond = 0
    companion object{
        val user_id = UUID.randomUUID()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        adapterCategory = AdapterCategory(this,arrayCategory,this)
        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)



        TrackScreen("Categories")

        db!!.collection("categories").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    var id = document.id
                    var name = document.getString("nameC")
                    var a = document.get("products") as ArrayList<Map<String,String?>>?

                    arrayCategory.add(ModelCategory(id,name,a))

                }
                Log.e("Array",arrayCategory.toString())
                recyclerCategory.adapter = adapterCategory
                recyclerCategory.layoutManager = LinearLayoutManager(this)
                recyclerCategory.setHasFixedSize(true)

                progressDialog.dismiss()

            }.addOnFailureListener { exception ->
                Log.e("TAG", "errorr >>>>>>> $exception")
            }
    }
    override fun onClick(modelCategory: ModelCategory) {
        TrackScreen(modelCategory.name!!)

        var i = Intent(this,ProductActivity::class.java)
        i.putExtra("category",modelCategory)
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
        Toast.makeText(this,"$hour:$minute:$second",Toast.LENGTH_SHORT).show()

        userData(user_id.toString(),"Categories","$hour:$minute:$second")

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
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

}