package com.example.harranhub.AnaSayfa;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.harranhub.Ilan;
import com.example.harranhub.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BildirimAlici extends BroadcastReceiver {

    private NotificationManager bildirimYonetici;
    private static final int BILDIRIM_ID = 0;

    private static final String KANAL_ID = "bildirim_kanali";

    FirebaseDatabase veritabani;
    DatabaseReference ilanTablosu;

    SharedPreferences kayitNesnesi;
    String sonilanID;
    private static final String kayitDosyasi = "com.example.harranhub";


    @Override
    public void onReceive(Context context, Intent intent) {
        kayitNesnesi = context.getSharedPreferences(kayitDosyasi, Context.MODE_PRIVATE);
        sonilanID = kayitNesnesi.getString("SON_ILAN", "");
        veritabani = FirebaseDatabase.getInstance();

        ilanTablosu = veritabani.getReference("ilanlar");

        bildirimYonetici = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        threadAc(context);
    }

    //Ilan tablosundaki degisiklikleri 3 saniye boyunca kontrol eden, yeni veri varsa bildirim gonderen, 3 saniye sonunda kontrolu durduran fonksiyon
    public void threadAc(Context ctx)
    {
        ChildEventListener dinleyici = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Ilan gelenVeri = snapshot.getValue(Ilan.class);
                if(!sonilanID.equals(gelenVeri.getId()))
                {

                    if(kayitNesnesi.getBoolean("LOGIN", false))
                    {
                        Log.d("BORADCAST THREAD", "KAYIT OKUNDU" + kayitNesnesi.getBoolean("LOGIN", false));
                        bildir(ctx, gelenVeri.getBaslik());
                    }
                    SharedPreferences.Editor editor = kayitNesnesi.edit();
                    editor.remove("SON_ILAN");
                    editor.putString("SON_ILAN", gelenVeri.getId());
                    sonilanID = gelenVeri.getId();
                    editor.apply();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1; i++)
                {
                    try {
                        ilanTablosu.orderByValue().limitToLast(1).addChildEventListener(dinleyici);
                        boolean girisBilgisi = kayitNesnesi.getBoolean("LOGIN", false);
                        Log.d("BROADCAST THREAD", "BILDIRIM DINLENIYOR..." + kayitNesnesi.getBoolean("LOGIN", false));
                        Thread.sleep(3000);
                        ilanTablosu.removeEventListener(dinleyici);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        yenile(ctx);
    }


    // 3 saniye sonrasina veri tabanini tekrar kontrol etmek uzere alarm kuran fonksiyon
    AlarmManager alarmYonetici;
    PendingIntent bekleyenIntent;
    public void yenile(Context ctx)
    {
        Intent NI = new Intent(ctx, this.getClass());
        bekleyenIntent = PendingIntent.getBroadcast(ctx, BILDIRIM_ID, NI, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmYonetici = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmYonetici.setExact(AlarmManager.RTC_WAKEUP, 3000, bekleyenIntent);
    }

    //Parametre olarak gelen baslik yazisini kullaniciya bildirim olarak gosteren fonksiyon
    private void bildir(Context ctx, String baslik)
    {
        //NotificationManager NM = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent bildirimIntent = new Intent(ctx, AnaSayfa.class);
        PendingIntent bekleyenIntent = PendingIntent.getActivity(ctx, BILDIRIM_ID, bildirimIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, KANAL_ID)
                .setSmallIcon(R.drawable.ic_notif_icon2)
                .setContentTitle("HarranHub YENİ İLAN")
                .setContentText(baslik)
                .setContentIntent(bekleyenIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        bildirimYonetici.notify(BILDIRIM_ID, builder.build());
    }
}