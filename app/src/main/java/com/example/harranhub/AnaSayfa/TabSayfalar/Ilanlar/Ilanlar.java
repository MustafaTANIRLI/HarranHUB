package com.example.harranhub.AnaSayfa.TabSayfalar.Ilanlar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harranhub.Admin;
import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.Ilan;
import com.example.harranhub.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Ilanlar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ilanlar extends Fragment implements IlanArabirimi {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Ilanlar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Ilanlar.
     */
    // TODO: Rename and change types and number of parameters
    public static Ilanlar newInstance(String param1, String param2) {
        Ilanlar fragment = new Ilanlar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private LinkedList<Ilan> ilanListesi;
    View rootView;
    RecyclerView ilanListeleyici;
    IlanListeAdapter listeAdapter;
    public static final String ILAN_LISTE_KEY = "ILAN_LISTESI_KEY";

    //Database nesneleri
    FirebaseDatabase database;
    DatabaseReference ilanTablosu;
    DatabaseReference adminTablosu;
    ValueEventListener listener;

    //KayitDosyasi
    public SharedPreferences kayitNesnesi;
    public static final String kayitDosyasi = "com.example.harranhub";
    FloatingActionButton yeniIlanTusu;
    private String mevcutKullanici;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ilanlar, container, false);

        ilanListesi = new LinkedList<>();

        kayitNesnesi = rootView.getContext().getSharedPreferences(kayitDosyasi, Context.MODE_PRIVATE);
        mevcutKullanici = kayitNesnesi.getString("EPOSTA", "");

        ilanListeleyici = (RecyclerView) rootView.findViewById(R.id.ilanListeleyiciRecyclerView);
        listeAdapter = new IlanListeAdapter(this, ilanListesi);
        ilanListeleyici.setAdapter(listeAdapter);
        ilanListeleyici.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        yeniIlanTusu = (FloatingActionButton) rootView.findViewById(R.id.ilan_floatingActionButton);
        yeniIlanTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClick();
            }
        });
        yeniIlanTusu.setVisibility(View.GONE);



        dataBaseYukle();
        return rootView;
    }


    //Ilan gonderme tusuna basildiginda ilan gonderme arayuzune yonlendirme yapan fonksiyon
    private void fabClick()
    {
        AnaSayfaArabirimi arabirim = (AnaSayfa)getContext();
        arabirim.direktYonlendir(R.id.action_viewPagerSayfasi_to_ilanGonder, new Bundle());
    }

    //Veri tabani islemlerini baslatan fonksiyon
    private void dataBaseYukle()
    {
        database = FirebaseDatabase.getInstance();
        adminTablosu = database.getReference("yoneticiler");
        ilanTablosu = database.getReference("ilanlar");
        ilanBildirimAc();
        adminTablosuKontrol();
    }

    //Giris yapan kullanicinin yonetici olup olmadigini kontrol edip, ilan gonderme tusunu aktif veya devre disi birakan fonksiyon
    public void adminTablosuKontrol()
    {
        ValueEventListener adminKontrolcu = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren())
                {
                    Admin gelenVeri = ds.getValue(Admin.class);
                    if(gelenVeri.getEposta().equals(mevcutKullanici))
                    {
                        yeniIlanTusu.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                yeniIlanTusu.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ADMIN TABLOSU OKUNAMADI", error.getMessage());
            }
        };

        adminTablosu.addValueEventListener(adminKontrolcu);
    }

    //Veri tabanindaki tum ilanlara erisen ve bunlari ekranda listeleyen fonksiyon
    public void ilanBildirimAc()
    {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Ilan gelenVeri;// = new Ilan();
                    ilanListeleyici.removeAllViews();
                    listeAdapter.clear();

                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        gelenVeri = ds.getValue(Ilan.class);
                        int ilanSayisi = ilanListesi.size();
                        ilanListesi.addFirst(gelenVeri);
                        ilanListeleyici.getAdapter().notifyItemInserted(ilanSayisi);
                    }
                }
                catch (Exception e)
                {
                    Log.e("DATABASE ERROR", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ILAN ALMA HATASI: ", error.getMessage());
            }
        };

        ilanTablosu.addValueEventListener(listener);
    }



    //Ustune tiklanan ilani gosteren sayfaya yonlendirme yapan fonksiyon
    @Override
    public void ilanGoster(Ilan ilan) {

        AnaSayfaArabirimi arabirim = (AnaSayfa)getContext();
        Bundle argumanlar = new Bundle();
        argumanlar.putString(IlanIncele.BASLIK_PARAMETRESI, ilan.getBaslik());
        argumanlar.putString(IlanIncele.ICERIK_PARAMETRESI, ilan.getIcerik());
        arabirim.direktYonlendir(R.id.action_viewPagerSayfasi_to_ilanIncele, argumanlar);

    }
}