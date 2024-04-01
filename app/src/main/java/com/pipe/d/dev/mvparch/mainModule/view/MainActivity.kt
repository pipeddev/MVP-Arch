package com.pipe.d.dev.mvparch.mainModule.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pipe.d.dev.mvparch.common.SportEvent
import com.pipe.d.dev.mvparch.databinding.ActivityMainBinding
import com.pipe.d.dev.mvparch.mainModule.presenter.MainPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() , OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(this)
        presenter.onCreate()

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            //adapter.clear()
            //getEvents()
            //binding.btnAd.visibility = View.VISIBLE
            lifecycleScope.launch { presenter.refresh() }
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    //binding.srlResults.isRefreshing = true
                    //val events = getAdEventsInRealtime()
                    //EventBus.instance().publish(events.first())
                    lifecycleScope.launch { presenter.registerAd() }
                }

            }

            setOnLongClickListener { _ ->
                lifecycleScope.launch {
                    //binding.srlResults.isRefreshing = true
                    //EventBus.instance().publish(SportEvent.CloseAdEvent)
                    //view.visibility = View.GONE
                    lifecycleScope.launch { presenter.closeAd() }
                }
                true
            }
        }
    }

    /*private fun getEvents() {
        lifecycleScope.launch {
            /*val events = getResultEventsInRealtime()
            events.forEach { event ->
                delay(someTime())
                EventBus.instance().publish(event)
            }*/
            presenter.getEvents()
        }
    }*/

    override fun onStart() {
        super.onStart()
        //binding.srlResults.isRefreshing = true
        //getEvents()
        lifecycleScope.launch { presenter.getEvents() }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    /*
        OnclickListener
     */
    override fun onClick(result: SportEvent.ResultSuccess) {
        lifecycleScope.launch {
            //binding.srlResults.isRefreshing = true
            //EventBus.instance().publish(SportEvent.SaveEvent)
            //SportService.instance().saveResult(result)
            presenter.saveResult(result)
        }
    }

    /*
    * View Layer
    * */

    fun add(event: SportEvent.ResultSuccess) {
        adapter.add(event)
    }

    fun clearAdapter(){
        adapter.clear()
    }

    suspend fun showAdUI(isVisible: Boolean) = withContext(Dispatchers.Main) {
        binding.btnAd.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun showProgress(isVisible: Boolean) {
        binding.srlResults.isRefreshing = isVisible
    }

    suspend fun showToast(msg: String) = withContext(Dispatchers.Main){
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
    }

    fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}