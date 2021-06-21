package com.rsschool.quiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.rsschool.quiz.databinding.FragmentResultsBinding

private var correctAnswers :Int = 0
private var totalQuestions :Int = 0

class ResultsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        //val binding = FragmentResultsBinding.inflate(inflater, container,false)

        val binding = DataBindingUtil.inflate<FragmentResultsBinding>(inflater,
            R.layout.fragment_results, container,false)

        binding.shareButton.setOnClickListener {  shareSuccess() }
        binding.restartQuizButton.setOnClickListener {
            view?.findNavController()?.navigate(
                ResultsFragmentDirections.actionResultsFragmentToQuizFragment(0, intArrayOf(-1,-1,-1,-1,-1)))
        }
        binding.exitButton.setOnClickListener { ActivityCompat.finishAffinity(requireActivity()) }


        val args = ResultsFragmentArgs.fromBundle(requireArguments())
        correctAnswers = args.correctAnswers
        totalQuestions = args.totalQuestions
        binding.textView.text = getString(R.string.result_text_share, correctAnswers, totalQuestions)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    view?.findNavController()?.navigate(
                        ResultsFragmentDirections.actionResultsFragmentToQuizFragment(0, intArrayOf(-1,-1,-1,-1,1)))
                }
            }
            )
        return binding.root
    }

    private fun getShareIntent() : Intent {
        val args = ResultsFragmentArgs.fromBundle(requireArguments())

        val answers = args.listQuestionAnswer.joinToString(System.lineSeparator())
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setText(getString(R.string.result_text_share, args.correctAnswers, args.totalQuestions) + System.lineSeparator() +
                     answers)
            .setType("text/plain")
            .intent
    }

    private fun shareSuccess() {
        startActivity(getShareIntent())
    }


}