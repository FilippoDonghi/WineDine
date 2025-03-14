package it.unimib.winedine.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;


public class BottleRecyclerAdapter extends RecyclerView.Adapter<BottleRecyclerAdapter.ViewHolder> {
    private String selectedWine;
    public interface OnItemClickListener {
        void onBottleItemClick(Bottle bottle, String selectedWine);
        }

    private int layout;
    private List<Bottle> bottleList;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewTitle;
        private final TextView textViewScore;
        private final TextView textViewPrice;
        private final ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            imageView = view.findViewById(R.id.imageView);
            view.setOnClickListener(this);
        }


        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextViewScore() {
            return textViewScore;
        }

        public TextView getTextViewPrice() {
            return textViewPrice;
        }

        public ImageView getImageView() {
            return imageView;
        }


    @Override
    public void onClick(View v) {
        onItemClickListener.onBottleItemClick(bottleList.get(getAdapterPosition()), selectedWine);
        }
    }


    public BottleRecyclerAdapter(int layout, List<Bottle> bottleList, OnItemClickListener onItemClickListener, String selectedWine) {
        this.layout = layout;
        this.bottleList = bottleList;
        this.onItemClickListener = onItemClickListener;
        this.selectedWine = selectedWine; // Inizializza selectedWine
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(bottleList.get(position).getTitle());
        viewHolder.getTextViewScore().setText(bottleList.get(position).getScore());
        viewHolder.getTextViewPrice().setText(bottleList.get(position).getPrice());



        context = viewHolder.getImageView().getContext();
        Glide.with(context)
                .load(bottleList.get(position).getImageUrl())
                .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_errorContainer_highContrast)))
                .into(viewHolder.getImageView());
    }

    @Override
    public int getItemCount() {
        return bottleList.size();
    }
}
