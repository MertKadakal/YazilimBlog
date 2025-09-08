package mert.kadakal.yazlmblog.ui.blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.api.Sikayet;
import mert.kadakal.yazlmblog.api.Yorum;
import mert.kadakal.yazlmblog.ui.hesap.Blogger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogEkran extends AppCompatActivity {

    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    TextView baslik;
    TextView detay;
    TextView metin;
    TextView etiketler;
    TextView secenekler;
    TextView yorum_yok_text;
    View bottomSheet;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);
        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        baslik = findViewById(R.id.blog_baslik);
        baslik.setText(intent.getStringExtra("blog_baslik"));
        metin = findViewById(R.id.blog_metin);
        metin.setText(intent.getStringExtra("blog_metin"));
        detay = findViewById(R.id.blog_detay);
        etiketler = findViewById(R.id.blog_etiketler);
        yorum_yok_text = findViewById(R.id.yorum_yok_text);

        bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        ArrayList <Yorum> yorumList = new ArrayList<>();
        ListView listView = findViewById(R.id.yorumlar_list);
        YorumAdapter adapter = new YorumAdapter(this, yorumList);
        listView.setAdapter(adapter);

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Call<List<Yorum>> call = apiService.getYorumlar();
                        call.enqueue(new Callback<List<Yorum>>() {
                            @Override
                            public void onResponse(Call<List<Yorum>> call, Response<List<Yorum>> response) {
                                yorumList.clear();
                                for (Yorum yorum : response.body()) {
                                    if (yorum.getEklenen_blog() == intent.getIntExtra("blog_id", -1)) {
                                        yorumList.add(yorum);
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if (!yorumList.isEmpty()) {
                                    yorum_yok_text.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Yorum>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                        listView.setOnItemClickListener((parent, view1, position, id) -> {
                            if (yorumList.get(position).getEkleyen_id() == sharedPreferences.getInt("userid", -1)) {
                                return;
                            }

                            String[] options = {"Şikayet Et", "Bloggerı Görüntüle"};
                            new AlertDialog.Builder(BlogEkran.this)
                                    .setTitle("Ne yapmak istiyorsunuz?")
                                    .setItems(options, (dialog, which) -> {
                                        if (which == 0) {
                                            // EditText oluştur
                                            EditText input = new EditText(BlogEkran.this);
                                            input.setHint("Bu yorum hakkındaki şikayetinizi girin");
                                            input.setBackgroundResource(android.R.drawable.edit_text);
                                            input.setPadding(30, 20, 30, 40);

                                            LinearLayout container = new LinearLayout(BlogEkran.this);
                                            container.setOrientation(LinearLayout.VERTICAL);
                                            container.setPadding(40,50,40,40);

                                            container.addView(input);

                                            new AlertDialog.Builder(BlogEkran.this)
                                                    .setTitle("Şikayet Et")
                                                    .setView(container) // margin uygulanmış container'ı kullanıyoruz
                                                    .setPositiveButton("Gönder", (dialog1, whichButton) -> {
                                                        String aciklama = input.getText().toString().trim();

                                                        LocalDate today = null;
                                                        DateTimeFormatter formatter = null;
                                                        String formattedDate = null;
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            today = LocalDate.now();
                                                            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                                            formattedDate = today.format(formatter);
                                                        }

                                                        Sikayet sikayet = new Sikayet();
                                                        sikayet.setYorum_id(yorumList.get(position).getId());
                                                        sikayet.setAciklama(aciklama);
                                                        sikayet.setTarih(formattedDate);

                                                        Call<Sikayet> call2 = apiService.addSikayet(sikayet);
                                                        call2.enqueue(new Callback<Sikayet>() {
                                                            @Override
                                                            public void onResponse(Call<Sikayet> call, Response<Sikayet> response) {
                                                                if (response.isSuccessful()) {
                                                                    new AlertDialog.Builder(BlogEkran.this)
                                                                            .setMessage("✅ Şikayetiniz gönderildi ✅\n\nEn kısa sürede inceleme yapılacaktır")
                                                                            .setPositiveButton("Tamam", null)
                                                                            .show();
                                                                } else {
                                                                    try {
                                                                        String errorJson = response.errorBody().string();
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




                                        } else if (which == 1) {
                                            getKullanicilar(kullanicilar -> {
                                                int blogger_id = -1;
                                                for (Kullanici k : kullanicilar) {
                                                    if (k.getId() == yorumList.get(position).getEkleyen_id()) {
                                                        blogger_id = k.getId();
                                                        break;
                                                    }
                                                }

                                                Intent intent = new Intent(BlogEkran.this, Blogger.class);
                                                intent.putExtra("blogger_id", blogger_id);
                                                Toast.makeText(BlogEkran.this, "Blogger ID: " + blogger_id, Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                            });
                                        }
                                    })
                                    .show();


                        });
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // Kapalı
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });


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

        secenekler = findViewById(R.id.blog_secenekler);
        secenekler.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);

            // Dinamik seçenekler ekle
            String[] secenekListesi = new String[0];

            secenekListesi = new String[]{"Bloggerı görüntüle"};
            if (sharedPreferences.getInt("userid", -1) == Integer.parseInt(intent.getStringExtra("blog_ekleyen"))) { //kendi blogunsa
                secenekListesi = new String[]{"Düzenle", "Sil"};
            } else if (sharedPreferences.getInt("userid", -1) > 0) {
                secenekListesi = new String[]{"Puanla ve yorum yap", "Şikayet Et", "Bloggerı görüntüle"};
            }

            for (int i = 0; i < secenekListesi.length; i++) {
                popupMenu.getMenu().add(0, i, i, secenekListesi[i]);
            }

            // Seçenek tıklama olayları
            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString(); // menüde görünen yazıyı al
                switch (title) {
                    case "Şikayet Et":
                        EditText input = new EditText(BlogEkran.this);
                        input.setHint("Bu blog hakkındaki şikayetinizi girin");
                        input.setBackgroundResource(android.R.drawable.edit_text);
                        input.setPadding(30, 20, 30, 40);

                        LinearLayout container = new LinearLayout(BlogEkran.this);
                        container.setOrientation(LinearLayout.VERTICAL);
                        container.setPadding(40,50,40,40);

                        container.addView(input);

                        // AlertDialog oluştur
                        new AlertDialog.Builder(BlogEkran.this)
                                .setTitle("Şikayet Et")
                                .setView(container)
                                .setPositiveButton("Gönder", (dialog1, whichButton) -> {
                                    String aciklama = input.getText().toString().trim();

                                    LocalDate today;
                                    DateTimeFormatter formatter;
                                    String formattedDate = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        today = LocalDate.now();
                                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                        formattedDate = today.format(formatter);
                                    }

                                    Sikayet sikayet = new Sikayet();
                                    sikayet.setAciklama(aciklama);
                                    sikayet.setBlog_id(intent.getIntExtra("blog_id", -1));
                                    sikayet.setTarih(formattedDate);

                                    Call<Sikayet> call2 = apiService.addSikayet(sikayet);
                                    call2.enqueue(new Callback<Sikayet>() {
                                        @Override
                                        public void onResponse(Call<Sikayet> call, Response<Sikayet> response) {
                                            if (response.isSuccessful()) {
                                                new AlertDialog.Builder(BlogEkran.this)
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

                        break;
                    case "Sil":
                        Call<Void> call = apiService.deleteBlog(intent.getIntExtra("blog_id", -1));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                sharedPreferences.edit().putBoolean("blog_silindi", true).apply();
                                onBackPressed();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(BlogEkran.this, "Silinirken hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case "Düzenle":
                        Intent duzenleIntent = new Intent(this, BlogEkle.class);
                        duzenleIntent.putExtra("blog_baslik", intent.getStringExtra("blog_baslik"));
                        duzenleIntent.putExtra("blog_metin", intent.getStringExtra("blog_metin"));
                        duzenleIntent.putExtra("blog_etiketler", intent.getStringExtra("blog_etiketler"));
                        duzenleIntent.putExtra("blog_id", intent.getIntExtra("blog_id", -1));
                        duzenleIntent.putExtra("blog_tarih", intent.getStringExtra("blog_tarih"));
                        duzenleIntent.putExtra("blog_ekle_duzenle", "duzenle");
                        startActivity(duzenleIntent);
                        break;

                    case "Puanla ve yorum yap":
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(30, 30, 30, 30);

                        LinearLayout kutuLayout = new LinearLayout(this);
                        kutuLayout.setOrientation(LinearLayout.HORIZONTAL);

                        final int[] secilenPuan = {-1}; // Seçilen puanı tutmak için

                        for (int i = 1; i <= 10; i++) {
                            final int puan = i;
                            TextView tv = new TextView(this);
                            tv.setText(String.valueOf(i));
                            tv.setTextSize(13f);
                            tv.setPadding(10, 10, 10, 10);
                            tv.setBackgroundResource(R.drawable.block_background);
                            tv.setGravity(Gravity.CENTER);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                            );
                            params.setMargins(0,0,0,20);
                            tv.setLayoutParams(params);

                            tv.setOnClickListener(v2 -> {
                                secilenPuan[0] = puan;

                                int background = 0;
                                if (puan >= 0 && puan <= 3) {
                                    background = R.drawable.block_background_selected_1_3;
                                } else if (puan >= 4 && puan <= 6) {
                                    background = R.drawable.block_background_selected_4_6;
                                } else if (puan >= 7 && puan <= 10) {
                                    background = R.drawable.block_background_selected_7_10;
                                }

                                for (int j = 0; j < kutuLayout.getChildCount(); j++) {
                                    View child = kutuLayout.getChildAt(j);
                                    child.setBackgroundResource(R.drawable.block_background);

                                    if (j < puan) {
                                        child.setBackgroundResource(background);
                                    }
                                }
                            });

                            kutuLayout.addView(tv);
                        }

                        layout.addView(kutuLayout);

                        EditText yorum = new EditText(this);
                        yorum.setHint("Yorumunuz");
                        yorum.setSingleLine(false);
                        yorum.setPadding(20,20,20,20);
                        yorum.setMinLines(5); // başlangıçta 3 satır yüksekliğinde
                        yorum.setGravity(Gravity.TOP | Gravity.START);
                        yorum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        yorum.setHorizontallyScrolling(false); // yatayda kaydırma yerine alt satıra geçsin
                        GradientDrawable border = new GradientDrawable();
                        border.setStroke(2, Color.BLACK); // 2px kalınlık, siyah renk
                        border.setColor(Color.WHITE);     // arka plan beyaz olsun
                        yorum.setBackground(border);
                        layout.addView(yorum);

                        builder.setView(layout);

                        builder.setPositiveButton("Tamam", (dialog, which) -> {
                            if (secilenPuan[0] == -1) {
                                Toast.makeText(this, "Lütfen puan seçiniz", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (yorum.getText().toString().isEmpty()) {
                                Toast.makeText(this, "Lütfen yorum kısmını doldurunuz", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //yorum ekle
                            LocalDate today = null;
                            DateTimeFormatter formatter = null;
                            String formattedDate = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                today = LocalDate.now();
                                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                formattedDate = today.format(formatter);
                            }

                            Yorum yeniYorum = new Yorum();
                            yeniYorum.setEklenen_blog(intent.getIntExtra("blog_id",-1));
                            yeniYorum.setEkleyen_id(sharedPreferences.getInt("userid", -1));
                            yeniYorum.setIcerik(yorum.getText().toString());
                            yeniYorum.setTarih(formattedDate);
                            yeniYorum.setPuan(secilenPuan[0]);

                            Call<Yorum> callYorum = apiService.addYorum(yeniYorum);
                            callYorum.enqueue(new Callback<Yorum>() {
                                @Override
                                public void onResponse(Call<Yorum> call, Response<Yorum> response) {
                                    if (response.isSuccessful()) {
                                        new AlertDialog.Builder(BlogEkran.this)
                                                .setMessage("✅ Yorum başarıyla eklendi! ✅")
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
                                public void onFailure(Call<Yorum> call, Throwable t) {
                                    Log.e("API", "İstek başarısız: " + t.getMessage());
                                }
                            });
                        });

                        builder.setNegativeButton("İptal", null);
                        builder.show();

                        break;

                        case "Bloggerı görüntüle":
                            getKullanicilar(kullanicilar -> {
                                Intent intent2 = new Intent(BlogEkran.this, Blogger.class);
                                intent2.putExtra("blogger_id", Integer.parseInt(intent.getStringExtra("blog_ekleyen")));
                                startActivity(intent2);
                            });
                        break;
                }
                return true;
            });

            popupMenu.show();
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



    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferences.getBoolean("blog_guncellendi", false)) {
            baslik.setText(sharedPreferences.getString("blog_baslik",""));
            metin.setText(sharedPreferences.getString("blog_metin",""));

            String etiketler_text = sharedPreferences.getString("blog_etiketler", "");
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
            sb.append(sharedPreferences.getString("blog_tarih", ""));
            getKullanicilar(kullanicilar -> {
                for (Kullanici k : kullanicilar) {
                    if (k.getId() == Integer.parseInt(sharedPreferences.getString("blog_ekleyen",""))) {
                        sb.insert(0, k.getKullanici_adi() + " ● ");
                        break;
                    }
                }

                detay.setText(sb.toString());
            });

            new AlertDialog.Builder(this)
                    .setMessage("✅ Blog başarıyla güncellendi! ✅")
                    .setPositiveButton("Tamam", null)
                    .show();

            sharedPreferences.edit()
                    .putBoolean("blog_guncellendi", false)
                    .apply();
        }
    }
}

