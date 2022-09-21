package com.example.harranhub.AnaSayfa.MenuSayfalari.Oyun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.harranhub.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OyunAnaSayfa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OyunAnaSayfa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OyunAnaSayfa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OyunAnaSayfa.
     */
    // TODO: Rename and change types and number of parameters
    public static OyunAnaSayfa newInstance(String param1, String param2) {
        OyunAnaSayfa fragment = new OyunAnaSayfa();
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

    View rootView;
    OyunSayfaArabirimi arabirim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_oyun_ana_sayfa, container, false);
        arabirim = ((OyunActivity)getContext());

        Button start_btn = (Button) rootView.findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arabirim.yonlendir(OyunAnaSayfaDirections.actionOyunAnaSayfaToOyunOynaSayfa(), new Bundle());
            }
        });

        Button cikis_btn = (Button) rootView.findViewById(R.id.oyun_cikis_button);
        cikis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arabirim.geriDon();
            }
        });

        return  rootView;
    }
}