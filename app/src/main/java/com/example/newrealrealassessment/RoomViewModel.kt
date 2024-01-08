package com.example.newrealrealassessment

import android.app.Application
import android.location.Location
import android.telephony.ims.feature.MmTelFeature
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap

class RoomViewModel(app: Application): AndroidViewModel(app)  {
    var db = PointsOfInterestDatabase.getDatabase(app)
    var selectedFeature = MutableLiveData<String>()


    val pointsBySelectedFeature: LiveData<List<Item>> = selectedFeature.switchMap { feature ->
        if (feature.isEmpty()) {
            db.PointsOfInterestDAO().getAllPoints()
        } else {
            db.PointsOfInterestDAO().getPointsByFeature(feature)
        }
    }

    init {
        setFeatureChoice("")
    }


    fun setFeatureChoice(feature: String) {
        selectedFeature.value = feature
        //Log.e("viewmodel", "Feature choice updated: $feature")
       // Log.e("viewmodel", "Feature choice updated: ${selectedFeature.value}")
    }

    fun getFeatureChoice(): LiveData<String>{
        Log.e("viewmodel", "getting featurechoice $selectedFeature")
        return selectedFeature
    }



}