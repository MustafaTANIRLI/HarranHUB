package com.example.harranhub.AnaSayfa.MenuSayfalari.Oyun;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harranhub.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OyunOynaSayfa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OyunOynaSayfa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OyunOynaSayfa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OyunOynaSayfa.
     */
    // TODO: Rename and change types and number of parameters
    public static OyunOynaSayfa newInstance(String param1, String param2) {
        OyunOynaSayfa fragment = new OyunOynaSayfa();
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

    private int elma;
    private TextView call_1, puan_text;                          // oyun ekranındaki text viewleri tanımladık
    private ImageView maincharacter, yellow, black, pink; // imageler tanımladık
    private ConstraintLayout cl;                              // ConstrainLayoutu tanımladık

    //Karakterin haraketleri
    private int xEkseni, yEkseni;
    private Timer timer = new Timer();                         //belli aralıklarla iş yaptırmamız için timer ekliyoruz
    private Handler handler = new Handler();                   //ara yüzdeki nesnelerin hareketini görmek için handler ile güncellemeliyiz


    //Dokunmasını algılama
    private boolean dokunduMu = false;                          //Dokundu mu dokunmadı mı kontrolü
    private boolean basladiMi = false;                          //Oyunun başlaması için bir sorgu

    //Ekrandan çıkmasın diye ayarlama için boyut ayarlama
    private int ekraninGenisligi;
    private int ekranYukseligi;
    private int characterYuksekligi;
    private int characterGenisligi;

    //tuzakların konumlarını belirtme
    private int xBlack;
    private int yBlack;
    private int xYellow;
    private int yYellow;
    private int xPink;
    private int yPink;

    //Farklı ekranlar için Hız optimizasyonu

    private int characterHizi;
    private int blackHizi;
    private int pinkHizi;
    private int yellowHizi;

    //entertainment
    private int skor = 0;

    View rootView;
    OyunSayfaArabirimi sayfaArabirimi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_oyun_oyna_sayfa, container, false);

        sayfaArabirimi = ((OyunActivity)getContext());


        cl = rootView.findViewById(R.id.cl);


        call_1 = rootView.findViewById(R.id.call_1);
        puan_text = rootView.findViewById(R.id.puan_text);
        // id ler ile bağlama
        maincharacter = rootView.findViewById(R.id.maincharacter);
        yellow = rootView.findViewById(R.id.yellow);
        black = rootView.findViewById(R.id.black);
        pink = rootView.findViewById(R.id.pink);

        //tuzakları sahne dışına almalıyız
        black.setX(-80);
        black.setX(-80);
        pink.setX(-80);
        pink.setY(-80);
        yellow.setY(-80);
        yellow.setX(-80);
       /* call_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameScreen.this , GameEndScreen.class));
                finish();

            }
        });*/

        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionevent) {                             // tıklama etkinliği komutları

                if (basladiMi) {                                                                    //oyun hemen başlamasın diye sorgu ekliyoruz

                    if (motionevent.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.e("MotionEvent", "Ekrana dokunuldu");
                        dokunduMu = true;
                    }
                    if (motionevent.getAction() == MotionEvent.ACTION_UP) {
                        Log.e("MotionEvent", "Ekranı bıraktı");
                        dokunduMu = false;
                    }

                } else {
                    basladiMi = true;                                      // tek bir kontrol lazım
                    call_1.setVisibility(View.INVISIBLE);                   // başlamak için dokun yazısını görünmez yapıyoruz
                    xEkseni = (int) maincharacter.getX();                   // karakterin konumu aldık
                    yEkseni = (int) maincharacter.getY();
                    characterGenisligi = maincharacter.getWidth();          //genişliğini alıyoruz  karakterin
                    characterYuksekligi = maincharacter.getHeight();        //Yüksekliğini alıyoruz
                    ekraninGenisligi = cl.getWidth();
                    ekranYukseligi = cl.getHeight();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {                   //handler ile ekran güncelleniyor
                                @Override
                                public void run() {
                                    karakterSınırveHaraket();               //karakter hareketi ve tuzak gelişleri çalışıyor
                                    Tuzakgelisleri();
                                    collisonEkleme();
                                }
                            });
                        }
                    }, 0, 30);        //geçikme ve çalışma aralığını belirledim
                }


                return true;
            }
        });

        return rootView;
    }


    public void karakterSınırveHaraket() {

        characterHizi = Math.round(ekranYukseligi / 60);   // karakter hızı optimizasyonu
        if (dokunduMu) {
            yEkseni -= characterHizi;                                 //Timer döngüsü içerinde pozisyonunu dokunmaya göre ayarlıyoruz
        } else {
            yEkseni += characterHizi;
        }
        if (yEkseni <= 0) {
            yEkseni = 0;
        }
        if (yEkseni >= ekranYukseligi - characterYuksekligi) {  // ekran yk. liğinden mc yk.lğini çıkarıyoruz sınır için
            yEkseni = ekranYukseligi - characterYuksekligi;
        }
        maincharacter.setY(yEkseni);                  //karakter pozisyonunu yeni pozisyona atıyoruz

    }

    public void Tuzakgelisleri() {

        yellowHizi = Math.round(ekraninGenisligi / 60);   // yellow hızı optimizasyonu
        blackHizi = Math.round(ekraninGenisligi / 60);   // black hızı optimizasyonu
        pinkHizi = Math.round(ekraninGenisligi / 45);   // pink hızı optimizasyonu

        xBlack -= blackHizi;
        if (xBlack < 0) {
            xBlack = ekraninGenisligi + 20;                //eğer siyah tuzak ekranın sol köşesine değerse sağ tarafta tekrar gitsin
            yBlack = (int) Math.floor(Math.random() * ekranYukseligi);
        }
        black.setX(xBlack);
        black.setY(yBlack);


        xYellow -= yellowHizi;
        if (xYellow < 0) {
            xYellow = ekraninGenisligi + 20;                //eğer yellow tuzak ekranın sol köşesine değerse sağ tarafta tekrar gitsin
            yYellow = (int) Math.floor(Math.random() * ekranYukseligi);
        }
        yellow.setX(xYellow);
        yellow.setY(yYellow);


        xPink -= pinkHizi;
        if (xPink < 0) {
            xPink = ekraninGenisligi + 20;                //eğer pink tuzak ekranın sol köşesine değerse sağ tarafta tekrar gitsin
            yPink = (int) Math.floor(Math.random() * ekranYukseligi);
        }
        pink.setX(xPink);
        pink.setY(yPink);

    }

    public void collisonEkleme() {
        int yellowCollisionX = xYellow + yellow.getWidth() / 2;    // yellowun X ni yellowun genişliğinin yarısıyla toplayarak
        int yellowCollisionY = yYellow + yellow.getHeight() / 2;    // yellowun merkez noktasını buluyoruz

        //karaterimiz kara olduğu için 4 farklı durumdan çarpışma noktası belirledik
        if (0 <= yellowCollisionX && yellowCollisionX <= characterGenisligi
                && yEkseni <= yellowCollisionY && yellowCollisionY <= characterYuksekligi + yEkseni) {

            skor += 20;
            xYellow = -10;

        }


        int pinkCollisionX = xPink + pink.getWidth() / 2;    // pink in X ni yellowun genişliğinin yarısıyla toplayarak
        int pinkCollisionY = yPink + pink.getHeight() / 2;    // pink in  merkez noktasını buluyoruz

        //karaterimiz kara olduğu için 4 farklı durumdan çarpışma noktası belirledik
        if (0 <= pinkCollisionX && pinkCollisionX <= characterGenisligi
                && yEkseni <= pinkCollisionY && pinkCollisionY <= characterYuksekligi + yEkseni) {

            skor += 100;
            xPink = -10;

        }


        int blackCollisionX = xBlack + black.getWidth() / 2;    // yellowun X ni yellowun genişliğinin yarısıyla toplayarak
        int blackCollisionY = yBlack + black.getHeight() / 2;    // yellowun merkez noktasını buluyoruz

        //karaterimiz kara olduğu için 4 farklı durumdan çarpışma noktası belirledik
        if (0 <= blackCollisionX && blackCollisionX <= characterGenisligi
                && yEkseni <= blackCollisionY && blackCollisionY <= characterYuksekligi + yEkseni) {

            //skor += 100000;
            xBlack = -10;

            timer.cancel();             //black'e çarpınca timer dursun
            timer = null;
            //Intent intent = new Intent(OyunEkranActivity.this, OyunSonuActivity.class);
            //intent.putExtra("skor", skor);   //skoru diğer ekrana geçirme
            //startActivity(intent);
            Bundle arguman = new Bundle();
            arguman.putInt("skor", skor);
            sayfaArabirimi.yonlendir(OyunOynaSayfaDirections.actionOyunOynaSayfaToOyunBittiSayfa(), arguman);

        }
        puan_text.setText(String.valueOf(skor));
    }



}