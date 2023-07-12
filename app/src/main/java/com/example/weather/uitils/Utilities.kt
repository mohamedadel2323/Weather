package com.example.weather.uitils

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity
import java.util.Locale

fun checkPermissions(context: Context): Boolean {
    return (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

@RequiresApi(Build.VERSION_CODES.M)
fun checkConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities != null && networkCapabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_INTERNET
    )
}

fun WeatherResponse.toWeatherResponseEntity() = WeatherResponseEntity(
    this.alerts,
    this.current, this.daily, this.hourly, this.lat, this.lon, this.timezone, this.timezone_offset
)

fun updateLocale(context: Context, locale: Locale): ContextWrapper {
    val configuration = context.resources.configuration
    configuration.apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    val mContext = context.createConfigurationContext(configuration)
    return ContextWrapper(mContext)
}

