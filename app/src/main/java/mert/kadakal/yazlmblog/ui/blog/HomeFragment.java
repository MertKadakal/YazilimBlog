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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    Button filtrele;
    Button ara;
    List<Blog> blogList;
    BlogAdapter adapter;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        blog_ekle = view.findViewById(R.id.blog_ekle);
        filtrele = view.findViewById(R.id.filtrele);
        ara = view.findViewById(R.id.metin_ara);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        ListView listView = view.findViewById(R.id.listView); // XML'deki ListView id'si
        blogList = new ArrayList<>();
        adapter = new BlogAdapter(getContext(), blogList);
        listView.setAdapter(adapter);

        getBloglar(blogs -> {
            blogList.clear();
            blogList.addAll(blogs);
            adapter.notifyDataSetChanged();
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

        if (sharedPreferences.getInt("userid", -1) <= 0) blog_ekle.setVisibility(View.GONE);
        blog_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BlogEkle.class);
                intent.putExtra("blog_ekle_duzenle", "ekle");
                startActivity(intent);
            }
        });

        ArrayList<String> etiketlerList = new ArrayList<>(List.of(
                "Yazılım Dilleri",
                "Oyun Geliştirme",
                "Web Geliştirme",
                "Eğitim",
                "Java",
                "Python",
                "C++",
                "C#",
                "JavaScript",
                "Kotlin",
                "Swift",
                "Go",
                "Rust",
                "PHP",
                "Ruby",
                "TypeScript",
                "Dart",
                "R",
                "Scala",
                "Perl",
                "HTML & CSS",
                "Veri Yapıları",
                "Algoritmalar",
                "Yapay Zeka",
                "Makine Öğrenmesi",
                "Derin Öğrenme",
                "Veri Bilimi",
                "Siber Güvenlik",
                "Blockchain",
                "Mobil Geliştirme",
                "Backend Geliştirme",
                "Frontend Geliştirme",
                "Fullstack Geliştirme",
                "Bulut Bilişim",
                "DevOps",
                "Veritabanları",
                "SQL",
                "NoSQL",
                "API Geliştirme",
                "Mikroservisler",
                "Agile & Scrum",
                "Yazılım Testi",
                "Unit Test",
                "Clean Code",
                "Design Patterns",
                "OOP",
                "Functional Programming",
                "Versiyon Kontrol (Git)",
                "Linux & Sistem Programlama"
        ));
        filtrele.setOnClickListener(view -> {
            LayoutInflater inflater1 = LayoutInflater.from(getContext());
            View popupView = inflater1.inflate(R.layout.pop_op_blog, null);

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(popupView)
                    .create();

            dialog.show();

            Button filtrele = popupView.findViewById(R.id.etiketleri_onayla);

            RecyclerView recyclerEtiketler = popupView.findViewById(R.id.recycler_etiketler);
            recyclerEtiketler.setLayoutManager(new LinearLayoutManager(getContext()));

            Map<String, Boolean> secimDurumuGlobal = new LinkedHashMap<>();
            for (int i = 0; i < 49; i++) {
                secimDurumuGlobal.put(etiketlerList.get(i), false);
            }

            EtiketAdapter adapter = new EtiketAdapter(etiketlerList, secimDurumuGlobal);
            recyclerEtiketler.setAdapter(adapter);

            filtrele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder fltr = new StringBuilder("");
                    for (Boolean baslik :  secimDurumuGlobal.values()) {
                        fltr.append(baslik ? "1," : "0,");
                    }
                    fltr.deleteCharAt(fltr.length() - 1);

                    List<Blog> filtreBloglar = new ArrayList<>();
                    Call<List<Blog>> call1 = apiService.getBloglar();
                    call1.enqueue(new Callback<List<Blog>>() {
                        @Override
                        public void onResponse(Call<List<Blog>> call1, Response<List<Blog>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                for (Blog blog : response.body()) {
                                    String[] blogEtkt = blog.getEtiketler().split(",");
                                    String[] filtre = fltr.toString().split(",");

                                    //filtreleme
                                    for (int i = 0; i < blogEtkt.length; i++) {
                                        int bit1 = Integer.parseInt(blogEtkt[i]);
                                        int bit2 = Integer.parseInt(filtre[i]);
                                        int andResult = bit1 & bit2; // bitwise AND

                                        if (andResult == 1) {
                                            filtreBloglar.add(blog);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            LayoutInflater inflater1 = getLayoutInflater();
                                            View dialogView = inflater1.inflate(R.layout.filtre_list, null);

                                            builder.setView(dialogView);
                                            AlertDialog dialog = builder.create();

                                            ListView listView1 = dialogView.findViewById(R.id.dialog_list);
                                            BlogAdapter adapter = new BlogAdapter(getContext(), filtreBloglar);
                                            listView1.setAdapter(adapter);

                                            listView1.setOnItemClickListener((parent, view1, position, id) -> {
                                                Blog secilenBlog = filtreBloglar.get(position);
                                                Intent intent = new Intent(getContext(), BlogEkran.class);
                                                intent.putExtra("blog_baslik", secilenBlog.getBaslik());
                                                intent.putExtra("blog_ekleyen", String.valueOf(secilenBlog.getEkleyen_id()));
                                                intent.putExtra("blog_metin", secilenBlog.getMetin());
                                                intent.putExtra("blog_tarih", secilenBlog.getTarih());
                                                intent.putExtra("blog_etiketler", secilenBlog.getEtiketler());
                                                intent.putExtra("blog_id", secilenBlog.getId());
                                                startActivity(intent);
                                            });

                                            dialog.show();

                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Blog>> call1, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                    dialog.dismiss();
                }
            });
        });

        ara.setOnClickListener(view -> {
            EditText input = new EditText(getContext());
            input.setHint("Aramak istediğiniz metni giriniz");
            input.setBackgroundResource(android.R.drawable.edit_text);
            input.setPadding(30, 20, 30, 40);

            LinearLayout containerAra = new LinearLayout(getContext());
            containerAra.setOrientation(LinearLayout.VERTICAL);
            containerAra.setPadding(40,50,40,40);

            containerAra.addView(input);
            new AlertDialog.Builder(getContext())
                    .setTitle("Arama")
                    .setView(containerAra)
                    .setPositiveButton("Ara", (dialog1, whichButton) -> {
                        getBloglar(blogs -> {
                            List<Blog> araBloglar = new ArrayList<>();
                            for (Blog blog : blogs) {
                                if (blog.getMetin().contains(input.getText().toString()) ||
                                        blog.getBaslik().contains(input.getText().toString())) {
                                    araBloglar.add(blog);
                                }
                            }

                            if (araBloglar.isEmpty()) {
                                Toast.makeText(getContext(), "Hiçbir arama sonucu bulunamadı!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            LayoutInflater inflater1 = getLayoutInflater();
                            View dialogView = inflater1.inflate(R.layout.filtre_list, null);
                            TextView topText = dialogView.findViewById(R.id.dialog_title);
                            topText.setText("Arama Sonuçları");

                            builder.setView(dialogView);
                            builder.setNegativeButton("Geri", null);
                            AlertDialog dialog = builder.create();

                            ListView listViewara = dialogView.findViewById(R.id.dialog_list);
                            BlogAdapter adapter = new BlogAdapter(getContext(), araBloglar);
                            listViewara.setAdapter(adapter);

                            listViewara.setOnItemClickListener((parent, view1, position, id) -> {
                                Blog secilenBlog = araBloglar.get(position);
                                Intent intent = new Intent(getContext(), BlogEkran.class);
                                intent.putExtra("blog_baslik", secilenBlog.getBaslik());
                                intent.putExtra("blog_ekleyen", String.valueOf(secilenBlog.getEkleyen_id()));
                                intent.putExtra("blog_metin", secilenBlog.getMetin());
                                intent.putExtra("blog_tarih", secilenBlog.getTarih());
                                intent.putExtra("blog_etiketler", secilenBlog.getEtiketler());
                                intent.putExtra("blog_id", secilenBlog.getId());
                                startActivity(intent);
                            });

                            dialog.show();
                        });
                    })
                    .setNegativeButton("İptal", null)
                    .show();
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

        getBloglar(blogs -> {
            blogList.clear();
            blogList.addAll(blogs);
            adapter.notifyDataSetChanged();
        });
    }

}