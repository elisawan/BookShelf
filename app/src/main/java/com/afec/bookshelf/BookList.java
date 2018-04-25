package com.afec.bookshelf;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookList extends AppCompatActivity {

    GridView gv;
    List<Book> myBooks;
    Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        myToolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(myToolbar);

        myBooks = new ArrayList<Book>();
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));
        myBooks.add(new Book( "android", "123456789", "Malnati", "Torino"));



        gv = findViewById(R.id.book_list_grid);
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return myBooks.size();
            }

            @Override
            public Object getItem(int position) {
                return myBooks.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null) {
                    convertView = getLayoutInflater().inflate(R.layout.book_preview, parent,false);
                }
                ImageView iv = (ImageView) convertView.findViewById(R.id.book_image_preview);
                myBooks.get(position).setThumbnailUrl("http://books.google.com/books/content?id=BlfqRgAACAAJ&printsec=frontcover&img=1&zoom=1&imgtk=AFLRE713W4vHhhKaoyzSnhZBzvgRoEYirqmfzR5iJ6Y7wjbRtCILro3DqXUEsAIkvUVMunOJBgJG1wgCI_ls7amybyAAZJB2Go5jF88JIGJrLcXjnjF-fdmXTy_iPU87qgTOvGDqnz_S&source=gbs_api");
                Picasso.with(getApplicationContext()).load(myBooks.get(position).getThumbnailUrl()).noPlaceholder()
                        .resize(300,400)
                        .into(iv);
                TextView title_tv =(TextView) convertView.findViewById(R.id.book_title_preview);
                myBooks.get(position).setTitle("La divina commedia");
                title_tv.setText(myBooks.get(position).getTitle());

                Log.d("isbn",myBooks.get(position).getIsbn() );
                return convertView;
            }
        });

    }

}
