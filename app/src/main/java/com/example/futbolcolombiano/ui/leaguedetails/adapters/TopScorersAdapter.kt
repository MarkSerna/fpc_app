package com.example.futbolcolombiano.ui.leaguedetails.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.futbolcolombiano.R // Para el placeholder
import com.example.futbolcolombiano.data.model.PlayerSeasonStats
import com.example.futbolcolombiano.databinding.ListItemTopScorerBinding

class TopScorersAdapter : ListAdapter<PlayerSeasonStats, TopScorersAdapter.TopScorerViewHolder>(TopScorerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopScorerViewHolder {
        val binding = ListItemTopScorerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopScorerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopScorerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class TopScorerViewHolder(private val binding: ListItemTopScorerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stats: PlayerSeasonStats) {
            binding.tvPlayerRank.text = "${stats.rank}."
            binding.tvPlayerName.text = stats.playerName ?: "N/A"
            binding.tvScorerTeamName.text = stats.teamName ?: "N/A"
            binding.tvGoals.text = stats.goals?.toString() ?: "-"

            // Cargar foto del jugador
            Glide.with(binding.root.context)
                .load(stats.playerPhotoUrl)
                .placeholder(R.drawable.ic_default_player_photo) // Necesitaremos crear este placeholder
                .error(R.drawable.ic_default_player_photo)
                .circleCrop() // Para fotos de jugadores, circleCrop suele verse bien
                .into(binding.ivPlayerPhoto)

            // Cargar logo del equipo
            Glide.with(binding.root.context)
                .load(stats.teamLogoUrl)
                .placeholder(R.drawable.ic_default_team_logo) // Reutilizamos el placeholder de equipo
                .error(R.drawable.ic_default_team_logo)
                .into(binding.ivScorerTeamLogo)
        }
    }

    class TopScorerDiffCallback : DiffUtil.ItemCallback<PlayerSeasonStats>() {
        override fun areItemsTheSame(oldItem: PlayerSeasonStats, newItem: PlayerSeasonStats): Boolean {
            return oldItem.playerId == newItem.playerId && oldItem.teamId == newItem.teamId // Un jugador en un equipo específico
        }

        override fun areContentsTheSame(oldItem: PlayerSeasonStats, newItem: PlayerSeasonStats): Boolean {
            return oldItem == newItem
        }
    }
}
