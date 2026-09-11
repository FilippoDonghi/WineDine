package it.unimib.winedine.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Locale;
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
        private final TextView textViewAverageRating;
        private final TextView textViewRatingCount;
        private final RatingBar ratingBar;
        private final TextView textPrice;
        private final ImageView imageView;
        private final CheckBox favoriteCheckbox;


        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewAverageRating = view.findViewById(R.id.textViewAverageRating);
            textViewRatingCount = view.findViewById(R.id.textViewRatingCount);
            ratingBar = view.findViewById(R.id.rating_bar);
            textPrice = view.findViewById(R.id.priceText);
            imageView = view.findViewById(R.id.imageView);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);

            if (favoriteCheckbox != null) {
                favoriteCheckbox.setOnClickListener(this);}
            view.setOnClickListener(this);
    }


        public TextView getTextViewTitle() {
            return textViewTitle;
        }


        public ImageView getImageView() {
            return imageView;
        }

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }

       public TextView getViewPrice() {
            return textPrice;
       }

        public TextView getTextViewAverageRating() {
            return textViewAverageRating;
        }

        public TextView getTextViewRatingCount() {
            return textViewRatingCount;
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
        Bottle bottle = bottleList.get(position);
        viewHolder.getTextViewTitle().setText(bottle.getTitle());
        int ratingCount = (int) Double.parseDouble(bottle.getRatingCount());
        viewHolder.getTextViewRatingCount().setText(
                context.getString(R.string.review_count, ratingCount));

        viewHolder.getViewPrice().setText(bottle.getPrice());
        String originalRating = bottle.getAverageRating();

        try {
            float ratingValue = Float.parseFloat(originalRating) * 5; // Scala il rating su 5 stelle
            viewHolder.getRatingBar().setRating(ratingValue);
            viewHolder.getTextViewAverageRating().setText(
                    String.format(Locale.getDefault(), "%.1f", ratingValue));
        } catch (NumberFormatException e) {
            viewHolder.getRatingBar().setRating(0);
            viewHolder.getTextViewAverageRating().setText("0.0");
            Log.e("RatingError", "Formato rating non valido: " + originalRating, e);
        }

        if (viewHolder.getFavoriteCheckbox() != null) {
            viewHolder.getFavoriteCheckbox().setChecked(bottleList.get(position).getLiked());
            viewHolder.getFavoriteCheckbox().setVisibility(heartVisible ? View.VISIBLE : View.INVISIBLE);
        }

        context = viewHolder.getImageView().getContext();
        Glide.with(context)
                .load(bottleList.get(position).getImageUrl())
                .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_secondary)))
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
