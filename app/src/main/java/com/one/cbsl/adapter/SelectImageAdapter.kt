package com.one.cbsl.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.one.cbsl.databinding.RawItemImageBinding
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Utils
import java.io.File

class SelectImageAdapter(var list: ArrayList<Uri>) :
    RecyclerView.Adapter<SelectImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectImageAdapter.ViewHolder {
        val binding =
            RawItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectImageAdapter.ViewHolder, position: Int) {

        Glide.with(Cbsl.getInstance()).load(Uri.fromFile(File(Utils.getPath(list[position]))))
            .into(holder.binding.ivImage);

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}