package com.`as`.maps

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private fun createPolyLines(){
        val polyLineOptions : PolylineOptions = PolylineOptions()
            .add(LatLng(1.207002897699515, -77.2907916454535))
            .add(LatLng(1.2069170861144836, -77.286457196088))
            .add(LatLng(1.2104353613174783, -77.2880450635203))
            .add(LatLng(1.2094699814066219, -77.29439653419784))
            .width(10f)
            .color(ContextCompat.getColor(this, R.color.Kotlin))
        val polyLine:Polyline = map.addPolyline(polyLineOptions)
        val pattern: List<PatternItem> = listOf(
            Dot(), Gap(10f), Dash(50f), Gap(10f)
        )

        polyLine.pattern = pattern

        polyLine.isClickable = true
        map.setOnPolylineClickListener {polyLine -> changedColor(polyLine) }
       // polyLine.startCap = RoundCap()
       // polyLine.endCap = RoundCap()
        //polyLine.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))//Solo png, se puede customizar la linea, el final y el inicio

    }

    fun changedColor(polyline: Polyline){
        val color:Int =(0..3).random()
        when (color){
            0 -> polyline.color = ContextCompat.getColor(this, R.color.Red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.Blue)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.Yellow)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.Green)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        map.setOnMyLocationButtonClickListener (this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
        createPolyLines()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun createMarker() {
        val coordinates = LatLng(1.1077844042682556, -77.27713001540613)
        val marker = MarkerOptions().position(coordinates).title("La cocha")
        map.addMarker((marker))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 10f), 1000, null
        )
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //Metodo para activar la localizacion,
    // solicitando los permisos y accediendo a otro metodo en caso de que el usuario no los acepte
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {//}
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        return false //Opciones de boton de ubicacion actual, modificable a conveniencia
    }

    override fun onMyLocationClick(p0: Location) {
        //Evento para hacer click sobre el puntero de ubicacion actual
        Toast.makeText(this, "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
    }




