package com.example.futbolcolombiano.ui.leaguedetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.futbolcolombiano.ui.leaguedetails.fragments.MatchesFragment
import com.example.futbolcolombiano.ui.leaguedetails.fragments.StandingsFragment
import com.example.futbolcolombiano.ui.leaguedetails.fragments.TopScorersFragment

private const val NUM_TABS = 3 // Partidos, Tabla, Goleadores

class LeaguePagerAdapter(activity: FragmentActivity, private val competitionApiId: Int) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().apply {
            putInt(ARG_COMPETITION_API_ID, competitionApiId)
        }
        return when (position) {
            0 -> MatchesFragment().apply { arguments = bundle }
            1 -> StandingsFragment().apply { arguments = bundle }
            2 -> TopScorersFragment().apply { arguments = bundle }
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "Partidos"
            1 -> "Tabla"
            2 -> "Goleadores"
            else -> ""
        }
    }

    companion object {
        const val ARG_COMPETITION_API_ID = "competition_api_id_arg"
    }
}
