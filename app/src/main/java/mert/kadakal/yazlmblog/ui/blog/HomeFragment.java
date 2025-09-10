package mert.kadakal.yazlmblog.ui.blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import mert.kadakal.yazlmblog.R;
import mert.kadakal.yazlmblog.api.ApiClient;
import mert.kadakal.yazlmblog.api.ApiService;
import mert.kadakal.yazlmblog.api.Blog;
import mert.kadakal.yazlmblog.api.Kullanici;
import mert.kadakal.yazlmblog.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private View view;
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Button blog_ekle;
    List<Blog> blogList;
    BlogAdapter adapter;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        blog_ekle = view.findViewById(R.id.blog_ekle);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        ListView listView = view.findViewById(R.id.listView); // XML'deki ListView id'si
        blogList = new ArrayList<>();
        adapter = new BlogAdapter(getContext(), blogList);
        listView.setAdapter(adapter);

        Call<List<Blog>> call = apiService.getBloglar();
        call.enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                Log.d("api", "bağlanıyor");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("api", "bağlandı - " + response.body().size());
                    blogList.clear();
                    blogList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                Log.d("api", String.valueOf(blogList.size()));
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                t.printStackTrace();
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

        blog_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BlogEkle.class);
                intent.putExtra("blog_ekle_duzenle", "ekle");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferences.getBoolean("blog_eklendi", false)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("✅ Blog başarıyla eklendi! ✅")
                    .setPositiveButton("Tamam", null)
                    .show();

            sharedPreferences.edit()
                    .putBoolean("blog_eklendi", false)
                    .apply();
        }

        if (sharedPreferences.getBoolean("blog_silindi", false)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("✅ Blog başarıyla silindi! ✅")
                    .setPositiveButton("Tamam", null)
                    .show();

            sharedPreferences.edit()
                    .putBoolean("blog_silindi", false)
                    .apply();
        }

        Call<List<Blog>> call = apiService.getBloglar();
        call.enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    blogList.clear();
                    blogList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}