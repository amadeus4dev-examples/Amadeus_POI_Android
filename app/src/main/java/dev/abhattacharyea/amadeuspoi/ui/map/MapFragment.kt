package dev.abhattacharyea.amadeuspoi.ui.map

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.abhattacharyea.amadeuspoi.PlacesManager
import dev.abhattacharyea.amadeuspoi.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var lastKnownLocation: Location? = null
    private lateinit var placesManager: PlacesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        placesManager = PlacesManager(requireContext())

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateCurrentLocationUI()
        getPlaces()
    }

    @SuppressLint("MissingPermission")
    private fun updateCurrentLocationUI() {
        if (!::map.isInitialized) return
        try {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("EXCEPTION %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getPlaces() {
        try {
            placesManager.getDeviceLocation().addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()))
                        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
                            val places = PlacesManager(requireContext())
                                .fetchPlaces(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude,
                                    2
                                )
                            places?.forEach { location ->
                                location.geoCode?.let {
                                    map.addMarker(MarkerOptions()
                                        .position(LatLng(it.latitude, it.longitude))
                                        .title(location.name))
                                }

                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                    map.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private val TAG = MapFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
    }
}