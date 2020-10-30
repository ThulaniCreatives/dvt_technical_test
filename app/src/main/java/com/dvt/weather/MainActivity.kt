package com.dvt.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weather.adaptor.WeatherAdapter
import com.dvt.weather.database.CurrentWeather
import com.dvt.weather.database.Forecast
import com.dvt.weather.database.ForecastDao
import com.dvt.weather.database.RecyclerViewModel
import com.dvt.weather.repository.ForecastViewModel
import com.dvt.weather.restApi.CurrentWeather.CurrentWeatherResponse
import com.dvt.weather.restApi.ForecastViewModelApi
import com.dvt.weather.restApi.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    private lateinit var forecastViewModel: ForecastViewModel
    lateinit var viewModel: WeatherViewModel

    lateinit var forecastViewModelApi: ForecastViewModelApi

    //------RoomDao
    lateinit var forecastDao: ForecastDao
    lateinit var currentWeatherResponse: CurrentWeatherResponse

    //Recyclerview
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var recyclerViewModel: RecyclerViewModel
    val item = ArrayList<Forecast>()

    //global variable of FusedLocationProviderClient
    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var current_lat: Double = 0.0
    var current_long: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hode action bar
        setContentView(R.layout.activity_main)
        //init items
        val textViewCurrentTemp1: TextView = findViewById(R.id.textViewCurrentTemp1)
        val textViewCurrentTemp2: TextView = findViewById(R.id.textViewCurrentTemp2)
        val textViewMinTemmp: TextView = findViewById(R.id.textViewMinTemp)
        val textViewMaxTemp: TextView = findViewById(R.id.textViewMaxTemp)
        val textViewTempDesc: TextView = findViewById(R.id.textViewTempDesc)
        val ImageViewTemp: ImageView = findViewById(R.id.ImageViewTemp)
        val textViewUpdatedAt: TextView = findViewById(R.id.textViewUpdatedAt)
        val textViewCity: TextView = findViewById(R.id.textViewCity)
        val textViewFeel: TextView = findViewById(R.id.textViewFeelLike)
        // initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //get current location
        getLastLocation()


        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))

        weatherAdapter = WeatherAdapter(mutableListOf())

        //init ForecastViewModel Room database
        forecastViewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        forecastViewModel.getForecast().observe(this, Observer<List<Forecast>> { forecast ->
            weatherAdapter.setListData(forecast)


        })
        recyclerView.adapter = weatherAdapter
        //currentWeather data from Room
        forecastViewModel.getCurrent().observe( this,Observer<List<CurrentWeather>> { currentWeather ->
                // assigning elements
                println(currentWeather)
                var total_list = currentWeather.size
                var i = 0
                var temperatureDescription: String = ""

                while (i < total_list) {
                    textViewCity.setText("${currentWeather[i].place}")
                    textViewCurrentTemp1.setText("${currentWeather[i].temperature_current} ° ")
                    textViewCurrentTemp2.setText("${currentWeather[i].temperature_current} ° ")
                    textViewMaxTemp.setText("${currentWeather[i].temperature_max} ° ")
                    textViewMinTemp.setText("${currentWeather[i].temperature_min} ° ")
                    textViewTempDesc.setText(" ${currentWeather[i].temperature}")
                    textViewFeelLike.setText("Feels like ${currentWeather[i].feelLike}°")
                    temperatureDescription = currentWeather[i].temperature
                    ImageViewTemp.setImageResource(currentWeather[i].weatherIcon)
                    i++

                }
            val backgroundColorId = when (temperatureDescription ) {
                "Clouds" -> R.color.cloudy //Log.d("Current", "Clouds")
                "Sunny" -> R.color.sunny
                "Rain" -> R.color.rainy
                "Clear" -> R.color.sunny
                else -> R.color.design_default_color_primary
            }


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                     window.statusBarColor = ContextCompat.getColor(this, backgroundColorId)
                }
                val mainView = findViewById<View>(R.id.main)
                mainView.setBackgroundColor(ContextCompat.getColor(this, backgroundColorId))
                weatherAdapter.notifyDataSetChanged()


            })
        //
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddCityActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCurrentWeather(city: String) {
        //room view model
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        //filter by current location
        viewModel.setCurrentCity(city)

        viewModel.currentWeather.observe(this, Observer { user ->

            //---Method 1
            val tempDesc: String
            val tempTitle: String
            val arrayOf_tect = arrayOf(user.weather)
            val test = arrayOf_tect.get(0).get(0).description
            tempTitle = arrayOf_tect.get(0).get(0).main
            tempDesc = "${test}"
            //Round to 1 decimal
            val convertedTempCurrent = convertTemperature(user.main.temp)
            val convertedTempMax = convertTemperature(user.main.tempMax)
            val convertedTempMin = convertTemperature(user.main.tempMin)
            val convertedFeelLike = convertTemperature(user.main.feelsLike)
            //convert update at from unix to date time
            val timestamp = user.dt
            val netDate = Date(timestamp.toLong() * 1000)
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val updatedAt = sdf.format(netDate)
            textViewUpdatedAt.setText("Updated ${updatedAt}")

            //change the temp image based on condition and theme color


            val resourceId = when (tempTitle) {
                "Clouds" -> R.drawable.forest_cloudy //Log.d("Current", "Clouds")
                "Sunny" -> R.drawable.forest_sunny
                "Rain" -> R.drawable.forest_rainy
                "Clear" -> R.drawable.forest_sunny
                else -> R.drawable.default_list_image
            }




            addCurrentData(
                city,
                convertedTempMin,
                convertedTempCurrent,
                convertedTempMax,
                tempTitle,
                tempDesc,
                resourceId,
                convertedFeelLike
            )

        })


    }

    private fun convertTemperature(tempValue: Double): Long {
        val temperatureValue: Double = tempValue.toDouble()
        val roundTemp = Math.round(temperatureValue)
        return roundTemp
    }

    private fun addCurrentData(
        place: String,
        temp_min: Long,
        temp_current: Long,
        temp_max: Long,
        temp: String,
        tempDesc: String,
        icon: Int,
        feelLike: Long
    ) {
        val currentWeather = CurrentWeather(
            0,
            place,
            temp_min,
            temp_current,
            temp_max,
            temp,
            tempDesc,
            icon,
            feelLike
        )

        forecastViewModel.insertCurrent(currentWeather)

    }

    private fun addForecast(
        forecastDate: String,
        temperature: Double,
        temperatureDescription: String,
        weatherIcon: Int,
        timestamp: Int
    ) {
        //init ForecastViewModel room database

        val forecastData =
            Forecast(0, forecastDate, temperature, temperatureDescription, weatherIcon, timestamp)
        forecastViewModel.insertForecast(forecastData)
        //weatherAdapter.setListData(forecastData)
        //weatherAdapter.notifyDataSetChanged()


    }


    private fun getForecast(city: String) {
        //testing Forecast

        forecastViewModelApi = ViewModelProvider(this).get(ForecastViewModelApi::class.java)
        forecastViewModelApi.setCurrentCity(city)

        forecastViewModelApi.forecast.observe(this, Observer { user ->
            Log.d(
                "thulani",
                "forecast: ${user}"
            )
            val total_list = user.list.size
            var i = 5 //select day(tommorrow at twelf) at 12 pm

            user.list.forEach { list ->
                Log.d(
                    "thulani",
                    "forecast: ${list.weather.get(0).main}  desc :  ${list.weather.get(0).description}"
                )
            }

            var tempMax1: Double
            var date1: String
            var timeStamp: Int
            var weatherMain: String
            var weatherDescription: String
            var icon: String

            while (i < total_list) {

                tempMax1 = user.list.get(i).main.tempMax
                date1 = user.list.get(i).dtTxt
                timeStamp = user.list.get(i).dt
                weatherMain = user.list[i].weather[0].main
                weatherDescription = user.list[i].weather[0].description
                icon = user.list[i].weather[0].icon

                //------------------

               val resourceId = when (weatherMain) {
                    "Clouds" -> R.drawable.partlysunny//Log.d("Current", "Clouds")
                    "Clear" -> R.drawable.clear
                    "Sunny" -> R.drawable.clear
                    "Rain" -> R.drawable.rain
                    else -> R.drawable.rain
                }
                //------------------
               // item.add(RecyclerViewModel(date1, resourceId, tempMax1))
               // weatherAdapter.setListData(user)
               // weatherAdapter.notifyDataSetChanged()
                //move to next day at 12PM
                i += 8
                addForecast(date1, tempMax1, weatherDescription, resourceId, timeStamp)

            }


        })

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
        forecastViewModelApi.cancelJobs()
    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            val geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address>

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {
                        current_lat = location.latitude
                        current_long = location.longitude

                        addresses = geocoder.getFromLocation(current_lat, current_long, 1)

                        val address: String = addresses[0].locality
                        //Assigning latLong to find current weather
                        getCurrentWeather(address)
                        getForecast(address)

                        //
                    }
                }

        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
    }
}
