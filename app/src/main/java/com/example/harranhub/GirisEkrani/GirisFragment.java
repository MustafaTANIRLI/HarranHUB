package com.example.harranhub.GirisEkrani;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GirisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GirisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GirisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GirisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GirisFragment newInstance(String param1, String param2) {
        GirisFragment fragment = new GirisFragment();
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
    private TextView editTextEposta, editTextSifre; //Giriş login inputları
    private ProgressBar progressBar; //Progressbar
    private SharedPreferences.Editor editor; //Shared Preferences Editcisi

    FirebaseAuth auth; //Firebase Authentication
    SharedPreferences sp; //Shared Preferences

//    GirisArabirimi kontrolcu;
    AnaSayfaArabirimi kontrolcu;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_giris, container, false);

//        kontrolcu = ((GirisEkrani)getContext());
        kontrolcu = ((AnaSayfa)getContext());

        auth=FirebaseAuth.getInstance();
        sp= getContext().getSharedPreferences("kullaniciVeri", Context.MODE_PRIVATE); //Kullanıcı verileri için Shared Preference
        editor=sp.edit();

        FirebaseUser user=auth.getCurrentUser();

        if (user!=null) {
            //kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToAnaSayfa());
        }

        editTextEposta=rootView.findViewById(R.id.eposta);
        editTextSifre=rootView.findViewById(R.id.sifre);
        progressBar=rootView.findViewById(R.id.progressBar);

        Button girisTusu = rootView.findViewById(R.id.giris);
        Button kayitTusu = rootView.findViewById(R.id.kayit);

        girisTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girisYap(v);
            }
        });

        kayitTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToKayitFragment());
            }
        });

        TextView sifremiUnuttum = (TextView) rootView.findViewById(R.id.sifremiUnuttum);
        sifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToSifremiUnuttum2());
            }
        });


        return rootView;
    }

    public void girisYap (View view) { //Giriş yapma foknsiyonu

        String eposta=editTextEposta.getText().toString().trim(); //E-posta değişkeni
        String sifre=editTextSifre.getText().toString().trim(); //Şifre değişkeni

        if (eposta.isEmpty()) { //Eğer e-posta girdisi boş ise hata ver
            editTextEposta.setError("Bu alan boş bırakılamaz!");
            editTextEposta.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(eposta).matches()) { //Eğer e-posta, e-posta formatında değilse hata ver
            editTextEposta.setError("Lütfen geçerli bir e-posta girin!");
            editTextEposta.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        if (sifre.isEmpty()) { //Eğer şifre girdisi boş ise hata ver
            editTextSifre.setError("Bu alan boş bırakılamaz!");
            editTextSifre.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        progressBar.setVisibility(View.VISIBLE); //Eğer girdilerde bir sorun yoksa progressBar'ı görünür yap



        auth.signInWithEmailAndPassword(eposta, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Firebase Authentication e-posta ve şifre ile giriş metodu
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //Task tamamlandığında çalışacak metod

                try {
                    if (task.isSuccessful()) { //Eğer task başarıyla tamamlanırsa çalış
                        Toast.makeText(rootView.getContext(), "Başarıyla giriş yapıldı!", Toast.LENGTH_SHORT).show();

                        kontrolcu.veriKontrol(true, eposta);
                    }
                    else { //Eğer task başarısız bir şekilde tamamlanırsa çalış

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) { //Eğer kullanıcı bilgileri geçersiz ise hata ver
                            Toast.makeText(rootView.getContext(), "Giriş yapılamadı! Lütfen bilgilerinizi kontrol edip tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidUserException) { //Eğer kullanıcı bilgileri yanlış ise hata ver
                            Toast.makeText(rootView.getContext(), "Giriş yapılamadı! Lütfen bilgilerinizi kontrol edip tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        }
                        else if (task.getException() instanceof FirebaseTooManyRequestsException) { //Eğer çok fazla yanlış deneme olursa hata ver
                            Toast.makeText(rootView.getContext(), "Üst üste çok fazla deneme yaptınız! Lütfen şifrenizi sıfırlayın veya daha sonra tekrar deneyiz.", Toast.LENGTH_SHORT).show();
                        }
                        kontrolcu.veriKontrol(false, "");
                    }
                }
                catch (Exception e)
                {
                    Log.e("Giris Frag ERROR:", e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}