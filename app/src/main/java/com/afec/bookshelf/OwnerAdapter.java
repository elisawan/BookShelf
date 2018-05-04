package com.afec.bookshelf;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Owner;

import java.util.List;

public class OwnerAdapter extends ArrayAdapter<Owner> {

    //Owner est la liste des models à afficher
    public OwnerAdapter(Context context, List<Owner> owners) {
        super(context, 0, owners);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_owner,parent, false);
        }

        OwnerViewHolder viewHolder = (OwnerViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new OwnerViewHolder();
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> owner
        Owner owner = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.pseudo.setText(owner.getPseudo());
        viewHolder.text.setText(owner.getText());
        viewHolder.distance.setText(owner.getDistance());
        viewHolder.avatar.setImageDrawable(new ColorDrawable(owner.getColor()));

        return convertView;
    }

    private class OwnerViewHolder{
        public TextView pseudo;
        public TextView text;
        public TextView distance;
        public ImageView avatar;
    }
}