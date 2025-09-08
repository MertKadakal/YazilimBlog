package mert.kadakal.yazlmblog.ui.blog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.api.Yorum;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogAdapter extends ArrayAdapter<Blog> {

    private Context context;
    private List<Blog> blogList;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public BlogAdapter(Context context, List<Blog> blogList) {
        super(context, 0, blogList);
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.blog_list_item, parent, false);
        }

        Blog blog = blogList.get(position);

        TextView textBaslik = convertView.findViewById(R.id.textBaslik);
        TextView textMetin = convertView.findViewById(R.id.textMetin);
        TextView textTarih = convertView.findViewById(R.id.textTarih);
        TextView textEtiketler = convertView.findViewById(R.id.textEtiketler);

        textBaslik.setText(blog.getBaslik());
        textMetin.setText(blog.getMetin().substring(0,300)+"...");

        ArrayList<String> etiketler = new ArrayList<>(List.of("Yazılım Dilleri", "Oyun Geliştirme", "Web Geliştirme", "Eğitim"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blog.getEtiketler().split(",").length; i++) {
            sb.append(blog.getEtiketler().split(",")[i].equals("1") ? etiketler.get(i) + ", " : "");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        textEtiketler.setText(sb.toString().isEmpty() ? "Etiket girilmemiş" : sb.toString());

        getYorumlar(yorumlar -> {
            double total = 0;
            double count = 0;
            for (Yorum yorum : yorumlar) {
                if (yorum.getEklenen_blog() == blog.getId()) {
                    count++;
                    total += yorum.getPuan();
                }
            }

            double avg = count == 0 ? 0 : total / count;

            double finalCount = count;
            getKullanicilar(kullanicilar -> {
                for (Kullanici k : kullanicilar) {
                    if (k.getId() == blog.getEkleyen_id()) {
                        String ek = finalCount == 0 ? "\nHenüz puanlanmamış" : "\nOrtalama Puan: " + avg + "/10";
                        textTarih.setText("Tarih: " + blog.getTarih() + "\nYazar: " + k.getKullanici_adi() + ek);
                    }
                }
            });
        });



        return convertView;
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

    private void getYorumlar(Consumer<List<Yorum>> callback) {
        Call<List<Yorum>> call = apiService.getYorumlar(); // her seferinde yeni Call oluştur
        call.enqueue(new Callback<List<Yorum>>() {
            @Override
            public void onResponse(Call<List<Yorum>> call, Response<List<Yorum>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(new ArrayList<>(response.body()));
                } else {
                    callback.accept(new ArrayList<>()); // boş liste dön
                }
            }

            @Override
            public void onFailure(Call<List<Yorum>> call, Throwable t) {
                t.printStackTrace();
                callback.accept(new ArrayList<>()); // hata olursa boş liste dön
            }
        });
    }
}
