package mert.kadakal.yazlmblog.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Yorum {
    @Expose(serialize = false) // JSON gönderirken hariç tut
    private int id;

    @SerializedName("ekleyen_id")
    private int ekleyen_id;

    @SerializedName("eklenen_blog")
    private int eklenen_blog;

    @SerializedName("tarih")
    private String tarih;

    @SerializedName("icerik")
    private String icerik;

    @SerializedName("puan")
    private int puan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEkleyen_id() {
        return ekleyen_id;
    }

    public void setEkleyen_id(int ekleyen_id) {
        this.ekleyen_id = ekleyen_id;
    }

    public int getEklenen_blog() {
        return eklenen_blog;
    }

    public void setEklenen_blog(int eklenen_blog) {
        this.eklenen_blog = eklenen_blog;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }
}