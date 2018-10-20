package com.hacaller.pollapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mDatabaseAndroid: DatabaseReference? = null
    var mDatabaseIOS: DatabaseReference? = null
    var androidVotes : Long = 0
    var iOSVotes : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDatabase = FirebaseDatabase.getInstance().getReference("platforms")
        mDatabaseAndroid = mDatabase?.child("Android")
        mDatabaseIOS = mDatabase?.child("iOS")

        mDatabase?.orderByKey()?.equalTo("Android")?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error?.toException())

            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                androidVotes = dataSnapshot.value as Long
                loadVotes()
            }
        })

        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                androidVotes = dataSnapshot.child("Android").childrenCount
                iOSVotes = dataSnapshot.child("iOS").childrenCount
                loadVotes()
                //val value = dataSnapshot.getValue(Customer::class.java)
                //Log.d("TAG", "Value is: " + value)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error?.toException())
            }
        })

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

    private fun loadVotes(){
        androidCount.text = "ANDROID: " + androidVotes.toString()
        iosCount.text = "iOS: " + iOSVotes.toString()
    }

    private fun voteForAndroid(gender: String, country: String, age: Int) {
        val customer = Customer()
        customer.age = age
        customer.gender = gender
        customer.country = country
        mDatabaseAndroid?.child("customers")?.push()?.setValue(customer)
    }

    private fun voteForIOS(gender: String, country: String, age: Int) {
        val customer = Customer()
        customer.age = age
        customer.gender = gender
        customer.country = country
        mDatabaseIOS?.child("customers")?.push()?.setValue(customer)
    }





}
