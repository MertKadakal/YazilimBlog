package mert.kadakal.yazlmblog.ui.blog;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import mert.kadakal.yazlmblog.R;

public class EtiketAdapter extends RecyclerView.Adapter<EtiketAdapter.ViewHolder> {

    private List<String> etiketler;
    private Map<String, Boolean> secimDurumu;

    public EtiketAdapter(List<String> etiketler, Map<String, Boolean> secimDurumu) {
        this.etiketler = etiketler;
        this.secimDurumu = secimDurumu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etiket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String etiket = etiketler.get(position);
        holder.textEtiket.setText(etiket);

        // Seçim durumuna göre renk ayarla
        if (Boolean.TRUE.equals(secimDurumu.get(etiket))) {
            holder.cardEtiket.setCardBackgroundColor(Color.parseColor("#4CAF50")); // yeşil
        } else {
            holder.cardEtiket.setCardBackgroundColor(Color.parseColor("#F2F2F2")); // yeşil
        }

        holder.cardEtiket.setOnClickListener(v -> {
            boolean secili = secimDurumu.getOrDefault(etiket, false);
            secimDurumu.put(etiket, !secili);

            notifyItemChanged(position); // sadece bu item güncellensin
        });
    }

    @Override
    public int getItemCount() {
        return etiketler.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardEtiket;
        TextView textEtiket;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEtiket = itemView.findViewById(R.id.card_etiket);
            textEtiket = itemView.findViewById(R.id.text_etiket);
        }
    }
}
