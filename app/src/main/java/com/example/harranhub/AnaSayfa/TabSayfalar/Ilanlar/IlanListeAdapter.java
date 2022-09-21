package com.example.harranhub.AnaSayfa.TabSayfalar.Ilanlar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harranhub.Ilan;
import com.example.harranhub.R;

import java.util.LinkedList;

public class IlanListeAdapter extends RecyclerView.Adapter<IlanListeAdapter.IlanViewHolder>{
    private final LinkedList<Ilan> ilanListesi;
    private LayoutInflater inflater;

    @NonNull
    @Override
    public IlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ilanTextView = inflater.inflate(R.layout.ilanlistesi_ilan, parent, false);

        return new IlanViewHolder(ilanTextView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IlanViewHolder holder, int position) {
        Ilan mevcut = ilanListesi.get(position);
        holder.ilanTextView.setText(mevcut.getBaslik());
        holder.ilanTextView.setTextSize(25);
    }

    public void clear()
    {
        int size = ilanListesi.size();
        if (size > 0)
        {
            for (int i = 0; i < size; i++)
            {
                ilanListesi.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemCount() {
        return ilanListesi.size();
    }

    class IlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView ilanTextView;
        final IlanListeAdapter adapter;

        public IlanViewHolder(@NonNull View itemView, IlanListeAdapter adapter) {
            super(itemView);
            ilanTextView = itemView.findViewById(R.id.ilanBaslik);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            adapter.notifyDataSetChanged();

            arabirim.ilanGoster(ilanListesi.get(position));
        }
    }

    IlanArabirimi arabirim;

    public IlanListeAdapter(Ilanlar ctx, LinkedList<Ilan> ilanListesi)
    {
        inflater = LayoutInflater.from(ctx.getContext());

        arabirim = (Ilanlar)ctx;

        this.ilanListesi = ilanListesi;
    }
}
