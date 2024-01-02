package com.example.newrealrealassessment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

// We need to pass in the Application object
class RoomViewModel(app: Application): AndroidViewModel(app)  {
    // Get a reference to the database, using the Application object
    var db = PointsOfInterestDatabase.getDatabase(app)
    var points : LiveData<List<Item>>

    // When we initialise the ViewModel, get the LiveData from the DAO
    // The variable 'students' will always contain the latest LiveData.
    init {
        points = db.PointsOfInterestDAO().getAllPoints()
    }

    // Return the LiveData, so it can be observed, e.g. from the MainActivity
    fun getAllPoints(): LiveData<List<Item>> {
        return points
    }
}