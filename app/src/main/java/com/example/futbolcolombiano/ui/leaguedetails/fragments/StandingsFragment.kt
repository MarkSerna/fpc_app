package com.example.futbolcolombiano.ui.leaguedetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futbolcolombiano.databinding.FragmentStandingsBinding
import com.example.futbolcolombiano.ui.leaguedetails.LeagueDetailsViewModel
import com.example.futbolcolombiano.ui.leaguedetails.LeaguePagerAdapter
import com.example.futbolcolombiano.ui.leaguedetails.adapters.StandingsAdapter

class StandingsFragment : Fragment() {

    private var _binding: FragmentStandingsBinding? = null
    private val binding get() = _binding!!

    private val leagueDetailsViewModel: LeagueDetailsViewModel by activityViewModels()
    private var competitionApiId: Int = -1 // Cambiado a Int
    private lateinit var standingsAdapter: StandingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Leer el ID como Int
            competitionApiId = it.getInt(LeaguePagerAdapter.ARG_COMPETITION_API_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (competitionApiId != -1) {
             if (leagueDetailsViewModel.standings.value.isNullOrEmpty()) { // Cargar solo si no hay datos
                // Usar el competitionApiId (Int)
                leagueDetailsViewModel.loadStandings(competitionApiId)
            }
        } else {
            Toast.makeText(context, "Error: ID de competición API no disponible en StandingsFragment", Toast.LENGTH_LONG).show()
            binding.tvNoStandings.text = "Error: ID de competición API no encontrado."
            binding.tvNoStandings.visibility = View.VISIBLE
            binding.standingsHeader.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        standingsAdapter = StandingsAdapter()
        binding.rvStandings.apply {
            adapter = standingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        leagueDetailsViewModel.standings.observe(viewLifecycleOwner) { standings ->
            standingsAdapter.submitList(standings)
            val noData = standings.isNullOrEmpty() && leagueDetailsViewModel.errorStandings.value == null && (leagueDetailsViewModel.isLoadingStandings.value == false)

            binding.standingsHeader.visibility = if (noData || leagueDetailsViewModel.errorStandings.value != null) View.GONE else View.VISIBLE

            if (noData) {
                binding.tvNoStandings.text = "Tabla de posiciones no disponible."
                binding.tvNoStandings.visibility = View.VISIBLE
            } else if (!standings.isNullOrEmpty()){
                 binding.tvNoStandings.visibility = View.GONE
            }
        }

        leagueDetailsViewModel.isLoadingStandings.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvNoStandings.text = "Cargando tabla..."
                binding.tvNoStandings.visibility = View.VISIBLE
                binding.standingsHeader.visibility = View.GONE
                binding.rvStandings.visibility = View.GONE
            } else {
                binding.rvStandings.visibility = View.VISIBLE
                if (standingsAdapter.itemCount > 0) {
                    binding.tvNoStandings.visibility = View.GONE
                    binding.standingsHeader.visibility = View.VISIBLE
                }
            }
        }

        leagueDetailsViewModel.errorStandings.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (standingsAdapter.itemCount == 0) {
                    binding.tvNoStandings.text = it
                    binding.tvNoStandings.visibility = View.VISIBLE
                    binding.standingsHeader.visibility = View.GONE
                    binding.rvStandings.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
