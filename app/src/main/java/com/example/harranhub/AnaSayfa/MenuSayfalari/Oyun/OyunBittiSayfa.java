package com.example.harranhub.AnaSayfa.MenuSayfalari.Oyun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.harranhub.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OyunBittiSayfa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OyunBittiSayfa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OyunBittiSayfa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OyunBittiSayfa.
     */
    // TODO: Rename and change types and number of parameters
    public static OyunBittiSayfa newInstance(String param1, String param2) {
        OyunBittiSayfa fragment = new OyunBittiSayfa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView score_text, maxScore_text;  // scoreları gösteren text viewleri tanımladık
    private Button reStart_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            skor = getArguments().getInt("skor", 0);
        }
    }

    View rootView;
    int skor;
    OyunSayfaArabirimi sayfaArabirimi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_oyun_bitti_sayfa, container, false);

        sayfaArabirimi = ((OyunActivity)getContext());

        score_text = rootView.findViewById(R.id.score_textView);
        maxScore_text = rootView.findViewById(R.id.maxScoreTextView);                  // id ler bağlama
        reStart_btn = rootView.findViewById(R.id.reStart_btn);

        score_text.setText(String.valueOf(skor));
        SharedPreferences sp = rootView.getContext().getSharedPreferences("SONUC", Context.MODE_PRIVATE);
        int maxScore = sp.getInt("maxScore", 0);

        if (skor > maxScore) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("maxScore", skor);
            editor.apply();

            maxScore_text.setText(String.valueOf(skor));                                   //skorla en yüksek skoru karşılaştırdı en büyük olanı yazdırdı
        } else {
            maxScore_text.setText(String.valueOf(maxScore));
        }

        reStart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(OyunSonuActivity.this, OyunAnaActivity.class));  //bitiş ekranından ana ekrana geçiş intenti
//                finish();  //sayfa silinsin gerek yok çünkü
                sayfaArabirimi.geriDon();
            }
        });  // Yeniden oynamak için tuşu ayarlıyoruz



        return rootView;
    }
}