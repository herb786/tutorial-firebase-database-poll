package com.hacaller.pollapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.DocumentSnapshot


class MainActivity : AppCompatActivity() {

    val TAG : String = "Poll-MainActivity"

    var mDatabase: FirebaseFirestore? = null
    var mPlatformsCollection: CollectionReference? = null
    var androidVotes : Long = 0
    var iOSVotes : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDatabase = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        mDatabase!!.firestoreSettings = settings
        mPlatformsCollection = mDatabase?.collection("platforms")

        updateAndroidVotes()
        updateIOSVotes()

        btnAndroid.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                voteForAndroid("male", "Japan", 20)
            }
        })

        btnIOS.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                voteForIOS("male", "Japan", 20)
            }
        })

    }

    private fun showVotes(){
        androidCount.text = "ANDROID: " + androidVotes.toString()
        iosCount.text = "iOS: " + iOSVotes.toString()
    }

    private fun voteForAndroid(gender: String, country: String, age: Int) {
        val customer = Customer()
        customer.age = age
        customer.gender = gender
        customer.country = country
        addNewVote(androidVotes, "Android")
    }

    private fun voteForIOS(gender: String, country: String, age: Int) {
        val customer = Customer()
        customer.age = age
        customer.gender = gender
        customer.country = country
        addNewVote(iOSVotes, "iOS")
    }

    fun addNewVote(platformVotes: Long, platformName: String){
        val vote = HashMap<String, Any>()
        vote.put("count", platformVotes+1)
        mPlatformsCollection?.document(platformName)?.set(vote)?.
            addOnSuccessListener(object: OnSuccessListener<Void> {
                override fun onSuccess(aVoid : Void?) {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
            })?.
            addOnFailureListener(object: OnFailureListener {
                override fun onFailure(e : Exception) {
                    Log.w(TAG, "Error writing document", e)
                }
            })
    }

    fun initVotes(){
        mPlatformsCollection?.get()?.addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
            override fun onComplete(task: Task<QuerySnapshot>) {
                if (task.isSuccessful) {
                    for (document : QueryDocumentSnapshot in task.result!!) {
                        if (document.id == "Android")
                            androidVotes = document.data["count"] as Long
                        if (document.id == "iOS")
                            iOSVotes = document.data["count"] as Long
                        Log.d(TAG, document.id + " => " + document.data)
                        showVotes()
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
        })
    }

    fun updateAndroidVotes(){
        val androidRef = mPlatformsCollection?.document("Android")
        androidRef?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(snapshot: DocumentSnapshot?, e : FirebaseFirestoreException?) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.data!!)
                    androidVotes = snapshot.data!!["count"] as Long
                    showVotes()
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
        })

    }

    fun updateIOSVotes(){
        val androidRef = mPlatformsCollection?.document("iOS")
        androidRef?.addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(snapshot: DocumentSnapshot?, e : FirebaseFirestoreException?) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.data!!)
                    iOSVotes = snapshot.data!!["count"] as Long
                    showVotes()
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
        })

    }

}
