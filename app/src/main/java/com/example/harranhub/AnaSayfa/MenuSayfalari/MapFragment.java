package com.example.harranhub.AnaSayfa.MenuSayfalari;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harranhub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.tabs.TabLayout;


public class MapFragment extends Fragment {
    GoogleMap harita;

    @Override
    //OncreateView oluşturulur
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //fragmentmapi container ile inflate edilir
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        TabLayout yolculukTab = (TabLayout) root.findViewById(R.id.mapTabLayout);
        yolculukTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(GPS.yolCizim)
                {
                    mesajGoster("Yeni yeni yol tarifi hesaplanıyor");
                }
                if(tab.getPosition() == 0)
                {
                    GPS.yolculukModu = "driving";
                }
                else
                {
                    GPS.yolculukModu = "walking";
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //supportmapfragment ile googlemap idsi ile çağrılır
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
//harita senkronizasyonu başlar ve harita hazır olunduğunda ne yapılması gerektiği söylenir
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                harita = googleMap;//harita hazır olduğunda googlemap eşitle
                //UZUN SÜRE BASILIRSA RASTGELE BİR YER İŞARETLESİN
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        harita.clear();
                        harita.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
                        harita.addMarker(markerOptions);

                    }
                });
                googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
                {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        for(int i = 0; i < GPS.tanimliCizgiler.size(); i++)
                        {
                            GPS.tanimliCizgiler.get(i).setColor(getResources().getColor(R.color.harita_cizim_rengi));
                        }
                        polyline.setColor(getResources().getColor(R.color.harita_secim_rengi));
                    }
                });

                //harita hazır olduğunda harran fakültesini gösterecek kod
                harita.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.1663622952463, 38.99533965747851), 14.5f));

            }

        });


        return root;
    }



    //isaretle sınıfı mainactivityden aldığımız nesnelerin kordinat girilerek göstereceği işaret sınıfıdır
    public void isaretle(@NonNull LatLng llatLng, String name) {
        //işaretlediğimiz yere göre merkeze alır
        harita.moveCamera(CameraUpdateFactory.newLatLng(llatLng));
        //işaretler
        harita.addMarker(new MarkerOptions().position(llatLng));
        MarkerOptions isaretle = new MarkerOptions().title(name);//işaretin hem başlığını hemde pozisyonu belirler
        isaretle.position(llatLng);

        harita.clear();//daha önceden işaret varsa silinir
        harita.animateCamera(//animasyonlar
                CameraUpdateFactory.newLatLngZoom(llatLng, 15)
        );
        harita.addMarker(isaretle);//ve işareti ekler
    }

    public void mesajGoster(String mesaj)
    {
        Toast.makeText(getContext(), mesaj, Toast.LENGTH_SHORT).show();
    }

    private LatLng stringToLatLng(String latlng)
    {
        String lat = "";
        String lng = "";
        lat = latlng.substring(latlng.indexOf('(') + 1, latlng.indexOf(','));
        lng = latlng.substring(latlng.indexOf(',') + 1, latlng.indexOf(')'));

        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }
}