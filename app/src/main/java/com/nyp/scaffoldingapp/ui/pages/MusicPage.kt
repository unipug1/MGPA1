package com.nyp.scaffoldingapp.ui.pages

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.nyp.scaffoldingapp.R
import com.nyp.scaffoldingapp.ui.KaiserClasses.MusicViewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.Song
import java.io.File

object MusicList{
    val musicList = listOf(
        Song("Up in the clouds", R.raw.song_up_in_the_clouds),
        Song("1000 Handz", R.raw.handz, R.drawable.handz),
        Song("Jingle Bells", R.raw.happy_jingle_bells, R.drawable.jingle_bells),
        Song("Joy To The World", R.raw.joy_to_the_world, R.drawable.joy_to_the_world),
        Song("LoFi Christmas", R.raw.lofi_christmas),
        Song("Midnight Somewhere", R.raw.midnight_somewhere),
        Song("Paradise with a sky of flames", R.raw.paradise_with_a_sky_of_flames),
        Song("So many virtues to be signaled", R.raw.so_many_virtues_to_be_signaled),
        Song("Sun to Darkness Moon to Blood", R.raw.the_sun_to_darkness_moon_to_blood),
        Song("Every Step Counts", R.raw.every_step_counts)
    )
}

@Composable
fun MusicPage(viewModel: MusicViewModel) {


    val context = LocalContext.current



    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val items = MusicList.musicList.map { song ->
                val id = song.fileID
                MediaItem.fromUri("android.resource://${context.packageName}/$id")
            }
            this.setMediaItems(items, 0, 0L)
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(player) {
        onDispose { player.release() }
    }
    DisplayMusicList(viewModel, context, player)
//
//    Button(onClick = {
//        if (player.isPlaying) player.pause() else player.play()
//        isPlaying = player.isPlaying
//    }) {
//        Text(if (isPlaying) "Pause" else "Play")
//    }
}
enum class MusicSelectionPage{
    BROWSE,
    PLAYLIST
}

@Composable
fun MusicSearchBar(list : MutableList<Song>) : List<Song>{
    var returnList = listOf<Song>()

    var searchVal by rememberSaveable() { mutableStateOf("") }

    OutlinedTextField(readOnly = false, value = searchVal, onValueChange = {searchVal = it}, label = { Text("Playlist Title") }, singleLine = true)

    returnList = list.filter { it.name.contains(searchVal, ignoreCase = true) }

    return returnList
}

