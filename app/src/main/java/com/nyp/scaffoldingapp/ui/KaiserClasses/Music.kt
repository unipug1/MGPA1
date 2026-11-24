package com.nyp.scaffoldingapp.ui.KaiserClasses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime


import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalTime
import java.time.LocalDate

import java.text.SimpleDateFormat
import java.util.Calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


import androidx.lifecycle.viewModelScope
import com.google.common.collect.SetMultimap
import com.nyp.scaffoldingapp.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.util.UUID
import java.time.temporal.TemporalAdjusters
import kotlin.collections.plus
import kotlin.time.Duration

data class Song(
    val name : String,
    var fileID : Int,
    val imgID : Int = R.drawable.default_icon,
    val id: String = UUID.randomUUID().toString()
)

data class SongList(
    var songs : MutableList<Song> = mutableListOf<Song>()

)

data class Playlist(
    var list : SongList,
    var name : String,
    val imgID : Int = R.drawable.default_icon,
    val id: String = UUID.randomUUID().toString()
)

class MusicViewModel : ViewModel() {
    private val _musicList = MutableStateFlow(SongList(mutableListOf()))
    val musicList : StateFlow<SongList> = _musicList.asStateFlow()

    private val _selectedMusic = MutableStateFlow<Song?>(null)
    val selectedMusic : StateFlow<Song?> = _selectedMusic.asStateFlow()

    private val _musicPlaylists = MutableStateFlow<MutableList<Playlist>>(mutableListOf())
    val musicPlaylists : StateFlow<MutableList<Playlist>> = _musicPlaylists.asStateFlow()

    fun GetPlaylists() : MutableList<Playlist>{
        return _musicPlaylists.value
    }

    fun AddPlaylist(playlist : Playlist) {
        _musicPlaylists.value = (musicPlaylists.value + playlist).toMutableList()
    }

    fun AddToMusicList(song : Song) : MutableList<Song>{
        if (song in _musicList.value.songs){
            _musicList.value.songs = (musicList.value.songs - song).toMutableList()
        }

        _musicList.value = musicList.value.copy(
            songs = (musicList.value.songs + song).toMutableList()
        )
        return musicList.value.songs
    }


    fun SetMusicList(list : List<Song>) {
        _musicList.value = _musicList.value.copy(songs = list.toMutableList())
    }

    fun SelectMusic(song : Song?) {
        _selectedMusic.value = song
    }

    fun RemoveSongFromPlaylist(playlist: Playlist, song: Song) {
        _musicPlaylists.update { currentPlaylists ->
            currentPlaylists.map { currPlaylist ->
                if (currPlaylist.id == playlist.id) {
                    currPlaylist.copy(
                        list = SongList(
                            (currPlaylist.list.songs.filter {
                                it != song
                            }).toMutableList()
                        )
                    )
                } else {
                    currPlaylist
                }

            }.toMutableList()
        }
    }

    fun AddSongTpPlaylist(playlist: Playlist, song: Song) {
        _musicPlaylists.update { currentPlaylists ->
            currentPlaylists.map { currPlaylist ->
                if (currPlaylist.id == playlist.id) {
                    currPlaylist.copy(
                        list = SongList((currPlaylist.list.songs + song).toMutableList())
                    )
                } else {
                    currPlaylist
                }

            }.toMutableList()
        }
    }

    fun GetPlaylist(id: String?): Flow<Playlist?> {
        return _musicPlaylists.map { playlists ->
            playlists.firstOrNull({it.id == id})
        }
    }

    fun GetSongAtIndex(index : Int) : Song{
        return _musicList.value.songs[index]
    }
    fun GetSelectedSong() : Song?{
        return _selectedMusic.value
    }
}