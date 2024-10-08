package com.koi.projectlib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.koi.projectlib.dialog.CustomDialog
import com.koi.projectlib.R
import com.koi.projectlib.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container ,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHighlightMore.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_highlightMoreFragment)
        }

        binding.btnHighlight.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_highlightGuideFragment)
        }

        binding.btnPopup.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_popupFragment)
        }

        binding.btnRecyclerView.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recyclerViewFragment)
        }

        binding.btnDialog.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        CustomDialog().show(childFragmentManager, CustomDialog.TAG)
    }

    companion object {
        fun create() : MainFragment {
            return MainFragment().apply {
                arguments = bundleOf(
                
                )
            }
        }
    }
}