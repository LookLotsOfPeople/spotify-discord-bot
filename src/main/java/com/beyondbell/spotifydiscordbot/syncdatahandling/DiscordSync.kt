package com.beyondbell.spotifydiscordbot.syncdatahandling

import com.beyondbell.spotifydiscordbot.music.Music
import com.beyondbell.spotifydiscordbot.spotifysync.SyncableData
import java.util.EnumMap

object DiscordSync {
	lateinit var lastData: EnumMap<SyncableData, String>

	fun handleSyncedData(data: EnumMap<SyncableData, String>) {
		if (this::lastData.isInitialized) {
			if (data[SyncableData.SongTitle] != lastData[SyncableData.SongTitle]) {
				Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString("${data[SyncableData.SongTitle]} ${data[SyncableData.SongArtist]}"), data[SyncableData.SongTimestamp]?.toLong()
						?: 0)
			} else if (Math.abs(data[SyncableData.SongTimestamp]!!.toLong() - lastData[SyncableData.SongTimestamp]!!.toLong()) > 2000) {
				Music.goToTimestamp(data[SyncableData.SongTimestamp]!!.toLong())
			}
		} else {
			Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString("${data[SyncableData.SongTitle]} ${data[SyncableData.SongArtist]}"), data[SyncableData.SongTimestamp]?.toLong()
					?: 0)
		}
		val isPlayingData = data[SyncableData.IsPlaying]?.toBoolean()
		if (isPlayingData != null) {
			Music.paused = !isPlayingData
		}
		lastData = data
	}
}