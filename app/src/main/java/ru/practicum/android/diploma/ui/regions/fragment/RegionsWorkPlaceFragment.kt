package ru.practicum.android.diploma.ui.regions.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterRegionBinding
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.ui.countries.adapter.RegionsAdapter
import ru.practicum.android.diploma.ui.countries.viewmodel.CountriesState
import ru.practicum.android.diploma.ui.regions.viewmodel.RegionsState
import ru.practicum.android.diploma.ui.regions.viewmodel.RegionsViewModel
import ru.practicum.android.diploma.util.REGION_BACKSTACK_KEY

class RegionsWorkPlaceFragment : Fragment() {

    private var _binding: FragmentFilterRegionBinding? = null
    private val binding: FragmentFilterRegionBinding
        get() = _binding!!

    private val viewModel by viewModel<RegionsViewModel>()
    private var textWatcher: TextWatcher? = null
    private val adapter = RegionsAdapter { region ->
        selectRegion(region)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFilterRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rwResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rwResult.adapter = adapter

        val areaId = null

        viewModel.getRegions(areaId)
        viewModel.state.observe(viewLifecycleOwner, ::renderState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                with(binding.searchTextInputLayout) {
                    if (s.isNullOrBlank()) {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        setEndIconDrawable(R.drawable.search)
                        findViewById<View>(com.google.android.material.R.id.text_input_end_icon).isClickable = false
                    } else {
                        setEndIconDrawable(R.drawable.close)
                        setEndIconOnClickListener { s.clear() }
                    }
                }
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
    }

    private fun selectRegion(region: Region) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(REGION_BACKSTACK_KEY, region)
        findNavController().popBackStack()
    }

    private fun renderState(state: RegionsState) {
        when (state) {
            is RegionsState.Loading -> showLoading()
            is RegionsState.Error -> showError()
            is RegionsState.Content -> showContent(state.data)
        }
    }

    private fun showLoading() {
        binding.rwResult.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.pbLoading.isVisible = true
    }

    private fun showError() {
        binding.rwResult.isVisible = false
        binding.llPlaceholder.isVisible = true
        binding.pbLoading.isVisible = false

        binding.ivPlaceholders.setImageDrawable(requireContext().getDrawable(R.drawable.placeholder_get_list))
        binding.tvPlaceholders.text = requireContext().getText(R.string.cant_get_list)
    }

    private fun showContent(data: List<Region>) {
        binding.rwResult.isVisible = true
        binding.llPlaceholder.isVisible = false
        binding.pbLoading.isVisible = false

        adapter.clear()
        adapter.setData(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
