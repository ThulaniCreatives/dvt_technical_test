package com.dvt.weather


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weather.adaptor.FavoriteAdaptor
import com.dvt.weather.database.FavoriteModel
import com.dvt.weather.database.Forecast
import com.dvt.weather.repository.ForecastViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.dvt.weather.adaptor.WeatherAdapter


class AddCityActivity : AppCompatActivity() {

    lateinit var favoriteAdaptor: FavoriteAdaptor
    val item = ArrayList<FavoriteModel>()
    private lateinit var forecastViewModel: ForecastViewModel
    lateinit var selectPlace:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)
        val apiKey = getString(R.string.google_maps_key)

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey)
        }
        // Create a new Places client instance.
        val placesClient = Places.createClient(this)
        // Initialize the AutocompleteSupportFragment.
        val fabAddCity = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplte) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("AddAutocomplete", "Place: " + place.name + ", " + place.id)
                selectPlace = "${place.name}"
            }

            override fun onError(status: Status) {
                Log.i("AddAutocomplete", "Place: ${status.statusMessage}")
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        favoriteAdaptor = FavoriteAdaptor(mutableListOf())


        //init ForecastViewModel Room database
        forecastViewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        forecastViewModel.getFavorite().observe(this, Observer<List<FavoriteModel>> { favorite ->
            favoriteAdaptor.setListData(favorite)
            favoriteAdaptor.notifyDataSetChanged()

        })
        recyclerView.adapter = favoriteAdaptor

        //

        fabAddCity.setOnClickListener {


if (selectPlace =="")
{
    Toast.makeText(this,"Please select a place",Toast.LENGTH_SHORT).show()
}else
{
    val dataFavarite = FavoriteModel(0,selectPlace, 24.0)
    forecastViewModel.insertFavorite(dataFavarite)
    Toast.makeText(this,"Favorite place successfully added",Toast.LENGTH_SHORT).show()
    favoriteAdaptor.notifyDataSetChanged()
}

 }





    }
}