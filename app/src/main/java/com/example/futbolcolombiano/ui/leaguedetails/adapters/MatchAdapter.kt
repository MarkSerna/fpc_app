package com.example.futbolcolombiano.ui.leaguedetails.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.futbolcolombiano.R // Necesario para R.drawable.ic_default_team_logo
import com.example.futbolcolombiano.data.model.Match
import com.example.futbolcolombiano.data.model.MatchStatus
import com.example.futbolcolombiano.databinding.ListItemMatchBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter(private val onItemClicked: (Match) -> Unit) :
    ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ListItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bind(match, timeFormat, dateFormat)
        holder.itemView.setOnClickListener {
            onItemClicked(match)
        }
    }

    class MatchViewHolder(private val binding: ListItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match, timeFormat: SimpleDateFormat, dateFormat: SimpleDateFormat) {
            binding.tvLocalTeamName.text = match.localTeam.name
            binding.tvVisitorTeamName.text = match.visitorTeam.name

            // Cargar logo del equipo local con Glide
            if (!match.localTeam.logoUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(match.localTeam.logoUrl)
                    .placeholder(R.drawable.ic_default_team_logo)
                    .error(R.drawable.ic_default_team_logo)
                    .into(binding.ivLocalTeamLogo)
            } else {
                binding.ivLocalTeamLogo.setImageResource(R.drawable.ic_default_team_logo)
            }

            // Cargar logo del equipo visitante con Glide
            if (!match.visitorTeam.logoUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(match.visitorTeam.logoUrl)
                    .placeholder(R.drawable.ic_default_team_logo)
                    .error(R.drawable.ic_default_team_logo)
                    .into(binding.ivVisitorTeamLogo)
            } else {
                binding.ivVisitorTeamLogo.setImageResource(R.drawable.ic_default_team_logo)
            }


            when (match.status) {
                MatchStatus.SCHEDULED -> {
                    binding.tvScore.text = timeFormat.format(match.dateTime)
                    binding.tvMatchStatus.text = "Programado"
                    binding.tvMatchStatus.setTextColor(Color.DKGRAY)
                    binding.tvMatchDateTime.text = dateFormat.format(match.dateTime)
                }
                MatchStatus.LIVE -> {
                    binding.tvScore.text = "${match.localScore ?: '-'} - ${match.visitorScore ?: '-'}"
                    binding.tvMatchStatus.text = "EN VIVO ${match.liveMinute ?: ""}"
                    binding.tvMatchStatus.setTextColor(Color.RED)
                    binding.tvMatchDateTime.text = "Jugando ahora"
                }
                MatchStatus.FINISHED -> {
                    binding.tvScore.text = "${match.localScore ?: '-'} - ${match.visitorScore ?: '-'}"
                    binding.tvMatchStatus.text = "Finalizado"
                    binding.tvMatchStatus.setTextColor(Color.BLUE) // O un color adecuado
                    binding.tvMatchDateTime.text = dateFormat.format(match.dateTime)
                }
                MatchStatus.POSTPONED -> {
                    binding.tvScore.text = "APLAZADO"
                    binding.tvMatchStatus.text = "Aplazado"
                    binding.tvMatchStatus.setTextColor(Color.GRAY)
                    binding.tvMatchDateTime.text = dateFormat.format(match.dateTime)
                }
                MatchStatus.CANCELED -> {
                    binding.tvScore.text = "CANCELADO"
                    binding.tvMatchStatus.text = "Cancelado"
                    binding.tvMatchStatus.setTextColor(Color.GRAY)
                    binding.tvMatchDateTime.text = dateFormat.format(match.dateTime)
                }
            }
        }
    }

    class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}
