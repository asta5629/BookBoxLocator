package com.astardev.bookboxlocator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.util.Log
import com.android.volley.toolbox.StringRequest

class MainActivity : AppCompatActivity(), OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    lateinit var spinner: Spinner
    lateinit var searchTypeInputLabel: TextView
    lateinit var searchParamEdit: EditText
    lateinit var searchButton: Button
    lateinit var queue : RequestQueue
    lateinit var returnValue: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner = findViewById(R.id.Search_Sprinner)
        searchTypeInputLabel = findViewById(R.id.TypeInputLabel)
        searchParamEdit = findViewById(R.id.searchParamEdit)
        searchButton = findViewById(R.id.submitBtn)

        queue = Volley.newRequestQueue(this)

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


        searchButton.setOnClickListener{

            fetchDataByZipcode(1)

        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()

        if (text == "Nearby" || text == "On Map"){
            searchTypeInputLabel.isEnabled = false
            searchParamEdit.isEnabled = false
        }
        else{
            searchTypeInputLabel.isEnabled = true
            searchParamEdit.isEnabled = true
        }

        searchTypeInputLabel.text = text
    }


    private fun fetchDataByZipcode(zipcode: Int){

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(true)


        val url = "https://50e8a4bd-675e-4466-9de3-eddf8d5741ac.mock.pstmn.io/fetchData"
        val jsonObjectRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                alertDialogBuilder.setMessage(response.toString())
                alertDialogBuilder.show()
            },
            Response.ErrorListener { error ->
                alertDialogBuilder.setMessage("That didn't work")
                alertDialogBuilder.show()
            }
        )

        queue.add(jsonObjectRequest)
    }

}