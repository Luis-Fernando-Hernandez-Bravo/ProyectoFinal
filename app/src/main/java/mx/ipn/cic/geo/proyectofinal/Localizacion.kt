package mx.ipn.cic.geo.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mx.ipn.cic.geo.proyectofinal.databinding.ActivityLocalizacionBinding
import mx.ipn.cic.geo.proyectofinal.databinding.ActivityMainBinding

class Localizacion : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityLocalizacionBinding
    private var permissionDenied = false
    var latitud=19.432608
    var longitud=-99.133209

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Ocultar el app bar.
        supportActionBar?.hide()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val bundle=intent.extras
        val longi=bundle?.getDouble("longitud")!!
        val lati=bundle?.getDouble("latitud")!!

        Toast.makeText(this, "Posición Actual:\n($lati, $longi)", Toast.LENGTH_LONG).show()


        // Add a marker in Mexico City and move the camera
        val ciudadMexico = LatLng(lati, longi)
        this.googleMap.addMarker(MarkerOptions().position(ciudadMexico).title("Lat:$lati, Long:$longi"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(ciudadMexico))
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(10F))


        // Modificar propiedades en tiempo de ejecución.
        this.googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //this.googleMap.isTrafficEnabled = true
        this.googleMap.isIndoorEnabled = true

    }
}