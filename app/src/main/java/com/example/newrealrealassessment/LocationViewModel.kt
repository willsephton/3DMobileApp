package com.example.newrealrealassessment

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    // Declare variables to store latitude and longitude
    private val _currentLatitude = MutableLiveData<Double>(0.0)
    val currentLatitude: LiveData<Double>
        get() = _currentLatitude

    private val _currentLongitude = MutableLiveData<Double>(0.0)
    val currentLongitude: LiveData<Double>
        get() = _currentLongitude

    // Function to update the location
    fun updateLocation(location: Location) {
        _currentLatitude.value = location.latitude
        _currentLongitude.value = location.longitude
        Log.e("viewmodel", "updated")

    }
}
