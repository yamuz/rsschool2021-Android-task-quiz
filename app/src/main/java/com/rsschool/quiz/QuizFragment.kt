package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.rsschool.quiz.databinding.FragmentQuizBinding

class QuizFragment: Fragment() {


    lateinit var answers: MutableList<String>
    var selectedAnswers:IntArray = intArrayOf(-1,-1,-1,-1,-1)
    var correctAnswers = 0
    private var questionIndex  = 0
    lateinit var binding:FragmentQuizBinding
    lateinit var currentQuestion:Question

    private val questions: MutableList<Question> = mutableListOf(
        Question(text = "What is Android Jetpack?",
            answers = listOf("all of these", "tools", "documentation", "libraries", "pack of jets"),
            correctAnserIndex =0 ),
        Question(text = "Base class for Layout?",
            answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot", "Layout"),
            correctAnserIndex =0),
        Question(text = "Layout for complex Screens?",
            answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout", "Complexlayout"),
            correctAnserIndex =0),
        Question(text = "Pushing structured data into a Layout?",
            answers = listOf("Data Binding", "Data Pushing", "Set Text", "OnClick","View Binding"),
            correctAnserIndex =0),
        Question(text = "Inflate layout in fragments?",
            answers = listOf("onCreateView", "onViewCreated", "onCreateLayout", "onInflateLayout", "onAttach"),
            correctAnserIndex =0 )
    )

    data class Question(
        val text: String,
        val answers: List<String>,
        val correctAnserIndex:Int
    )
    override fun onCreateView(    inflater: LayoutInflater,
                                 container: ViewGroup?,
                        savedInstanceState: Bundle?):  View
    {
        try {
            val args = QuizFragmentArgs.fromBundle(requireArguments())
            args.let {
                questionIndex = args.questionIndex ?: 0
                selectedAnswers = args.selectedAnswers ?: intArrayOf(-1, -1, -1, -1, -1)
            }
        } catch(exc:Exception){
            questionIndex =  0
            selectedAnswers = intArrayOf(-1, -1, -1, -1, -1)
        }

        when (questionIndex){
            0->activity?.setTheme(R.style.Theme_Quiz)
            1->activity?.setTheme(R.style.Theme_Quiz_First)
            2->activity?.setTheme(R.style.Theme_Quiz_Second)
            3->activity?.setTheme(R.style.Theme_Quiz_Third)
            4->activity?.setTheme(R.style.Theme_Quiz_Fourth)
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false)

        setAnswers()

        binding.nextButton.text = if (questionIndex==questions.lastIndex) "Submit" else "Next"
        binding.toolbar.navigationIcon =
                 if (questionIndex==0) null
                 else activity?.let {
                     getDrawable(it, R.drawable.ic_baseline_chevron_left_24)
                 }

        binding.previousButton.isClickable = (questionIndex>0)
        binding.previousButton.alpha       = if (questionIndex>0) 1.0f else 0.3f

        with(binding.nextButton) {
                val checkedId = binding.radioGroup.checkedRadioButtonId
                isClickable = if (checkedId > -1) true else false
                alpha       = if (checkedId > -1) 1.0f  else 0.3f
                //getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        }
        binding.game = this
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                with(binding.nextButton) {
                    isClickable = if (checkedId > -1) true else false
                    alpha       = if (checkedId > -1) 1.0f  else 0.3f
                }
                var answerId = -1
                when(checkedId){
                    R.id.option_one->  answerId=0
                    R.id.option_two->  answerId=1
                    R.id.option_three->answerId=2
                    R.id.option_four-> answerId=3
                    R.id.option_five-> answerId=4
                }
                selectedAnswers[questionIndex] = answerId

            }

        binding.nextButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {

                    val checkedId = binding.radioGroup.checkedRadioButtonId
                    if (checkedId == -1)
                        return

                    //if last question -Submit click
                    if (questionIndex == questions.lastIndex){
                        val listQuestionAnswer = mutableListOf<String>()

                        for ((index, q) in questions.withIndex()){
                            listQuestionAnswer.add("${index + 1}) ${questions[index].text} " +
                                    System.lineSeparator() + "${questions[index].answers[selectedAnswers[index]]}.")
                            if  (q.correctAnserIndex== selectedAnswers[index])
                                correctAnswers++
                        }

                        view?.findNavController()?.navigate(
                            QuizFragmentDirections.actionQuizFragmentToResultsFragment(correctAnswers, questions.size,
                                listQuestionAnswer.toTypedArray()  ))

                    } else { //Next click
                        questionIndex = Math.min(++questionIndex, questions.lastIndex)
                        binding.invalidateAll()
                        //using navigation action to frsgment itself
                        view?.findNavController()?.navigate(
                            QuizFragmentDirections.actionQuizFragmentSelf(questionIndex, selectedAnswers))

                    }

                }
            })

        binding.previousButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                questionIndex = Math.max(--questionIndex, 0)
                binding.invalidateAll()

                //using navigation action to frsgment itself
                view?.findNavController()?.navigate(
                    QuizFragmentDirections.actionQuizFragmentSelf(questionIndex, selectedAnswers))
            }

        })
        return binding.root
        }


    private fun setAnswers(){
        currentQuestion = questions[questionIndex]
        answers   = questions[questionIndex].answers.toMutableList()
        val selId = selectedAnswers[questionIndex]

        if (selId >=0) {
            when (selId) {
                0 -> binding.radioGroup.check(R.id.option_one)
                1 -> binding.radioGroup.check(R.id.option_two)
                2 -> binding.radioGroup.check(R.id.option_three)
                3 -> binding.radioGroup.check(R.id.option_four)
                4 -> binding.radioGroup.check(R.id.option_five)
            }
        }
        else {
            binding.radioGroup.clearCheck()
        }
        binding.toolbar.title = " QUESTION " + (questionIndex +1).toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ResultsFragment()
    }


}