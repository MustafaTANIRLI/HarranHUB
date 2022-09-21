package com.example.harranhub.AnaSayfa.TabSayfalar;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.AnaSayfa.SliderAdapter;
import com.example.harranhub.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPagerSayfasi#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPagerSayfasi extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public ViewPagerSayfasi() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment ViewPagerSayfasi.
   */
  // TODO: Rename and change types and number of parameters
  public static ViewPagerSayfasi newInstance(String param1, String param2) {
    ViewPagerSayfasi fragment = new ViewPagerSayfasi();
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
  public static int mevcutTabNumarasi;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.fragment_view_pager_sayfasi, container, false);


    SliderAdapter adapter = new SliderAdapter(this, 2);
    ViewPager2 vp2 = (ViewPager2) rootView.findViewById(R.id.fragViewPager2);
    vp2.setAdapter(adapter);
    TabLayout TL = (TabLayout) rootView.findViewById(R.id.fragTabLayout);

    //Tab tuslari ve ekran kaydirma ozelligin birbirine baglayan fonksiyon
    new TabLayoutMediator(TL, vp2,
            (tab, position) -> {
              switch (position)
              {
                case 0: tab.setText("HUB"); break;
                case 1: tab.setText("İlanlar"); break;
              }
            }).attach();

    mevcutTabNumarasi = 0;

    //Ilanlar tablosunda verilerin yuklenebilmesi icin internet baglantisi kontrolu yapan fonksiyon
    TL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        mevcutTabNumarasi = tab.getPosition();
        if(mevcutTabNumarasi == 1)
        {
          AnaSayfaArabirimi arabirim = (AnaSayfa)getContext();
          arabirim.internetKontrol();
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    return rootView;
  }


}