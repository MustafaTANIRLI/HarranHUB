package com.example.harranhub.AnaSayfa.TabSayfalar.Ilanlar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.harranhub.Ilan;
import com.example.harranhub.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IlanIncele#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IlanIncele extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String BASLIK_PARAMETRESI = "ILAN_BASLIK";
    public static final String ICERIK_PARAMETRESI = "ILAN_ICERIK";

    // TODO: Rename and change types of parameters
    private String gelenBaslik;
    private String gelenIcerik;

    public IlanIncele() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IlanIncele.
     */
    // TODO: Rename and change types and number of parameters
    public static IlanIncele newInstance(String param1, String param2) {
        IlanIncele fragment = new IlanIncele();
        Bundle args = new Bundle();
        args.putString(BASLIK_PARAMETRESI, param1);
        args.putString(ICERIK_PARAMETRESI, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gelenBaslik = getArguments().getString(BASLIK_PARAMETRESI);
            gelenIcerik = getArguments().getString(ICERIK_PARAMETRESI);
        }
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ilan_incele, container, false);

        TextView baslikTv = (TextView) rootView.findViewById(R.id.ilanBaslik);
        TextView icerikTv = (TextView) rootView.findViewById(R.id.ilanIcerik);

        baslikTv.setText(gelenBaslik);
        icerikTv.setText(gelenIcerik);

        // Inflate the layout for this fragment
        return rootView;
    }

}