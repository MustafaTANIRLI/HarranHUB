package com.example.harranhub.AnaSayfa.MenuSayfalari;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harranhub.R;

import org.mariuszgromada.math.mxparser.Expression;

public class HesapMakinesi extends AppCompatActivity
{
    EditText display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiti_hesap_makinesi);
        display = findViewById(R.id.display);
        display.setShowSoftInputOnFocus(false);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getString(R.string.hesap_tap_here).equals(display.getText().toString())){
                    display.setText("");
                }
            }
        });
    }

    public void anybutton(View view) {
        switch (view.getId()){

            case R.id.clear_btn     :display.setText(""); break;
            case R.id.parantez_btn  :parantezekliyici();break;
            case R.id.us_btn        :updateDisplay("^");break;
            case R.id.bolme_btn     :updateDisplay("/");break;
            case R.id.seven_btn     :updateDisplay("7");break;
            case R.id.eight_btn     :updateDisplay("8");break;
            case R.id.nine_btn      :updateDisplay("9");break;
            case R.id.carp_btn      :updateDisplay("x");break;
            case R.id.four_btn      :updateDisplay("4");break;
            case R.id.five_btn      :updateDisplay("5");break;
            case R.id.six_btn       :updateDisplay("6");break;
            case R.id.eksi_btn      :updateDisplay("-");break;
            case R.id.one_btn       :updateDisplay("1");break;
            case R.id.two_btn       :updateDisplay("2");break;
            case R.id.three_btn     :updateDisplay("3");break;
            case R.id.topla_btn     :updateDisplay("+");break;
            case R.id.threeZero_btn :updateDisplayThreeZero();break;
            case R.id.zero_btn      :updateDisplay("0");break;
            case R.id.dat_btn       :updateDisplay(".");break;
            case R.id.equal_btn     :calculateEqual();break;
            case R.id.backclear     :backSpace();break;
            case R.id.sin_btn       :updateDisplay("sin(");break;
            case R.id.cos_btn       :updateDisplay("cos(");break;
            case R.id.log_btn       :updateDisplay("ln(");break;
            case R.id.kok_btn       :updateDisplay("sqrt(");break;
        }
    }

    private void backSpace() {
        int cursorPos = display.getSelectionStart();
        if(cursorPos > 0){
            String oldDisplay = display.getText().toString();
            String leftSideOfDisplay =  oldDisplay.substring(0,cursorPos-1);
            String rightSideOfDisplay = oldDisplay.substring(cursorPos);
            String newText =leftSideOfDisplay+rightSideOfDisplay;
            display.setText(newText);
            display.setSelection(cursorPos-1);}


    }

    private void calculateEqual() {
        String textDisplay = display.getText().toString();
        String newTextDisplay = textDisplay.replaceAll("÷" , "/");
        newTextDisplay = textDisplay.replaceAll("x" , "*");
        //newTextDisplay = textDisplay.replaceAll("X" , "*");
        Expression ifade = new Expression(newTextDisplay);
        String result = String.valueOf(ifade.calculate()).toString();
        if (!result.equals("hatali islem")){
            display.setText(result);
            display.setSelection(result.length());
        }else{

        }
    }

    private void updateDisplayThreeZero() {
        int cursorPos = display.getSelectionStart();
        if(getString(R.string.hesap_tap_here).equals(display.getText().toString())){
            display.setText("000");
        }else{
            String oldDisplay = display.getText().toString();
            String leftSideOfDisplay =  oldDisplay.substring(0,cursorPos);
            String rightSideOfDisplay = oldDisplay.substring(cursorPos);
            String newText =leftSideOfDisplay+"000"+rightSideOfDisplay;
            display.setText(newText);
        }
        display.setSelection(cursorPos+3);

    }

    private void updateDisplay(String addCharToDisplay) {
        int cursorPos = display.getSelectionStart();
        String newText="";
        if(getString(R.string.hesap_tap_here).equals(display.getText().toString())){
            display.setText(addCharToDisplay);
        }else{
            String oldDisplay = display.getText().toString();
            String leftSideOfDisplay =  oldDisplay.substring(0,cursorPos);
            String rightSideOfDisplay = oldDisplay.substring(cursorPos);
            newText =leftSideOfDisplay+addCharToDisplay+rightSideOfDisplay;
            display.setText(newText);
        }

        display.setSelection(display.getText().toString().length());
        // Toast.makeText(this, String.valueOf(cursorPos+1), Toast.LENGTH_SHORT).show();
    }

    private void parantezekliyici() {
        String textDisplay = display.getText().toString();
        int cursorPos =  display.getSelectionStart();
        int countBrackets = 0;
        for (int i = 0 ; i < textDisplay.length();i++){
            if (textDisplay.substring(i,i+1).equalsIgnoreCase("(")) countBrackets++;
            if (textDisplay.substring(i,i+1).equalsIgnoreCase(")")) countBrackets--;
        }

        String lastCharOfDisplay = textDisplay.substring(Math.max(textDisplay.length()-1, 0));

        if (countBrackets==0 || lastCharOfDisplay.equals("(")) updateDisplay("(");
        else if (countBrackets>0 && !lastCharOfDisplay.equals(")")) updateDisplay(") ");
    }
}
