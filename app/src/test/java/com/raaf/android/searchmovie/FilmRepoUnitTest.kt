package com.raaf.android.searchmovie

import com.raaf.android.searchmovie.api.FilmFetcher
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FilmRepoUnitTest {

    @Mock
    var mockedFilmFetcher = mock(FilmFetcher::class.java)

    @Test
    fun findNumberOfItemTest() {
        val listItems = listOf("item1", "item2", "item3")
        val containedElement = "item1"
        val notContainedElement = "item"

        assertNotEquals(-1, mockedFilmFetcher.findNumberOfItem(listItems, containedElement))
        assertEquals(-1, mockedFilmFetcher.findNumberOfItem(listItems, notContainedElement))
    }
}