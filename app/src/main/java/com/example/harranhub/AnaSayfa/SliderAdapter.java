package com.example.harranhub.AnaSayfa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.harranhub.AnaSayfa.TabSayfalar.HUB;
import com.example.harranhub.AnaSayfa.TabSayfalar.Ilanlar.Ilanlar;

public class SliderAdapter extends FragmentStateAdapter {

    private int sayfaSayisi;

    public SliderAdapter(Fragment fa, int sayfaSayisi)
    {
        super(fa);
        this.sayfaSayisi = sayfaSayisi;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        //Ilk sekme HUB, ikinci sekme Ilanlar
        switch (position)
        {
            case 0: return new HUB();
            case 1: return new Ilanlar();
        }
        return new Ilanlar();
    }

    @Override
    public int getItemCount() {
        return sayfaSayisi;
    }
}