@Composable
fun BrowseMusic(viewModel: MusicViewModel, player : ExoPlayer, context : Context, onSelect : (Song) -> Unit, modifier: Modifier){

    val pageItems = 3

    var pageIndex by remember { mutableStateOf(0) }

    var sortedSongs = remember { mutableStateOf(listOf<Song>()) }

    Scaffold(modifier = modifier,
        topBar = {
            Column() {
                sortedSongs.value = MusicSearchBar(MusicList.musicList.toMutableList())
                Row() {
                    IconButton(onClick = { pageIndex-- }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { pageIndex++ }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            pageIndex =
                Math.clamp(pageIndex.toLong(), 0, (sortedSongs.value.size / 3).toInt())

            for (i in 1..pageItems) {
                val musicIndex = pageIndex * pageItems + i - 1
                if (musicIndex < sortedSongs.value.size && musicIndex >= 0) {
                    val music = sortedSongs.value[musicIndex]
                    Button(
                        onClick = {
                            LoadSong(viewModel, context, player, music)
                            viewModel.AddToMusicList(music)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(music.imgID),
                                contentDescription = "Music Icon",
                                modifier = Modifier.height(80.dp).padding(5.dp)
                            )
                            Text(
                                music.name,
                                color = Color.Black,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayMusicPlaylists(viewModel: MusicViewModel, context : Context, player : ExoPlayer, modifier : Modifier, onPlay : (Song)->Unit, onPlaylist : (List<Song>)->Unit){
    var playlists = viewModel.GetPlaylists()
    var openedPlaylistID by rememberSaveable() { mutableStateOf<String?>(null) }
    var editPlaylistScreen by rememberSaveable() { mutableStateOf(false) }

    if (openedPlaylistID != null){
        val playlist = viewModel.GetPlaylists().firstOrNull({ it.id == openedPlaylistID })
        if (playlist == null)
            return
        Dialog(
            onDismissRequest = {openedPlaylistID = null},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.6f),
                shape = RoundedCornerShape(20.dp)
            ) {
                LazyColumn(Modifier.fillMaxWidth(), userScrollEnabled = true){
                    item(playlist.id) {
                        Column() {
                            Image(
                                painter = painterResource(playlist.imgID),
                                contentDescription = "Playlist Icon",
                                modifier = Modifier.padding(5.dp)
                            )
                            Text(playlist.name)
                            Row(horizontalArrangement = Arrangement.Center){
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = {
                                    viewModel.SetMusicList(playlist.list.songs)
                                    onPlaylist(viewModel.musicList.value.songs)}) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = "Play"
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = {
                                    viewModel.SetMusicList(playlist.list.songs.shuffled())
                                    onPlaylist(viewModel.musicList.value.songs)}) {
                                    Icon(
                                        Icons.Default.Shuffle,
                                        contentDescription = "Shuffle"
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    items(playlist.list.songs){ music ->
                        Row(Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                onPlay(music)},
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                Image(
                                    painter = painterResource(music.imgID),
                                    contentDescription = "Music Icon",
                                    modifier = Modifier.height(80.dp).padding(5.dp)
                                )
                                Text(
                                    music.name,
                                    color = Color.Black,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    else {
        Column(modifier = modifier) {
            Row(Modifier.fillMaxWidth()){
                Text("Add playlist")
                Spacer(Modifier.width(5.dp))
                Button(
                    onClick = { editPlaylistScreen = true },
                    modifier = Modifier
                        .size(30.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(color = Color.Black, width = 2.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text(text = "+", fontSize = 24.sp) }
            }
            LazyColumn(userScrollEnabled = true) {
                playlists.forEach { playlist ->
                    item(playlist.id) {
                        Row(Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(playlist.imgID),
                                contentDescription = "Music Icon",
                                modifier = Modifier.height(80.dp).padding(5.dp)
                            )
                            Text(
                                playlist.name,
                                color = Color.Black,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                    items(playlist.list.songs) {

                    }
                }
            }
        }
    }

}

@Composable
fun DisplayMusicList(viewModel: MusicViewModel, context : Context, player : ExoPlayer){
    var selectedMusicPage by rememberSaveable { mutableStateOf(MusicSelectionPage.BROWSE) }


    val selectedMusic by viewModel.selectedMusic.collectAsStateWithLifecycle()
    val musicList by viewModel.musicList.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {Row(Modifier.fillMaxWidth().padding(15.dp)){
            Button(onClick = {selectedMusicPage = MusicSelectionPage.BROWSE}){
                Text("Music")
            }
            Button(onClick = {selectedMusicPage = MusicSelectionPage.PLAYLIST}){
                Text("Playlist")
            }
        } },
        bottomBar = {
            selectedMusic?.let {
                MusicPlayer(
                    it,
                    player,
                    {
                        val selectedIndex =
                            musicList.songs.indexOfFirst({ thisSong -> thisSong == selectedMusic })

                        if (selectedIndex > 0) {
                            val newSong = musicList.songs[selectedIndex - 1]
                            LoadSong(viewModel, context, player, newSong)
                        }
                    },
                    {
                        val selectedIndex =
                            musicList.songs.indexOfFirst({ thisSong -> thisSong == selectedMusic })

                        if (selectedIndex < musicList.songs.size - 1) {
                            val newSong = musicList.songs[selectedIndex + 1]
                            LoadSong(viewModel, context, player, newSong)

                        } else {
                            var newSong: Song
                            do {
                                newSong = MusicList.musicList.random()
                            } while (newSong == selectedMusic)
                            LoadSong(viewModel, context, player, newSong)
                            viewModel.AddToMusicList(newSong)
                        }
                    })
            }
        }
    ) { paddingValues ->
        if (selectedMusicPage == MusicSelectionPage.BROWSE)
            BrowseMusic(viewModel, player, context, {}, Modifier.padding(paddingValues))
        else
            DisplayMusicPlaylists(viewModel, context, player, Modifier.padding(paddingValues),
                { music ->
                LoadSong(viewModel, context, player, music)
                viewModel.AddToMusicList(music)
            })
    }


}

@Composable
fun LoadPlaylist(viewModel : MusicViewModel,context : Context,player : ExoPlayer, resIds: List<Song>, startIndex: Int = 0) {
    val items = resIds.map { song ->
        val id = song.fileID
        MediaItem.fromUri("android.resource://${context.packageName}/$id")
    }
    player.setMediaItems(items, startIndex, 0L)
    player.prepare()
    viewModel.SelectMusic(resIds[startIndex])
}


fun LoadSong(viewModel: MusicViewModel, context : Context, player: ExoPlayer, song: Song){
    val item = MediaItem.fromUri("android.resource://${context.packageName}/${song.fileID}")
    player.setMediaItem(item)
    viewModel.SelectMusic(song)
}

@Composable
fun MusicPlayer(music : Song, player : ExoPlayer, onPrev : () -> Unit, onNext : () -> Unit){
    var isPlaying by rememberSaveable() { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().height(100.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(music.imgID),
            contentDescription = "Music Icon",
            Modifier.height(100.dp).padding(15.dp)
        )
        Text(music.name, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onPrev() }
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        IconButton(onClick = { if (isPlaying) player.pause() else player.play()
            isPlaying = !isPlaying
        }) {
            Icon(
                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
        IconButton(
            onClick = { onNext() }
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }
}