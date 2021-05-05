package com.astardev.bookboxlocator

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.lang.Exception


class MainActivity : AppCompatActivity(), OnMapReadyCallback, AdapterView.OnItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocation: Location
    lateinit var spinner: Spinner
    lateinit var searchTypeInputLabel: TextView
    lateinit var searchParamEdit: EditText
    lateinit var searchButton: Button
    lateinit var alertBuilder: AlertDialog.Builder

    private lateinit var datafetcher: DataFetcher

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner = findViewById(R.id.Search_Sprinner)
        searchTypeInputLabel = findViewById(R.id.TypeInputLabel)
        searchParamEdit = findViewById(R.id.searchParamEdit)
        searchButton = findViewById(R.id.submitBtn)

        ArrayAdapter.createFromResource(
            this,
            R.array.search_array,
            android.R.layout.simple_spinner_item
        ).also{
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)

        searchButton.setOnClickListener{

            if(spinner.selectedItem == "On Map"){
                datafetcher.fetchBoxesOnMap()
            }
            else if(spinner.selectedItem == "Zip-Code"){

                var intCheck = false
                var zip = ""
                try{
                    zip = searchParamEdit.text.toString()
                    if(zip.length == 5){
                        intCheck = true
                    }
                    else{
                        throw Exception("Not a zipcode")
                    }
                }
                catch(e: Exception){
                    alertBuilder.setMessage("You need to enter valid 5 digit zipcode.")
                    alertBuilder.show()
                }

                if(intCheck){
                    datafetcher.fetchBoxesByZipcode(zip)
                }
            }
            else if(spinner.selectedItem == "City, State"){

                var cityState = searchParamEdit.text.trim()

                var city = cityState.split(",")[0].trim().toUpperCase()
                var state = cityState.split(",")[1].trim()


                if(city == "" || state== ""){
                    alertBuilder.setMessage("You need to enter a valid city and state")
                    alertBuilder.show()
                }

                datafetcher.fetchBoxesByCityState(city, state)
            }
            else if(spinner.selectedItem == "Nearest"){
                datafetcher.fetchBoxesNearby(currentLocation)
            }

        }

        getCurrentLocation()



        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync{ map ->
            googleMap = map
            val default = LatLng(44.81506093766411, -93.22060361088181)
            map.moveCamera(CameraUpdateFactory.zoomTo(10f))
            map.moveCamera(CameraUpdateFactory.newLatLng(default))
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isTiltGesturesEnabled = false

            enableMyLocation()
            datafetcher = DataFetcher(this, googleMap, alertBuilder)
        }




    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun enableMyLocation() {
        if(EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        ){
            googleMap.isMyLocationEnabled = true
        } else{
            Snackbar.make(
                requireViewById(R.id.V1234),
                getString(R.string.map_snackbar),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.map_rationale),
                        RC_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun getCurrentLocation() {

        if(EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location

                }
            }
        } else{
            Snackbar.make(
                requireViewById(R.id.V1234),
                getString(R.string.locations_snackbar),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.ok){
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.location_rational),
                    RC_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }.show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults, this
        )
    }

    companion object {
        const val RC_LOCATION = 10
    }

    override fun onMapReady(googleMap: GoogleMap) {
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(LatLng(0.0,0.0))
//                .title("Current Location")
//        )


    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        searchParamEdit.text.clear()

        var text: String = parent?.getItemAtPosition(position).toString()

        if (text == "Nearest" || text == "On Map"){
            searchTypeInputLabel.isEnabled = false
            searchParamEdit.isEnabled = false
        }
        else{
            searchTypeInputLabel.isEnabled = true
            searchParamEdit.isEnabled = true
        }

        if(text == "Distance"){
            text = "Distance (Miles)"
        }
        text = "$text:"

        searchTypeInputLabel.text = text
    }


    internal class MyLocationLayerActivity: AppCompatActivity()

}