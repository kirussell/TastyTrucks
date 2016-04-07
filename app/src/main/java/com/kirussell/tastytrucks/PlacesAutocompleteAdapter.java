package com.kirussell.tastytrucks;

import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.kirussell.tastytrucks.common.DataAdapter;
import com.kirussell.tastytrucks.location.PlacesProvider;
import com.kirussell.tastytrucks.location.data.PlacePrediction;

import java.util.List;

/**
 * Created by russellkim on 07/04/16.
 * Adapter to display list of predicted places
 */
class PlacesAutocompleteAdapter extends DataAdapter<PlacePrediction, PlacesAutocompleteAdapter.VH>
        implements Filterable {

    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private static final CharacterStyle STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);

    public PlacesProvider placesProvider;

    public PlacesAutocompleteAdapter(PlacesProvider placesProvider) {
        this.placesProvider = placesProvider;
    }

    @Override
    protected int getItemLayoutResource() {
        return android.R.layout.simple_expandable_list_item_2;
    }

    @Override
    protected VH createViewHolder(View convertView) {
        return new VH(
                (TextView) convertView.findViewById(android.R.id.text1),
                (TextView) convertView.findViewById(android.R.id.text2)
        );
    }

    @Override
    protected void onBindViewHolder(VH holder, PlacePrediction item) {
        holder.title.setText(item.getName());
        holder.subtitle.setText(item.getInfo());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (constraint != null) {
                    List<PlacePrediction> places = placesProvider.getPlaces(constraint.toString(), STYLE_BOLD, STYLE_NORMAL);
                    replaceData(places);
                    result.values = places;
                    result.count = places.size();
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    class VH {

        private final TextView title;
        private final TextView subtitle;

        public VH(TextView title, TextView subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
