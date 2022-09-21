package com.example.harranhub.GirisEkrani;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SifremiUnuttum#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SifremiUnuttum extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SifremiUnuttum() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SifremiUnuttum.
     */
    // TODO: Rename and change types and number of parameters
    public static SifremiUnuttum newInstance(String param1, String param2) {
        SifremiUnuttum fragment = new SifremiUnuttum();
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
    private EditText editTextEmail;
    private Button btnGonder;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    View root;
//    GirisArabirimi kontrolcu;
    AnaSayfaArabirimi kontrolcu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_sifremi_unuttum, container, false);

//        kontrolcu = ((GirisArabirimi) getContext());
        kontrolcu = ((AnaSayfaArabirimi) getContext());
        editTextEmail=root.findViewById(R.id.forgotEmail);
        btnGonder=root.findViewById(R.id.gonder);
        progressBar=root.findViewById(R.id.progressBar3);

        auth=FirebaseAuth.getInstance();

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailGonder(v);
            }
        });

        return root;
    }

    private void mailGonder(View view) {

        String eposta=editTextEmail.getText().toString().trim();

        if (eposta.isEmpty()) {
            editTextEmail.setError("Bu alan zorunludur!");
            editTextEmail.requestFocus();
            return;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(eposta).matches()) {
            editTextEmail.setError("Lütfen geçerli bir e-posta adresi giriniz!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(eposta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Şifre sıfırlama bağlantısı gönderildi!", Toast.LENGTH_LONG).show();
                    kontrolcu.geriDon();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(getContext(), "Kullanıcı bulunumadı! Lütfen e-posta adresinizi kontrol ediniz.", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}