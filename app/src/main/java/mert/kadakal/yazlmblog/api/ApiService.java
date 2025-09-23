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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    //görseller
    @Multipart
    @POST("api/upload")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part image
    );

    @GET("images/{filename}")
    Call<ResponseBody> getImage(@Path("filename") String filename);

    //kullanıcılar
    @GET("api/Kullanici")
    Call<List<Kullanici>> getKullanicilar();

    @POST("api/Kullanici")
    Call<Kullanici> addKullanici(@Body Kullanici yeniKullanici);

    @DELETE("api/Kullanici/{id}")
    Call<Void> deleteKullanici(@Path("id") String id);

    @PUT("api/Kullanici")
    Call<Kullanici> updateKullanici(@Body Kullanici yeniKullanici);

    //bloglar
    @GET("api/Blog")
    Call<List<Blog>> getBloglar();

    @POST("api/Blog")
    Call<Blog> addBlog(@Body Blog yeniBlog);

    @DELETE("api/Blog/{id}")
    Call<Void> deleteBlog(@Path("id") int id);

    @PUT("api/Blog")
    Call<Blog> updateBlog(@Body Blog yeniBlog);

    //yorumlar
    @GET("api/Yorum")
    Call<List<Yorum>> getYorumlar();

    @POST("api/Yorum")
    Call<Yorum> addYorum(@Body Yorum yeniYorum);

    @DELETE("api/Yorum/{id}")
    Call<Void> deleteYorum(@Path("id") int id);

    //şikayetler
    @GET("api/Sikayet")
    Call<List<Sikayet>> getSikayetler();

    @POST("api/Sikayet")
    Call<Sikayet> addSikayet(@Body Sikayet yeniSikayet);
}

