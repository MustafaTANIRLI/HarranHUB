package com.example.harranhub.AnaSayfa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telecom.ConnectionRequest;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.harranhub.AnaSayfa.TabSayfalar.ViewPagerSayfasiDirections;
import com.example.harranhub.GirisEkrani.GirisFragmentDirections;
import com.example.harranhub.Ilan;
import com.example.harranhub.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import java.sql.Connection;

public class AnaSayfa extends AppCompatActivity implements AnaSayfaArabirimi {

    //navigasyon isimleri
    NavigationView menuGoruntusu;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle menuSwitch;

    //Database nesneleri
    FirebaseDatabase database;
    DatabaseReference ilanTablosu;
    FirebaseAuth auth;

    //Kaydedilecek verilerle ilgili nesneler
    public SharedPreferences kayitNesnesi;
    public static final String kayitDosyasi = "com.example.harranhub";

    NavHostFragment navHostFragment;
    NavController yonKontrolcu;



    private static final int BILDIRIM_ID = 0;
    private static final String KANAL_ID = "bildirim_kanali";

    NotificationManager bildirimYonetici;
    AlarmManager alarmYonetici;


    PendingIntent bekleyenIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ana_sayfa);

        dataBaseYukle();
        auth = FirebaseAuth.getInstance();

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.anaSayfaFragmentContainer);
        yonKontrolcu = navHostFragment.getNavController();

        navBarAyarla();
        bildirimKanaliOlustur();

        navBarAc(anaSayfayaYonlendir());
    }

    //Ekran sol ust menusunun aktif veya devre disi birakilabilmesini saglayan fonksiyon
    public void navBarAc(boolean onay)
    {
        menuSwitch.setDrawerIndicatorEnabled(onay);
        if(onay)
        {
            drawerLayout.addDrawerListener(menuSwitch);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else
        {
            drawerLayout.removeDrawerListener(menuSwitch);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        menuSwitch.setDrawerIndicatorEnabled(onay);
        menuSwitch.syncState();
    }

    //Veri tabani ilan dinleyicisinin baslatilmasi
    public void bildirimDinleyiciyiAc()
    {
        Intent bildirimIntent = new Intent(this, BildirimAlici.class);
        bekleyenIntent = PendingIntent.getBroadcast(this, BILDIRIM_ID, bildirimIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmYonetici = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmYonetici.setExact(AlarmManager.RTC_WAKEUP, 1000, bekleyenIntent);
    }

    //Eger son kullanici girisinde oturum kapatilmadiysa kullaniciyi ana sayfaya yonlendir
    public boolean anaSayfayaYonlendir()
    {
        if(auth.getCurrentUser() != null)
        {
            SharedPreferences sp = getSharedPreferences(kayitDosyasi, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("LOGIN");
            editor.putBoolean("LOGIN", true);
            editor.apply();
            yonKontrolcu.navigate(GirisFragmentDirections.actionGirisFragmentToViewPagerSayfasi());
            bildirimDinleyiciyiAc();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //Eger ana sayfada geri tusuna basilirsa sistem ekranina geri don
        if(String.valueOf(yonKontrolcu.getCurrentDestination().getLabel()).equals("fragment_view_pager_sayfasi") && auth.getCurrentUser() != null)
        {
            finish();
        }
        else
        {
            super.onBackPressed();
        }

    }

    //Bildirim gonderebilmek uzere bildirim kanali olusturulmasi
    public void bildirimKanaliOlustur()
    {
        bildirimYonetici = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        bildirimYonetici.cancelAll();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel bildirimKanali = new NotificationChannel(KANAL_ID, "HarranHub Ilan Bildirimi", NotificationManager.IMPORTANCE_HIGH);
            bildirimKanali.enableLights(true);
            bildirimKanali.setLightColor(Color.RED);
            bildirimKanali.enableVibration(true);
            bildirimKanali.setDescription("HARRAN HUB ILAN BILDIRIMI");
            bildirimYonetici.createNotificationChannel(bildirimKanali);
        }
    }



    //CardView'ler uzerinden click olaylarinin ele alinip ilgili sayfaya yonlendirme islemi
    public void duyuruCardTikla(View v)
    {
        yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToUniDuyurular());
    }

    public void haritaCardTikla(View v)
    {
        yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToGPS());
    }

    public void yemekhaneCardTikla(View v)
    {
        yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToYemekhane());
    }
    public void oyunCardTikla(View v)
    {
        //Oyun ekranina geciste hata yasanma durumunun ele alinmasi
        try {
            yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToOyunActivity());
        }
        catch (Exception e)
        {
            Log.e("OYUNCARD ERROR", e.getMessage());
        }

    }

    public void hesapMakinesiCardTikla(View v)
    {
        try {
            yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToHesapMakinesi());
        }
        catch (Exception e)
        {
            mesajGoster(e.getMessage());
        }

    }

    public void haruzemCardTikla(View v)
    {
        yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToHaruzem());
    }

    public void OBSCardTikla(View v)
    {
        yonKontrolcu.navigate(ViewPagerSayfasiDirections.actionViewPagerSayfasiToOBS2());
    }
