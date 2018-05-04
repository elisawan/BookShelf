package com.afec.bookshelf;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.google.zxing.Result;

public class IsbnScanner extends Fragment implements ZXingScannerView.ResultHandler{

    ZXingScannerView scannerView;

    public IsbnScanner() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        scannerView = new ZXingScannerView(getActivity());
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        return scannerView;
    }

    @Override
    public void handleResult(Result result) {
        String isbn = result.getText();
        scannerView.stopCamera();
        Bundle b = new Bundle();
        b.putString("isbn",isbn);
        Fragment newFragment = new AddBook();
        newFragment.setArguments(b);
        // Create new fragment and transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
