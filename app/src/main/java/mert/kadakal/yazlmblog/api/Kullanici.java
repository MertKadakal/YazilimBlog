package mert.kadakal.yazlmblog.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kullanici {
    @Expose(serialize = false) // JSON gönderirken hariç tut
    private int id;

    @SerializedName("kayit_yontem")
    private String kayit_yontem;

    @SerializedName("kullanici_adi")
    private String kullanici_adi;

    @SerializedName("kayit_tarihi")
    private String kayit_tarihi;

    @SerializedName("mail")
    private String mail;

    @SerializedName("tel")
    private String tel;

    @SerializedName("parola")
    private String parola;

    @SerializedName("favoriler")
    private String favoriler;

    // Getter ve Setter metodları


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKayit_yontem() { return kayit_yontem; }
    public void setKayit_yontem(String kayit_yontem) { this.kayit_yontem = kayit_yontem; }

    public String getKullanici_adi() { return kullanici_adi; }
    public void setKullanici_adi(String kullanici_adi) { this.kullanici_adi = kullanici_adi; }

    public String getKayit_tarihi() { return kayit_tarihi; }
    public void setKayit_tarihi(String kayit_tarihi) { this.kayit_tarihi = kayit_tarihi; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getParola() {
        return parola;
    }
    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getFavoriler() {
        return favoriler;
    }

    public void setFavoriler(String favoriler) {
        this.favoriler = favoriler;
    }
}
