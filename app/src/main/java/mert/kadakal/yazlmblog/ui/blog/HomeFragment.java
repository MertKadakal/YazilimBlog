package mert.kadakal.yazlmblog.ui.blog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. ListView ve Adapter tanımı
        ListView listView = view.findViewById(R.id.listView); // XML'deki ListView id'si
        List<String> metinList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.blog_list_item,
                R.id.text1,
                metinList
        );
        listView.setAdapter(adapter);

// 2. API çağrısı
        Call<List<Blog>> call = apiService.getBloglar();
        call.enqueue(new Callback<List<Blog>>() {
            @Override
            public void onResponse(Call<List<Blog>> call, Response<List<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    for (Blog k : response.body()) {
                        metinList.add("\n\n\n\n"+k.getMetin()); // her Kullanici'nun metin alanını ekle
                        Log.d("aa", k.getMetin());
                    }


                    adapter.notifyDataSetChanged(); // listeyi güncelle
                }
            }

            @Override
            public void onFailure(Call<List<Blog>> call, Throwable t) {
                t.printStackTrace();
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}