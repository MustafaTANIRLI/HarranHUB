package com.example.harranhub.AnaSayfa.MenuSayfalari.Oyun;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.harranhub.R;

public class OyunActivity extends AppCompatActivity implements OyunSayfaArabirimi
{
    NavHostFragment nhf;
    NavController nc;

    @Override
    protected void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        setContentView(R.layout.oyun_activity);

        nhf = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.oyun_fragmentContainerView);
        nc = nhf.getNavController();
    }

    @Override
    public void yonlendir(NavDirections yon, Bundle arguman) {
        try {
            nc.navigate(yon.getActionId(), arguman);
        }
        catch (Exception e)
        {
            Log.d("OYUN YONLENDIRME HATA:\n", e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!nc.navigateUp())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void geriDon() {
        if(!nc.navigateUp())
        {
            super.onBackPressed();
        }
    }
}
