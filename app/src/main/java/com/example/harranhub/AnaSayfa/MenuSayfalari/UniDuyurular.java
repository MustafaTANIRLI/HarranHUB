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
 * Use the {@link UniDuyurular#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UniDuyurular extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UniDuyurular() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UniDuyurular.
     */
    // TODO: Rename and change types and number of parameters
    public static UniDuyurular newInstance(String param1, String param2) {
        UniDuyurular fragment = new UniDuyurular();
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
    String duyuruTablosu;

    SharedPreferences htmlKayit;
    public static final String HTML_DUYURU_KEY = "SON_DUYURU_HTML";

    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_webview_layout, container, false);


        pb = (ProgressBar) rootView.findViewById(R.id.webViewProgressBar);

        htmlSayfasi = rootView.findViewById(R.id.fragmentWebView);

        htmlOzellikAyarla();
        bellektenYukle();

        duyuruSayfasiOku();


        return rootView;
    }

    public void bellektenYukle()
    {
        htmlKayit = rootView.getContext().getSharedPreferences(AnaSayfa.kayitDosyasi, Context.MODE_PRIVATE);
        duyuruTablosu = htmlKayit.getString(HTML_DUYURU_KEY, "");
        htmlSayfasi.loadDataWithBaseURL(null, duyuruTablosu, "text/html", "utf-8", null);
    }

    public void htmlKaydet(String url)
    {
        SharedPreferences.Editor kayitDuzenleyici = htmlKayit.edit();
        kayitDuzenleyici.remove(HTML_DUYURU_KEY);
        kayitDuzenleyici.putString(HTML_DUYURU_KEY, url);
        kayitDuzenleyici.apply();
    }

    public void duyuruSayfasiOku()
    {
        istekKuyrugu = Volley.newRequestQueue(rootView.getContext());
        String url = getResources().getString(R.string.url_duyuru);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //istek cevabi geldiginde web icerigini yukle
                        webIcerigiYukle(response);
                        pb.setVisibility(View.GONE);
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

                //mesajGoster(dokuman.toString());
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
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                pb.setVisibility(View.VISIBLE);
                Log.d("WEBCLIENT: sayfa_yukl:", url);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

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

        htmlSayfasi.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                if(intent.resolveActivity(getActivity().getPackageManager()) != null) //getactivity dene olmazsa
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                else
                {
                    
                    mesajGoster("Bu baglantıyı açacak uygulama bulunamadı.");
                }
            }
        });
    }

    public void webIcerigiYukle(String response)
    {
        org.jsoup.nodes.Document dokuman = Jsoup.parse(response);
        org.jsoup.nodes.Element etiket = dokuman.selectFirst("div#page-main");
        dokuman = Jsoup.parse(response);
        etiket = dokuman.selectFirst("div#page-main");

        duyuruTablosu = "<!DOCTYPE html><html> <head> <style>" + getResources().getString(R.string.bootstrap) + "</style></head><body>";
        duyuruTablosu = duyuruTablosu.concat(etiket.toString());
        duyuruTablosu = duyuruTablosu.concat("</body></html>");
        duyuruTablosu = duyuruTablosu.replaceAll("<a href=\"sayfa", "<a href=\"https://www.harran.edu.tr/sayfa");
        //Tiklanma ani ovverride fonksiyonunda gelen url'i iste, duylist.aspx ise parcala, parcalanmis halini yukle boylece linke gitmeden tablonun sonraki sayfasi cekilmis olur
        duyuruTablosu = duyuruTablosu.replaceAll("\"duylist.aspx", "\"https://www.harran.edu.tr/duylist.aspx");


        htmlKaydet(duyuruTablosu);
        //mesajGoster("ONLINE YUKLEME BASLADI");
        htmlSayfasi.loadDataWithBaseURL(null, duyuruTablosu, "text/html", "utf-8", null);
    }

    public void mesajGoster(String message)
    {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}