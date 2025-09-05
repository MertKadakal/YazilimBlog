package mert.kadakal.yazlmblog.ui.blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
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
        Map<String, Boolean> secimDurumuGlobal = new HashMap<>();

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

                CardView card_egitim_hakkinda = popupView.findViewById(R.id.card_egitim_hakkinda);
                CardView card_oyun_gelistirme = popupView.findViewById(R.id.card_oyun_gelistirme);
                CardView card_web_gelistirme = popupView.findViewById(R.id.card_web_gelistirme);
                CardView card_yazilim_dilleri = popupView.findViewById(R.id.card_yazilim_dilleri);

                // Kartları map'e ekle, eğer global map'te yoksa false olarak ekle
                if (!secimDurumuGlobal.containsKey("egitim")) secimDurumuGlobal.put("egitim", false);
                if (!secimDurumuGlobal.containsKey("oyun")) secimDurumuGlobal.put("oyun", false);
                if (!secimDurumuGlobal.containsKey("web")) secimDurumuGlobal.put("web", false);
                if (!secimDurumuGlobal.containsKey("yazilim")) secimDurumuGlobal.put("yazilim", false);

                // Kartları bir map ile eşle
                Map<CardView, String> kartMap = new HashMap<>();
                kartMap.put(card_egitim_hakkinda, "egitim");
                kartMap.put(card_oyun_gelistirme, "oyun");
                kartMap.put(card_web_gelistirme, "web");
                kartMap.put(card_yazilim_dilleri, "yazilim");

                // Açılırken renkleri güncelle
                for (Map.Entry<CardView, String> entry : kartMap.entrySet()) {
                    if (secimDurumuGlobal.get(entry.getValue())) {
                        entry.getKey().setCardBackgroundColor(Color.parseColor("#4CAF50")); // yeşil
                    } else {
                        entry.getKey().setCardBackgroundColor(Color.WHITE);
                    }
                }

                // Ortak click listener
                View.OnClickListener kartTiklamaListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CardView kart = (CardView) view;
                        String key = kartMap.get(kart);
                        boolean secili = secimDurumuGlobal.get(key);

                        if (!secili) {
                            kart.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                            secimDurumuGlobal.put(key, true);
                        } else {
                            kart.setCardBackgroundColor(Color.WHITE);
                            secimDurumuGlobal.put(key, false);
                        }
                    }
                };

                // Tüm kartlara listener ekle
                for (CardView c : kartMap.keySet()) {
                    c.setOnClickListener(kartTiklamaListener);
                }
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
}

