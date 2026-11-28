package com.nyp.scaffoldingapp.ui.music

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.nyp.scaffoldingapp.R
import kotlinx.coroutines.delay

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
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(5.dp)
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
fun CreatePlaylistScreen(viewModel: MusicViewModel, onCreate : (Playlist)->Unit, onDismiss : ()->Unit, modifier : Modifier){
    val songs = rememberSaveable() { mutableStateListOf<Song>() }
    var name by rememberSaveable() { mutableStateOf("") }
    var imgID by remember { mutableStateOf(R.drawable.default_icon) }
    var addSongSearch by rememberSaveable() { mutableStateOf(false) }
    var deleteSongDropdown by remember { mutableStateOf<Song?>(null) }

    if (!addSongSearch) {
        Scaffold(
            topBar = {
                Row(Modifier.padding(30.dp)) {
                    IconButton(
                        onClick = { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text("New Playlist", color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            if (songs.size > 0 && name != "") onCreate(Playlist(SongList(songs.toMutableList()), name, imgID))
                            onDismiss()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "Back",
                            tint = if (songs.size > 0 && name != "") Color.Green else Color.Gray
                        )
                    }
                }

            },
            modifier = modifier
        ) { paddingValues ->
            Column(Modifier
                .padding(paddingValues)
                .padding(horizontal = 30.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it })
                LazyColumn(userScrollEnabled = true) {
                    item() {
                        Row(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { addSongSearch = true },
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
                    }
                    items(songs) { song ->
                        Row(Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(song.imgID),
                                contentDescription = "Music Icon",
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(5.dp)
                            )
                            Text(
                                song.name,
                                color = Color.Black,
                                modifier = Modifier.padding(5.dp)
                            )
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { deleteSongDropdown = song}) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    tint = Color.Black,
                                    contentDescription = "Settings"
                                )
                            }
                            DropdownMenu((deleteSongDropdown == song), {deleteSongDropdown = null}, offset = DpOffset(y = (0).dp, x = (-40).dp)){
                                DropdownMenuItem(text = {Text("Remove")}, onClick = {songs.remove(song)})

                            }
                        }
                    }
                }
            }
        }
    }
    else {
        SelectSong({songs.add(it)}, {addSongSearch = false}, modifier)
    }
}

