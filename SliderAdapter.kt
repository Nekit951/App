package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.project.databinding.SliderItemContainerBinding

class SliderAdapter(private var sliderItems: List<SliderModel>, private val viewpager: ViewPager2): RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context
    private val runnable = Runnable{
        sliderItems = sliderItems
        notifyDataSetChanged()
    }

    class SliderViewHolder(val binding: SliderItemContainerBinding): RecyclerView.ViewHolder(binding.root){
        fun setImage(sliderModel: SliderModel, context: Context){
            Glide.with(context).load(sliderModel.url).into(binding.imageSlide)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        context = parent.context
        val binding = SliderItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SliderViewHolder,
        position: Int
    ) {
        holder.setImage(sliderItems[position], context)
        if(position == sliderItems.lastIndex - 1){
            viewpager.post(runnable)
        }
    }

    override fun getItemCount(): Int = sliderItems.size
}