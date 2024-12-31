package com.example.dicodingstory.ui.story

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ListItemsBinding
import com.example.dicodingstory.response.StoryItem
import com.example.dicodingstory.ui.detail.StoryDetailActivity

class StoryRecyclerAdapter : PagingDataAdapter<StoryItem, StoryRecyclerAdapter.StoryViewHolder>(DIFFERENCE_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bindStoryData(storyItem)
        } else {
            Log.w("StoryRecyclerAdapter", "Null data at $position")
        }
    }

    class StoryViewHolder(private val itemBinding: ListItemsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val imageThumbnail: ImageView = itemView.findViewById(R.id.imageStoryThumbnail)
        private val textName: TextView = itemView.findViewById(R.id.textStoryName)
        private val textShortDescription: TextView = itemView.findViewById(R.id.textStoryDescription)

        fun bindStoryData(story: StoryItem) {
            itemBinding.textStoryName.text = story.name
            itemBinding.textStoryDescription.text = story.description
            Glide.with(itemBinding.root)
                .load(story.photoUrl)
                .into(itemBinding.imageStoryThumbnail)

            itemView.setOnClickListener {
                val detailIntent = Intent(itemView.context, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.EXTRA_ID, story.id)
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imageThumbnail, "image_transition"),
                        Pair(textName, "title_transition"),
                        Pair(textShortDescription, "description_transition"),
                    )
                itemView.context.startActivity(detailIntent,  optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFFERENCE_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean = oldItem == newItem
        }
    }
}