//------------------------------------------------------------------------------------------

    //Sol ust menu ayarlarinin yapilmasi ve menunun kurulumu
    public void navBarAyarla()
    {
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        drawerLayout=findViewById(R.id.drawer_layout);

        menuGoruntusu=(NavigationView)findViewById(R.id.nav_header);

        menuSwitch=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.ac,R.string.kapa);

        drawerLayout.addDrawerListener(menuSwitch);
        menuSwitch.syncState();


        menuGoruntusu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //HER TUŞUN İDSİNİ AKTARIP İŞLEM YAPTIR

                switch(item.getItemId()){
                    case R.id.anaSayfaItem:
                        yonKontrolcu.navigate(R.id.viewPagerSayfasi);
                        break;
                    case R.id.cikis:
                        auth.signOut();

                        navBarAc(false);

                        kayitNesnesi = getSharedPreferences(kayitDosyasi, MODE_PRIVATE);
                        SharedPreferences.Editor edit = kayitNesnesi.edit();
                        edit.remove("LOGIN");
                        edit.putBoolean("LOGIN", false);
                        edit.apply();
                        //yonKontrolcu.popBackStack();
                        while(yonKontrolcu.navigateUp());
                        break;
                }
                drawerLayout.closeDrawers();


                return true;
            }
        });
    }


    //Veri tabaninin yuklenip ilan tablosunun alinmasi
    private void dataBaseYukle()
    {
        database = FirebaseDatabase.getInstance();
        try {
            database.setPersistenceEnabled(true);
        }
        catch (Exception e)
        {
            Log.e("DATABASE ERR:", e.getMessage());
        }

        ilanTablosu = database.getReference("ilanlar");
        ilanTablosu.keepSynced(true);
    }

    //Parametre olarak gelen string yazisini ekranda mesaj olarak gosteren fonksiyon
    public void mesajGoster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    //Bir onceki fragment penceresine donen fonksiyon
    @Override
    public void geriDon() {
        yonKontrolcu.navigateUp();
    }

    //Uygulama penceresini parametre olarak gelen yondeki pencereye yonlendiren fonksiyon
    @Override
    public void yonlendir(NavDirections yon) {
        yonKontrolcu.navigate(yon);
    }

    //Uygulama penceresini parametre olarak gelen Fragment penceresine, Fragment'a deger gondererek yonlendiren fonksiyon
    @Override
    public void direktYonlendir(int actionID, Bundle argumanlar) {

        try {
            yonKontrolcu.navigate(actionID, argumanlar);
        }
        catch (Exception e)
        {
            Log.e("NAVCONTROLLER HATA: ", e.getMessage());
        }
    }

    //Veri tabanina ilan gonderen fonksiyon
    @Override
    public void ilanGonder(Ilan ilan) {
        String id = ilanTablosu.push().getKey();
        ilan.setId(id);
        ilanTablosu.child(id).setValue(ilan);
    }

    //Kullanicinin verileri uyusuyorsa uygulamaya giris yapan, ana sayfaya yonlendiren fonksiyon
    @Override
    public void veriKontrol(Boolean girisKontrol, String kullaniciAdi) {
        if(girisKontrol)
        {
            SharedPreferences dosya = getSharedPreferences(kayitDosyasi, MODE_PRIVATE);
            SharedPreferences.Editor editor = dosya.edit();
            editor.remove("LOGIN");
            editor.putBoolean("LOGIN", true);
            editor.remove("EPOSTA");
            editor.putString("EPOSTA", kullaniciAdi);
            editor.apply();
            bildirimDinleyiciyiAc();
            yonKontrolcu.navigate(GirisFragmentDirections.actionGirisFragmentToViewPagerSayfasi());

            navBarAc(true);

        }
    }

    //Internet baglantisi yoksa bunun uyarisini ekranda kullaniciya gosteren fonksiyon
    @Override
    public void internetKontrol()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(networkInfo != null && networkInfo.isConnectedOrConnecting()))
        {
            mesajGoster("INTERNET BAGLANTISI YOK");
        }
    }
}