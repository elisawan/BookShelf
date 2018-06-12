package com.afec.bookshelf

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.widget.*
import com.afec.bookshelf.Models.Review
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ShowUserPublic : Fragment() {

    lateinit var uid:String;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        val v:View = inflater.inflate(R.layout.activity_show_user, container, false)
        val b:Bundle? = arguments;
        var username:String
        var borrowedBooks:String
        var lentBooks:String
        var bio:String
        var rating:String


        if(b!=null){
            username = b.getString("username","-")
            borrowedBooks = b.getString("borrowedBooks","0")
            lentBooks = b.getString("lentBooks","0")
            bio = b.getString("bio","-")
            uid = b.getString("uid","-")
            rating = b.getString("rating","0")
        }else{
            return null;
        }

        val dbRef : DatabaseReference = FirebaseDatabase.getInstance().reference

        val nomeUtenteTV : TextView = v.findViewById(R.id.nomeUtente);
        nomeUtenteTV.text = username;
        val bioTV:TextView = v.findViewById(R.id.bioUtente);
        bioTV.setText(bio);
        val borrowedTV:TextView = v.findViewById(R.id.borrowed_book_count);
        borrowedTV.setText(borrowedBooks);
        val lentTV:TextView = v.findViewById(R.id.lent_book_count);
        lentTV.setText(lentBooks);
        val ratingRB:RatingBar = v.findViewById(R.id.ratingUser);
        ratingRB.rating = rating.toFloat()
        v.findViewById<TextView>(R.id.credit_user).visibility = View.GONE;
        v.findViewById<TextView>(R.id.credit_user_count).visibility = View.GONE;
        val imageView: ImageView = v.findViewById(R.id.immagineUtente)
        val mImageRef = FirebaseStorage.getInstance().getReference( uid + "/profilePic.png")
        mImageRef.downloadUrl.addOnSuccessListener{
            uri -> Picasso.with(context).load(uri.toString()).noPlaceholder().into(imageView)
        }.addOnFailureListener {
            exception -> Log.e("ERRORE RECUPERO IMG: ", exception.message.toString())
            Picasso.with(context).load(R.drawable.ic_account_circle_black_24dp).into(imageView)
        }


        //for the review part ----------------------------------------------------------------------
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        var mSectionsPagerAdapter: ReviewPage.SectionsPagerAdapter? = null
        var mViewPager: ViewPager? = null
        mSectionsPagerAdapter = ReviewPage.SectionsPagerAdapter(activity!!.supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = v.findViewById<View>(R.id.rev_container_user) as ViewPager
        setupViewPager(mViewPager)

        val tabLayout = v.findViewById<View>(R.id.rev_tabs_user) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))

        //------------------------------------------------------------------------------------------
        return v;
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.setGroupVisible(R.id.defaultMenu, false)
        menu.setGroupVisible(R.id.showProfileMenu, false)
    }

    private fun setupViewPager(mViewPager: ViewPager) {
        val adapter = ReviewPage.SectionsPagerAdapter(activity!!.supportFragmentManager)

        val b_written = Bundle()
        b_written.putInt("query", Review.STATUS_WRITTEN)
        b_written.putString("user", uid);
        val writtenReviews = ReviewList()
        writtenReviews.arguments = b_written
        adapter.addFragment(writtenReviews, resources.getString(R.string.written))

        val b_received = Bundle()
        b_received.putInt("query", Review.STATUS_RECEIVED)
        b_received.putString("user", uid);
        val receivedReviews = ReviewList()
        receivedReviews.arguments = b_received
        adapter.addFragment(receivedReviews, resources.getString(R.string.received))

        mViewPager.adapter = adapter
    }
}
