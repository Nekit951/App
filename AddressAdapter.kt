package com.example.maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.maps.databinding.ViewholderAddressBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AddressAdapter(private val addressList: MutableList<Address>): RecyclerView.Adapter<AddressAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderAddressBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.Viewholder {
        val binding = ViewholderAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: AddressAdapter.Viewholder, position: Int) {
        val address = addressList[position]

        holder.binding.apply {
            city.text = address.city

            // 1. УПРАВЛЕНИЕ ВИДИМОСТЬЮ ПОДЪЕЗДА
            if (address.podiezd == 0) {
                // Если пусто — скрываем и цифру, и текстовый заголовок "Подъезд"
                podiezd.visibility = View.GONE
                // ВАЖНО: Замените 'tvPodiezdLabel' на реальный ID вашего TextView с текстом "Подъезд" из XML, если он есть
                // если вы объединили их в Layout (как в прошлом шаге), скрывайте сам layout: layoutPodiezd.visibility = View.GONE
            } else {
                podiezd.visibility = View.VISIBLE
                podiezd.text = address.podiezd.toString()
            }

            // 2. УПРАВЛЕНИЕ ВИДИМОСТЬЮ ЭТАЖА
            if (address.etazh == 0) {
                etazh.visibility = View.GONE
            } else {
                etazh.visibility = View.VISIBLE
                etazh.text = address.etazh.toString()
            }

            // 3. УПРАВЛЕНИЕ ВИДИМОСТЬЮ КВАРТИРЫ
            if (address.kvartira == 0) {
                kvartira.visibility = View.GONE
            } else {
                kvartira.visibility = View.VISIBLE
                kvartira.text = address.kvartira.toString()
            }

            // БЕЗОПАСНОЕ УДАЛЕНИЕ ЭЛЕМЕНТА
            buttonOptions.setOnClickListener {
                // Получаем актуальную позицию элемента в списке на момент клика
                val currentPosition = holder.adapterPosition

                // Проверяем, что элемент всё ещё существует в адаптере
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val addressDel = addressList[currentPosition]
                    val database = FirebaseDatabase.getInstance().getReference("user_addresses")

                    database.child(addressDel.id).removeValue().addOnSuccessListener {
//                        addressList.removeAt(currentPosition)
//                        notifyItemRemoved(currentPosition)
//                        notifyItemRangeChanged(currentPosition, addressList.size)
                    }
                        .addOnFailureListener { error ->
                            Toast.makeText(holder.itemView.context, "Не удалось удалить: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    override fun getItemCount(): Int = addressList.size
}