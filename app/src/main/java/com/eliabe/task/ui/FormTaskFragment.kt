package com.eliabe.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.eliabe.task.R
import com.eliabe.task.databinding.FragmentFormTaskBinding
import com.eliabe.task.util.initToolbar
import com.eliabe.task.util.showBottomSheet
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class FormTaskFragment : Fragment() {
    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var status: Status = Status.TODO
    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListener()
        reference = Firebase.database.reference
        auth = Firebase.auth
    }
    private fun initListener(){
        binding.buttonSave.setOnClickListener {
            valideData()
        }
        binding.radioGroup.setOnCheckedChangeListener { _, id-> status =
            when(id){
                R.id.rbTodo -> Status.TODO
                R.id.rbDoing -> Status.DOING
                else -> Status.DONE
            }
        }
    }

    private fun valideData(){
        val description = binding.editTextDescricao.text.toString().trim()
        if (description.isNotBlank()){
            binding.progressBar.isVisible = true
            if (newTask) task = Task()
            task.id = reference.database.reference.push().key?:""
            task.description = description
            task.status = status
            saveTask()
        }else{
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }

    private fun saveTask(){
        reference
            .child("task")
            .child(auth.currentUser?.uid?:"")
            .child(task.id)
            .setValue(task).addOnCompleteListener { result ->
                if (result.isSuccessful){
                    Toast.makeText(
                        requireContext(),
                        R.string.text_save_sucess_form_task_fragment,
                        Toast.LENGTH_SHORT).show()
                    if (newTask){
                        findNavController().popBackStack()
                    }else{
                        binding.progressBar.isVisible = false
                    }
                }else{
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = getString(R.string.error_generic))
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}