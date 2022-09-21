package com.example.harranhub.GirisEkrani;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KayitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KayitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KayitFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KayitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KayitFragment newInstance(String param1, String param2) {
        KayitFragment fragment = new KayitFragment();
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

    View root;
    private EditText editTextKayitEposta,editTextKayitSifre;
    private Button btnKayitOl;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference ref=database.getReference("users");
    SharedPreferences sp;
    private SharedPreferences.Editor editor;

//    GirisArabirimi kontrolcu;
    AnaSayfaArabirimi kontrolcu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_kayit, container, false);

//        kontrolcu = ((GirisArabirimi) getContext());
        kontrolcu = ((AnaSayfaArabirimi)getContext());

        auth=FirebaseAuth.getInstance();
        sp=getContext().getSharedPreferences("kullaniciVeri", Context.MODE_PRIVATE);

        editTextKayitEposta=root.findViewById(R.id.kayitEposta);
        editTextKayitSifre=root.findViewById(R.id.kayitSifre);

        progressBar=root.findViewById(R.id.progressBar2);
        btnKayitOl=root.findViewById(R.id.kayit_ol);

        btnKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kayitOl(v);
            }
        });

        return root;
    }

    public void kayitOl (View view) {

        String eposta=editTextKayitEposta.getText().toString().trim();
        String sifre=editTextKayitSifre.getText().toString().trim();

        if (eposta.isEmpty()) {
            editTextKayitEposta.setError("Bu alan boş bırakılmaz!");
            editTextKayitEposta.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(eposta).matches()) {
            editTextKayitEposta.setError("Lütfen geçerli bir e-posta adresi giriniz!");
            editTextKayitEposta.requestFocus();
            return;
        }

        if (sifre.isEmpty()) {
            editTextKayitSifre.setError("Bu alan boş bırakılamaz!");
            editTextKayitSifre.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(eposta, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    editor = sp.edit();
                    editor.putString("eposta", eposta);
                    editor.putInt("skor", 0);

                    Toast.makeText(getContext(), "Başarıyla kayıt olundu!", Toast.LENGTH_SHORT).show();

                    auth.signOut();
                    kontrolcu.geriDon();

                }
                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getContext(), "Bu e-posta adresi kullanılıyor! Lütfen başka bir tane deneyiz.", Toast.LENGTH_LONG).show();
                    }
                    else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(getContext(), "Şifreniz çok zayıf! Lütfen daha güvenli bir şifre seçiniz.", Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}