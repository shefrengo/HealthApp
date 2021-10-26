package com.shefrengo.health.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.shefrengo.health.DialogRecyclerview
import com.shefrengo.health.activity.PostDetails
import com.shefrengo.health.R
import com.shefrengo.health.SetTime
import com.shefrengo.health.activity.LoginActivity
import com.shefrengo.health.adapters.RecyclerViewAdapter
import com.shefrengo.health.model.*
import com.shefrengo.health.utils.extentions.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_home.view.*
import java.lang.Exception
import java.util.ArrayList


class Home : BaseFragment() {


    private val TAG = "Home"

    private var adapter: RecyclerViewAdapter<Posts, Data>? = null
    private var postsList = ArrayList<Posts>()

    private var dataList = ArrayList<Data>()
    private val count = 0
    private var recyclerView: RecyclerView? = null
    private var progressRelative: RelativeLayout? = null
    private val AD_COUNT = 2
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private var circleImageView: CircleImageView? = null
    private var name: TextView? = null
    private var question = "What is you question."
    private var lastVisible: DocumentSnapshot? = null
    private val useridList = ArrayList<String>()
    private val objects: List<Any>? = null
    private var adList: List<NativeAd>? = null
    private val limit = 12

    private val strings: List<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        adapter = getAdapter()
        postsList = ArrayList()
        circleImageView = view.findViewById(R.id.home_profile_pic)
        name = view.findViewById(R.id.question_textview)
        progressRelative = view.findViewById(R.id.pagination_relatvie)

        dataList = ArrayList()
        adList = ArrayList()

        val cardview: CardView = view.findViewById(R.id.home_cardview)
        showProgress()
        recyclerView = view.findViewById(R.id.recyclerview)
        initRecyclerview()
        setAdapterClick()

        cardview.setOnClickListener { v: View? ->
            val dialogRecyclerview = DialogRecyclerview()
            val fm =
                requireActivity().supportFragmentManager
            dialogRecyclerview.show(fm, "tag")
        }


        setMainImage()
        getData()


