package mert.kadakal.yazlmblog.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/Kullanici")
    Call<List<Kullanici>> getKullanicilar();

    @POST("api/Kullanici")
    Call<Kullanici> addKullanici(@Body Kullanici yeniKullanici);

    @DELETE("api/Kullanici/{id}")
    Call<Void> deleteKullanici(@Path("id") String id);
}

