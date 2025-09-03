package mert.kadakal.yazlmblog.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Blog {
    @Expose(serialize = false) // JSON gönderirken hariç tut
    private int id;

    @SerializedName("ekleyen_id")
    private int ekleyen_id;

    @SerializedName("metin")
    private String metin;

    @SerializedName("tarih")
    private String tarih;

    @SerializedName("baslik")
    private String baslik;

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

    public String getMetin() {
        return metin;
    }

    public void setMetin(String metin) {
        this.metin = metin;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }
}