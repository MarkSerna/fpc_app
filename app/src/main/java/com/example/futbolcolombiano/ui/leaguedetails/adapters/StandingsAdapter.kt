package com.example.futbolcolombiano.ui.leaguedetails.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.futbolcolombiano.R // Necesario para R.drawable.ic_default_team_logo
import com.example.futbolcolombiano.data.model.StandingItem
import com.example.futbolcolombiano.databinding.ListItemStandingRowBinding

class StandingsAdapter : ListAdapter<StandingItem, StandingsAdapter.StandingViewHolder>(StandingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val binding = ListItemStandingRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        val standingItem = getItem(position)
        holder.bind(standingItem)
        // holder.itemView.setOnClickListener { /* Podríamos hacer algo al clickear una fila si fuera necesario */ }
    }

    class StandingViewHolder(private val binding: ListItemStandingRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StandingItem) {
            binding.tvRank.text = item.rank.toString()
            binding.tvTeamNameStandings.text = item.team.name
            binding.tvPlayed.text = item.played.toString()
            binding.tvWins.text = item.wins.toString()
            binding.tvDraws.text = item.draws.toString()
            binding.tvLosses.text = item.losses.toString()
            binding.tvGoalDifference.text = item.goalDifference.toString()
            binding.tvPoints.text = item.points.toString()

            // Cargar logo del equipo con Glide
            if (!item.team.logoUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.team.logoUrl)
                    .placeholder(R.drawable.ic_default_team_logo)
                    .error(R.drawable.ic_default_team_logo)
                    .into(binding.ivTeamLogoStandings)
            } else {
                binding.ivTeamLogoStandings.setImageResource(R.drawable.ic_default_team_logo)
            }
        }
    }

    class StandingDiffCallback : DiffUtil.ItemCallback<StandingItem>() {
        override fun areItemsTheSame(oldItem: StandingItem, newItem: StandingItem): Boolean {
            // Suponiendo que el rank y el id del equipo son suficientes para identificar una fila única
            return oldItem.rank == newItem.rank && oldItem.team.id == newItem.team.id
        }

        override fun areContentsTheSame(oldItem: StandingItem, newItem: StandingItem): Boolean {
            return oldItem == newItem
        }
    }
}
