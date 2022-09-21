package com.example.harranhub;

//Veritabani yayinlanan ilan verileri
public class Ilan {

  private String id, baslik, icerik;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBaslik() {
    return baslik;
  }

  public void setBaslik(String baslik) {
    this.baslik = baslik;
  }

  public String getIcerik() {
    return icerik;
  }

  public void setIcerik(String icerik) {
    this.icerik = icerik;
  }

  public Ilan(String id, String baslik, String icerik) {
    this.id = id;
    this.baslik = baslik;
    this.icerik = icerik;
  }

  @Override
  public String toString()
  {
    return id + " " + " " + baslik + " " + icerik;
  }

  public Ilan()
  {

  }
}
