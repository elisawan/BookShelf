package com.afec.bookshelf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;

public class BaseActivity extends AppCompatActivity {

    Client client;
    Index index;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        client = new Client("BDPR8QJ6ZZ", "0f52ad691623ea9e72a2515aebb880c7");
        index = client.getIndex("bookShelf");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_edit_profile:
                intent = new Intent(getBaseContext(), EditUser.class);
                startActivity(intent);
                return true;

            case R.id.action_add_book:
                intent = new Intent(getBaseContext(), AddBook.class);
                startActivity(intent);
                return true;

            case R.id.action_my_books_list:
                intent = new Intent(getBaseContext(), BookList.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

