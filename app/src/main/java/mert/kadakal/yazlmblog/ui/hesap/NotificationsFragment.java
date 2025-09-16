package mert.kadakal.yazlmblog.ui.hesap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import mert.kadakal.yazlmblog.R;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.ui.blog.BlogAdapter;
import mert.kadakal.yazlmblog.ui.blog.BlogEkran;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;


public class NotificationsFragment extends Fragment {
    View view;
    Button mail;
    Button kaydol;
    Button giris;
    TextView cikis;
    TextView hesap_ismi;
    TextView hesap_kaydolma;
    TextView hesap_total;
    TextView ayarlar;
    TextView favs;
    LinearLayout secenekler;
    LinearLayout hesap;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Call<List<Kullanici>> call = apiService.getKullanicilar();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int totalBlog = 0;
    private final List<AlertDialog> acikDialoglar = new ArrayList<>();


    ListView listView;
    ArrayList<Blog> blogList;
    BlogAdapter adapter;
    //String sunucuKok = "13.60.84.136/yazilimBlog";
    //private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int userid = sharedPreferences.getInt("userid", -1);

        listView = view.findViewById(R.id.listView); // XML'deki ListView id'si
        blogList = new ArrayList<>();
        adapter = new BlogAdapter(getContext(), blogList);
        listView.setAdapter(adapter);

