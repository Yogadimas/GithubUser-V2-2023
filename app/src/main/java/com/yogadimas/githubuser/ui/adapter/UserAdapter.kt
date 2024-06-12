package com.yogadimas.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.databinding.ItemRowUserBinding
import com.yogadimas.githubuser.ui.interfaces.OnItemClickCallback

class UserAdapter(private val listUsers: ArrayList<ItemsItem?>?) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(var binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRowUserBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val getPosition = listUsers?.get(position)
        val avatar = getPosition?.avatarUrl
        val login = getPosition?.login


        Glide.with(holder.itemView.context)
            .load(avatar)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                .error(R.drawable.ic_error))
            .into(holder.binding.imgItemAvatar)

        holder.binding.tvItemLogin.text = login



        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getPosition) }

    }

    override fun getItemCount(): Int {
        return if (listUsers?.size == null) 0 else listUsers.size
    }
}