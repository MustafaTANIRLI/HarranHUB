package com.example.harranhub.AnaSayfa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;

import com.example.harranhub.Ilan;

//Fragment'lar icerisinden activity ile iletisim kurabilmek uzere arayuz
public interface AnaSayfaArabirimi {
    void geriDon();
    void yonlendir(NavDirections yon);
    void direktYonlendir(int actionID, Bundle argumanlar);
    void ilanGonder(Ilan ilan);
    void veriKontrol(Boolean veri, String kullaniciAdi);
    void internetKontrol();
}
