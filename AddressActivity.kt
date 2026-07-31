package com.example.maps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maps.databinding.ActivityAddressBinding
import com.example.maps.databinding.ViewholderAddressBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter
    private val addressList = mutableListOf<Address>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addAddressView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        setupRecycleView()
        addressInit()
    }

    private fun setupRecycleView(){
        addressAdapter = AddressAdapter(addressList)
        binding.recyclerViewAddress.layoutManager = LinearLayoutManager(this@AddressActivity,
            LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewAddress.adapter = addressAdapter
    }

    private fun addressInit() {
        binding.apply {
            val databaseReference = FirebaseDatabase.getInstance().getReference("user_addresses")
            databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    addressList.clear()

                    for(addressSnapshot in snapshot.children){
                        val address = addressSnapshot.getValue(Address::class.java)
                        if(address != null){
                            addressList.add(address)
                        }
                    }

                    addressAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddressActivity, "Ошибка загрузки: ${error.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }
}