package com.afec.bookshelf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
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

            case R.id.action_show_profile:
                intent = new Intent(getBaseContext(), ShowUser.class);
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

