package com.afec.bookshelf


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView

class ShowUserPublic : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v:View = inflater.inflate(R.layout.fragment_show_user_public, container, false)
        val b:Bundle? = arguments;
        var username:String
        var borrowedBooks:String
        var lentBooks:String
        var bio:String
        var rating:Int
        if(b!=null){
            username = b.getString("username","-")
            borrowedBooks = b.getString("borrowedBooks","0")
            lentBooks = b.getString("lentBooks","0")
            bio = b.getString("bio","-")
            rating = b.getInt("rating",0)
        }else{
            return null;
        }

        val nomeUtenteTV : TextView = v.findViewById(R.id.nomeUtente);
        nomeUtenteTV.text = username;
        val bioTV:TextView = v.findViewById<TextView>(R.id.bioUtente);
        bioTV.setText(bio);
        val borrowedTV:TextView = v.findViewById<TextView>(R.id.taken_book_count);
        borrowedTV.setText(borrowedBooks);
        val lentTV:TextView = v.findViewById<TextView>(R.id.shared_book_count);
        lentTV.setText(lentBooks);
        val ratingRB:RatingBar = v.findViewById<RatingBar>(R.id.ratingUser) as RatingBar;
        ratingRB.numStars = rating

        return v;
    }


}

