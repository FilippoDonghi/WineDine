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
        private final TextView textViewViewAverageRating;
        private final TextView textViewViewRatingCount;


        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewPrice = view.findViewById(R.id.textViewPrice);
            textViewViewAverageRating = view.findViewById(R.id.textViewAverageRating);
            textViewViewRatingCount = view.findViewById(R.id.textViewRatingCount);
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

        public TextView getTextViewViewAverageRating() {
            return textViewViewAverageRating;
        }

        public TextView getTextViewViewRatingCount() {
            return textViewViewRatingCount;
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.favoriteButton) {
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
        this.selectedWine = selectedWine;
        this.heartVisible = heartVisible;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_bottle, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(bottleList.get(position).getTitle());

        viewHolder.getTextViewPrice().setText(bottleList.get(position).getPrice());
        viewHolder.getTextViewViewAverageRating().setText(bottleList.get(position).getAverageRating());
        viewHolder.getTextViewViewRatingCount().setText(bottleList.get(position).getRatingCount());

        try {
            String originalRating = bottleList.get(position).getAverageRating();
            String truncatedRating = originalRating.length() > 4 ?
                    originalRating.substring(0, 4) : originalRating;
            double ratingValue = Double.parseDouble(truncatedRating);
            double scaledRating = ratingValue * 5;
            String formattedRating = String.format("%.1f ★", scaledRating);
            viewHolder.getTextViewViewAverageRating().setText(formattedRating);
        } catch (NumberFormatException e) {
            viewHolder.getTextViewViewAverageRating().setText("0.0 ★"); // Valore di default
        }


        if (viewHolder.getFavoriteCheckbox() != null) {
            viewHolder.getFavoriteCheckbox().setChecked(bottleList.get(position).getLiked());

            //imposta la visibilità della CheckBox in base al valore di heartVisible.
            //se heartVisible è true, la CheckBox sarà visibile (View.VISIBLE).
            //Se heartVisible è false, la CheckBox sarà invisibile (View.INVISIBLE)
            viewHolder.getFavoriteCheckbox().setVisibility(heartVisible ? View.VISIBLE : View.INVISIBLE);
        }

        context = viewHolder.getImageView().getContext();
        Glide.with(context)
                .load(bottleList.get(position).getImageUrl())
                .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_onSecondaryContainer)))
                .into(viewHolder.getImageView());

    }

    @Override
    public int getItemCount() {
        return bottleList.size();
    }

    public void setBottles(List<Bottle> bottles) {
        this.bottleList.clear();
        this.bottleList.addAll(bottles);
        notifyDataSetChanged();
    }
}
