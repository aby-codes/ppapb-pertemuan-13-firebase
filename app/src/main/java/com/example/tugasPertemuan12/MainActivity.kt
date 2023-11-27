package com.example.tugasPertemuan12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugasPertemuan12.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("budgets")
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private val budgetListLiveData: MutableLiveData<List<Budget>> by lazy {
        MutableLiveData<List<Budget>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
        setupRecyclerView()
        observeBudgets()
    }


    override fun onStart() {
        super.onStart()
        loadNote()
    }

    private fun observeBudgets() {
        budgetListLiveData.observe(this) { budget ->
            noteAdapter.setData(budget.toMutableList())
        }
    }
    private fun loadNote(){
        budgetCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val item = snapshots?.toObjects(Budget::class.java)
            if (item != null) {
                budgetListLiveData.postValue(item)
            }
        }
    }
    private fun intentEdit(id:String, intentType:Int){
        startActivity(Intent(this@MainActivity,EditActivity::class.java).putExtra("id",id).putExtra("intentType",intentType))
    }

    private fun setupListener(){
        with(binding){
            buttonCreate.setOnClickListener{
                intentEdit("0",1)
            }
        }
    }

    private fun setupRecyclerView(){
        noteAdapter = NoteAdapter(arrayListOf(),object :NoteAdapter.OnAdapterListener{
            override fun onClick(budget: Budget) {
                intentEdit(budget.id,0)
            }

            override fun onUpdate(budget: Budget) {
                intentEdit(budget.id,2)
            }

            override fun onDelete(budget: Budget) {
                deleteDialog(budget)
            }

        })
        with(binding){
            listNote.apply {
                layoutManager= LinearLayoutManager(this@MainActivity)
                adapter=noteAdapter
            }
        }
    }
    private fun deleteBudget(budget: Budget) {
        if (budget.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: budget ID is empty!")
            return
        }
        budgetCollectionRef.document(budget.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting budget: ", it)
            }
    }
    private fun deleteDialog(budget: Budget){
        val alertDialog=AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Apakah anda yakin?")
            setNegativeButton("Batal"){dialogInterface,i->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus"){dialogInterface,i->
                dialogInterface.dismiss()
                deleteBudget(budget)
            }
        }
        alertDialog.show()
    }
}