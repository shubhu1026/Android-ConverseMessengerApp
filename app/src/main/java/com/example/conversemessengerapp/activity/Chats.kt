package com.example.conversemessengerapp.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conversemessengerapp.R
import com.example.conversemessengerapp.adapter.MessageAdapter
import com.example.conversemessengerapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class Chats : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var btnSend: ImageView
    private lateinit var messageBox: EditText
    private lateinit var userTitle: TextView
    private lateinit var btnBack: ImageView

    private lateinit var chatsProfilePic: CircleImageView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        btnSend = findViewById(R.id.btnSend)
        messageBox = findViewById(R.id.messageBox)
        userTitle = findViewById(R.id.user_name)
        btnBack = findViewById(R.id.btnBack)
        chatsProfilePic = findViewById(R.id.chatsProfilePic)

        val name: String? = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.hide()
        userTitle.text = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        val fileName = receiverUid
        val storageRef = FirebaseStorage.getInstance().reference.child("images/profile_pic/$fileName")

        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            chatsProfilePic.setImageBitmap(bitmap)
        }

        //Logic for adding data to the recycler view

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        btnSend.setOnClickListener {

            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)

            //adding the messages to the database
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject) }

            messageBox.setText("")
        }

        chatsProfilePic.setOnClickListener {
                val intent = Intent(this, OtherUserProfile::class.java)
                intent.putExtra("uid", receiverUid)

                startActivity(intent)
            }

        btnBack.setOnClickListener{
            val intent = Intent(this@Chats, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}