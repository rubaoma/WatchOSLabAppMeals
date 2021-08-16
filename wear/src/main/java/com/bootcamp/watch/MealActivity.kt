package com.bootcamp.watch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bootcamp.watch.databinding.ActivityMealBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.rubdev.shared.Meal
import android.support.wearable.activity.ConfirmationActivity
import android.view.View

class MealActivity : Activity(),
    GoogleApiClient.ConnectionCallbacks {

    private lateinit var binding: ActivityMealBinding
    private lateinit var client: GoogleApiClient
    private var currentMeal: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(Wearable.API)
            .build()
        client.connect()

        binding.star.setOnClickListener {
            it.visibility = View.GONE
            binding.star2.visibility = View.VISIBLE
            sendLike()
        }
        binding.star2.setOnClickListener {
            it.visibility = View.GONE
            binding.star.visibility = View.VISIBLE
            sendLike()
        }

    }

    override fun onConnected(p0: Bundle?) {
        Wearable.MessageApi.addListener(client) { messageEvent ->
            currentMeal = Gson().fromJson(String(messageEvent.data), Meal::class.java)
            updateView()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.w("Wear", "Google Api Client connection suspended!")
    }

    private fun updateView() {
        currentMeal?.let {
            binding.apply {
                mealTitle.text = it.title
                calories.text = getString(R.string.calories, it.calories)
                ingredients.text = it.ingredients.joinToString(separator = ", ")
            }
        }
    }

    private fun sendLike() {
        currentMeal?.let {
            val bytes = Gson().toJson(it.copy(favorited = true)).toByteArray()
            Wearable.DataApi.putDataItem(
                client,
                PutDataRequest.create("/liked")
                    .setData(bytes)
                    .setUrgent()
            ).setResultCallback {
                showConfirmationScreen()
            }
        }

    }

    private fun showConfirmationScreen() {
        val intent = Intent(this, ConfirmationActivity::class.java)
        intent.putExtra(
            ConfirmationActivity.EXTRA_ANIMATION_TYPE,
            ConfirmationActivity.SUCCESS_ANIMATION
        )
        intent.putExtra(
            ConfirmationActivity.EXTRA_MESSAGE,
            getString(R.string.starred_meal)
        )
        startActivity(intent)
    }

}
