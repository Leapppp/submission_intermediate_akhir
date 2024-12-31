package com.example.dicodingstory.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.response.StoryItem
import com.example.dicodingstory.ui.DataDummy
import com.example.dicodingstory.ui.MainDispatcherRule
import com.example.dicodingstory.ui.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val token = "dummyToken"
        val dummyStoryList = DataDummy.generateDummyList()
        val pagingData = QuotePagingSource.snapshot(dummyStoryList)

        Mockito.`when`(repository.getUserToken()).thenReturn(token)
        val expectedLiveData = MutableLiveData<PagingData<StoryItem>>()
        expectedLiveData.value = pagingData
        Mockito.`when`(repository.getStoryPagingData(token)).thenReturn(expectedLiveData)

        val mainViewModel = StoryFeedViewModel(repository)
        val actualData = mainViewModel.storyData.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRecyclerAdapter.DIFFERENCE_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStoryList.size, differ.snapshot().size)
        Assert.assertEquals(dummyStoryList[0], differ.snapshot()[0])
    }


    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val token = "dummyToken"
        Mockito.`when`(repository.getUserToken()).thenReturn(token)
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expectedLiveData = MutableLiveData<PagingData<StoryItem>>()
        expectedLiveData.value = data
        Mockito.`when`(repository.getStoryPagingData(token)).thenReturn(expectedLiveData)

        val mainViewModel = StoryFeedViewModel(repository)

        val actualData: PagingData<StoryItem> = mainViewModel.storyData.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRecyclerAdapter.DIFFERENCE_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class QuotePagingSource : PagingSource<Int, StoryItem>() {
    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}