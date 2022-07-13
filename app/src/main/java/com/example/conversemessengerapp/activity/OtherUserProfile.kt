package com.example.conversemessengerapp.activity

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.conversemessengerapp.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class OtherUserProfile : AppCompatActivity() {

    private lateinit var profileNameShow: TextView
    private lateinit var profileEmailShow: TextView
    private lateinit var profileStatusShow: TextView
    private lateinit var profilePicture: CircleImageView

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        val receiverUid: String? = intent.getStringExtra("uid")

        profileNameShow = findViewById(R.id.profileName)
        profileEmailShow = findViewById(R.id.profileEmail)
        profileStatusShow = findViewById(R.id.profileStatus)

        profilePicture = findViewById(R.id.profile_img)

        storageRef = FirebaseStorage.getInstance().reference.child("$receiverUid/profilePic.jpg")

        databaseReference = FirebaseDatabase.getInstance().getReference("user")

        databaseReference.child(receiverUid!!).get().addOnSuccessListener {
            if (it.exists()) {
                profileNameShow.text = it.child("name").value.toString()
                profileEmailShow.text = it.child("email").value.toString()
                profileStatusShow.text = it.child("status").value.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

        loadProfilePicture(receiverUid)

    }

    private fun loadProfilePicture(receiverUid: String) {
        val fileName = receiverUid
        val storageRef = FirebaseStorage.getInstance().reference.child("images/profile_pic/$fileName")

        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profilePicture.setImageBitmap(bitmap)
        }
    }

}