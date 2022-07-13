package com.example.conversemessengerapp.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.conversemessengerapp.R
import com.example.conversemessengerapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File

class Profile : AppCompatActivity() {

    private lateinit var profileNameShow: TextView
    private lateinit var profileEmailShow: TextView
    private lateinit var profileStatusShow: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var profilePictureAdd: ImageButton
    private lateinit var profileNameEdit: EditText
    private lateinit var profileEmailEdit: EditText
    private lateinit var profileStatusEdit: EditText

    private lateinit var profileUpdate: Button
    private lateinit var profileSave: Button

    private lateinit var profileIcon: ImageView
    private lateinit var EmailIcon: ImageView
    private lateinit var StatusIcon: ImageView

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    //Profile Photo
    private lateinit var storageRef : StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileNameShow = findViewById(R.id.profileName)
        profileEmailShow = findViewById(R.id.profileEmail)
        profileStatusShow = findViewById(R.id.profileStatus)

        profilePicture = findViewById(R.id.profile_img)
        profilePictureAdd = findViewById(R.id.profile_pic_add)

        profileNameEdit = findViewById(R.id.etProfileName)
        profileEmailEdit = findViewById(R.id.etProfileEmail)
        profileStatusEdit = findViewById(R.id.etProfileStatus)

        profileUpdate = findViewById(R.id.btnUpdateProfile)
        profileSave = findViewById(R.id.btnSaveProfile)

        profileIcon = findViewById(R.id.profile_ic)
        EmailIcon = findViewById(R.id.email_ic)
        StatusIcon = findViewById(R.id.status_ic)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("user")

        val userId = firebaseUser.uid
        storageRef = FirebaseStorage.getInstance().reference.child("$userId/profilePic.jpg")


        databaseReference.child(firebaseUser.uid).get().addOnSuccessListener {
            if (it.exists()) {
                profileNameShow.text = it.child("name").value.toString()
                profileEmailShow.text = it.child("email").value.toString()
                profileStatusShow.text = it.child("status").value.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

        profileUpdate.visibility = View.VISIBLE
        loadProfilePicture()

        profileUpdate.setOnClickListener {
            onProfileUpdate()
        }

        profileSave.setOnClickListener {
            onProfileSave()
        }

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                uploadImage(it)
            }
        )

        profilePictureAdd.setOnClickListener {
            getImage.launch("image/*")
        }
    }


    private fun loadProfilePicture() {
        val fileName = firebaseUser.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("images/profile_pic/$fileName")

        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profilePicture.setImageBitmap(bitmap)
        }
    }


    private fun uploadImage(ImageUri: Uri) {
        val fileName = firebaseUser.uid
        val storageReference = FirebaseStorage.getInstance().getReference("images/profile_pic/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            profilePicture.setImageURI(null)
            Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Uploading Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onProfileSave() {
        profileNameShow.visibility = View.VISIBLE
        profileEmailShow.visibility = View.VISIBLE
        profileStatusShow.visibility = View.VISIBLE

        profileIcon.visibility = View.VISIBLE
        EmailIcon.visibility = View.VISIBLE
        StatusIcon.visibility = View.VISIBLE

        profileUpdate.visibility = View.VISIBLE


        profileNameEdit.visibility = View.GONE
        profileEmailEdit.visibility = View.GONE
        profileStatusEdit.visibility = View.GONE

        profileSave.visibility = View.GONE


        val name = profileNameEdit.text.toString()
        val email = profileEmailEdit.text.toString()
        val status = profileStatusEdit.text.toString()

        updateData(name, email, status)
    }

    private fun onProfileUpdate() {
        profileNameShow.visibility = View.GONE
        profileEmailShow.visibility = View.GONE
        profileStatusShow.visibility = View.GONE

        profileIcon.visibility = View.GONE
        EmailIcon.visibility = View.GONE
        StatusIcon.visibility = View.GONE

        profileNameEdit.visibility = View.VISIBLE
        profileEmailEdit.visibility = View.VISIBLE
        profileStatusEdit.visibility = View.VISIBLE

        profileSave.visibility = View.VISIBLE
        profileUpdate.visibility = View.GONE

        profileNameEdit.text =
            Editable.Factory.getInstance().newEditable(profileNameShow.text.toString())
        profileEmailEdit.text =
            Editable.Factory.getInstance().newEditable(profileEmailShow.text.toString())
        profileStatusEdit.text =
            Editable.Factory.getInstance().newEditable(profileStatusShow.text.toString())
    }

    private fun updateData(name: String, email: String, status: String) {

        databaseReference = FirebaseDatabase.getInstance().getReference("user")
        val User = mapOf<String,String>(
            "email" to email,
            "name" to name,
            "status" to status
        )

        databaseReference = FirebaseDatabase.getInstance().getReference("user")
        databaseReference.child(firebaseUser.uid).updateChildren(User).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}