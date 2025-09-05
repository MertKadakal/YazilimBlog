package mert.kadakal.yazlmblog.ui.blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.ui.hesap.EmailCheckResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogEkran extends AppCompatActivity {

    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    TextView baslik;
    TextView detay;
    TextView metin;
    TextView etiketler;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);
        Intent intent = getIntent();

        baslik = findViewById(R.id.blog_baslik);
        baslik.setText(intent.getStringExtra("blog_baslik"));
        metin = findViewById(R.id.blog_metin);
        metin.setText(intent.getStringExtra("blog_metin"));
        detay = findViewById(R.id.blog_detay);
        etiketler = findViewById(R.id.blog_etiketler);
        String etiketler_text = intent.getStringExtra("blog_etiketler");
        ArrayList<String> etiketlerList = new ArrayList<>(List.of("Yazılım Dilleri", "Oyun Geliştirme", "Web Geliştirme", "Eğitim"));
        StringBuilder etiketlerSb = new StringBuilder("");
        for (int i = 0; i < etiketler_text.split(",").length; i++) {
            if (etiketler_text.split(",")[i].equals("1")) {
                etiketlerSb.append(etiketlerList.get(i) + ", ");
            }
        }
        if (etiketlerSb.length() > 0) {
            etiketlerSb.setLength(etiketlerSb.length() - 2);
        }
        etiketler.setText(etiketlerSb.toString().isEmpty() ? "Etiket girilmemiş" : etiketlerSb.toString());

        StringBuilder sb = new StringBuilder();
        sb.append(intent.getStringExtra("blog_tarih"));
        getKullanicilar(kullanicilar -> {
            for (Kullanici k : kullanicilar) {
                if (k.getId() == Integer.parseInt(intent.getStringExtra("blog_ekleyen"))) {
                    sb.insert(0, k.getKullanici_adi() + " ● ");
                    break;
                }
            }

            detay.setText(sb.toString());
        });

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    private void getKullanicilar(Consumer<List<Kullanici>> callback) {
        Call<List<Kullanici>> call = apiService.getKullanicilar(); // her seferinde yeni Call oluştur
        call.enqueue(new Callback<List<Kullanici>>() {
            @Override
            public void onResponse(Call<List<Kullanici>> call, Response<List<Kullanici>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(new ArrayList<>(response.body()));
                } else {
                    callback.accept(new ArrayList<>()); // boş liste dön
                }
            }

            @Override
            public void onFailure(Call<List<Kullanici>> call, Throwable t) {
                t.printStackTrace();
                callback.accept(new ArrayList<>()); // hata olursa boş liste dön
            }
        });
    }
}

