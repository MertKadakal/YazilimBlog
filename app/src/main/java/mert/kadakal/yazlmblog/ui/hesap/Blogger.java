package mert.kadakal.yazlmblog.ui.hesap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.api.Sikayet;
import mert.kadakal.yazlmblog.ui.blog.BlogAdapter;
import mert.kadakal.yazlmblog.ui.blog.BlogEkran;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Blogger extends AppCompatActivity {
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    TextView hesap_ismi;
    TextView hesap_kaydolma;
    TextView hesap_total;
    TextView blog_yok;
    TextView sikayet;
    TextView info;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blogger);

        int blogger_id = getIntent().getIntExtra("blogger_id", -1);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        hesap_ismi = findViewById(R.id.hesap_isim);
        hesap_kaydolma = findViewById(R.id.hesap_kaydolmatarihi);
        hesap_total = findViewById(R.id.hesap_toplamblog);
        sikayet = findViewById(R.id.sikayet);
        info = findViewById(R.id.bilgi);
        blog_yok = findViewById(R.id.blog_yok_img_blogger);
        blog_yok.setVisibility(View.INVISIBLE);

        getKullanicilar(kullanicilar -> {
            for (Kullanici k : kullanicilar) {
                if (k.getId() == blogger_id) {
                    hesap_ismi.setText(k.getKullanici_adi());
                    hesap_kaydolma.setText(k.getKayit_tarihi() + " tarihinde katıldı");
                }
            }
        });

        //blogg listesi
        ListView listView = findViewById(R.id.listView); // XML'deki ListView id'si
        ArrayList <Blog> blogList = new ArrayList<>();
        BlogAdapter adapter = new BlogAdapter(this, blogList);
        listView.setAdapter(adapter);

        Call<List<Blog>> call = apiService.getBloglar();
        call.enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                blogList.clear();
                for (Blog blog : response.body()) {
                    if (blog.getEkleyen_id() == blogger_id) {
                        blogList.add(blog);
                        adapter.notifyDataSetChanged();
                    }
                }

                if (blogList.isEmpty()) {
                    blog_yok.setVisibility(View.VISIBLE);
                    hesap_total.setText("Henüz blog eklenmemiş");
                } else {
                    hesap_total.setText("Toplamda "+blogList.size()+" yayınlanan blog");
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Blog secilenBlog = blogList.get(position);
            Intent intent = new Intent(this, BlogEkran.class);
            intent.putExtra("blog_baslik", secilenBlog.getBaslik());
            intent.putExtra("blog_ekleyen", String.valueOf(secilenBlog.getEkleyen_id()));
            intent.putExtra("blog_metin", secilenBlog.getMetin());
            intent.putExtra("blog_tarih", secilenBlog.getTarih());
            intent.putExtra("blog_etiketler", secilenBlog.getEtiketler());
            intent.putExtra("blog_id", secilenBlog.getId());
            startActivity(intent);
        });

        sikayet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = new EditText(Blogger.this);
                input.setHint("Bu hesap hakkındaki şikayetinizi girin");
                input.setBackgroundResource(android.R.drawable.edit_text);
                input.setPadding(30, 20, 30, 40);

                LinearLayout container = new LinearLayout(Blogger.this);
                container.setOrientation(LinearLayout.VERTICAL);
                container.setPadding(40,50,40,40);

                container.addView(input);

                // AlertDialog oluştur
                new AlertDialog.Builder(Blogger.this)
                        .setTitle("Şikayet Et")
                        .setView(container)
                        .setPositiveButton("Gönder", (dialog1, whichButton) -> {
                            String aciklama = input.getText().toString().trim();

                            LocalDate today;
                            DateTimeFormatter formatter;
                            String formattedDate = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                today = LocalDate.now();
                                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                formattedDate = today.format(formatter);
                            }

                            Sikayet sikayet = new Sikayet();
                            sikayet.setAciklama(aciklama);
                            sikayet.setHesap_id(blogger_id);
                            sikayet.setTarih(formattedDate);
                            sikayet.setEden_id(sharedPreferences.getInt("userid",-1));

                            Call<Sikayet> call2 = apiService.addSikayet(sikayet);
                            call2.enqueue(new Callback<Sikayet>() {
                                @Override
                                public void onResponse(Call<Sikayet> call, Response<Sikayet> response) {
                                    if (response.isSuccessful()) {
                                        new AlertDialog.Builder(Blogger.this)
                                                .setMessage("✅ Şikayetiniz gönderildi ✅\n\nEn kısa sürede inceleme yapılacaktır")
                                                .setPositiveButton("Tamam", null)
                                                .show();
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
                                public void onFailure(Call<Sikayet> call, Throwable t) {
                                    Log.e("API", "İstek başarısız: " + t.getMessage());
                                }
                            });
                        })
                        .setNegativeButton("İptal", (dialog1, whichButton) -> dialog1.dismiss())
                        .show();
            }
        });

        info.setOnClickListener(view -> {
            getKullanicilar(kullanicilar -> {
                Kullanici secilenKullanici = null;

                // Kullanıcıyı bul
                for (Kullanici k : kullanicilar) {
                    if (k.getId() == blogger_id) {
                        secilenKullanici = k;
                        break;
                    }
                }

                if (secilenKullanici != null) {
                    // Mail TextView
                    TextView mail = new TextView(Blogger.this);
                    mail.setPadding(30, 10, 30, 10);
                    mail.setTextColor(Color.BLACK);
                    mail.setText(secilenKullanici.getMail() == null ? "Mail: ---" : "Mail: " + secilenKullanici.getMail());

                    // Tel TextView
                    TextView tel = new TextView(Blogger.this);
                    tel.setPadding(30, 10, 30, 10);
                    tel.setTextColor(Color.BLACK);
                    tel.setText(secilenKullanici.getTel() == null ? "Telefon: ---" : "Telefon: " + secilenKullanici.getTel());

                    // Container layout
                    LinearLayout container = new LinearLayout(Blogger.this);
                    container.setOrientation(LinearLayout.VERTICAL);
                    container.setPadding(40, 50, 40, 40);

                    container.addView(mail);
                    container.addView(tel);

                    // AlertDialog oluştur
                    new AlertDialog.Builder(Blogger.this)
                            .setTitle(secilenKullanici.getKullanici_adi() + " hakkında")
                            .setView(container)
                            .setNegativeButton("Tamam", (dialog1, whichButton) -> dialog1.dismiss())
                            .show();
                }
            });
        });

        ImageView pp = findViewById(R.id.pp);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads/"+blogger_id);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri.toString())
                    .transform(new RoundedCorners(30))
                    .into(pp);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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
