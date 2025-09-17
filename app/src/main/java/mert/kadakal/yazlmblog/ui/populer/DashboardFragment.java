package mert.kadakal.yazlmblog.ui.populer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Yorum;
import mert.kadakal.yazlmblog.ui.blog.BlogAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private View view;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    SharedPreferences sharedPreferences;
    ListView listView;
    ArrayList<Blog> blogList;
    BlogAdapter adapter;
    Map<Blog, Double> blogAvgPuanlar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        listView = view.findViewById(R.id.listView); // XML'deki ListView id'si
        blogAvgPuanlar = new HashMap<>();
        blogList = new ArrayList<>();
        adapter = new BlogAdapter(getContext(), blogList);
        listView.setAdapter(adapter);

        getBloglar(blogs -> {
            blogAvgPuanlar.clear();
            blogList.clear();

            if (blogs.isEmpty()) return;

            final int[] kalan = {blogs.size()}; // Kaç blog kaldığını saymak için

            for (Blog blog : blogs) {
                String girilenTarihStr = blog.getTarih();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate girilenTarih = LocalDate.parse(girilenTarihStr, formatter);
                    long gunFarki = ChronoUnit.DAYS.between(girilenTarih, LocalDate.now());

                    if (gunFarki <= 30) {
                        AtomicReference<Double> total = new AtomicReference<>((double) 0);
                        AtomicReference<Double> puanTotal = new AtomicReference<>((double) 0);

                        getYorumlar(yorumlar -> {
                            for (Yorum yorum : yorumlar) {
                                if (yorum.getEklenen_blog() == blog.getId()) {
                                    puanTotal.updateAndGet(v -> v + yorum.getPuan());
                                    total.updateAndGet(v -> v + 1);
                                }
                            }
                            double avg = (total.get() > 0) ? puanTotal.get() / total.get() : 0.0;
                            blogAvgPuanlar.put(blog, avg);

                            kalan[0]--; // bu blog için yorumlar işlendi
                            if (kalan[0] == 0 || blogAvgPuanlar.size() == 10) { // tüm bloglar tamamlandı veya 10 blog sınırına gelindi
                                blogList.clear();
                                blogAvgPuanlar.entrySet().stream()
                                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                                        .forEach(entry -> blogList.add(entry.getKey()));

                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        kalan[0]--;
                        if (kalan[0] == 0) {
                            blogList.clear();
                            blogAvgPuanlar.entrySet().stream()
                                    .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                                    .forEach(entry -> blogList.add(entry.getKey()));

                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "ChronoUnit SDK Error", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return view;
    }

    private void getBloglar(Consumer<List<Blog>> callback) {
        Call<List<Blog>> call12 = apiService.getBloglar();
        call12.enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call12, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(new ArrayList<>(response.body()));
                } else {
                    callback.accept(new ArrayList<>()); // boş liste dön
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call12, Throwable t) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}