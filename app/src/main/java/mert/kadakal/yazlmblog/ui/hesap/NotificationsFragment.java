package mert.kadakal.yazlmblog.ui.hesap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import mert.kadakal.yazlmblog.R;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Kullanici;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class NotificationsFragment extends Fragment {
    View view;
    Button mail;
    Button kaydol;
    Button giris;
    Button cikis;
    LinearLayout secenekler;
    LinearLayout hesap;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Call<List<Kullanici>> call = apiService.getKullanicilar();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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

        cikis = view.findViewById(R.id.cikis);
        cikis.setOnClickListener(view12 -> {
            editor.putInt("userid", -1);
            editor.apply();
            ekranGuncelle(-1);
        });
        mail = view.findViewById(R.id.mail);
        mail.setOnClickListener(view1 -> showMailInputDialog());

        kaydol = view.findViewById(R.id.kaydol);
        kaydol.setOnClickListener(view1 -> { kaydolDialog("kaydol"); });

        giris = view.findViewById(R.id.giris);
        giris.setOnClickListener(view1 -> { kaydolDialog("giriş"); });

        secenekler = view.findViewById(R.id.secenekler_layout);
        hesap = view.findViewById(R.id.hesap_layout);

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
                    e.printStackTrace();
                }
            }).start();

            showNumberInputDialog(mail, kod);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void showNumberInputDialog(String mail, int kod) {
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

                                kullaniciEkle(yeniKullanici);
                            }
                        });
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
                if (k.getMail().equals(email)) {
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

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String isim = input1.getText().toString().trim();
                String parola = input2.getText().toString().trim();
                if (isim.isEmpty() || parola.isEmpty()) {
                    Toast.makeText(getContext(), "Boş alan bırakılamaz", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (tarz.equals("kaydol")) {
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

                        kullaniciEkle(yeniKullanici);
                    } else {
                        getKullanicilar(kullanicilar -> {
                            boolean find = false;
                            for (Kullanici k : kullanicilar) {
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
            });
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}