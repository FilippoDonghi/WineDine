package it.unimib.winedine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class WineAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> categorie;
    private HashMap<String, List<String>> viniMappa;

    public WineAdapter(Context context, List<String> categorie, HashMap<String, List<String>> viniMappa) {
        this.context = context;
        this.categorie = categorie;
        this.viniMappa = viniMappa;
    }

    // Metodo per aggiornare la mappa dei vini
    public void updateWinesMap(HashMap<String, List<String>> newViniMappa) {
        this.viniMappa = newViniMappa; // Aggiorna la mappa dei vini
        notifyDataSetChanged(); // Notifica l'adapter del cambiamento
    }

    // Metodo per aggiornare la lista delle categorie
    public void updateCategories(List<String> newCategorie) {
        this.categorie = newCategorie; // Aggiorna la lista delle categorie
        notifyDataSetChanged(); // Notifica l'adapter del cambiamento
    }

    @Override
    public int getGroupCount() {
        return categorie.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return viniMappa.get(categorie.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categorie.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return viniMappa.get(categorie.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText((String) getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}