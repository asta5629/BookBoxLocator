package com.astardev.bookboxlocator

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import android.app.AlertDialog
import android.location.Location
import com.astardev.bookboxlocator.models.Box
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class DataFetcher(
    private var screenContext: Context,
    private var googleMap: GoogleMap,
    private var alertBuilder: AlertDialog.Builder
) {

    var client = OkHttpClient()
    val gson = Gson()

    public fun fetchBoxesByZipcode(zipcode: String){

        var urlBuilder = "http://10.0.2.2:8000/api/getBoxesByZip".toHttpUrlOrNull()!!.newBuilder()

        urlBuilder.addQueryParameter("zip_code", zipcode)

        var url = urlBuilder.build().toString()

        var request = Request.Builder().url(url).build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseString = response.body!!.string()

                val BoxArray = gson.fromJson<Array<Box>>(responseString, Array<Box>::class.java)

                addMarkers(BoxArray)

            }
        })

    }

    public fun fetchBoxesByCityState(city: String, state: String){

        if(state.length != 2 || state =="") {
            alertBuilder.setMessage("Invalid city/State")
            alertBuilder.show()
        }

        var urlBuilder = "http://10.0.2.2:8000/api/getBoxesByCityAndState".toHttpUrlOrNull()!!.newBuilder()

        urlBuilder.addQueryParameter("City", city)
        urlBuilder.addQueryParameter("State", state)

        var url = urlBuilder.build().toString()

        var request = Request.Builder().url(url).build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseString = response.body!!.string()

                val BoxArray = gson.fromJson<Array<Box>>(responseString, Array<Box>::class.java)

                addMarkers(BoxArray)

            }
        })

    }

    public fun fetchBoxesNearby(location: Location){
        //Request to server will have one query string "location" that will have the latitude and longitude, comma delimited.

        var latitude = location.latitude.toString()
        var longitude = location.longitude.toString()

        var urlBuilder = "http://10.0.2.2:8000/api/getBoxesNearby".toHttpUrlOrNull()!!.newBuilder()

        urlBuilder.addQueryParameter("Latitude", latitude)
        urlBuilder.addQueryParameter("Longitude", longitude)

        var url = urlBuilder.build().toString()

        var request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseString = response.body!!.string()

                val BoxArray = gson.fromJson<Array<Box>>(responseString, Array<Box>::class.java)

                addMarkers(BoxArray)

            }
        })

    }

    public fun fetchBoxesOnMap(){
        var NEBoundLat = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude;
        val NEBoundLong = googleMap.projection.visibleRegion.latLngBounds.northeast.longitude

        val SWBoundLat = googleMap.projection.visibleRegion.latLngBounds.southwest.latitude
        val SWBoundLong = googleMap.projection.visibleRegion.latLngBounds.southwest.longitude

        val urlBuilder = "http://10.0.2.2:8000/api/getBoxesOnMap".toHttpUrlOrNull()!!.newBuilder()

        val NEBounds = "$NEBoundLat,$NEBoundLong"
        val SWBounds = "$SWBoundLat,$SWBoundLong"

        urlBuilder.addQueryParameter("neCorner", NEBounds)
        urlBuilder.addQueryParameter("swCorner", SWBounds)

        val url = urlBuilder.build().toString()

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseString = response.body!!.string()

                val BoxArray = gson.fromJson<Array<Box>>(responseString, Array<Box>::class.java)

                addMarkers(BoxArray)

            }
        })
    }

    fun addMarkers(BoxArray: Array<Box>){
        val handler = Handler(Looper.getMainLooper())

        handler.post{

            if(BoxArray.isEmpty()){
                alertBuilder.setMessage("No Book Boxes were found.\nPlease try another search.")
                alertBuilder.show()
            }
            else{
                googleMap.clear()

                BoxArray.forEach { box ->
                    run {
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(box.Latitude, box.Longitude))
                                .title(box.address)
                        )
                    }
                }
            }

        }
    }


//

}