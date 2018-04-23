package com.afec.bookshelf;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ListViewActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mListView = (ListView) findViewById(R.id.list_of_owner);

        List<Owner> owners = genererOwner();

        OwnerAdapter adapter = new OwnerAdapter(ListViewActivity.this, owners);
        mListView.setAdapter(adapter);
    }

    private List<Owner> genererOwner(){
        List<Owner> owners = new ArrayList<Owner>();
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        return owners;
    }
}

class OwnerViewHolder{
    public TextView pseudo;
    public TextView text;
    public ImageView avatar;
}
