package com.astardev.bookboxlocator

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.GoogleMap

class DataFetcher(
    private var screenContext: Context,
    private var googleMap: GoogleMap
) {

    public fun fetchBoxesByZipcode(zipcode: Int){
        //Request to server will have query string "zipcode"

    }

    public fun fetchBoxesByCityState(cityState: String){
        //Request to server can have two query strings "City" and "state" or one "City, state" that is comma delimited
    }

    public fun fetchBoxesByDistance(){
        //Request to server will have one query string "location" that will have the latitude and longitude, comma delimited.
    }

    public fun fetchBoxesOnMap(){
        //Request to server will two query strings "NEBound" which is the location of the northeast corner of the map and "SWBound" which is the
        //SouthWest corner. Both strings are the latitude and longitude comma delimited.



        val alertDialogBuilder = AlertDialog.Builder(screenContext)
        alertDialogBuilder.setCancelable(true)

        val NEBound = googleMap.projection.visibleRegion.latLngBounds.northeast
        val SWBound = googleMap.projection.visibleRegion.latLngBounds.southwest

        alertDialogBuilder.setMessage("$NEBound, $SWBound")
        alertDialogBuilder.show()
    }

//     val url = "https://50e8a4bd-675e-4466-9de3-eddf8d5741ac.mock.pstmn.io/fetchData"
//        val jsonObjectRequest = StringRequest(
//            Request.Method.GET, url,
//            Response.Listener { response ->
//                alertDialogBuilder.setMessage(response.toString())
//                alertDialogBuilder.show()
//            },
//            Response.ErrorListener { error ->
//                alertDialogBuilder.setMessage("That didn't work")
//                alertDialogBuilder.show()
//            }
//        )
//
//        queue.add(jsonObjectRequest)

}