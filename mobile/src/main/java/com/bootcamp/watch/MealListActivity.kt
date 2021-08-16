package com.bootcamp.watch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bootcamp.watch.databinding.ActivityMainBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.rubdev.shared.Meal

class MealListActivity : AppCompatActivity(),
    MealListAdapter.Callback,
    GoogleApiClient.ConnectionCallbacks {

    private lateinit var binding: ActivityMainBinding

    private var adapter: MealListAdapter? = null
    private lateinit var client: GoogleApiClient
    private var connectedNode: List<Node>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val meals = MealStore.fetchMeals(this)
        adapter = MealListAdapter(meals, this)
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(this)

        client = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()

        client.connect()


    }

    override fun mealClicked(meal: Meal) {
        val gson = Gson()
        connectedNode?.forEach { node ->
            val bytes = gson.toJson(meal).toByteArray()
            Wearable.MessageApi.sendMessage(client, node.id, "/meal", bytes)
        }
    }

    override fun onConnected(p0: Bundle?) {
        Wearable.NodeApi.getConnectedNodes(client).setResultCallback {
            connectedNode = it.nodes

            Wearable.DataApi.addListener(client) { data ->
                val meal = Gson().fromJson(String(data.first().dataItem.data), Meal::class.java)
                adapter?.updateMeal(meal)
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        connectedNode = null
    }
}
