package com.example.harranhub.AnaSayfa.TabSayfalar.Ilanlar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.Ilan;
import com.example.harranhub.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IlanGonder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IlanGonder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IlanGonder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IlanGonder.
     */
    // TODO: Rename and change types and number of parameters
    public static IlanGonder newInstance(String param1, String param2) {
        IlanGonder fragment = new IlanGonder();
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
    EditText baslikText;
    EditText icerikText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ilan_gonder, container, false);

        Button gonderButton = (Button) rootView.findViewById(R.id.ilanGonderButton);
        baslikText = (EditText) rootView.findViewById(R.id.ilanBaslikGonderText);
        icerikText = (EditText) rootView.findViewById(R.id.ilanIcerikGonderText);

        gonderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnaSayfaArabirimi arabirim = (AnaSayfa)getContext();
                arabirim.ilanGonder(new Ilan(" ", baslikText.getText().toString(), icerikText.getText().toString()));
                arabirim.geriDon();
            }
        });

        return rootView;
    }
}