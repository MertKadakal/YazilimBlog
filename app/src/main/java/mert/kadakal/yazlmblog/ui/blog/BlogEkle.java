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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogEkle extends AppCompatActivity {

    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Button ekle;
    Button etiketler;
    EditText blog_metin;
    EditText blog_baslik;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_ekle);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ekle = findViewById(R.id.ekle_button);
        etiketler = findViewById(R.id.etiket_button);
        blog_metin = findViewById(R.id.blog_metin);
        blog_baslik = findViewById(R.id.blog_baslik);

        // Activity seviyesinde bir map tanımla, tüm seçimleri burada tutacağız
        Map<String, Boolean> secimDurumuGlobal = new LinkedHashMap<>();

        // Kartları map'e ekle, eğer global map'te yoksa false olarak ekle
        for (String etkt : EtiketlerList.LIST) {
            if (!secimDurumuGlobal.containsKey(etkt)) secimDurumuGlobal.put(etkt, false);
        }

        if (getIntent().getStringExtra("blog_ekle_duzenle").equals("duzenle")) {
            blog_baslik.setText(getIntent().getStringExtra("blog_baslik"));
            blog_metin.setText(getIntent().getStringExtra("blog_metin"));
            ekle.setText("Tamamla");

            for (int i = 0; i < getIntent().getStringExtra("blog_etiketler").split(",").length; i++) {
                Log.d("etiket", getIntent().getStringExtra("blog_etiketler").split(",")[i]);
                secimDurumuGlobal.put(EtiketlerList.LIST.get(i), false);
                if (getIntent().getStringExtra("blog_etiketler").split(",")[i].equals("1")) {
                    secimDurumuGlobal.put(EtiketlerList.LIST.get(i), true);
                }
            }
        }

// Etiketler butonunun click'i
        etiketler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(BlogEkle.this);
                View popupView = inflater.inflate(R.layout.pop_op_blog, null);

                AlertDialog dialog = new AlertDialog.Builder(BlogEkle.this)
                        .setView(popupView)
                        .create();

                dialog.show();

                TextView sabitText = popupView.findViewById(R.id.sabit_text);
                Button sabitButon = popupView.findViewById(R.id.etiketleri_onayla);

                sabitText.setVisibility(View.GONE);
                sabitButon.setVisibility(View.GONE);

                RecyclerView recyclerEtiketler = popupView.findViewById(R.id.recycler_etiketler);
                recyclerEtiketler.setLayoutManager(new LinearLayoutManager(BlogEkle.this));

                // Map başlangıçta false değerlerle dolsun
                for (String e : EtiketlerList.LIST) {
                    secimDurumuGlobal.putIfAbsent(e, false);
                }

                EtiketAdapter adapter = new EtiketAdapter(EtiketlerList.LIST, secimDurumuGlobal);
                recyclerEtiketler.setAdapter(adapter);

            }
        });


        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blog_baslik.getText().toString().isEmpty() || blog_metin.getText().toString().isEmpty()) {
                    Toast.makeText(BlogEkle.this, "Alanlar boş bırakılamaz", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (blog_metin.getText().toString().length() < 300) {
                    Toast.makeText(BlogEkle.this, "Blog uzunluğu 300 karakterden az olamaz", Toast.LENGTH_SHORT).show();
                    return;
                }

                int trueCount = 0;
                for (Boolean b : secimDurumuGlobal.values()) {
                    if (b != null && b) {  // null kontrolü
                        trueCount++;
                    }
                }
                if (trueCount > 10 || trueCount == 0) {
                    Toast.makeText(BlogEkle.this, "1 ila 10 arası sayıda etiket seçebilirsiniz (" + trueCount + " tane seçtiniz)", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (ekle.getText().toString().equals("Ekle")) {
                    LocalDate today = null;
                    DateTimeFormatter formatter = null;
                    String formattedDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        today = LocalDate.now();
                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        formattedDate = today.format(formatter);
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Boolean secim : secimDurumuGlobal.values()) {
                        sb.append(secim ? "1" : "0").append(",");
                    }
                    if (sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                    }
                    String etiketler = sb.toString();

                    Blog yeniBlog = new Blog();
                    yeniBlog.setEkleyen_id(sharedPreferences.getInt("userid", -1));
                    yeniBlog.setMetin(blog_metin.getText().toString());
                    yeniBlog.setBaslik(blog_baslik.getText().toString());
                    yeniBlog.setTarih(formattedDate);
                    yeniBlog.setEtiketler(etiketler);

                    blogEkle(yeniBlog);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Boolean secim : secimDurumuGlobal.values()) {
                        sb.append(secim ? "1" : "0").append(",");
                    }
                    if (sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                    }
                    String etiketler = sb.toString();

                    Blog yeniBlog = new Blog();
                    yeniBlog.setEkleyen_id(sharedPreferences.getInt("userid", -1));
                    yeniBlog.setMetin(blog_metin.getText().toString());
                    yeniBlog.setBaslik(blog_baslik.getText().toString());
                    yeniBlog.setTarih(getIntent().getStringExtra("blog_tarih") + " (Güncellendi)");
                    yeniBlog.setId(getIntent().getIntExtra("blog_id", -1));
                    yeniBlog.setEtiketler(etiketler);

                    blogGuncelle(yeniBlog);
                }
            }
        });
    }

    private void blogEkle(Blog yeniBlog) {
        Call<Blog> call = apiService.addBlog(yeniBlog);
        call.enqueue(new Callback<Blog>() {
            @Override
            public void onResponse(Call<Blog> call, Response<Blog> response) {
                if (response.isSuccessful()) {
                    sharedPreferences.edit()
                            .putBoolean("blog_eklendi", true)
                            .apply();

                    onBackPressed();
                } else {
                    try {
                        String errorJson = response.errorBody().string(); // errorBody string olarak al
                        Log.e("API", "Hata: " + response.code() + " - " + errorJson);
                    } catch (Exception e) {
                        Log.e("API", "Hata body okunamadı", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<Blog> call, Throwable t) {
                Log.e("API", "İstek başarısız: " + t.getMessage());
            }
        });
    }

    private void blogGuncelle(Blog yeniBlog) {
        Call<Blog> call = apiService.updateBlog(yeniBlog);
        call.enqueue(new Callback<Blog>() {
            @Override
            public void onResponse(Call<Blog> call, Response<Blog> response) {
                if (response.isSuccessful()) {
                    sharedPreferences.edit()
                            .putBoolean("blog_guncellendi", true)
                            .putBoolean("blog_guncellendi_hesap", true)
                            .apply();

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("blog_baslik", yeniBlog.getBaslik());
                    editor.putString("blog_ekleyen", String.valueOf(yeniBlog.getEkleyen_id()));
                    editor.putString("blog_metin", yeniBlog.getMetin());
                    editor.putString("blog_tarih", yeniBlog.getTarih());
                    editor.putString("blog_etiketler", yeniBlog.getEtiketler());
                    editor.putInt("blog_id", yeniBlog.getId());
                    editor.apply(); // verileri kaydet

                    onBackPressed();
                } else {
                    try {
                        String errorJson = response.errorBody().string(); // errorBody string olarak al
                        Log.e("API", "Hata: " + response.code() + " - " + errorJson);
                    } catch (Exception e) {
                        Log.e("API", "Hata body okunamadı", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<Blog> call, Throwable t) {
                Log.e("API", "İstek başarısız: " + t.getMessage());
            }
        });
    }
}

