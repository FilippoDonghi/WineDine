package it.unimib.winedine.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.unimib.winedine.R;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.repository.wine.WinesRepository;


public class BottleRecyclerAdapter extends RecyclerView.Adapter<BottleRecyclerAdapter.ViewHolder> {
    public static final String TAG = BottleRecyclerAdapter.class.getName();

    private String selectedWine;

    public interface OnItemClickListener {
        void onBottleItemClick(Bottle bottle, String selectedWine);
        void onFavoriteButtonClick(int position);
        }

    private int layout;
    private List<Bottle> bottleList;
    private Context context;
    private final OnItemClickListener onItemClickListener;
    private boolean heartVisible;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewTitle;
        private final TextView textViewScore;
        private final TextView textViewPrice;
        private final ImageView imageView;
        private final CheckBox favoriteCheckbox;


        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            imageView = view.findViewById(R.id.imageView);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);

            if (favoriteCheckbox != null) {
                favoriteCheckbox.setOnClickListener(this);}

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

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }

        @Override
    public void onClick(View v) {

            if (v.getId() == R.id.favoriteButton) {
                //setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonClick(getAdapterPosition());
            } else {
                onItemClickListener.onBottleItemClick(bottleList.get(getAdapterPosition()), selectedWine);
            }
        }
    }

    public BottleRecyclerAdapter(int layout, List<Bottle> bottleList, String selectedWine, boolean heartVisible, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.bottleList = bottleList;
        this.onItemClickListener = onItemClickListener;
        this.selectedWine = selectedWine; // Inizializza selectedWine
        this.heartVisible = heartVisible;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_wine, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(bottleList.get(position).getTitle());
        viewHolder.getTextViewScore().setText(bottleList.get(position).getScore());
        viewHolder.getTextViewPrice().setText(bottleList.get(position).getPrice());


        if (viewHolder.getFavoriteCheckbox() != null) {
            // Set the current state based on the bottle's liked status
            viewHolder.getFavoriteCheckbox().setChecked(bottleList.get(position).getLiked());

            // Make it visible or invisible based on heartVisible flag
            viewHolder.getFavoriteCheckbox().setVisibility(heartVisible ? View.VISIBLE : View.INVISIBLE);
        }




/*
        if (favoriteCheckbox != null) {
           // Log.d("onBindViewHolder: favoriteCheckbox is not null");
        viewHolder.getFavoriteCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    WineRoomDatabase.getDatabase(viewHolder.getTextViewTitle().getContext()).
                            wineDao().insert(bottleList.get(position));
                } else {
                    WineRoomDatabase.getDatabase(viewHolder.getTextViewTitle().getContext()).
                            wineDao().delete(bottleList.get(position));
                }
            }
        });}*/


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
