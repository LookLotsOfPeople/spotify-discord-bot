package com.beyondbell.spotifyDiscordBot.syncDataHandling

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.google.api.services.youtube.model.SearchListResponse
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

object YouTubeTrackFinder {
	private val youTube: YouTube

	init {
		val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
		val jsonFactory = JacksonFactory.getDefaultInstance()
		youTube = YouTube.Builder(httpTransport, jsonFactory, AuthorizationCodeInstalledApp(GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, GoogleClientSecrets.load(jsonFactory, InputStreamReader(FileInputStream("client_secret.json"))), Arrays.asList(YouTubeScopes.YOUTUBE_READONLY)).setDataStoreFactory(FileDataStoreFactory(java.io.File(System.getProperty("user.home"), ".credentials/bugisoft"))).setAccessType("offline").build(), LocalServerReceiver()).authorize("user")).setApplicationName("Bugisoft Track Finder").build()
	}

	fun getAudioTrackURLFromString(songContext: String): String {
		val searchListByKeywordRequest: YouTube.Search.List = youTube.search().list("id")
		searchListByKeywordRequest.type = "video"
		searchListByKeywordRequest.maxResults = 1
		searchListByKeywordRequest.q = songContext

		val response: SearchListResponse = searchListByKeywordRequest.execute()
		return response.items[0].id.videoId
	}
}