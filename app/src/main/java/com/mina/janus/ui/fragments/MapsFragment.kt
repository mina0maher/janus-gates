package com.mina.janus.ui.fragments

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mina.janus.R
import com.mina.janus.utilities.Constants.showToast
import org.json.JSONException


class MapsFragment : Fragment() {

    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var textYourLocation:TextView
    private lateinit var textWhereto: TextView
    ///private val shobraBanha = LatLng(30.206953, 31.232221)
    private lateinit var wheretoMarker: Marker
    private lateinit var yourLocationMarker: Marker


    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap=googleMap
        if(locationPermissionGranted){
            getDeviceLocation()
            try {
                googleMap.isMyLocationEnabled = true
            }catch (e:SecurityException){
                    Toast.makeText(this.requireContext(),"error",Toast.LENGTH_SHORT).show()
            }
        }
        direction(LatLng(30.78847 ,31.00192),LatLng(30.005493,31.477898))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initViews(view)
        getLocationPermission()

        Places.initialize(requireContext(),getString(R.string.google_maps_api_key))
        textWhereto.isFocusable = false
        textWhereto.setOnClickListener {
            val fieldList = listOf(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext())

            startActivityForResult(intent,100)
        }
        textYourLocation.isFocusable = false
        textYourLocation.setOnClickListener{
            val fieldList = listOf(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext())

            startActivityForResult(intent,200)
        }
    }
    private fun initViews(view: View){
        textWhereto =view.findViewById(R.id.textWhereto)
        textYourLocation = view.findViewById(R.id.textYourLocation)
    }



    private fun getLocationPermission(){
        val permissions = arrayOf(ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION)
        if(ContextCompat.checkSelfPermission(this.requireContext(),
            ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this.requireContext(),
                    ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationPermissionGranted = true
        }else{
            requestPermissions(permissions, 1234)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== 100&& resultCode== RESULT_OK){
            val place= Autocomplete.getPlaceFromIntent(data)
            textWhereto.text = place.address
            if(::wheretoMarker.isInitialized) {
                wheretoMarker.remove()
            }
            wheretoMarker = place.latLng?.let { MarkerOptions().position(it).title(place.address) }
                ?.let { googleMap.addMarker(it) }!!
            place.latLng?.let { moveCamera(it,20f) }
        }
        if(requestCode== 200&& resultCode== RESULT_OK){
            val place= Autocomplete.getPlaceFromIntent(data)
            textYourLocation.text = place.address
            if(::yourLocationMarker.isInitialized) {
                yourLocationMarker.remove()
            }
            yourLocationMarker = place.latLng?.let { MarkerOptions().position(it).title(place.address) }
                ?.let { googleMap.addMarker(it) }!!
            place.latLng?.let { moveCamera(it,20f) }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted =false
        when(requestCode){
            1234 -> {
                if(grantResults.isNotEmpty()){
                    for(i in grantResults){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionGranted =false
                            return
                        }
                    }

                    locationPermissionGranted= true
                    getDeviceLocation()
                    try {
                        googleMap.isMyLocationEnabled = true
                    }catch (e:SecurityException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun getDeviceLocation(){
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this.requireActivity())
        try {
            if(locationPermissionGranted){
                val location = fusedLocationProviderClient.lastLocation
                location.addOnCompleteListener {
                    if (it.isSuccessful){
                        val currentLocation = it.result
                        moveCamera(LatLng(currentLocation.latitude,currentLocation.longitude),15f)
                    }else{

                    }
                }
            }
        }catch (securityException:SecurityException){
            securityException.printStackTrace()
        }
    }
    private fun moveCamera(latLng: LatLng, zoom:Float){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))
    }

    private fun direction(origin: LatLng, dest: LatLng) {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
            .buildUpon()
            .appendQueryParameter("origin", origin.latitude.toString() + ", " + origin.longitude)
            .appendQueryParameter("destination", dest.latitude.toString() + ", " + dest.longitude)
            .appendQueryParameter("mode", "driving")
            .appendQueryParameter("key", "AIzaSyBJMDGZL3iNE2n-kUYQJYM3J0whIWGupaM")
            .toString()
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getString("status")
                    showToast(status,requireContext())
                    if (status == "OK") {
                        val routes = response.getJSONArray("routes")
                        var points: java.util.ArrayList<LatLng?>
                        var polylineOptions: PolylineOptions? = null
                        for (i in 0 until routes.length()) {
                            points = java.util.ArrayList()
                            polylineOptions = PolylineOptions()
                            val legs = routes.getJSONObject(i).getJSONArray("legs")
                            for (j in 0 until legs.length()) {
                                val steps = legs.getJSONObject(j).getJSONArray("steps")
                                for (k in 0 until steps.length()) {
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline")
                                        .getString("points")
                                    val list = decodePoly(polyline)
                                    for (l in list!!.indices) {
                                        val position = LatLng(list[l].latitude, list[l].longitude)
                                        points.add(position)
                                    }
                                }
                            }
                            polylineOptions.addAll(points)
                            polylineOptions.width(15f)
                            polylineOptions.color(ContextCompat.getColor(requireContext(),R.color.red))
                            polylineOptions.geodesic(true)
                        }
                        polylineOptions?.let { googleMap.addPolyline(it) }

                     //   googleMap.addMarker()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = retryPolicy
        requestQueue.add(jsonObjectRequest)
    }
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

}