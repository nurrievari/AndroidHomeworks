package ru.itis.homework

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.provider.Settings
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import retrofit2.HttpException

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(), SearchView.OnQueryTextListener {

    private val service: WeatherService by lazy {
        ApiFactory.weatherService
    }
    private var adapter: CityAdapter? = null
    private val broadcast = ConflatedBroadcastChannel<String>()
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var longitude = 0.0
    private var latitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView.setOnQueryTextListener(this)
        getLastLocation()
        getWeatherNear()
        launch {
            var lastTimeout: Job? = null
            broadcast.consumeEach {
                lastTimeout?.cancel()
                lastTimeout = launch {
                    delay(Utils.DELAY_WEATHER_SEARCH)
                    try {
                        val response = withContext(Dispatchers.IO) {
                            service.weatherByName(it)
                        }
                        goToItem(response.id)
                    } catch (e: HttpException) {
                        Log.e("EXC_HANDLER", "$e")
                        Snackbar.make(
                            constraint_layout,
                            getString(R.string.tv_error_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
            lastTimeout?.join()
        }
    }

    private fun getWeatherNear() {
        launch {
            delay(Utils.DELAY_WEATHER_CIRCLE)
            val response = withContext(Dispatchers.IO) {
                service.weatherCitiesInCycle(latitude, longitude, Utils.COUNT_WEATHER_CIRCLE)
            }
            rv_cities.addItemDecoration(
                ListPaddingDecoration(
                    this@MainActivity,
                    0,
                    0
                )
            )
            rv_cities.adapter = CityAdapter(response.list as MutableList<WeatherResponse>) {
                goToItem(it)
            }
        }
    }

    private fun goToItem(id: Int) {
        val intent = Intent(this@MainActivity, WeatherActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    override fun onQueryTextSubmit(newText: String?): Boolean {
        broadcast.offer(newText ?: "")
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        broadcast.offer(newText ?: "")
        return true
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        setCoordinates(location)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                requestNewLocationData()
            }
        } else {
            requestPermissions()
        }
    }

    private fun setCoordinates(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            setCoordinates(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }

    companion object {
        const val PERMISSION_ID = 42
    }
}
