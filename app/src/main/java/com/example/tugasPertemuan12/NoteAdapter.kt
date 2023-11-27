package com.example.tugasPertemuan12

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasPertemuan12.databinding.NoteItemBinding

class NoteAdapter (private val note: ArrayList<Budget>, private val listener:OnAdapterListener):
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding:NoteItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return note.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.apply {
            textNama.text= note[position].nama
            textTitle.text = note[position].judul
            textTitle.setOnClickListener(){
                listener.onClick(note[position])
            }
            iconEdit.setOnClickListener(){
                listener.onUpdate(note[position])
            }
            iconDelete.setOnClickListener(){
                listener.onDelete(note[position])
            }
        }
    }

    fun setData(list: List<Budget>){
        note.clear()
        note.addAll(list)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(budget: Budget)
        fun onUpdate(budget: Budget)
        fun onDelete(budget: Budget)
    }
}