package mert.kadakal.yazlmblog;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Sikayet;
import mert.kadakal.yazlmblog.api.Yorum;
import mert.kadakal.yazlmblog.ui.blog.BlogEkran;
import mert.kadakal.yazlmblog.ui.blog.HomeFragment;
import mert.kadakal.yazlmblog.ui.hesap.NotificationsFragment;
import mert.kadakal.yazlmblog.ui.populer.DashboardFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    SharedPreferences sharedPreferences;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    MenuItem navIsim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        navIsim = navigationView.getMenu().findItem(R.id.nav_isim);
        SpannableString spanTitle = new SpannableString("YazılımBlog");
        spanTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, spanTitle.length(), 0);
        spanTitle.setSpan(new AbsoluteSizeSpan(24, true), 0, spanTitle.length(), 0);
        navIsim.setTitle(spanTitle);

        // Menü tıklamalarını yakala
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            } else if (id == R.id.nav_popular) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            } else if (id == R.id.nav_account) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NotificationsFragment())
                        .commit();
            } else if (id == R.id.nav_kvkk) {
                showKvkkDialog();
            } else if (id == R.id.nav_kahve) {
                showKahveDialog();
            } else if (id == R.id.nav_fikironeri) {
                if (sharedPreferences.getInt("userid", -1) == -1) {
                    Toast.makeText(this, "Bir hesaba giriş yapmalısınız", Toast.LENGTH_SHORT).show();
                    return true;
                }
                showFikirDialog();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // 🔹 İlk açılışta HomeFragment göster
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home); // menüde seçili görünmesi için
        }
    }

    // KVKK AlertDialog gösterme fonksiyonu
    private void showKvkkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kişisel Verilerin Korunması Hakkında");

        String kvkkText = "6698 sayılı Kişisel Verilerin Korunması Kanunu (KVKK) uyarınca, YazılımBlog uygulaması olarak kullanıcılarımızın kişisel verilerini aşağıda açıklanan çerçevede işlemekteyiz.\n\n" +
                "1. Veri Sorumlusu\n" +
                "YazılımBlog\n" +
                "E-posta: mert.kadakal1629@gmail.com\n" +
                "Adres: Hacettepe Üniversitesi, ANKARA/Çankaya\n\n" +
                "2. İşlenen Kişisel Veriler\n" +
                "- Kullanıcı adı\n" +
                "- Kullanıcı profil fotoğrafı\n" +
                "- E-posta adresi\n" +
                "- Telefon numarası\n\n" +
                "3. Kişisel Verilerin İşlenme Amaçları\n" +
                "- Uygulama hizmetlerinin sunulabilmesi ve yönetilmesi\n" +
                "- Kullanıcı hesaplarının oluşturulması ve yönetimi\n" +
                "- Kullanıcı deneyiminin iyileştirilmesi\n" +
                "- Yasal yükümlülüklerin yerine getirilmesi\n\n" +
                "4. Kişisel Verilerin Aktarılması\n" +
                "Toplanan kişisel verileriniz, yalnızca yasal zorunluluklar veya hizmetin gereklilikleri çerçevesinde iş ortaklarımızla paylaşılabilir.\n\n" +
                "5. Kişisel Verilerin Saklanma Süresi\n" +
                "Verileriniz, işleme amaçlarının ortadan kalkmasına veya ilgili mevzuatta öngörülen sürelerin dolmasına kadar saklanacaktır.\n\n" +
                "6. Kullanıcı Hakları\n" +
                "KVKK’nın 11. maddesi uyarınca kullanıcılar;\n" +
                "- Kişisel verilerinin işlenip işlenmediğini öğrenme,\n" +
                "- İşlenmişse buna ilişkin bilgi talep etme,\n" +
                "- İşlenme amacını öğrenme,\n" +
                "- Eksik veya yanlış işlenmişse düzeltilmesini isteme,\n" +
                "- Silinmesini veya anonimleştirilmesini isteme,\n" +
                "- İşlemeye itiraz etme\n" +
                "haklarına sahiptir.\n\n" +
                "7. Başvuru Yöntemi\n" +
                "Haklarınızı kullanmak için taleplerinizi mert.kadakal1629@gmail.com üzerinden iletebilirsiniz.";

        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(kvkkText);
        textView.setPadding(40, 30, 40, 30);
        scrollView.addView(textView);

        builder.setView(scrollView);

        builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showKahveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bana Kahve Ismarla :)");

        String kvkkText = "Üniversite öğrencisi olarak geliştirdiğim bu uygulamayı beğendiysen bana bi' kahve ısmarlamak ister misin? Şimdiden çok teşekkür ediyorum :)\n\n" +
                "TR240006701000000023402080";

        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(kvkkText);
        textView.setPadding(40, 30, 40, 30);
        scrollView.addView(textView);

        builder.setView(scrollView);

        builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());

        // Kopyala butonu metni panoya kopyalıyor
        builder.setNeutralButton("Kopyala", (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Kopyala", "TR240006701000000023402080");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Metin panoya kopyalandı", Toast.LENGTH_SHORT).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFikirDialog() {
        EditText input = new EditText(this);
        input.setHint("Fikir ve önerilerinizi giriniz");
        input.setBackgroundResource(android.R.drawable.edit_text);
        input.setPadding(30, 20, 30, 40);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40,50,40,40);

        container.addView(input);

        // AlertDialog oluştur
        new AlertDialog.Builder(this)
                .setTitle("Fikir ve önerilerinizi önemsiyoruz")
                .setView(container)
                .setPositiveButton("Gönder", (dialog1, whichButton) -> {
                    String aciklama = input.getText().toString().trim();

                    if (aciklama.isEmpty()) {
                        Toast.makeText(this, "Boş bırakılamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Yorum yeniYorum = new Yorum();
                    yeniYorum.setEklenen_blog(-1);
                    yeniYorum.setEkleyen_id(sharedPreferences.getInt("userid", -1));
                    yeniYorum.setIcerik(aciklama);
                    yeniYorum.setTarih("00-00-0000");
                    yeniYorum.setPuan(0);

                    Call<Yorum> callYorum = apiService.addYorum(yeniYorum);
                    callYorum.enqueue(new Callback<Yorum>() {
                        @Override
                        public void onResponse(Call<Yorum> call, Response<Yorum> response) {
                            if (response.isSuccessful()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("✅ Geri bildiriminizi aldık ✅")
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
                })
                .setNegativeButton("İptal", (dialog1, whichButton) -> dialog1.dismiss())
                .show();
    }
}
