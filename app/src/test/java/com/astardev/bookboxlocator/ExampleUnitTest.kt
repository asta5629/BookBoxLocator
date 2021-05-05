package com.astardev.bookboxlocator

import com.astardev.bookboxlocator.models.Box
import org.junit.Test

import org.junit.Assert.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class ExampleUnitTest {

    var client = OkHttpClient()
    val gson = Gson()

    @Test
    fun test_fetchBoxesByZipcode(){
        var urlBuilder = "http://10.0.2.2:8000/api/getBoxesByZip".toHttpUrlOrNull()!!.newBuilder()

        urlBuilder.addQueryParameter("zip_code", "55123")

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

                assertEquals(BoxArray.size, 33)

            }
        })
    }


}