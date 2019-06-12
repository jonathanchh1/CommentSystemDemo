package com.emi.commentdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.emi.commentdemo.databinding.ItemCommentBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class CommentAdapter constructor(var messageList : ArrayList<Message>, var ref : DatabaseReference) :
    RecyclerView.Adapter<CommentAdapter.MessageViewHolder>() {

    private val commentIds = ArrayList<String>()
    private val childEventListener : ChildEventListener?
    init {

        val childListener = object : ChildEventListener{


            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d(CommentAdapter::class.java.simpleName, "onChildAdded:" + dataSnapshot.key!!)

                val message = dataSnapshot.getValue(Message::class.java)

                val commentKey = dataSnapshot.key
                commentIds.add(dataSnapshot.key!!)

                if(!messageList.contains(message)) {
                    messageList.add(message!!)
                    notifyItemInserted(messageList.size - 1)
                }else{
                    Log.d(CommentAdapter::class.java.simpleName, "onChildAdded:unknown ${commentKey}")
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d(CommentAdapter::class.java.simpleName, dataSnapshot.key)
                val Newmessage = dataSnapshot.getValue(Message::class.java)
                val commentKey = dataSnapshot.key

                val commentIndex = commentIds.indexOf(commentKey)
                if (commentIndex > -1 && Newmessage != null) {
                    messageList[commentIndex] = Newmessage
                    notifyItemChanged(commentIndex)
                } else {
                    Log.w(CommentAdapter::class.java.simpleName, "onChildChanged: $commentKey")
                }
            }

            override fun onChildMoved(shot: DataSnapshot, p1: String?) {
                Log.d(CommentAdapter::class.java.simpleName, shot.key)

                val movedComment = shot.getValue(Message::class.java)
                val commentKey = shot.key
                val commentIndex = commentIds.indexOf(commentKey)

                if(commentIndex > -1 && movedComment != null){
                    messageList[commentIndex] = movedComment
                    notifyItemMoved(commentIndex, 0)
                }


            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val commentKey = dataSnapshot.key

                val commentIndex = commentIds.indexOf(commentKey)
                if (commentIndex > -1) {
                    commentIds.removeAt(commentIndex)
                    messageList.removeAt(commentIndex)
                    notifyItemRemoved(commentIndex)
                } else {
                    Log.w(CommentAdapter::class.java.simpleName, "onChildRemoved:" + commentKey!!)
                }
            }
            override fun onCancelled(shot: DatabaseError) {
                Log.d(CommentAdapter::class.java.simpleName, "${shot.toException()}")
                println("there was an error, ${shot.message}")
            }
        }

        ref.addChildEventListener(childListener)
        this.childEventListener = childListener
    }


    fun cleanUpListener(){
        childEventListener?.let {
            ref.removeEventListener(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding : ItemCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_comment, parent, false)

        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        return holder.bind(messageList.get(position))
    }


    internal fun updateMessage(message : ArrayList<Message>){
        this.messageList = message
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount(): Int {
        return messageList.size
    }
    inner class  MessageViewHolder(val binding : ItemCommentBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(message : Message){
            if(binding.messageModel == null){
                binding.messageModel = MessageViewModel(message)
            }else{
                binding.messageModel!!.message = message
            }
        }
    }
}