@Composable
fun SelectSong(onSelect: (Song) -> Unit, onDismiss: () -> Unit, modifier : Modifier){
    var songs : List<Song> = listOf()
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    Scaffold(
        topBar = {
            Column(modifier) {
                Row(Modifier.padding(30.dp)) {
                    IconButton(
                        onClick = { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text("Select Song", color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { selectedSong?.let{
                            onSelect(it)
                            onDismiss()
                        } }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "Back",
                            tint = selectedSong?.let{Color.Green}?:Color.Gray
                        )
                    }
                }
                songs = MusicSearchBar(MusicList.musicList.toMutableList())
            }
        }
    ){ paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            items(songs){ song->
                Button(onClick = { selectedSong = song }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black)) {
                    Row(Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(song.imgID),
                            contentDescription = "Music Icon",
                            modifier = Modifier
                                .height(80.dp)
                                .padding(5.dp)
                        )
                        Text(
                            song.name,
                            color = Color.Black,
                            modifier = Modifier.padding(5.dp)
                        )
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

    var deleteSongQueue by remember { mutableStateOf<Pair<Song, Playlist>?>(null) }

    var deleteSongDropdown by remember { mutableStateOf<Song?>(null) }

    val selectedPlaylist by viewModel.GetPlaylist(openedPlaylistID)
        .collectAsStateWithLifecycle(initialValue = null)

    if (editPlaylistScreen){
        CreatePlaylistScreen(viewModel, {
            viewModel.AddPlaylist(it)
        },
            {editPlaylistScreen = false},
            modifier)
    }
    else if (openedPlaylistID != null && selectedPlaylist != null){
        val playlist = selectedPlaylist!!
        Scaffold(modifier = modifier,
            topBar = {
                IconButton(
                    onClick = { openedPlaylistID = null }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(Modifier
                .fillMaxWidth()
                .padding(paddingValues), userScrollEnabled = true) {
                item(playlist.id) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(playlist.imgID),
                            contentDescription = "Playlist Icon",
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(playlist.name)
                        Row(horizontalArrangement = Arrangement.Center) {
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = {
                                viewModel.SetMusicList(playlist.list.songs)
                                onPlaylist(viewModel.musicList.value.songs)
                            }) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Play"
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = {
                                viewModel.SetMusicList(playlist.list.songs.shuffled())
                                onPlaylist(viewModel.musicList.value.songs)
                            }) {
                                Icon(
                                    Icons.Default.Shuffle,
                                    contentDescription = "Shuffle"
                                )
                            }
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                items(playlist.list.songs) { song ->
                    Row(Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                onPlay(song)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(song.imgID),
                                contentDescription = "Music Icon",
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(5.dp)
                            )
                            Text(
                                song.name,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(5.dp),
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            deleteSongDropdown = song
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                tint = Color.Black,
                                contentDescription = "Settings"
                            )
                        }
                        DropdownMenu((deleteSongDropdown == song), {deleteSongDropdown = null}, offset = DpOffset(y = (-10).dp, x = (-40).dp)){
                            DropdownMenuItem(text = {Text("Remove")}, onClick = {
                                viewModel.RemoveSongFromPlaylist(playlist, song)
                                deleteSongDropdown = null
                                if (viewModel.GetSelectedSong() != null) LoadPlaylist(viewModel, context, player,playlist.list.songs)
                            })
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
                        Button(onClick = {openedPlaylistID = playlist.id}, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            Row(Modifier.fillMaxWidth()) {
                                Image(
                                    painter = painterResource(playlist.imgID),
                                    contentDescription = "Music Icon",
                                    modifier = Modifier
                                        .height(80.dp)
                                        .padding(5.dp)
                                )
                                Text(
                                    playlist.name,
                                    color = Color.Black,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
//                    items(playlist.list.songs) { song ->
//                        Row(Modifier.fillMaxWidth()) {
//                            Button(
//                                onClick = {
//                                    onPlay(song)
//                                },
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
//                            ) {
//                                Image(
//                                    painter = painterResource(song.imgID),
//                                    contentDescription = "Music Icon",
//                                    modifier = Modifier.height(80.dp).padding(5.dp)
//                                )
//                                Text(
//                                    song.name,
//                                    color = Color.Black,
//                                    modifier = Modifier.padding(5.dp)
//                                )
//                            }
//                        }
//                    }
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
        topBar = {Row(Modifier
            .fillMaxWidth()
            .padding(15.dp)){
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
                    },
                    viewModel)
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
            },
                {
                    LoadPlaylist(viewModel, context, player, it, 0)
                })
    }


}


fun LoadPlaylist(viewModel : MusicViewModel,context : Context,player : ExoPlayer, list: List<Song>, startIndex: Int = 0) {
    val items = list.map { song ->
        val id = song.fileID
        MediaItem.fromUri("android.resource://${context.packageName}/$id")
    }
    player.setMediaItems(items, startIndex, 0L)
    player.prepare()
    viewModel.SelectMusic(list[startIndex])
}


fun LoadSong(viewModel: MusicViewModel, context : Context, player: ExoPlayer, song: Song){
    val item = MediaItem.fromUri("android.resource://${context.packageName}/${song.fileID}")
    player.setMediaItem(item)
    viewModel.SelectMusic(song)
}

@Composable
fun MusicPlayer(song : Song, player : ExoPlayer, onPrev : () -> Unit, onNext : () -> Unit, viewModel: MusicViewModel){
    var isPlaying by rememberSaveable() { mutableStateOf(false) }
    var expandDetails by rememberSaveable() { mutableStateOf(false) }

    if (expandDetails)
        SongDetailsPage(song, player, onPrev, onNext, {expandDetails = false}, isPlaying,
            {
                player.pause()
                isPlaying = false
            },
            {
                player.play()
                isPlaying = true
            },
            viewModel)
    else{
        Row(Modifier
            .fillMaxWidth()
            .height(100.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {expandDetails = true}, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black), modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(song.imgID),
                    contentDescription = "Music Icon",
                    Modifier
                        .height(100.dp)
                        .padding(15.dp)
                )
                Text(song.name, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
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
}

@Composable
fun SongDetailsPage(song : Song,
                    player : ExoPlayer,
                    onPrev : () -> Unit,
                    onNext : () -> Unit,
                    onDismiss : ()->Unit,
                    isPlaying : Boolean,
                    onPause : ()->Unit,
                    onPlay : ()->Unit,
                    viewModel: MusicViewModel
){
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }
    var addToPlayListScreen by rememberSaveable() { mutableStateOf(false) }

    LaunchedEffect(player, isPlaying) {
        while (isPlaying) {
            position = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
            delay(500)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(topBar = {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(5.dp)){
                    IconButton(
                        onClick = { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { addToPlayListScreen = true}){
                        Text("Save To Playlist")
                    }

                }

            }) { paddingValues ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(song.imgID),
                        contentDescription = "Song Image",
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(30.dp))
                    Text(song.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(30.dp))
                    Slider(
                        value = position.toFloat(),
                        onValueChange = { position = it.toLong() },
                        onValueChangeFinished = { player.seekTo(position) },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f)
                    )
                    Spacer(Modifier.height(30.dp))
                    Row() {

                        IconButton(
                            onClick = { onPrev() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {
                            if (isPlaying) onPause() else onPlay()
                        }) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { onNext() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }


                }
            }
        }
    }
    if (addToPlayListScreen){
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.5f),
                shape = RoundedCornerShape(20.dp)
            ){
                SelectPlaylist(viewModel,{addToPlayListScreen = false}, { viewModel.AddSongTpPlaylist(it, song)} )
            }
        }
    }
}

@Composable
fun SelectPlaylist(viewModel: MusicViewModel, onDismiss: () -> Unit, onSelect: (Playlist) -> Unit, modifier : Modifier = Modifier){
    val playlists = viewModel.GetPlaylists()
    Scaffold(
        topBar = {
        IconButton(
            onClick = { onDismiss() }
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }){ paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            playlists.forEach { playlist ->
                item(playlist.id) {
                    Button(
                        onClick = { onSelect(playlist) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(playlist.imgID),
                                contentDescription = "Music Icon",
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(5.dp)
                            )
                            Text(
                                playlist.name,
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