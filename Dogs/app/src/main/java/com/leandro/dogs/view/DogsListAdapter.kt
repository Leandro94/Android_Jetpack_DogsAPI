package com.leandro.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.leandro.dogs.R
import com.leandro.dogs.databinding.ItemDogBinding
import com.leandro.dogs.model.DogBreed
import com.leandro.dogs.util.getProgressDrawble
import com.leandro.dogs.util.loadImage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener {

    fun updateDogList(newDogsList: List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        //val view = inflater.inflate(R.layout.item_dog, parent, false)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater,R.layout.item_dog, parent,false)
        return DogViewHolder(view)

    }

    override fun getItemCount()= dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int)
    {
        holder.view.dog = dogsList[position]
        holder.view.listener=this
        /*holder.view.textName.text = dogsList[position].dogBreed
        holder.view.textLifeSpan.text = dogsList[position].lifeSpan
        holder.view.setOnClickListener{
            Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToDetailFragment().setDogUuid(dogsList[position].uuid))
        }
        holder.view.imgDogList.loadImage(dogsList[position].imageUrl, getProgressDrawble(holder.view.imgDogList.context))*/

    }

    //class DogViewHolder(var view: View): RecyclerView.ViewHolder(view)
    class DogViewHolder(var view: ItemDogBinding): RecyclerView.ViewHolder(view.root)

    override fun onDogClicked(v: View) {
        val uuid =  v.textId.text.toString().toInt()
        val action = ListFragmentDirections.actionListFragmentToDetailFragment()
        action.dogUuid = uuid
        Navigation.findNavController(v).navigate(action)
    }
}