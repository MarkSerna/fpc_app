package com.example.futbolcolombiano.ui.leaguedetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futbolcolombiano.databinding.FragmentTopScorersBinding
import com.example.futbolcolombiano.ui.leaguedetails.LeagueDetailsViewModel
import com.example.futbolcolombiano.ui.leaguedetails.LeaguePagerAdapter
import com.example.futbolcolombiano.ui.leaguedetails.adapters.TopScorersAdapter

class TopScorersFragment : Fragment() {

    private var _binding: FragmentTopScorersBinding? = null
    private val binding get() = _binding!!

    private val leagueDetailsViewModel: LeagueDetailsViewModel by activityViewModels()
    private var competitionApiId: Int = -1
    private lateinit var topScorersAdapter: TopScorersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            competitionApiId = it.getInt(LeaguePagerAdapter.ARG_COMPETITION_API_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopScorersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (competitionApiId != -1) {
            // Cargar solo si no hay datos o si el ID de competición cambió (poco probable aquí pero buena práctica)
            if (leagueDetailsViewModel.topScorers.value.isNullOrEmpty()) {
                leagueDetailsViewModel.loadTopScorers(competitionApiId)
            }
        } else {
            Toast.makeText(context, "Error: ID de competición API no disponible en TopScorersFragment", Toast.LENGTH_LONG).show()
            binding.tvNoTopScorers.text = "Error: ID de competición API no encontrado."
            binding.tvNoTopScorers.visibility = View.VISIBLE
            binding.topScorersHeader.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        topScorersAdapter = TopScorersAdapter()
        binding.rvTopScorers.apply {
            adapter = topScorersAdapter
            layoutManager = LinearLayoutManager(context)
            // Opcional: Añadir ItemDecoration para líneas divisorias si se desea
        }
    }

    private fun observeViewModel() {
        leagueDetailsViewModel.topScorers.observe(viewLifecycleOwner) { scorers ->
            topScorersAdapter.submitList(scorers)
            val noData = scorers.isNullOrEmpty() && leagueDetailsViewModel.errorTopScorers.value == null && (leagueDetailsViewModel.isLoadingTopScorers.value == false)

            binding.topScorersHeader.visibility = if (noData || leagueDetailsViewModel.errorTopScorers.value != null) View.GONE else View.VISIBLE

            if (noData) {
                binding.tvNoTopScorers.text = "Goleadores no disponibles."
                binding.tvNoTopScorers.visibility = View.VISIBLE
            } else if (!scorers.isNullOrEmpty()) {
                binding.tvNoTopScorers.visibility = View.GONE
            }
        }

        leagueDetailsViewModel.isLoadingTopScorers.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarTopScorers.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.tvNoTopScorers.text = "Cargando goleadores..."
                binding.tvNoTopScorers.visibility = View.VISIBLE
                binding.topScorersHeader.visibility = View.GONE
                binding.rvTopScorers.visibility = View.GONE
            } else {
                binding.rvTopScorers.visibility = View.VISIBLE
                if (topScorersAdapter.itemCount > 0) {
                    binding.tvNoTopScorers.visibility = View.GONE
                    binding.topScorersHeader.visibility = View.VISIBLE
                }
            }
        }

        leagueDetailsViewModel.errorTopScorers.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (topScorersAdapter.itemCount == 0) {
                    binding.tvNoTopScorers.text = it
                    binding.tvNoTopScorers.visibility = View.VISIBLE
                    binding.topScorersHeader.visibility = View.GONE
                    binding.rvTopScorers.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
