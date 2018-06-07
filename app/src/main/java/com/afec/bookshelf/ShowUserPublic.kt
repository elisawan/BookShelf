package com.afec.bookshelf

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afec.bookshelf.Models.Review
import com.afec.bookshelf.Models.User
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

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
        var rating:String
        var uid:String

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
        val bioTV:TextView = v.findViewById<TextView>(R.id.bioUtente);
        bioTV.setText(bio);
        val borrowedTV:TextView = v.findViewById<TextView>(R.id.taken_book_count);
        borrowedTV.setText(borrowedBooks);
        val lentTV:TextView = v.findViewById<TextView>(R.id.shared_book_count);
        lentTV.setText(lentBooks);
        val ratingRB:RatingBar = v.findViewById<RatingBar>(R.id.ratingUser) as RatingBar;
        ratingRB.rating = rating.toFloat()
        val imageView: ImageView = v.findViewById(R.id.immagineUtente)
        val mImageRef = FirebaseStorage.getInstance().getReference( uid + "/profilePic.png")
        mImageRef.downloadUrl.addOnSuccessListener{
            uri -> Picasso.with(context).load(uri.toString()).noPlaceholder().into(imageView)
        }.addOnFailureListener {
            exception -> Log.e("ERRORE RECUPERO IMG: ", exception.message.toString())
            Picasso.with(context).load(R.drawable.ic_account_circle_black_24dp).into(imageView)
        }
        var reviewListView : ListView
        reviewListView = v.findViewById(R.id.owner_review_list) as ListView
        var reviewList : ArrayList<Review> = ArrayList()
        var reviewAuthorList : ArrayList<User> = ArrayList()
        var reviewListAdapter = object : BaseAdapter() {
            override fun getCount(): Int {
                return reviewList.size
            }

            override fun getItem(position: Int): Any {
                return reviewList.get(position)
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
                var convertView = convertView
                try {
                    reviewList.get(position)
                } catch (e: Exception) {
                    return null
                }

                if (convertView == null)
                    convertView = layoutInflater.inflate(R.layout.received_review_layout, parent, false)
                val review_author = convertView!!.findViewById<View>(R.id.review_user_name) as TextView
                review_author.setText(reviewAuthorList.get(position).getUsername())

                val review_comment = convertView.findViewById<View>(R.id.review_comment) as TextView
                review_comment.setText(reviewList.get(position).getComment())

                val review_rating = convertView.findViewById<View>(R.id.review_score) as RatingBar
                review_rating.rating = reviewList.get(position).getScore()!!

                return convertView
            }
        }

        reviewListView.adapter = reviewListAdapter

        val reviewRef : DatabaseReference = dbRef.child("users").child(uid).child("myReviews")
        reviewRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (!p0!!.exists()) return;
                for(child:DataSnapshot  in p0!!.getChildren()) {
                    val r: Review = child.getValue(Review::class.java) as Review
                    if (r.status == Review.STATUS_RECEIVED) {
                        dbRef.child("users").child(r.uidfrom).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                                if (!p0!!.exists()) return;
                                val a: User = p0.getValue(User::class.java) as User
                                dbRef.child("reviews").child(r.id).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot?) {
                                        if (!p0!!.exists()) return;
                                        val r: Review = p0.getValue(Review::class.java) as Review
                                        reviewAuthorList.add(a)
                                        reviewList.add(r)
                                        reviewListAdapter.notifyDataSetChanged()
                                    }

                                })
                            }

                        })
                    }
                }
            }

        })

        return v;
    }
}
