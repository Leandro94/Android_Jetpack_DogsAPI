package com.leandro.dogs.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.leandro.dogs.R
import com.leandro.dogs.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {
    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter =
        DogsListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager =  LinearLayoutManager(context)
            adapter = dogsListAdapter
        }
        refreshLayout.setOnRefreshListener {
            dogsList.visibility = View.GONE
            textListError.visibility = View.GONE
            pgBarLoading.visibility = View.VISIBLE
            viewModel.refreshBypassCache()
            refreshLayout.isRefreshing = false
        }
        observeViewModel()
    }

    fun observeViewModel(){
        //lista
        viewModel.dogs.observe(this, Observer {dogs->
            dogs?.let {
                    dogsList.visibility = View.VISIBLE
                    dogsListAdapter.updateDogList(dogs)
            }
        })
        //erro para carregar itens da lista
        viewModel.dogsLoadError.observe(this, Observer {isError->
            isError?.let {
                textListError.visibility = if(it) View.VISIBLE else View.GONE
            }
        })
        //erro da acao de carregar
        viewModel.loading.observe(this, Observer { isLoading->
            isLoading?.let {
                pgBarLoading.visibility = if(it) View.VISIBLE else View.GONE
                Log.d("XXX","IT: "+it.toString())
                Log.d("XXX","IS LOADING: "+isLoading)
                //se for true tem erro, então não mostra a lista e esconde campo de erro
                if(it){
                    textListError.visibility = View.GONE
                    dogsList.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.action_listFragment_to_settingsFragment->{
                view?.let{
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToSettingsFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
