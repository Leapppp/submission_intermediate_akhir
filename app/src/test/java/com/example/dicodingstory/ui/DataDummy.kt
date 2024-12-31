package com.example.dicodingstory.ui

import com.example.dicodingstory.response.StoryItem

object DataDummy {
    fun generateDummyList(): List<StoryItem> {
        val stories = mutableListOf<StoryItem>()
        for (i in 1..10) {
            stories.add(
                StoryItem(
                    id = "story-$i",
                    name = "User $i",
                    description = "Description $i",
                    photoUrl = "https://via.placeholder.com/600x400.png?text=Story+$i",
                    createdAt = "2024-01-01T00:00:00Z",
                    lat = 0.0,
                    lon = 0.0
                )
            )
        }
        return stories
    }
}
