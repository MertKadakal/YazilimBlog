package mert.kadakal.yazlmblog.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    //kullanıcılar
    @GET("api/Kullanici")
    Call<List<Kullanici>> getKullanicilar();

    @POST("api/Kullanici")
    Call<Kullanici> addKullanici(@Body Kullanici yeniKullanici);

    @DELETE("api/Kullanici/{id}")
    Call<Void> deleteKullanici(@Path("id") String id);

    //bloglar
    @GET("api/Blog")
    Call<List<Blog>> getBloglar();
}

