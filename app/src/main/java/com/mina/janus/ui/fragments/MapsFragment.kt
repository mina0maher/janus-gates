package com.mina.janus.ui.fragments

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mina.janus.R
import com.mina.janus.models.AddressModel
import com.mina.janus.utilities.Constants.isOnline
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.viewmodles.ApiViewModel
import java.util.*


class MapsFragment : Fragment() {
    //views
    private lateinit var googleMap: GoogleMap
    private lateinit var textYourLocation:TextView
    private lateinit var textWhereto: TextView
    private lateinit var buttonConfirm:Button
    private lateinit var progressBar: ProgressBar
    //vars
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var wheretoMarker: Marker
    private lateinit var yourLocationMarker: Marker
    private lateinit var  yourLocationLatLng:LatLng
    private lateinit var  whereToLatLng:LatLng
    private var polylineFinal: Polyline? = null
    private var arr :IntArray?=null
    private lateinit var dialog: Dialog
    private var locationPermissionGranted = false
    private val apiViewModel: ApiViewModel by viewModels()

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
    }

    private fun getDirectionsFromLatLng(origin:LatLng, destination:LatLng){
            if(isOnline(requireContext())) {
                getDirection(
                    AddressModel(
                        "${origin.latitude}, ${origin.longitude}",
                        "${destination.latitude}, ${destination.longitude}"
                    )
                )
            }else{
                showDialog()
            }
    }
    private fun showDialog(){
        dialog.setContentView(R.layout.no_internet_for_buttons)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val textView = dialog.findViewById<TextView>(R.id.textDismiss)
        val button = dialog.findViewById<Button>(R.id.buttonContact)
        textView.visibility = View.GONE
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        button.setOnClickListener { dialog.dismiss() }
        dialog.setCancelable(true)
        dialog.window!!.setLayout(width, height)
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog = Dialog(requireContext())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }
    private var whereToResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val place= data?.let { Autocomplete.getPlaceFromIntent(it) }
            if(isOnline(requireContext())&&place!=null) {
                textWhereto.text = place.address
                if (::wheretoMarker.isInitialized) {
                    wheretoMarker.remove()
                }
                wheretoMarker =
                    place.latLng?.let { MarkerOptions().position(it).title(place.address) }
                        ?.let { googleMap.addMarker(it) }!!
                place.latLng?.let { moveCamera(it, 15f) }
                whereToLatLng = place.latLng!!
                if (::yourLocationLatLng.isInitialized) {
                    getDirectionsFromLatLng(yourLocationLatLng, whereToLatLng)
                }
            }else{
                showDialog()
            }
        }
    }
    private var yourLocationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
            if (isOnline(requireContext())&&place!=null) {
                textYourLocation.text = place.address
                if (::yourLocationMarker.isInitialized) {
                    yourLocationMarker.remove()
                }
                yourLocationMarker =
                    place.latLng?.let { MarkerOptions().position(it).title(place.address) }
                        ?.let { googleMap.addMarker(it) }!!
                place.latLng?.let { moveCamera(it, 15f) }
                yourLocationLatLng = place.latLng!!
                if (::whereToLatLng.isInitialized) {
                    getDirectionsFromLatLng(yourLocationLatLng, whereToLatLng)
                }
            }
        }else{
            showDialog()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initViews(view)
        getLocationPermission()

        Places.initialize(requireContext(),getString(R.string.google_maps_api_key), Locale("EG"))
        textWhereto.isFocusable = false
        textWhereto.setOnClickListener {
            val fieldList = listOf(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext())
            whereToResultLauncher.launch(intent)
        }



        textYourLocation.isFocusable = false
        textYourLocation.setOnClickListener{
            val fieldList = listOf(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(requireContext())
            yourLocationResultLauncher.launch(intent)

        }

        buttonConfirm.setOnClickListener{
            val bundle = bundleOf("gatesID" to arr )
            findNavController().navigate(R.id.action_mapsFragment_to_reservationFragment,bundle)
        }
    }
    private fun initViews(view: View){
        textWhereto =view.findViewById(R.id.textWhereto)
        textYourLocation = view.findViewById(R.id.textYourLocation)
        buttonConfirm = view.findViewById(R.id.buttonConfirm)
        progressBar = view.findViewById(R.id.progressBar)
    }


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationPermissionGranted= true
            getDeviceLocation()
            try {
                googleMap.isMyLocationEnabled = true
            }catch (e:SecurityException){
                e.printStackTrace()
            }
        }
        else {
            locationPermissionGranted =false
        }
    }
    private fun getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.requireContext(),
            ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this.requireContext(),
                    ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationPermissionGranted = true
        }else{
            permissionLauncher.launch(ACCESS_FINE_LOCATION)
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
                        moveCamera(LatLng(currentLocation.latitude,currentLocation.longitude))
                    }else{
                        showToast("couldn't get device location",requireContext())
                    }
                }
            }
        }catch (securityException:SecurityException){
            securityException.printStackTrace()
        }
    }
    private fun moveCamera(latLng: LatLng, zoom:Float = 15f){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))
    }

    private fun getDirection(addressModel: AddressModel) {
        loading(true)
        apiViewModel.getDirections(addressModel)
        apiViewModel.directionsBodyLiveData.observe(requireActivity()) {
            val status = it.directionsResult?.geocodedWaypoints?.get(0)?.geocoderStatus
            if (status == "OK") {
                val routes = it.directionsResult.routes
                var points: ArrayList<LatLng?>
                var polylineOptions: PolylineOptions? = null
                if (routes != null) {
                    for (i in routes.indices) {
                        points = ArrayList()
                        polylineOptions = PolylineOptions()
                        val legs = routes[i].legs
                        if (legs != null) {
                            for (j in legs.indices) {
                                val steps = legs[j].steps
                                if (steps != null) {
                                    for (k in steps.indices) {
                                        val polyline = steps[k].polyline?.encodedPath
                                        val list = polyline?.let { it1 -> decodePoly(it1) }
                                        if (list != null) {
                                            for (l in list.indices) {
                                                val position = LatLng(list[l].latitude, list[l].longitude)
                                                points.add(position)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        polylineOptions.addAll(points)
                        polylineOptions.width(15f)
                        polylineOptions.color(ContextCompat.getColor(requireContext(), R.color.red))
                        polylineOptions.geodesic(true)
                    }
                }
                polylineFinal?.remove()
             polylineFinal =    polylineOptions?.let {itPolyOptions -> googleMap.addPolyline(itPolyOptions) }
             val bounds = LatLngBounds.builder()
                 .include(yourLocationLatLng)
                 .include(whereToLatLng)
                 .build()

                val width: Int = Resources.getSystem().displayMetrics.widthPixels
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,width,600,30))
                loading(false)
                if(it.gatesAlongRoute != null) {
                    showToast("gate is ${it.gatesAlongRoute[0].id}",requireContext())
                    arr = IntArray(it.gatesAlongRoute.size)
                    for (i in arr!!){
                        arr!![i] = it.gatesAlongRoute[i].id!!
                    }
                }else{
                    arr = null
                }
            }
        }
        apiViewModel.errorMessageMD.observe(requireActivity()){
            loading(false)
            showToast(it,requireContext())
        }
    }

    private fun loading(isLoading:Boolean){
        if(isLoading){
            buttonConfirm.visibility=View.INVISIBLE
            progressBar.visibility=View.VISIBLE
        }else{
            buttonConfirm.visibility=View.VISIBLE
            progressBar.visibility=View.INVISIBLE
        }
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