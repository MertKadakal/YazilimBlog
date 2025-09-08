package mert.kadakal.yazlmblog.ui.blog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.api.Yorum;
import mert.kadakal.yazlmblog.ui.hesap.EmailCheckResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YorumAdapter extends ArrayAdapter<Yorum> {

    private Context context;
    private List<Yorum> yorumList;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public YorumAdapter(Context context, List<Yorum> yorumList) {
        super(context, 0, yorumList);
        this.context = context;
        this.yorumList = yorumList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.yorum_list_item, parent, false);
        }

        Yorum yorum = yorumList.get(position);

        TextView ekleyen_isim = convertView.findViewById(R.id.ekleyen_isim);
        TextView tarih = convertView.findViewById(R.id.ekleme_tarih);
        TextView icerik = convertView.findViewById(R.id.icerik);
        TextView puan = convertView.findViewById(R.id.puan);

        getKullanicilar(kullanicilar -> {
            for (Kullanici k : kullanicilar) {
                if (k.getId() == yorum.getEkleyen_id()) {
                    ekleyen_isim.setText(k.getKullanici_adi());
                    break;
                }
            }
        });
        tarih.setText(yorum.getTarih());
        icerik.setText(yorum.getIcerik());
        puan.setText(yorum.getPuan() + "/10");
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
}
