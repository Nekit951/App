package com.example.project

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(): ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _banner = MutableLiveData<List<SliderModel>>()

    val banner: LiveData<List<SliderModel>> = _banner

    fun loadBanners(){
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SliderModel>()
                for(child in snapshot.children){
                    val model = child.getValue(SliderModel::class.java)
                    if(model != null){
                        list.add(model)
                    }
                }
                _banner.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Ошибка загрузки данных: ${error.message}")
            }

        })
    }
}