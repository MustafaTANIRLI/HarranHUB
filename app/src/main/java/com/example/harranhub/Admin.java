package com.example.harranhub;

//Veri tabani yonetici verileri
public class Admin {
    private String eposta;

    public Admin(String eposta) {
        this.eposta = eposta;
    }

    public Admin()
    {

    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "eposta='" + eposta + '\'' +
                '}';
    }
}
