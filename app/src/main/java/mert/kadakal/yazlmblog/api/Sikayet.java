package mert.kadakal.yazlmblog.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sikayet {
    @Expose(serialize = false) // JSON gönderirken hariç tut
    private int id;

    @SerializedName("eden_id")
    private int eden_id;

    @SerializedName("blog_id")
    private int blog_id;

    @SerializedName("yorum_id")
    private int yorum_id;

    @SerializedName("hesap_id")
    private int hesap_id;

    @SerializedName("tarih")
    private String tarih;

    @SerializedName("aciklama")
    private String aciklama;

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int getHesap_id() {
        return hesap_id;
    }

    public void setHesap_id(int hesap_id) {
        this.hesap_id = hesap_id;
    }

    public int getYorum_id() {
        return yorum_id;
    }

    public void setYorum_id(int yorum_id) {
        this.yorum_id = yorum_id;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public int getEden_id() {
        return eden_id;
    }

    public void setEden_id(int eden_id) {
        this.eden_id = eden_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}