package com.example.harranhub.AnaSayfa.MenuSayfalari;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.harranhub.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.material.tabs.TabLayout;
import com.google.maps.android.PolyUtil;

public class GPS extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    com.google.android.gms.location.LocationRequest locationRequest;

    public NavigationView navigationView;
    private DrawerLayout drawer;
    private boolean izinVerildiMi;


    int LOCATION_REQUEST_CODE = 101;
    MapFragment fragment;

    LatLng benimKonumum;
    LatLng hedefKonum;
    public static ArrayList<Polyline> tanimliCizgiler;
    public static boolean yolCizim = false;
    public static String yolculukModu;

    private static final String KONUM_KEY = "KONUM";
    private static final String HEDEF_KEY = "HEDEF";
    private static final String MOD_KEY = "YOLCULUK_MODU";
    private static final String CIZIM_KEY = "YOL_CIZIM";
    private static final String ISARET_KEY = "ISARET";

    // konumlari string yap tut sonra geri cek
    @Override
    public void onSaveInstanceState(@NonNull Bundle kayit)
    {
        kayit.putString(KONUM_KEY, (benimKonumum != null) ? benimKonumum.toString() : null);
        kayit.putString(HEDEF_KEY, (hedefKonum != null) ? hedefKonum.toString() : null);
        kayit.putString(MOD_KEY, yolculukModu);
        kayit.putBoolean(CIZIM_KEY, yolCizim);
        super.onSaveInstanceState(kayit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps);
        tanimliCizgiler = new ArrayList<>();


        yolCizim = false;
        yolculukModu = "driving";

        konumIstegiOlustur();


        mesajGoster("SOL UST MENUDEN\nGIDILECEK KONUMU SECIN");
        //TOOLBAR NESNESİNİ TANIMLAR
        androidx.appcompat.widget.Toolbar toolbar=(androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        //TOOLBAR NESNESİNİ ACTİONBAR'A ATAR
        setSupportActionBar(toolbar);

        //MAPFRAGMENT SINIFINDAN BİR NESNE OLUŞTURULUR
        fragment = new MapFragment();
        //FRAMELAYOUTDAKİ NESNEYİ, FRAGMENT YERİNİ ALIR
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        //DRAWER NESNESİNİ TANIMLAR
        drawer=findViewById(R.id.drawer_layout);
        //NAVİGASYON NESNESİNİ TANIMLAR
        navigationView=(NavigationView)findViewById(R.id.nav_header);


        //MENU SOL ÜST TUŞTAKİ AÇILIP KAPANMAYI ATAR
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.ac,R.string.kapa);

        //DRAWER NESNESİNE BİR TOGGLE ADI ALTINDA DRAWER LİSTENER EKLİYOR
        drawer.addDrawerListener(toggle);
        //SENKRONİZE EDİLİYOR
        toggle.syncState();

        //GOOGLE KONUM HİZMETLERİNİ AKTARIYOR
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //NAVİGASYON MENUSUNDEKİ EŞYALARIN SEÇİLİP SEÇİLMEDİĞİNİ DİNLENEN BİR DİNLEYİCİ
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //NAVİGASYON (SLİDER MENU) EŞYALARINDAN SEÇİLME DURUMUNDA ÇALIŞACAK FONKSİYON
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                yolCizim = true;
                //MENULERİN İDLERİNDEN HANGİSİNE BASILIP BASILMADIĞINI SWİTCH CASE İLE KONTOL EDİYOR
                switch (item.getItemId()){
                    case R.id.tipfakultesi:
                        hedefKonum = new LatLng(37.168695675901695, 38.99437459565844);
                        fragment.isaretle(hedefKonum,"Tıp Fakültesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.ilahiyatfakutesi:
                        hedefKonum = new LatLng(37.16866147838418, 38.99733575436432);
                        fragment.isaretle(hedefKonum,"İlahiyat Fakültesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.muhendislikfakultesi:
                        hedefKonum = new LatLng(37.172611191236065, 39.004288039935396);
                        fragment.isaretle(hedefKonum,"Mühendislik Fakültesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.fenedebiyat:
                        hedefKonum = new LatLng(37.169157341108054, 39.00141271194542);
                        fragment.isaretle(hedefKonum,"Fen Edebiyat Fakültesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.egitimfakutesi:
                        hedefKonum = new LatLng(37.168294847646656, 39.002458242869515);
                        fragment.isaretle(hedefKonum,"Eğitim Fakültesi");

                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.ziraatfakutesi:
                        hedefKonum = new LatLng(37.17099620522781, 39.00191993030391);
                        fragment.isaretle(hedefKonum,"Ziraat Fakültesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.gobeklitepeyurdu:
                        hedefKonum = new LatLng(37.1777873545417, 38.9985118510176);
                        fragment.isaretle(hedefKonum,"Gobekli Tepe Erkek yurdu");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.haceranakizyurdu:
                        hedefKonum = new LatLng(37.173820814638034, 38.999541819232725);
                        fragment.isaretle(hedefKonum,"Hacer Ana Kız Yurdu");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.harrankizyurdu:
                        hedefKonum = new LatLng(37.17260274396195, 39.00117104337019);
                        fragment.isaretle(hedefKonum,"Harran Kız Yurdu");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.yuzmehavuzu:
                        hedefKonum = new LatLng(37.166047661381455, 39.0002813116951);
                        fragment.isaretle(hedefKonum,"Yüzme Havuzu");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.harranarastirma:
                        hedefKonum = new LatLng(37.16908074007785, 38.99118480403841);
                        fragment.isaretle(hedefKonum,"Harran arastırma ve uygulama hastanesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.merkezikafeterya:
                        hedefKonum = new LatLng(37.16976098214658, 39.001476585851016);
                        fragment.isaretle(hedefKonum,"Yemekhane");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.cami:
                        hedefKonum = new LatLng(37.1764444373628, 38.99432289214334);
                        fragment.isaretle(hedefKonum,"Cami");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.sosyaltesis:
                        hedefKonum = new LatLng(37.170738685675374, 38.996189787853);
                        fragment.isaretle(hedefKonum,"Sosyal Tesisler");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.kutuphane:
                        hedefKonum = new LatLng(37.1697854101042, 38.99601591150343);
                        fragment.isaretle(hedefKonum,"El Battani Kütüphanesi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.yasammerkezi:
                        hedefKonum = new LatLng(37.168418290277124, 38.99801604695814);
                        fragment.isaretle(hedefKonum,"Öğrenci Yaşam Merkezi");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.a101:
                        hedefKonum = new LatLng(37.16517763905294, 38.99674716369957);
                        fragment.isaretle(hedefKonum,"A101");
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                mesajGoster("Yol tarifi hesaplanıyor");
                return true;
            }
        });
    }

    private void konumIstegiOlustur()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult konumSonucu)
            {
                if(konumSonucu == null)
                {
                    mesajGoster("Konum bilgisi alınamadı");
                    return;
                }
                for(Location konum : konumSonucu.getLocations())
                {
                    benimKonumum = new LatLng(konum.getLatitude(), konum.getLongitude());
                    fragment.harita.setMyLocationEnabled(true);
                    if(yolCizim)
                    {
                        jsonIste(benimKonumum, hedefKonum, yolculukModu);
                    }
                }
            }
        };

        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private LatLng stringToLatLng(String latlng)
    {
        String lat = "";
        String lng = "";
        lat = latlng.substring(latlng.indexOf('(') + 1, latlng.indexOf(','));
        lng = latlng.substring(latlng.indexOf(',') + 1, latlng.indexOf(')'));

        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    @Override
    public void onBackPressed() {//GERİ TUŞUNA BASILDIĞINDA AÇILMIŞ SLİDERI KAPATIR
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //yaşam döngüsünde uygulama başladığında ilk yapılacak şey izin alıp almadığını kontrol ettirmek
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mesajGoster("Konum bilgisi alınıyor");
            izinVerildiMi = true;
            konumYenilemeBaslat();
        } else {
            izinal();//almadıysa izne geç
        }

    }

    private void konumYenilemeBaslat()
    {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(izinVerildiMi)
        {
            konumYenilemeBaslat();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //arka plana geçirtilirse uygulama konum almasın
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    //KONUM ALMAK İÇİN İZİN ALMAYA ÇALIŞIYOR
    public void izinal() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("ErorTag", "Error İzin alınamadı");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int istekKodu, @NonNull String[] izinler, @NonNull int[] sonuclar) {

        super.onRequestPermissionsResult(istekKodu, izinler, sonuclar);

        //Konum istegi kabul edildiyse izin verildi olarak isaretle ve konum yenilemeyi baslat
        if(istekKodu == LOCATION_REQUEST_CODE && sonuclar.length > 0 && sonuclar[0] == PackageManager.PERMISSION_GRANTED)
        {
            izinVerildiMi = true;
            konumYenilemeBaslat();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
    }


    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }


    public String tarifUrl(LatLng baslangic, LatLng hedef, String yolculukTuru)
    {
        String strBaslangic = "origin=" + baslangic.latitude + ","+ baslangic.longitude;
        String strHedef = "destination=" + hedef.latitude + "," + hedef.longitude;

        String mod = "mode=" + yolculukTuru;

        String parametreler = strBaslangic + "&" + strHedef + "&" + mod;
        String cikti = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + cikti + "?" + parametreler + "&key=" + getResources().getString(R.string.mapkey);
    }

    public void jsonIste(LatLng konum, LatLng hedef, String yolculukTipi)
    {
        //mesajGoster("JSON ISTENIYOR");
        String url = tarifUrl(konum, hedef, yolculukTipi);
        RequestQueue istekKuyrugu = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        yolCiz(response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mesajGoster("Yol tarifi isteği başarısız.");

                    }
                });

        istekKuyrugu.add(jsonObjectRequest);
    }

    public void mesajGoster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //Yol tarifini Json verisini parcalayarak cozen ve cizgi haline getiren fonksiyon
    public void yolCiz(String result)
    {
        for(int i = 0; i < tanimliCizgiler.size(); i++)
        {
            tanimliCizgiler.get(i).remove();
        }
        tanimliCizgiler.clear();


        try {
            JSONObject json = new JSONObject(result);
            JSONArray rotaDizisi = json.getJSONArray("routes");
            for(int i = 0; i < rotaDizisi.length(); i++)
            {
                JSONObject rotalar = rotaDizisi.getJSONObject(i);
                JSONObject polylineKodlari = rotalar.getJSONObject("overview_polyline");
                String sifreliKonumlar = polylineKodlari.getString("points");
                List<LatLng> cizgiParcalari = PolyUtil.decode(sifreliKonumlar);//decodePoly(encodedString);
                PolylineOptions rotaCizgisi = new PolylineOptions().addAll(cizgiParcalari)
                        .color(getResources().getColor((i == 0) ? R.color.harita_secim_rengi: R.color.harita_cizim_rengi))
                        .clickable(true);

                tanimliCizgiler.add(fragment.harita.addPolyline(rotaCizgisi));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}