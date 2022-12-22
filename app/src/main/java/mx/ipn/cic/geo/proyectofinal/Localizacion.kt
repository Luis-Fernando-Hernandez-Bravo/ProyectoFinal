package mx.ipn.cic.geo.proyectofinal
/*
* PROYECTO FINAL
*
* Autor: Luis Fernando Hernández Bravo
*
* */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import mx.ipn.cic.geo.proyectofinal.databinding.ActivityLocalizacionBinding
import mx.ipn.cic.geo.proyectofinal.databinding.ActivityMainBinding

class Localizacion : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityLocalizacionBinding
    private lateinit var database: DatabaseReference
    var latitud=19.432608
    var longitud=-99.133209
    var boton=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle=intent.extras
        //val longi=bundle?.getDouble("longitud")!!
        //val lati=bundle?.getDouble("latitud")!!
        this.longitud=bundle?.getDouble("longitud")!!
        this.latitud=bundle?.getDouble("latitud")!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Ocultar el app bar.
        supportActionBar?.hide()
        database=FirebaseDatabase.getInstance().getReference()


        //****************************************************************
        // FIREBASE URL: https://proyectofinal-b5570-default-rtdb.firebaseio.com/
        //****************************************************************
        binding.botonGuardar.setOnClickListener {



            val Puntos=Coordenadas(this.latitud,this.longitud)

            if (this.boton==0){
                database.child("Coordenadas").push().setValue(Puntos).addOnSuccessListener {
                    Toast.makeText(this,"Dato registrado exitosamente",Toast.LENGTH_LONG).show()
                    this.boton=1
                }.addOnFailureListener{
                    Toast.makeText(this,"Ocurrió un error",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Coordenada agregada previamente",Toast.LENGTH_LONG).show()
            }


        }

        binding.botonBorrar.setOnClickListener {
            database.child("").removeValue().addOnSuccessListener {
                Toast.makeText(this,"Datos eliminados correctamente",Toast.LENGTH_LONG).show()
                this.boton=0
            }.addOnFailureListener {
                Toast.makeText(this,"Ocurrió un error",Toast.LENGTH_LONG).show()
            }
        }

        binding.botonRegresar.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap



        //Toast.makeText(this, "Posición Actual:\n(${this.latitud}, ${this.longitud})", Toast.LENGTH_LONG).show()


        // Add a marker in Mexico City and move the camera
        val ciudadMexico = LatLng(this.latitud, this.longitud)
        this.googleMap.addMarker(MarkerOptions().position(ciudadMexico).title("Lat:${this.latitud}, Long:${this.longitud}"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(ciudadMexico))
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(10F))


        // Modificar propiedades en tiempo de ejecución.
        this.googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //this.googleMap.isTrafficEnabled = true
        this.googleMap.isIndoorEnabled = true

    }
}