        return view
    }

    private fun setMainImage() {
        val username = getUserName()
        val photo = getUserProfile()
        circleImageView?.loadImageFromUrl(photo, R.drawable.ic_profile,R.drawable.ic_profile)
        question = "What is your question, $username?"
        name?.text = question
    }

    private fun getData() {

        if (isLoggedIn()){
            assert(user != null)
            val myCommunitRef = db.collection("Users")
                .document(user!!.uid).collection("MyCommunities")
            myCommunitRef.get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (myCommunitySnapshot in queryDocumentSnapshots) {
                    val myCommunities = myCommunitySnapshot.toObject(
                        MyCommunities::class.java
                    )
                    val communityId = myCommunities.communityId
                    useridList.add(communityId)
                }
            }
        }

        getPosts()
        text()
    }

    private fun text() {
        val collectionReference = db.collection("Communities")
        collectionReference.get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
            for (queryDocumentSnapshot in queryDocumentSnapshots) {
                val communities =
                    queryDocumentSnapshot.toObject(Communities::class.java)
                //   Log.d(TAG, "onSuccess: " + communities.getAdminUserid());
                val collectionReference1 = db.collection("Users")
                val query =
                    collectionReference1.whereEqualTo("userId", communities.adminUserid)
                query.get()
                    .addOnSuccessListener { queryDocumentSnapshots1: QuerySnapshot ->
                        for (queryDocumentSnapshot1 in queryDocumentSnapshots1) {
                            Log.d(
                                TAG,
                                "onSuccessnnn: " + queryDocumentSnapshot1.id
                            )
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "onFailure: ", e)
                        Log.d(
                            TAG,
                            "onFailure: " + e.localizedMessage
                        )
                    }
            }
        }
    }

    private fun setAdapterClick() {
        adapter!!.onItemClick = { position: Int?, view: View?, posts: Posts?, data: Data? ->
            val image = posts!!.imageUrl
            val userid = posts.userid
            val timestamp = posts.timestamp.toString()
            val title = posts.title
            val description = posts.description
            val category = posts.category
            val reply = posts.replyCount
            val community = posts.community
            val photo = data!!.profilePhotoUrl
            val username = data.username
            val postid = posts.postId
            val intent = Intent(activity, PostDetails::class.java)
            intent.putExtra("userid", userid)
            intent.putExtra("image", image)
            intent.putExtra("photo", photo)
            intent.putExtra("username", username)
            intent.putExtra("community", community)
            intent.putExtra("timestamp", timestamp)
            intent.putExtra("category", category)
            intent.putExtra("postid", postid)
            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("reply", reply)
            startActivity(intent)

        }
    }

    private fun initRecyclerview() {
        recyclerView!!.setVerticalLayout(false)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
    }


    private fun getPosts() {
        val postRef = db.collection("Posts")
        val query = postRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        query.get().addOnSuccessListener { postSnapshot: QuerySnapshot ->
            for (queryDocumentSnapshot1 in postSnapshot) {
                val id = queryDocumentSnapshot1.id
                val posts =
                    queryDocumentSnapshot1.toObject(Posts::class.java).withId<Posts>(id)
                val userid = posts.userid
                postsList?.clear()
                dataList?.clear()
                db.collection("Users").document(userid).get()
                    .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                        val users = documentSnapshot.toObject(
                            Users::class.java
                        )!!
                        val username = users.username
                        val photo = users.profilePhotoUrl
                        for (ids in useridList) {
                            if (posts.community == ids) {
                                Log.d(TAG, "getPosts: " + posts.postId)
                                dataList?.add(Data(username, photo))
                                postsList?.add(posts)
                                adapter?.addItems(postsList, dataList)


                            }
                        }

                        /*        adapter.setList(postsList);
                            if (count == AD_COUNT) {

                                adapter.mixedData();
                            }*/hideProgress()
                    }
            }
            if (postSnapshot.size() > 0) {
                lastVisible = postSnapshot.documents[postSnapshot.size() - 1]
            }
            /**     nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
             * View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
             * int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
             * if (diff == 0) {
             * // load more
             *
             * }
             * }); */
            recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val bottomReached = !recyclerView.canScrollVertically(1)
                    if (bottomReached) {
                        //progressRelative!!.visibility = View.VISIBLE
                        //loadMore()
                    }
                }
            })
        }.addOnFailureListener { e: Exception? ->
            Log.e(
                TAG,
                "onFailure: ",
                e
            )
        }
    }


    private fun loadMore() {
        val postRef = db.collection("Posts")
        val query = postRef
            .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible)
            .limit(limit.toLong())

        // posts
        query.get().addOnSuccessListener { postSnapshot: QuerySnapshot ->
            if (postSnapshot.isEmpty) {
                progressRelative!!.visibility = View.GONE
            } else {
                for (queryDocumentSnapshot1 in postSnapshot) {
                    val id = queryDocumentSnapshot1.id
                    val posts =
                        queryDocumentSnapshot1.toObject(Posts::class.java).withId<Posts>(id)
                    val userid = posts.userid
                    db.collection("Users").document(userid).get()
                        .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                            val users = documentSnapshot.toObject(
                                Users::class.java
                            )!!
                            val username = users.username
                            val photo = users.profilePhotoUrl
                            progressRelative!!.visibility = View.GONE
                            for (ids in useridList) {
                                if (posts.community == ids) {
                                    dataList?.add(Data(username, photo))
                                    postsList?.add(posts)
                                }
                            }
                            adapter?.notifyDataSetChanged()
                        }
                }
            }
            if (postSnapshot.size() > 0) {
                lastVisible = postSnapshot.documents[postSnapshot.size() - 1]
            }
        }.addOnFailureListener { e: Exception? ->
            Log.e(
                TAG,
                "onFailure: ",
                e
            )
        }
    }


    private fun getAdapter(): RecyclerViewAdapter<Posts, Data> {
        return RecyclerViewAdapter(R.layout.item_home) { view: View, posts: Posts?, integer: Int?, data: Data? ->
            setPostItem(view, posts, data, integer)
        }
    }

    private fun setPostItem(view: View, posts: Posts?, data: Data?, position: Int?) {
        val image = posts?.imageUrl


        val timestamp = posts!!.timestamp.toString()
        val category = posts.category

        val replyCount = posts.replyCount
        val replyText = posts.replyCount.toString() + " Replies"

        if (replyCount == 0) {
            view.reply_count.text = requireActivity().resources.getString(R.string.first_reply)
        } else {
            view.reply_count.text = replyText
        }

        view.title.text = posts.title
        view.short_description.text = posts.description
        if (image != null) {
            if (image.isEmpty()) {
                view.image.hide()
            }
        }
        view.image.loadImageFromUrl(image!!,R.drawable.placeholder,R.drawable.placeholder)
        val username = data!!.username
        val profilePhotoUrl = data.profilePhotoUrl

        val text =
            " By @$username in $category's forum ..." + SetTime.TwitterTimeDifferentitaion(
                timestamp
            )
        view.category.text = text
        view.profile_pic.loadImageFromUrl(profilePhotoUrl,R.drawable.ic_profile,R.drawable.ic_profile)

    }
}