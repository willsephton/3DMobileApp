package com.example.newrealrealassessment

import android.app.Application
import android.location.Location
import android.telephony.ims.feature.MmTelFeature
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap

// We need to pass in the Application object
class RoomViewModel(app: Application): AndroidViewModel(app)  {
    // Get a reference to the database, using the Application object
    var db = PointsOfInterestDatabase.getDatabase(app)
    var points : LiveData<List<Item>>
    var selectedFeature = MutableLiveData<String>()


    val pointsBySelectedFeature: LiveData<List<Item>> = selectedFeature.switchMap { feature ->
        if (feature.isEmpty()) {
            db.PointsOfInterestDAO().getAllPoints()
        } else {
            db.PointsOfInterestDAO().getPointsByFeature(feature)
        }
    }

    // When we initialise the ViewModel, get the LiveData from the DAO
    // The variable 'students' will always contain the latest LiveData.
    init {
        points = db.PointsOfInterestDAO().getAllPoints()
        setFeatureChoice("")
        // Observe changes to selectedFeature directly within the ViewModel

    }

    // Return the LiveData, so it can be observed, e.g. from the MainActivity
    fun getAllPoints(): LiveData<List<Item>> {
        return points
    }

    fun setFeatureChoice(feature: String) {
        selectedFeature.value = feature
        Log.e("viewmodel", "Feature choice updated: $feature")
        Log.e("viewmodel", "Feature choice updated: ${selectedFeature.value}")
    }

    fun getFeatureChoice(): LiveData<String>{
        Log.e("viewmodel", "getting featurechoice $selectedFeature")
        return selectedFeature
    }

    fun getPointsByFeature(feature: String): LiveData<List<Item>> {
        Log.e("gettingpoints", "hererererererere")
        points = db.PointsOfInterestDAO().getPointsByFeature(feature)
        return points
    }


}