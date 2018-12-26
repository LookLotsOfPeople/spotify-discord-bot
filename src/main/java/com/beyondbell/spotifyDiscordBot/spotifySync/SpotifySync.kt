package com.beyondbell.spotifyDiscordBot.spotifySync

import com.beyondbell.spotifyDiscordBot.getLogger
import com.beyondbell.spotifyDiscordBot.syncDataHandling.DiscordSync
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import java.io.IOException
import java.util.EnumMap

object SpotifySync {
	private val syncingThread = Thread {
		while (true) {
			Thread {
				val syncedData = getSyncableData()
				if (syncedData != null && syncedData.containsKey(SyncableData.SongTitle)) {
					DiscordSync.handleSyncedData(syncedData)
				}
			}
			Thread.sleep(1000)
		}
	}

	fun start() {
		syncingThread.start()
	}

	private fun getSyncableData(): EnumMap<SyncableData, String>? {
		return try {
			val spotifyApi = SpotifyApi.Builder()
					.setAccessToken(SpotifyAuthorization.getToken())
					.build()
			val currentlyPlayingContext = spotifyApi.informationAboutUsersCurrentPlayback.build().execute()
			val syncedData = EnumMap<SyncableData, String>(SyncableData::class.java)
			if (currentlyPlayingContext != null) {
				syncedData[SyncableData.SongTitle] = currentlyPlayingContext.item.name
				syncedData[SyncableData.SongArtist] = currentlyPlayingContext.item.artists[0].name
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