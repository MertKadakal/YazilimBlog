package mert.kadakal.yazlmblog.ui.hesap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import mert.kadakal.yazlmblog.R;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Kullanici;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Properties;

public class NotificationsFragment extends Fragment {
    Button mail;
    Button github;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Call<List<Kullanici>> call = apiService.getKullanicilar();

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        mail = view.findViewById(R.id.mail);
        mail.setOnClickListener(view1 -> showMailInputDialog());

        github = view.findViewById(R.id.github);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.enqueue(new Callback<List<Kullanici>>() {
                    @Override
                    public void onResponse(Call<List<Kullanici>> call, Response<List<Kullanici>> response) {
                        if (response.isSuccessful()) {
                            List<Kullanici> kullanicilar = response.body();
                            Toast.makeText(getContext(), kullanicilar.get(0).getKayit_tarihi(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Kullanici>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        return view;
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

                        Call<Kullanici> call = apiService.addKullanici(yeniKullanici);
                        call.enqueue(new retrofit2.Callback<Kullanici>() {
                            @Override
                            public void onResponse(Call<Kullanici> call, retrofit2.Response<Kullanici> response) {
                                if (response.isSuccessful()) {
                                    Log.d("API", "Başarılı: " + response.body().getKullanici_adi());
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
                            public void onFailure(Call<Kullanici> call, Throwable t) {
                                Log.e("API", "İstek başarısız: " + t.getMessage());
                            }
                        });
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    input.setError("Geçerli bir sayı girin");
                }
            });
        });

        dialog.show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}