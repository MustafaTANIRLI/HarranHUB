package com.example.harranhub.AnaSayfa.MenuSayfalari.Oyun;

import android.os.Bundle;

import androidx.navigation.NavDirections;

//Fragment'lar icerisinden activity ile iletisim kurabilmek uzere arayuz
public interface OyunSayfaArabirimi {
    void yonlendir(NavDirections yon, Bundle arguman);
    void geriDon();
}