        cikis = view.findViewById(R.id.cikis);
        cikis.setOnClickListener(view12 -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Çıkış yapmak istediğinize emin misiniz?")
                    .setPositiveButton("Tamam", (dialogInterface, i) -> {
                        editor.putInt("userid", -1);
                        editor.apply();
                        ekranGuncelle(-1);
                    })
                    .setNegativeButton("İptal", (d, w) -> d.dismiss())
                    .show();
        });

        mail = view.findViewById(R.id.mail);
        mail.setOnClickListener(view1 -> showMailInputDialog());

        kaydol = view.findViewById(R.id.kaydol);
        kaydol.setOnClickListener(view1 -> { kaydolDialog("kaydol"); });

        giris = view.findViewById(R.id.giris);
        giris.setOnClickListener(view1 -> { kaydolDialog("giriş"); });

        secenekler = view.findViewById(R.id.secenekler_layout);
        hesap = view.findViewById(R.id.hesap_layout);

        ayarlar = view.findViewById(R.id.ayarlar);
        ayarlar.setOnClickListener(v -> getKullanicilar(kullanicilar -> {
            for (Kullanici k : kullanicilar) {
                if (k.getId() == sharedPreferences.getInt("userid", -1)) {

                    int margin = (int) (16 * getResources().getDisplayMetrics().density);

// Dış container
                    LinearLayout container2 = new LinearLayout(requireContext());
                    container2.setOrientation(LinearLayout.VERTICAL);
                    container2.setPadding(margin, margin, margin, margin);

// Hesap ismi
                    TextView labelIsim = new TextView(requireContext());
                    labelIsim.setText("Hesap İsmi");
                    EditText isim = new EditText(requireContext());
                    isim.setInputType(InputType.TYPE_CLASS_TEXT);
                    isim.setText(hesap_ismi.getText());

// Mail
                    TextView labelMail = new TextView(requireContext());
                    labelMail.setText("Mail Adresi");
                    EditText mail = new EditText(requireContext());
                    mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    mail.setText(k.getMail() == null ? "" : k.getMail()); // Kullanıcıdan çekilen mail

// Telefon
                    TextView labelTelefon = new TextView(requireContext());
                    labelTelefon.setText("Telefon Numarası");
                    EditText telefon = new EditText(requireContext());
                    telefon.setInputType(InputType.TYPE_CLASS_PHONE);
                    telefon.setText(k.getTel() == null ? "" : k.getTel()); // Kullanıcıdan çekilen telefon

// Parola değiştir
                    TextView labelParola = new TextView(requireContext());
                    labelParola.setText("Parola");
                    EditText parola = new EditText(requireContext());
                    parola.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    parola.setHint("Yeni Parola");

// Layout'a ekleme
                    container2.addView(labelIsim);
                    container2.addView(isim);
                    container2.addView(labelMail);
                    container2.addView(mail);
                    container2.addView(labelTelefon);
                    container2.addView(telefon);
                    container2.addView(labelParola);
                    container2.addView(parola);

// Başlık için layout oluştur
                    LinearLayout titleLayout = new LinearLayout(requireContext());
                    titleLayout.setOrientation(LinearLayout.VERTICAL);
                    titleLayout.setPadding(32, 32, 32, 32);
                    titleLayout.setGravity(Gravity.CENTER);

                    TextView title_text = new TextView(requireContext());
                    title_text.setText("Hesap Ayarları");
                    title_text.setTextColor(Color.parseColor("#000000"));
                    title_text.setTypeface(null, Typeface.NORMAL); // bold değil
                    title_text.setTextSize(20);
                    title_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    titleLayout.addView(title_text);

                    AlertDialog dialog = new AlertDialog.Builder(requireContext())
                            .setCustomTitle(titleLayout)  // özel başlık
                            .setView(container2)          // içerik
                            .setPositiveButton("Tamam", null)
                            .setNegativeButton("İptal", (d, w) -> d.dismiss())
                            .create();

                    dialog.setOnShowListener(dlg ->
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2 -> {
                                // Güncelleme işlemi
                                String yeniIsim = isim.getText().toString().trim();
                                String yeniMail = mail.getText().toString().trim();
                                String yeniTelefon = telefon.getText().toString().trim();
                                String yeniParola = parola.getText().toString().trim();

                                // Eğer değişiklik varsa
                                if ((!Objects.equals(k.getKullanici_adi(), yeniIsim) && !yeniIsim.isEmpty())
                                        || (!Objects.equals(k.getMail(), yeniMail) && !yeniMail.isEmpty())
                                        || (!Objects.equals(k.getTel(), yeniTelefon) && !yeniTelefon.isEmpty())
                                        || (!Objects.equals(k.getParola(), yeniParola) && !yeniParola.isEmpty())) {

                                    AlertDialog dialog2 = new AlertDialog.Builder(requireContext())
                                            .setMessage("Bilgiler güncellenecek. Onaylıyor musunuz?")
                                            .setPositiveButton("Tamam", null)
                                            .setNegativeButton("İptal", (d, w) -> d.dismiss())
                                            .create();



                                    dialog2.setOnShowListener(dlg2 ->
                                            dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v3 -> {
                                                Log.d("aa", "ss");

                                                for (Kullanici k2 : kullanicilar) {
                                                    if (k2.getId() != k.getId()) {
                                                        if (Objects.equals(k2.getKullanici_adi(), yeniIsim)) {
                                                            Toast.makeText(getContext(), "Bu isim kullanılıyor", Toast.LENGTH_SHORT).show();
                                                            return; // Burada bitiriyoruz
                                                        }
                                                        if (Objects.equals(k2.getMail(), yeniMail)) {
                                                            Toast.makeText(getContext(), "Bu mail kullanılıyor", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if (Objects.equals(k2.getTel(), yeniTelefon)) {
                                                            Toast.makeText(getContext(), "Bu telefon kullanılıyor", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    }
                                                }

                                                //mail yenilenmişse onay işlemi
                                                if (!Objects.equals(k.getMail(), yeniMail) && !yeniMail.isEmpty()) {
                                                    // Eğer buraya geldiyse hiçbir çakışma yok, kod gönder
                                                    sharedPreferences.edit().putBoolean("mail_guncelle", true).apply();
                                                    kodGonder(yeniMail);

                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!sharedPreferences.getBoolean("mail_guncelle", false)) {
                                                                Kullanici yeniKullanici = new Kullanici();
                                                                yeniKullanici.setId(k.getId());
                                                                yeniKullanici.setKayit_yontem(k.getKayit_yontem());
                                                                yeniKullanici.setKullanici_adi(k.getKullanici_adi());
                                                                yeniKullanici.setKayit_tarihi(k.getKayit_tarihi());
                                                                yeniKullanici.setMail(yeniMail);
                                                                yeniKullanici.setTel(k.getTel());
                                                                yeniKullanici.setParola(k.getParola());
                                                                yeniKullanici.setFavoriler(k.getFavoriler());

                                                                Call<Kullanici> call = apiService.updateKullanici(yeniKullanici);
                                                                call.enqueue(new Callback<Kullanici>() {
                                                                    @Override
                                                                    public void onResponse(Call<Kullanici> call, Response<Kullanici> response) {
                                                                        if (response.isSuccessful()) {
                                                                            Toast.makeText(getContext(), "Mailiniz güncellendi", Toast.LENGTH_SHORT).show();
                                                                            ekranGuncelle(response.body().getId());
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Güncelleme esnasında hata oluştu", Toast.LENGTH_SHORT).show();
                                                                            try {
                                                                                String errorJson = response.errorBody().string(); // errorBody string olarak al
                                                                                Log.e("API", "Hata: " + response.code() + " - " + errorJson);
                                                                            } catch (Exception e) {
                                                                                Log.e("API", "Hata body okunamadı", e);
                                                                            }
                                                                        }
                                                                    }
                                                                    @Override
                                                                    public void onFailure(Call<Kullanici> call, Throwable t) {
                                                                        Log.e("API", "İstek başarısız: " + t.getMessage());
                                                                    }
                                                                });
                                                            } else {
                                                                handler.postDelayed(this, 100); // 100 ms sonra tekrar kontrol
                                                            }
                                                        }
                                                    }, 100);
                                                }

                                                if (!Objects.equals(k.getTel(), yeniTelefon) && !yeniTelefon.isEmpty()) {
                                                    EditText kod = new EditText(requireContext());
                                                    kod.setInputType(InputType.TYPE_CLASS_TEXT);
                                                    kod.setHint("Kod");

                                                    LinearLayout container_telkod = new LinearLayout(requireContext());
                                                    container_telkod.setOrientation(LinearLayout.VERTICAL);
                                                    container_telkod.setPadding(margin, margin, margin, margin);
                                                    container_telkod.addView(kod);

                                                    AlertDialog dialog3 = new AlertDialog.Builder(requireContext())
                                                            .setMessage("Telefonunuza gelen 4 haneli kodu giriniz")
                                                            .setView(container_telkod)
                                                            .setPositiveButton("Tamam", null)
                                                            .setNegativeButton("İptal", (d, w) -> d.dismiss())
                                                            .create();

                                                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);
                                                    }

                                                    Random random = new Random();
                                                    int tel_kod = random.nextInt(9000)+1000;
                                                    String phoneNumber = yeniTelefon;
                                                    String message = "Yazılım Blog telefon doğrulama kodunuz: "+tel_kod;

                                                    try {
                                                        SmsManager smsManager = SmsManager.getDefault();
                                                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), "SMS gönderilemedi", Toast.LENGTH_SHORT).show();
                                                        e.printStackTrace();
                                                    }

                                                    dialog3.setOnShowListener(dlg3 ->
                                                            dialog3.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v4 -> {

                                                                if (kod.getText().equals(tel_kod)) {
                                                                    Kullanici yeniKullanici = new Kullanici();
                                                                    yeniKullanici.setId(k.getId());
                                                                    yeniKullanici.setKayit_yontem(k.getKayit_yontem());
                                                                    yeniKullanici.setKullanici_adi(k.getKullanici_adi());
                                                                    yeniKullanici.setKayit_tarihi(k.getKayit_tarihi());
                                                                    yeniKullanici.setMail(k.getMail());
                                                                    yeniKullanici.setTel(telefon.getText().toString());
                                                                    yeniKullanici.setParola(k.getParola());
                                                                    yeniKullanici.setFavoriler(k.getFavoriler());

                                                                    Call<Kullanici> call = apiService.updateKullanici(yeniKullanici);
                                                                    call.enqueue(new Callback<Kullanici>() {
                                                                        @Override
                                                                        public void onResponse(Call<Kullanici> call, Response<Kullanici> response) {
                                                                            if (response.isSuccessful()) {
                                                                                Toast.makeText(getContext(), "Telefon numaranız güncellendi", Toast.LENGTH_SHORT).show();
                                                                                ekranGuncelle(response.body().getId());
                                                                            } else {
                                                                                Toast.makeText(getContext(), "Telefon Güncelleme esnasında hata oluştu", Toast.LENGTH_SHORT).show();
                                                                                try {
                                                                                    String errorJson = response.errorBody().string(); // errorBody string olarak al
                                                                                    Log.e("API", "Hata: " + response.code() + " - " + errorJson);
                                                                                } catch (Exception e) {
                                                                                    Log.e("API", "Hata body okunamadı", e);
                                                                                }
                                                                            }
                                                                        }
                                                                        @Override
                                                                        public void onFailure(Call<Kullanici> call, Throwable t) {
                                                                            Log.e("API", "İstek başarısız: " + t.getMessage());
                                                                        }
                                                                    });
                                                                }

                                                            }));dialog3.show();


                                                }



                                                // Dialogları kapat
                                                dialog.dismiss();
                                                dialog2.dismiss();
                                            })
                                    );dialog2.show();

                                } else {
                                    dialog.dismiss();
                                }
                            })
                    );

                    dialog.show(); // sadece 1 kez çağrılıyor


                    break;
                }
            }
        }));

        favs = view.findViewById(R.id.favs);
        favs.setOnClickListener(view -> getKullanicilar(kullanicilar -> {
            for (Kullanici k : kullanicilar) {
                if (k.getId() == sharedPreferences.getInt("userid", -1)) {

                    if (k.getFavoriler() == null || k.getFavoriler().isEmpty()) {
                        Toast.makeText(getContext(), "Henüz favori blog yok", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> favs = Arrays.asList(k.getFavoriler().split(","));

                    Call<List<Blog>> call = apiService.getBloglar();
                    call.enqueue(new Callback<List<Blog>>() {
                        @Override
                        public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Blog> favBloglar = new ArrayList<>();
                                for (Blog blog : response.body()) {
                                    if (favs.contains(String.valueOf(blog.getId()))) {
                                        favBloglar.add(blog);
                                    }
                                }

                                // Dialog ve ListView burada oluşturuluyor, veri hazır
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                LayoutInflater inflater1 = getLayoutInflater();
                                View dialogView = inflater1.inflate(R.layout.filtre_list, null);
                                TextView topText = dialogView.findViewById(R.id.dialog_title);
                                topText.setText("Favorileriniz");

                                builder.setView(dialogView);
                                AlertDialog dialog = builder.create();

                                ListView listView = dialogView.findViewById(R.id.dialog_list);
                                BlogAdapter adapter = new BlogAdapter(getContext(), favBloglar);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener((parent, view1, position, id) -> {
                                    Blog secilenBlog = favBloglar.get(position);
                                    Intent intent = new Intent(getContext(), BlogEkran.class);
                                    intent.putExtra("blog_baslik", secilenBlog.getBaslik());
                                    intent.putExtra("blog_ekleyen", String.valueOf(secilenBlog.getEkleyen_id()));
                                    intent.putExtra("blog_metin", secilenBlog.getMetin());
                                    intent.putExtra("blog_tarih", secilenBlog.getTarih());
                                    intent.putExtra("blog_etiketler", secilenBlog.getEtiketler());
                                    intent.putExtra("blog_id", secilenBlog.getId());
                                    startActivity(intent);
                                });

                                acikDialoglar.add(dialog);
                                dialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Blog>> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getContext(), "Favori bloglar yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break; // Kullanıcı bulundu, döngüden çık
                }
            }
        }));

        ekranGuncelle(userid);

        return view;
    }

    private void ekranGuncelle(int userid) {
        if (userid == -1) {
            secenekler.setVisibility(ViewGroup.VISIBLE);
            hesap.setVisibility(ViewGroup.INVISIBLE);

            editor.putInt("userid", -1);
            editor.apply();
        } else {
            secenekler.setVisibility(ViewGroup.INVISIBLE);
            hesap.setVisibility(ViewGroup.VISIBLE);

            hesap_ismi = view.findViewById(R.id.hesap_isim);
            hesap_total = view.findViewById(R.id.hesap_toplamblog);
            hesap_kaydolma = view.findViewById(R.id.hesap_kaydolmatarihi);

            Call<List<Blog>> call = apiService.getBloglar();
            call.enqueue(new Callback<List<Blog>>() {
                @Override
                public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        blogList.clear();
                        totalBlog = 0;

                        for (Blog blog : response.body()) {
                            if (blog.getEkleyen_id() == sharedPreferences.getInt("userid", -1)) {
                                blogList.add(blog);
                                totalBlog += 1;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Blog>> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            getKullanicilar(kullanicilar -> {
                for (Kullanici k : kullanicilar) {
                    if (k.getId() == sharedPreferences.getInt("userid", -1)) {
                        hesap_ismi.setText(k.getKullanici_adi());
                        hesap_kaydolma.setText(k.getKayit_tarihi()+" tarihinde katıldı");
                        hesap_total.setText(totalBlog == 0 ? "Henüz eklenmiş blog yok" : "Toplamda "+totalBlog+" yayınlanan blog");
                        break;
                    }
                }
            });

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                Blog secilenBlog = blogList.get(position);
                Intent intent = new Intent(getContext(), BlogEkran.class);
                intent.putExtra("blog_baslik", secilenBlog.getBaslik());
                intent.putExtra("blog_ekleyen", String.valueOf(secilenBlog.getEkleyen_id()));
                intent.putExtra("blog_metin", secilenBlog.getMetin());
                intent.putExtra("blog_tarih", secilenBlog.getTarih());
                intent.putExtra("blog_etiketler", secilenBlog.getEtiketler());
                intent.putExtra("blog_id", secilenBlog.getId());
                startActivity(intent);
            });

            editor.putInt("userid", userid);
            editor.apply();
        }
    }

    private void kodGonder(String mail) {
        String to = mail;
        String from = "mert.kadakal1629@gmail.com";
        String host = "smtp.gmail.com"; // Gmail için SMTP host

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Bu önemli! Sertifikayı kabul etmesi için:
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("mert.kadakal1629@gmail.com", "chhj fukq lnic unji");
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("YazılımBlog Doğrulama Kodunuz");
            Random random = new Random();
            int kod = random.nextInt(9000)+1000;
            message.setText("Doğrulama kodunuz: " + kod);

            new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }).start();

            showNumberInputDialog(mail, kod);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void showNumberInputDialog(String mail, int kod) {
        Log.d("kod", String.valueOf(kod));
        // EditText'i oluştur
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // sadece sayı
        input.setHint("Kodunuz");
        input.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(4) }); // maks 9 hane

        // Kenarlara biraz boşluk eklemek için container
        int margin = (int) (16 * getResources().getDisplayMetrics().density);
        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, 0, margin, 0);
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Mailinize gelen 4 haneli kodu girin")
                .setView(container)
                .setPositiveButton("Tamam", null) // click'i onShow'da override edeceğiz
                .setNegativeButton("İptal", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String text = input.getText().toString().trim();
                if (text.isEmpty()) {
                    input.setError("Boş bırakılamaz");
                    return;
                }
                try {
                    int value = Integer.parseInt(text);
                    if (value == kod) {
                        if (sharedPreferences.getBoolean("mail_guncelle", false)) {
                            sharedPreferences.edit().putBoolean("mail_guncelle", false).apply();
                        } else {
                            emailKontrol(mail, result -> {
                                if (result.exists()) {
                                    Toast.makeText(getContext(), "Mail ile giriş yapıldı", Toast.LENGTH_SHORT).show();
                                    ekranGuncelle(result.getId());
                                } else {
                                    LocalDate today = null;
                                    DateTimeFormatter formatter = null;
                                    String formattedDate = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        today = LocalDate.now();
                                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                        formattedDate = today.format(formatter);
                                    }

                                    Kullanici yeniKullanici = new Kullanici();
                                    yeniKullanici.setKayit_yontem("mail");
                                    yeniKullanici.setKullanici_adi(mail);
                                    yeniKullanici.setKayit_tarihi(formattedDate);
                                    yeniKullanici.setMail(mail);
                                    yeniKullanici.setTel(null);
                                    yeniKullanici.setParola(null);
                                    yeniKullanici.setFavoriler(null);

                                    kullaniciEkle(yeniKullanici);
                                }
                            });
                        }

                    } else {
                        Toast.makeText(getContext(), "Kod yanlış girildi", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    input.setError("Geçerli bir sayı girin");
                }
            });
        });

        dialog.show();
    }

    private void kullaniciEkle(Kullanici yeniKullanici) {
        Call<Kullanici> call = apiService.addKullanici(yeniKullanici);
        call.enqueue(new Callback<Kullanici>() {
            @Override
            public void onResponse(Call<Kullanici> call, Response<Kullanici> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Mail ile kaydınız tamamlandı", Toast.LENGTH_SHORT).show();
                    ekranGuncelle(response.body().getId());
                } else {
                    Toast.makeText(getContext(), "Kayıt esnasında hata oluştu", Toast.LENGTH_SHORT).show();
                    try {
                        String errorJson = response.errorBody().string(); // errorBody string olarak al
                        Log.e("API", "Hata: " + response.code() + " - " + errorJson);
                    } catch (Exception e) {
                        Log.e("API", "Hata body okunamadı", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<Kullanici> call, Throwable t) {
                Log.e("API", "İstek başarısız: " + t.getMessage());
            }
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

    private void emailKontrol(String email, Consumer<EmailCheckResult> callback) {
        getKullanicilar(kullanicilar -> {
            boolean exists = false;
            Integer id = null;

            for (Kullanici k : kullanicilar) {
                if (k.getMail() != null && k.getMail().equals(email)) {
                    exists = true;
                    id = k.getId(); // Kullanici modelinde id varsa
                    break;
                }
            }

            callback.accept(new EmailCheckResult(exists, id));
        });
    }

    private void showMailInputDialog() {
        // EditText'i oluştur
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS); // sadece sayı
        input.setHint("xxx@xxx.com");

        // Kenarlara biraz boşluk eklemek için container
        int margin = (int) (16 * getResources().getDisplayMetrics().density);
        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, 0, margin, 0);
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Mail adresinizi girin")
                .setView(container)
                .setPositiveButton("Tamam", null) // click'i onShow'da override edeceğiz
                .setNegativeButton("İptal", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String mail = input.getText().toString().trim();
                if (mail.isEmpty()) {
                    input.setError("Boş bırakılamaz");
                    return;
                }
                try {
                    kodGonder(mail);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    input.setError("Geçerli bir sayı girin");
                }
            });
        });

        dialog.show();
    }

    private void kaydolDialog(String tarz) {
        // EditText'leri oluştur
        EditText input1 = new EditText(requireContext());
        input1.setInputType(InputType.TYPE_CLASS_TEXT);
        input1.setHint("İsminiz");

        EditText input2 = new EditText(requireContext());
        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input2.setHint("Şifreniz");

        int margin = (int) (16 * getResources().getDisplayMetrics().density);

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL); // alt alta olacak
        container.setPadding(margin, margin, margin, margin);

        container.addView(input1);
        container.addView(input2);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Hesap ismi ve şifrenizi girin")
                .setView(container)
                .setPositiveButton("Tamam", null) // click'i onShow'da override edeceğiz
                .setNegativeButton("İptal", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String isim = input1.getText().toString().trim();
            String parola = input2.getText().toString().trim();
            if (isim.isEmpty() || parola.isEmpty()) {
                Toast.makeText(getContext(), "Boş alan bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (tarz.equals("kaydol")) {
                    getKullanicilar(kullanicilar -> {
                        boolean ok = true;
                        for (Kullanici k : kullanicilar) {
                            if (k.getKullanici_adi().equals(isim)) {
                                Toast.makeText(getContext(), "Bu kullanıcı adı kullanılıyor", Toast.LENGTH_SHORT).show();
                                ok = false;
                            }
                        }
                        if (ok) {
                            LocalDate today = null;
                            DateTimeFormatter formatter = null;
                            String formattedDate = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                today = LocalDate.now();
                                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                formattedDate = today.format(formatter);
                            }

                            Kullanici yeniKullanici = new Kullanici();
                            yeniKullanici.setKayit_yontem("normal");
                            yeniKullanici.setKullanici_adi(isim);
                            yeniKullanici.setKayit_tarihi(formattedDate);
                            yeniKullanici.setMail(null);
                            yeniKullanici.setTel(null);
                            yeniKullanici.setParola(parola);
                            yeniKullanici.setFavoriler(null);

                            kullaniciEkle(yeniKullanici);
                        }
                    });
                } else {
                    getKullanicilar(kullanicilar -> {
                        boolean find = false;
                        for (Kullanici k : kullanicilar) {
                            Toast.makeText(getContext(), k.getKullanici_adi(), Toast.LENGTH_SHORT).show();
                            if (k.getKullanici_adi().equals(isim)) {
                                if (k.getParola().equals(parola)) {
                                    Toast.makeText(getContext(), "Giriş yapıldı", Toast.LENGTH_SHORT).show();
                                    editor.putInt("userid", k.getId());
                                    ekranGuncelle(k.getId());
                                } else {
                                    Toast.makeText(getContext(), "Parola yanlış girildi", Toast.LENGTH_SHORT).show();
                                }
                                find = true;
                            }
                        }
                        if (!find) {
                            Toast.makeText(getContext(), "Kullanıcı ismi bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Hata oluştu", Toast.LENGTH_SHORT).show();
            }
        }));

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (acikDialoglar.size() > 0) {
            for (AlertDialog d : acikDialoglar) {
                if (d.isShowing()) {
                    d.dismiss();
                }
            }
            favs.performClick();
        }


        if (sharedPreferences.getBoolean("blog_silindi", false)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("✅ Blog başarıyla silindi! ✅")
                    .setPositiveButton("Tamam", null)
                    .show();

            sharedPreferences.edit()
                    .putBoolean("blog_silindi", false)
                    .apply();

            ekranGuncelle(sharedPreferences.getInt("userid", -1));
        }

        if (sharedPreferences.getBoolean("blog_guncellendi_hesap", false)) {
            sharedPreferences.edit()
                    .putBoolean("blog_guncellendi_hesap", false)
                    .apply();

            ekranGuncelle(sharedPreferences.getInt("userid", -1));
        }
    }
}