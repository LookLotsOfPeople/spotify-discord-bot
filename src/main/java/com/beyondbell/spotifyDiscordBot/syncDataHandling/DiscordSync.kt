package com.beyondbell.spotifyDiscordBot.syncDataHandling

import com.beyondbell.spotifyDiscordBot.music.Music
import com.beyondbell.spotifyDiscordBot.spotifySync.SyncableData
import java.util.EnumMap

object DiscordSync {
	lateinit var lastData: EnumMap<SyncableData, String>

	@Synchronized
	fun handleSyncedData(data: EnumMap<SyncableData, String>) {
		if (this::lastData.isInitialized) {
			if (data[SyncableData.SongTitle] != lastData[SyncableData.SongTitle]) {
				if (data[SyncableData.SongTimestamp] != null) {
					Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString(data[SyncableData.SongTitle] + " " + data[SyncableData.SongArtist]), data[SyncableData.SongTimestamp]!!.toLong())
				} else {
					Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString(data[SyncableData.SongTitle] + " " + data[SyncableData.SongArtist]), 0)
				}
			}
			if (data[SyncableData.IsPlaying]?.toBoolean()!!) {
				Music.unpause()
			} else {
				Music.pause()
			}
			if (Math.abs(data[SyncableData.SongTimestamp]!!.toLong() - lastData[SyncableData.SongTimestamp]!!.toLong()) > 2000) {
				Music.goToTimestamp(data[SyncableData.SongTimestamp]!!.toLong())
			}
		} else {
			if (data[SyncableData.SongTimestamp] != null) {
				Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString(data[SyncableData.SongTitle] + " " + data[SyncableData.SongArtist]), data[SyncableData.SongTimestamp]!!.toLong())
			} else {
				Music.playTrack(YouTubeTrackFinder.getAudioTrackURLFromString(data[SyncableData.SongTitle] + " " + data[SyncableData.SongArtist]), 0)
			}
		}
		lastData = data
	}
}