package com.beyondbell.spotifydiscordbot.spotifysync

import com.beyondbell.spotifydiscordbot.getLogger
import com.beyondbell.spotifydiscordbot.syncdatahandling.DiscordSync
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.EnumMap

class SpotifySync {
	fun start() {
		GlobalScope.launch {
			while (this.isActive) {
				val syncedData = getSyncableData()
				if (syncedData != null && syncedData.containsKey(SyncableData.SongTitle)) {
					DiscordSync.handleSyncedData(syncedData)
				}
				delay(1000)
			}
		}
	}

	private fun getSyncableData(): EnumMap<SyncableData, String>? {
		return try {
			val currentlyPlayingContext = SpotifyApi.Builder()
					.setAccessToken(SpotifyAuthorization.getToken())
					.build().informationAboutUsersCurrentPlayback.build().execute()
			val syncedData = EnumMap<SyncableData, String>(SyncableData::class.java)
			if (currentlyPlayingContext != null) {
				syncedData[SyncableData.SongTitle] = currentlyPlayingContext.item.name
				syncedData[SyncableData.SongArtist] = currentlyPlayingContext.item.artists[0].name
				syncedData[SyncableData.SongLength] = currentlyPlayingContext.item.durationMs.toString()
				syncedData[SyncableData.SongTimestamp] = currentlyPlayingContext.progress_ms.toString()
				syncedData[SyncableData.IsPlaying] = currentlyPlayingContext.is_playing.toString()
			}
			syncedData
		} catch (e: IOException) {
			getLogger().error(e.message)
			null
		} catch (e: SpotifyWebApiException) {
			getLogger().error(e.message)
			null
		}
	}
}