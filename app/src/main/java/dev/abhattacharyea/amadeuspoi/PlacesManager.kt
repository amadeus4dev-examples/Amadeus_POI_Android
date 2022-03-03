package dev.abhattacharyea.amadeuspoi

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.amadeus.android.Amadeus
import com.amadeus.android.ApiResult
import com.amadeus.android.domain.resources.Location
import com.google.android.gms.location.LocationServices


class PlacesManager(context: Context) {

    private val amadeus = Amadeus.Builder(context)
        .setClientId(BuildConfig.AMADEUS_CLIENT_ID)
        .setClientSecret(BuildConfig.AMADEUS_CLIENT_SECRET)
        .setLogLevel(Amadeus.Builder.LogLevel.BODY)
        .build()
    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    suspend fun fetchPlaces(latitude: Double, longitude: Double, radius: Int, categories: List<String>? = null): List<Location>? {
        val pointsOfInterest = amadeus.referenceData.locations.pointsOfInterest.get(
            latitude,
            longitude,
            radius,
            categories = categories
        )

        return when (pointsOfInterest) {
            is ApiResult.Success -> {
                Log.d(TAG, pointsOfInterest.toString())
                pointsOfInterest.data
            }
            is ApiResult.Error -> {
                Log.e(TAG, pointsOfInterest.exception.toString())
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() = fusedLocationProvider.lastLocation

    companion object {
        private val TAG = PlacesManager::class.java.simpleName
    }
}