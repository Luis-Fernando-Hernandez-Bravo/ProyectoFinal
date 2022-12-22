package mx.ipn.cic.geo.proyectofinal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mx.ipn.cic.geo.proyectofinal.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import mx.ipn.cic.geo.proyectofinal.PermissionUtils.isPermissionGranted
import mx.ipn.cic.geo.proyectofinal.PermissionUtils.requestPermission
import mx.ipn.cic.geo.proyectofinal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private var permissionDenied = false
    var latitud=19.432608
    var longitud=-99.133209

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Ocultar el app bar.
        supportActionBar?.hide()

        // Asignar el código para cada uno de los eventos.
        binding.btnNormal.setOnClickListener { googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL }
        binding.btnHibrido.setOnClickListener { googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID }
        binding.btnSatelite.setOnClickListener { googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE }
        binding.btnTerreno.setOnClickListener { googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN }

        binding.switchTrafico.setOnCheckedChangeListener { _, isChecked ->
            this.googleMap.isTrafficEnabled = isChecked
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add a marker in Mexico City and move the camera
        val ciudadMexico = LatLng(19.432608, -99.133209)
        //this.googleMap.addMarker(MarkerOptions().position(ciudadMexico).title("Ciudad de México"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(ciudadMexico))
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(10F))


        // Modificar propiedades en tiempo de ejecución.
        this.googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        this.googleMap.isTrafficEnabled = true
        this.googleMap.isIndoorEnabled = true

        this.googleMap.setOnMyLocationButtonClickListener(this)
        this.googleMap.setOnMyLocationClickListener(this)
        this.googleMap.setOnMapClickListener(this)
        enableMyLocation()
    }

    // https://developers.google.com/maps/documentation/android-sdk/location
    // https://github.com/googlemaps/android-samples/blob/master/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/MyLocationDemoActivity.kt

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // Comprobar que la referencia al mapa googleMap es válida.
        if (!::googleMap.isInitialized) return
        // [START maps_check_location_permission]

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Ha presionado el botón de Posición Actual", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        // Obtener las coordenadas de latitud y longitud.
        val latitud = location.latitude
        val longitud = location.longitude

        Toast.makeText(this, "Posición Actual:\n($latitud, $longitud)", Toast.LENGTH_LONG).show()
    }

    override fun onMapClick(p0: LatLng) {
        this.latitud=p0.latitude
        this.longitud=p0.longitude
        println("Latitud Real: "+p0.latitude+" Longitud :"+p0.longitude)

        val intent= Intent(this,Localizacion::class.java)
        intent.putExtra("latitud",p0.latitude)
        intent.putExtra("longitud",p0.longitude)

        startActivity(intent)
    }





    // [START maps_check_location_permission_result]
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show()
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
