package com.example.harranhub.AnaSayfa.MenuSayfalari;

import android.content.Intent;
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

import com.example.harranhub.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OBS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OBS extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public OBS() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment OBS.
   */
  // TODO: Rename and change types and number of parameters
  public static OBS newInstance(String param1, String param2) {
    OBS fragment = new OBS();
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
  ProgressBar pb;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.fragment_webview_layout, container, false);
    htmlSayfasi = rootView.findViewById(R.id.fragmentWebView); //tanimladiktan sonra aktif et
    pb = (ProgressBar) rootView.findViewById(R.id.webViewProgressBar);
    htmlOzellikAyarla();

    htmlSayfasi.loadUrl(getResources().getString(R.string.url_obs));

    return rootView;
  }

  private void htmlOzellikAyarla()
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
        Log.d("Tiklanan link:", url);
        pb.setVisibility(View.VISIBLE);
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
        if(intent.resolveActivity(rootView.getContext().getPackageManager()) != null)
        {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
      }
    });
  }
}