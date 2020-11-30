package com.maps.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var address:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        address =findViewById(R.id.address);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap
        showMyCurrentLocation()

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun clickButtonLocation(view:View){
        startFetchAddressIntentService()
    }

    @SuppressLint("MissingPermission")
    fun showMyCurrentLocation(){
        if(mMap!=null){
            var permisos = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            if (hasPermission(permisos)){
                mMap.isMyLocationEnabled =true
                var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this) {
                        location->
                            if (location!=null){
                                addMarkerAndZoom(location,"me",15);
                            }

                    }
            }else{
                ActivityCompat.requestPermissions( this, permisos, 2 );
            }
        }
    }

    fun addMarkerAndZoom(location:Location, title:String, zoom: Int){
        var myLocation = LatLng( location.latitude, location.longitude);
        mMap.addMarker( MarkerOptions().position( myLocation ).title( title ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( myLocation, zoom.toFloat()));

    }

    fun hasPermission(permisos:Array<String>):Boolean{
        for ( permission  in permisos )
        {
            if ( ContextCompat.checkSelfPermission( this, permission ) == PackageManager.PERMISSION_DENIED )
            {
                return false;
            }
        }
        return true;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for ( grantResult in grantResults )
        {
            if ( grantResult == -1 )
            {
                return;
            }
        }
        when (requestCode){
            44 ->{
                showMyCurrentLocation();
                return
            }
                else-> super.onRequestPermissionsResult( requestCode, permissions, grantResults );
            }
        }

     @SuppressLint("MissingPermission")
     fun startFetchAddressIntentService(){
         fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it!=null){
                var addressResultReceiver = AddressResultReceiver( Handler() );
                addressResultReceiver.setAddressResultListener {
                    var res = it
                    runOnUiThread {
                        address.text = res
                        address.visibility = View.VISIBLE
                    }
                }
                var intent =  Intent( this, FetchAddressIntentService::class.java )
                intent.putExtra( FetchAddressIntentService.RECEIVER, addressResultReceiver )
                intent.putExtra( FetchAddressIntentService.LOCATION_DATA_EXTRA, it);
                startService( intent );
            }
         }
     }

    fun buttonAddMarker(view: View){
        var intent = Intent(this,FormActivity::class.java);
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var cont = data?.getSerializableExtra("data") as Array<Double>
        var myLocation = LatLng(cont[0] , cont[1]);
        mMap.addMarker( MarkerOptions().position( myLocation ).title( "" ) );
    }
}