package com.emi.commentdemo

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emi.commentdemo.databinding.ActivityCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_comment.*
import org.joda.time.DateTime
import org.joda.time.DateTimeField
import java.sql.Timestamp
import java.time.DateTimeException
import java.util.*
import kotlin.collections.ArrayList
import java.util.Arrays.toString as toString1


class CommentActivity : AppCompatActivity() {


    lateinit var binding: ActivityCommentBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var user: User
    private var picture  : String = String()
    private  var name : String = String()
    var adapter : CommentAdapter?=null
    private var uid : String = String()
    lateinit var messagesRef : DatabaseReference
    private var list: ArrayList<Message> = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        binding.activity = this
        mAuth = FirebaseAuth.getInstance()


        val profileUser = FirebaseAuth.getInstance().currentUser
         if(profileUser != null){
             for(items in profileUser.providerData){
                 user = User(items.displayName, items.email, items.uid, items.photoUrl.toString())
                 picture = items.photoUrl.toString()
                 name = items.displayName.toString()
                 uid = items.uid.toString()
             }
         }
        messagesRef = FirebaseDatabase.getInstance().getReference("comments")
        mAuthListener = FirebaseAuth.AuthStateListener { userData ->
            userData.currentUser?.let {

                if(userData.currentUser == null){
                }
            }
        }
    }




    override fun onStart() {
        super.onStart()
        val message = Message()
        val commentListener = object : ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                list.clear()


                snap.children.forEach {
                    Log.d(CommentActivity::class.java.simpleName, it.toString())
                }

                snap.children.mapNotNullTo(list){
                    it.getValue<Message>(Message::class.java)
                }


                list.forEach {
                    message.uuid = it.uuid
                    message.photo = it.photo
                    message.comment = it.comment
                    message.name = it.name
                    message.timestamp = it.timestamp
                }

                val factory = MessageViewModelFactory(message)
                val viewModel = ViewModelProviders.of(this@CommentActivity, factory).get(MessageViewModel::class.java)
                binding.messageModel = viewModel
                adapter = CommentAdapter(ArrayList(), messagesRef)
                adapter?.updateMessage(list)
                adapter?.notifyDataSetChanged()
                binding.recyclerPostComments.adapter = adapter
                val linearLayout = LinearLayoutManager(applicationContext)
                binding.recyclerPostComments.layoutManager = linearLayout

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(CommentActivity::class.java.simpleName, "${error.toException()}")
                println("there was an error, ${error.message}")

            }
        }

        messagesRef.addListenerForSingleValueEvent(commentListener)
      mAuth.addAuthStateListener(mAuthListener)
    }



    fun MessageButton(){
        val comments = fieldCommentText.text.toString()

        val now = DateTime.now()
        val timestamp = DateUtils.formatDateTime(this, now.millis, DateUtils.FORMAT_SHOW_TIME)
        if(comments.isNotEmpty()) {
            val availableData: ArrayList<Message> = ArrayList<Message>()
            val message =  Message(uid, name, comments, picture, timestamp)
            availableData.add(message)

            availableData.forEach {
                val key = messagesRef.push().key
                it.uuid = key!!
                messagesRef.child(key).setValue(it)
            }

            fieldCommentText.text = null
            Toast.makeText(applicationContext, "added to database", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "you cannot send an empty message", Toast.LENGTH_SHORT).show()
        }

    }

    /*
    fun MessageButton() {

        val comment = fieldCommentText.text.toString()
        if (comment.isNotEmpty()) {
            val id = messagesRef.push().key
           val message = Message(name, comment, picture)

            messagesRef.child(user.userUid!!).child("comments").child(id!!).setValue(message)
            fieldCommentText.text = null
            Toast.makeText(this, "added to database", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You cannot send an empty message", Toast.LENGTH_SHORT).show()
        }
    }

    */





    override fun onStop() {
        super.onStop()
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener)
        adapter?.cleanUpListener()
    }


}
