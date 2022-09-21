package com.example.harranhub.AnaSayfa.MenuSayfalari;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.R;

import org.jsoup.Jsoup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Yemekhane#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Yemekhane extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Yemekhane() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Yemekhane.
     */
    // TODO: Rename and change types and number of parameters
    public static Yemekhane newInstance(String param1, String param2) {
        Yemekhane fragment = new Yemekhane();
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
    WebView htmlSayfasi;
    String yemekTablosu;

    SharedPreferences htmlKayit;
    public static final String HTML_YEMEK_KEY = "SON_YEMEK_HTML";

    ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_webview_layout, container, false);

        htmlSayfasi = rootView.findViewById(R.id.fragmentWebView);
        pb = (ProgressBar) rootView.findViewById(R.id.webViewProgressBar);

        htmlOzellikAyarla();
        bellektenYukle();

        yemekhaneSayfasiOku();


        return rootView;
    }

    RequestQueue istekKuyrugu;
    //HTTP isteginin fragment kapanirsa iptal edilmesi
    @Override
    public void onPause() {
        super.onPause();
        istekKuyrugu.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    //Telefonda kayitli olan yemek listesi tablosunu goruntuleme islemi
    public void bellektenYukle()
    {
        htmlKayit = rootView.getContext().getSharedPreferences(AnaSayfa.kayitDosyasi, Context.MODE_PRIVATE);
        yemekTablosu = htmlKayit.getString(HTML_YEMEK_KEY, "");
        htmlSayfasi.loadDataWithBaseURL(null, yemekTablosu, "text/html", "utf-8", null);
    }

    //Elde edilen HTML tablosunu telefona string olarak kaydetme islemi
    public void htmlKaydet(String url)
    {
        SharedPreferences.Editor kayitDuzenleyici = htmlKayit.edit();
        kayitDuzenleyici.remove(HTML_YEMEK_KEY);
        kayitDuzenleyici.putString(HTML_YEMEK_KEY, url);
        kayitDuzenleyici.apply();
    }

    //Universite yemekhane sayfasina HTTP istegi atip donen istegi offline yukeyiciye gonderen fonksiyon
    public void yemekhaneSayfasiOku()
    {
        istekKuyrugu = Volley.newRequestQueue(rootView.getContext());
        String url = getResources().getString(R.string.url_yemekhane);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //istek cevabi geldiginde web icerigini yukle
                        pb.setVisibility(View.GONE);
                        htmlSayfasi.clearHistory();
                        webIcerigiYukle(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mesajGoster("HTTP ISTEGI BASARISIZ");
                pb.setVisibility(View.GONE);
            }
        });

        istekKuyrugu.add(stringRequest);
        pb.setVisibility(View.VISIBLE);
    }

    //Webview nesnesinin web tarayıcısı gibi davranması için gereken tüm özelliklerin aktif edilmesi
    public void htmlOzellikAyarla()
    {
        htmlSayfasi.getSettings().setJavaScriptEnabled(true);
        htmlSayfasi.getSettings().setBuiltInZoomControls(true);
        htmlSayfasi.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        htmlSayfasi.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // SSL Sertifika hatalarini yoksay
            }
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d("Web sayfasi yukleniyor:", url);
                super.onLoadResource(view, url);
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                pb.setVisibility(View.GONE);
                Log.d("WEBCLIENT", "sayfa yuklendi");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) //Tiklanan url adresine gitmeye yarayan fonksiyon
            {
                Log.d("Tiklanan link:", url);
                pb.setVisibility(View.GONE);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        //Eğer sistem geri tusuna basilirsa webview uzerinde geri donulebildigi surece webview'de geri donmeye yarayan fonksiyon
        htmlSayfasi.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP && htmlSayfasi.canGoBack())
                {
                    htmlSayfasi.goBack();
                    return true;
                }
                return false;
            }
        });

        //PDF gibi dosyalari sistem uzerinde acabilecek veya indirebilecek uygulamalara yonlendirme islemi
        htmlSayfasi.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if(intent.resolveActivity(rootView.getContext().getPackageManager()) != null)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });
    }

    //Gonderilen web icerigini webview uzerinde offline olarak goruntuleyen fonksiyon
    public void webIcerigiYukle(String response)
    {
        org.jsoup.nodes.Document dokuman = Jsoup.parse(response);
        org.jsoup.nodes.Element etiket = dokuman.selectFirst("div#page-main");
        dokuman = Jsoup.parse(response);
        etiket = dokuman.selectFirst("div#page-main");

        yemekTablosu = "<!DOCTYPE html><html> <head> <style>" + getResources().getString(R.string.bootstrap) + "</style></head><body>";
        yemekTablosu = yemekTablosu.concat(etiket.toString());
        yemekTablosu = yemekTablosu.concat("</body></html>");

        htmlKaydet(yemekTablosu);

        htmlSayfasi.loadDataWithBaseURL(null, yemekTablosu, "text/html", "utf-8", null);
    }

    //Ekrana mesaj gosteren fonksiyon
    public void mesajGoster(String message)
    {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}