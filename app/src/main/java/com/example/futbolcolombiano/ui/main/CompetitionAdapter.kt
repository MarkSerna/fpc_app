package com.example.futbolcolombiano.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.futbolcolombiano.R // Para el placeholder
import com.example.futbolcolombiano.data.model.Competition
import com.example.futbolcolombiano.databinding.ListItemCompetitionBinding

class CompetitionAdapter(private val onItemClicked: (Competition) -> Unit) :
    ListAdapter<Competition, CompetitionAdapter.CompetitionViewHolder>(CompetitionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder {
        val binding = ListItemCompetitionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompetitionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        val competition = getItem(position)
        holder.bind(competition)
        holder.itemView.setOnClickListener {
            onItemClicked(competition)
        }
    }

    class CompetitionViewHolder(private val binding: ListItemCompetitionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(competition: Competition) {
            binding.tvCompetitionName.text = competition.name
            binding.tvCompetitionCategory.text = "${competition.category} - ${competition.gender}"

            if (!competition.logoUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(competition.logoUrl)
                    .placeholder(R.drawable.ic_default_team_logo) // Usamos el mismo placeholder
                    .error(R.drawable.ic_default_team_logo)
                    .into(binding.ivCompetitionLogo)
            } else {
                binding.ivCompetitionLogo.setImageResource(R.drawable.ic_default_team_logo)
            }
        }
    }

    class CompetitionDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem == newItem
        }
    }
}
