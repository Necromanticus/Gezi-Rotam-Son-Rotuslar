package com.augusta.gezirotam.view

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.augusta.gezirotam.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.google.android.gms.maps.model.PolylineOptions

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


import com.google.maps.android.PolyUtil


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

import com.augusta.gezirotam.Model.DirectionResponses
import com.augusta.gezirotam.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.utsman.geolib.routes.createPlacesRoute
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception
import kotlin.random.Random


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private var currentMarker : Marker? = null

//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=40.983013,28.810269&radius=1500&type=restaurant&key=AIzaSyDxsPg7-OCYmLXd4nL5usNJLFOFRgXP7ZE"

    lateinit var newUserLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        auth = Firebase.auth

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener(selectingPlace)
        mMap.setOnMarkerClickListener(selectingMarker)

        val hahaa =LatLng(40.983013,28.810269)

        val markerOption = MarkerOptions().position(LatLng(hahaa.latitude,hahaa.longitude))
            .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .snippet(auth.currentUser?.email)

        currentMarker?.remove()
        currentMarker=mMap.addMarker(markerOption)
        currentMarker?.tag=703

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hahaa,15f))

        binding.gastronomi.setOnClickListener {
            mMap.clear()

            val markerOption = MarkerOptions().position(LatLng(hahaa.latitude,hahaa.longitude))
                .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(auth.currentUser?.email)

            currentMarker?.remove()
            currentMarker=mMap.addMarker(markerOption)
            currentMarker?.tag=703

            gastronomiMekan()

        }
        binding.kultur.setOnClickListener {
            mMap.clear()

            val markerOption = MarkerOptions().position(LatLng(hahaa.latitude,hahaa.longitude))
                .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(auth.currentUser?.email)

            currentMarker?.remove()
            currentMarker=mMap.addMarker(markerOption)
            currentMarker?.tag=703

            kulturMekan()

        }

        //"https://maps.googleapis.com/maps/api/directions/json?origin=10.3181466,123.9029382&destination=10.311795,123.915864&key=<AIzaSyDxsPg7-OCYmLXd4nL5usNJLFOFRgXP7ZE>"

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object  : LocationListener {
             override fun onLocationChanged(location: Location) {

                if (location != null){
                    //mMap.clear()
                    newUserLocation = LatLng(40.983013,28.810269)

                    println(newUserLocation)

                    val markerOption = MarkerOptions().position(LatLng(newUserLocation.latitude,newUserLocation.longitude))
                        .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .snippet(auth.currentUser?.email)

                    currentMarker?.remove()
                    currentMarker=mMap.addMarker(markerOption)
                    currentMarker?.tag=703

                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,15f))
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,10f))
                }
            }

        }

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation!=null){
                val lastKnownLatLng = LatLng(40.983013,28.810269)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,15f))
            }
        }
    }

    val selectingMarker = object  : GoogleMap.OnMarkerClickListener{
        override fun onMarkerClick(p0: Marker): Boolean {
            val hahaa =LatLng(40.983013,28.810269)

            val markerlat = p0.position.latitude
            val markerlong = p0.position.longitude
            mMap.clear()

            val rotamarkeri = MarkerOptions().position(LatLng(markerlat,markerlong))
                .title("Mekan Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            mMap.addMarker(rotamarkeri)

            val markerOption = MarkerOptions().position(LatLng(hahaa.latitude,hahaa.longitude))
                .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(auth.currentUser?.email)

            currentMarker?.remove()
            currentMarker=mMap.addMarker(markerOption)
            currentMarker?.tag=703

            val baslangic = hahaa.latitude.toString() + "," + hahaa.longitude.toString()
            val hedef = markerlat.toString() + "," + markerlong.toString()

            val apiServices = RetrofitClient.apiServices(this@MapsActivity)
            apiServices.getDirection(baslangic, hedef, getString(R.string.api_key))
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                        println(response)
                        drawPolyline(response)
                        Log.d("AAAAAAAAAAAAAA", response.message())
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        Log.e("BBBBBBBBBBBB", t.localizedMessage)
                    }
                })
            return true
        }
    }

    val selectingPlace = object : GoogleMap.OnMapClickListener{
        override fun onMapClick(p0: LatLng)  {
                    mMap.clear()
            val hahaa =LatLng(40.983013,28.810269)
            val hohoo = LatLng(-6.1890511,106.8251573)

            val mark = mMap.addMarker(MarkerOptions().position(p0).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Seçilen Konum"))

            val markerOption = MarkerOptions().position(LatLng(hahaa.latitude,hahaa.longitude))
                .title("Güncel Konum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(auth.currentUser?.email)

            currentMarker?.remove()
            currentMarker=mMap.addMarker(markerOption)
            currentMarker?.tag=703

            val baslangic = hahaa.latitude.toString() + "," + hahaa.longitude.toString()
            val hedef = p0.latitude.toString() + "," + p0.longitude.toString()

            val apiServices = RetrofitClient.apiServices(this@MapsActivity)
            apiServices.getDirection(baslangic, hedef, getString(R.string.api_key))
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                        println(response)
                        drawPolyline(response)
                        Log.d("AAAAAAAAAAAAAA", response.message())
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        Log.e("BBBBBBBBBBBB", t.localizedMessage)
                    }
                })

        }

    }

    private fun gastronomiMekan(){
        val canababa= MarkerOptions().position(LatLng(41.000637,28.792988))
            .title("Canbaba Restaurant").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//bar
        mMap.addMarker(canababa)
        val Walking=MarkerOptions().position(LatLng(40.9651538,28.79809879999999))
            .title("Walkin Brasserie").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(Walking)
        val Cookah=MarkerOptions().position(LatLng(40.959323,28.822556))
            .title("Cookah Cafe & Bistro").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//kafe
        mMap.addMarker(Cookah)
        val Lacasa=MarkerOptions().position(LatLng(40.9586712,28.8234899))
            .title("LaCasa Yeşilköy").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        mMap.addMarker(Lacasa)
        val Publig=MarkerOptions().position(LatLng(41.010963,28.81707399999999))
            .title("Publig Bar").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//bar
        mMap.addMarker(Publig)
        val shield=MarkerOptions().position(LatLng(40.9591667,28.8361111))
            .title("The North Shield Pub").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//bar
        mMap.addMarker(shield)
        val pippo=MarkerOptions().position(LatLng(40.9728171,28.8046708))
            .title("Pippo Shisha Café at DHMI").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(pippo)

        val winebar=MarkerOptions().position(LatLng(40.9812562,28.7931342))
            .title("M&G Wine Bar").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//bar
        mMap.addMarker(winebar)

        val zuzu=MarkerOptions().position(LatLng(40.9802427,28.7933144))
            .title("Zuzu Shisha Lounge").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(zuzu)

        val TheChef =MarkerOptions().position(LatLng(40.99122209999999,28.7959438))
            .title("The Chef").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(TheChef)

        val Roof=MarkerOptions().position(LatLng(40.974241,28.796319))
            .title("Roof 9").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(Roof)

        val KahveYeşilköy=MarkerOptions().position(LatLng(40.958262,28.820905))
            .title("Kahve Dünyası - Yeşilköy").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(KahveYeşilköy)

        val CafeRestaurant=MarkerOptions().position(LatLng(40.985575,28.830657))
            .title("57 Cafe & Restaurant").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(CafeRestaurant)

        val CafeCrown=MarkerOptions().position(LatLng(40.9731362,28.8044548))
            .title("Cafe Crown").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(CafeCrown)

        val Lavazza=MarkerOptions().position(LatLng(40.9731315,28.8045988))
            .title("Lavazza Best Coffee Shop").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(Lavazza)

        val Mondo=MarkerOptions().position(LatLng(40.9875639,28.7971652))
            .title("Mondo Lounge").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(Mondo)

        val Shozy=MarkerOptions().position(LatLng(40.9888114,28.7965261))
            .title("Shozy Cafe").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(Shozy)

        val FloryaKahvesi=MarkerOptions().position(LatLng(40.98166499999999,28.7940884))
            .title("Florya Kahvesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(FloryaKahvesi)

        val GAJA=MarkerOptions().position(LatLng(40.9815207,28.7937145))
            .title("GAJA GARDEN HOOKAH LOUNGE").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//cafe
        mMap.addMarker(GAJA)

        val Dhabı=MarkerOptions().position(LatLng(40.9909829,28.7956458))
            .title("Abu Dhabı Cafe & Restaurant").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(Dhabı)

        val Golden=MarkerOptions().position(LatLng(41.0018744,28.81714089999999))
            .title("Golden Izgara").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(Golden)

        val Munzur=MarkerOptions().position(LatLng(40.9929089,28.8356621))
            .title("Munzur Ocakbaşı").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(Munzur)

        val KIRKPINAR=MarkerOptions().position(LatLng(40.9937659,28.8046708))
            .title("KIRKPINAR ET BALIK RESTAURANT").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))//yemek
        mMap.addMarker(KIRKPINAR)

    }
    private fun kulturMekan() {
        val İKÜSAG= MarkerOptions().position(LatLng(40.9913737,28.832098))
            .title("İKÜSAG İstanbul Kültür Üniversitesi Sanat Galerisi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(İKÜSAG)

        val Sefaköy= MarkerOptions().position(LatLng(40.9922769,28.7891048))
            .title("T.C. Kültür ve Turizm Bakanlığı Sefaköy Halk Kütüphanesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Sefaköy)

        val Hava= MarkerOptions().position(LatLng(40.964241,28.8261242))
            .title("İstanbul Hava Kuvvetleri Müzesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Hava)

        val Avukatlık= MarkerOptions().position(LatLng(41.0050005,28.8119103))
            .title("Avukatlık Müzesi ( Museum of Lawyers)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Avukatlık)

        val CERABLUS= MarkerOptions().position(LatLng(41.0036457,28.8325555))
            .title("CERABLUS CEPHESİ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(CERABLUS)

        val Merkez= MarkerOptions().position(LatLng(41.00288969999999,28.8344105))
            .title("Merkez Camii").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Merkez)

        val Belediyesi= MarkerOptions().position(LatLng(40.9559636,28.8213597))
            .title("T.C Bakırköy Belediyesi Sanat Evi ve Kent Müzesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Belediyesi)

        val atatürk= MarkerOptions().position(LatLng(40.9559108,28.8212939))
            .title("Yeşilköy atatürk Müzesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(atatürk)

        val Fatih= MarkerOptions().position(LatLng(40.9908586,28.770798))
            .title("Fatih Çeşmesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Fatih)

        val Mehmet= MarkerOptions().position(LatLng(40.9909853,28.7707245))
            .title("Vezir Mehmet Paşa Çeşmesi").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Mehmet)

        val Akvaryum= MarkerOptions().position(LatLng(40.9651131,28.7989621))
            .title("İstanbul Akvaryum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//turisrt ceken
        mMap.addMarker(Akvaryum)

        val Ormanı= MarkerOptions().position(LatLng(40.9768634,28.7870159))
            .title("Florya Atatürk Ormanı").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Ormanı)

        val lunaparkf= MarkerOptions().position(LatLng(40.96249479999999,28.8053779))
            .title("Florya lunapark").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(lunaparkf)

        val Zafer= MarkerOptions().position(LatLng(41.0009393,28.83688580000001))
            .title("Zafer Meydanı Parkı").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Zafer)

        val Hamamı= MarkerOptions().position(LatLng(40.9954829,28.8384013))
            .title("Saray Hamamı").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))//bar
        mMap.addMarker(Hamamı)

    }
    private fun eglenceMekan(){

    }
        //rotaya random renk
    val rnd = Random.Default //kotlin.random
    val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(15f)
            .color(color)
        mMap.addPolyline(polyline)

    }
    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }
    private object RetrofitClient {
        fun apiServices(context: Context): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.resources.getString(R.string.base_url))
                .build()

            return retrofit.create<ApiServices>(ApiServices::class.java)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}