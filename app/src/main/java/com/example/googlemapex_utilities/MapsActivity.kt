package com.example.googlemapex_utilities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
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
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.infowindow.view.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var placesClient: PlacesClient? = null
    var location: Location? = null
    var pGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        Places.initialize(applicationContext, "AIzaSyCDRgfj8Zok-Abvdkk2Qe6DCvTdZ-69aJ4")
        placesClient = Places.createClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (pGranted) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(location!!.latitude, location!!.longitude)
                )
            )
        } else {
            mMap.run {
                addMarker(
                    MarkerOptions().position(LatLng(21.0, 78.0)).title("India")
                        .snippet("Popular Democracy")
                )
                moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.0, 78.0), 2.0F))
            }
        }

        infowindow()
        LocationPermissions()
        getLocation()


    }

     fun  infowindow(){
         mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter{
             override fun getInfoWindow(p0: Marker?): View {
                 TODO("Not yet implemented")
             }

             override fun getInfoContents(p0: Marker?): View {
                var v = layoutInflater.inflate(R.layout.infowindow,findViewById(R.id.map))
                 v.tv1_title.text = p0!!.title
                 v.tv2_snippet.text = p0.snippet
                 return v
             }
         })
     }

    fun LocationPermissions() {

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pGranted = true
            } else {
                 LocationPermissions()
            }
        }
    }

    fun getLocation() {
        if (pGranted) {
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.isMyLocationEnabled = true

            fusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                location = task.result as Location
            }
        } else {
            mMap.isMyLocationEnabled = false
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (pGranted) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                )
            )
        } else {
            LocationPermissions()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_events, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.polyline->{ Toast.makeText(this,"polyline",Toast.LENGTH_LONG).show() }
        }
        return true
    }
}
