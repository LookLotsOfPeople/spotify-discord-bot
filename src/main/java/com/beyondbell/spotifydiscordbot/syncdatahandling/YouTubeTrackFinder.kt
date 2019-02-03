package com.beyondbell.spotifydiscordbot.syncdatahandling

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Arrays

object YouTubeTrackFinder {
	private val youTube: YouTube

	init {
		val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
		val jsonFactory = JacksonFactory.getDefaultInstance()
		youTube = YouTube.Builder(httpTransport, jsonFactory, AuthorizationCodeInstalledApp(GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, GoogleClientSecrets.load(jsonFactory, InputStreamReader(FileInputStream("client_secret.json"))), Arrays.asList(YouTubeScopes.YOUTUBE_READONLY)).setDataStoreFactory(FileDataStoreFactory(File(System.getProperty("user.home"), ".credentials/bugisoft"))).setAccessType("offline").build(), LocalServerReceiver()).authorize("user")).setApplicationName("Bugisoft Track Finder").build()
	}

	fun getAudioTrackURLFromString(songContext: String, desiredLength: Long): String {
		return youTube.search().list("id")
				.setType("video")
				.setMaxResults(1)
				.setQ("$songContext lyrics").execute()
				.items[0].id.videoId
	}

	/*fun getAudioTrackURLFromString(songContext: String, desiredLength: Long): String {
		val request = youTube.search().list("id")
				.setType("video")
				.setMaxResults(2)
				.setQ("$songContext lyrics").execute()
		var ids = ""
		for (i in 0 until request.items.size) {
			ids += "${request.items[i].id.videoId},"
		}
		ids.removeSuffix(",")
		val s = youTube.videos().list("contentDetails")
				.setId(ids)
				.execute()

		var id = request.items[0].id.videoId
		var minimumDivergence = Math.abs(timeToMillis(s.items[0].contentDetails.duration))
		for (i in 1 until s.items.size) {
			val divergence = Math.abs(timeToMillis(s.items[i].contentDetails.duration))
			if (divergence < minimumDivergence) {
				minimumDivergence = divergence
				id = request.items[i].id.videoId
			}
		}
		return id
	}

	private fun timeToMillis(time: String) : Long {
		var millis = 0L
		val split = time.removePrefix("PT").removeSuffix("S").split("H", "M")
		for (i in 0 until split.size) {
			millis += Math.pow(60.0, i.toDouble()).toInt() * 1000 * split[split.size - 1 - i].toInt()
		}
		return millis
	}*/
}