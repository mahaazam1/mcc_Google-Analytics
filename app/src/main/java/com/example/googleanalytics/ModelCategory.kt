package com.example.googleanalytics

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelCategory( var id: String?,var name: String?,var products: ArrayList<Map<String, String?>>?) :
    Parcelable {

}