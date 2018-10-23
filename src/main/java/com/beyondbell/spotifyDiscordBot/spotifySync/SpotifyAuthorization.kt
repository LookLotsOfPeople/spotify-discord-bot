package com.beyondbell.spotifyDiscordBot.spotifySync

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.awt.Desktop
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

object SpotifyAuthorization {
	private const val clientId = "ae6c980068344712bc710a85752e6e7d"
	private const val clientSecret = "df6ed0bdad614229a2a7a694d014f310"
	private const val port = 8888
	private const val scope = "user-read-playback-state"

	private lateinit var token: String
	private lateinit var refreshToken: String

	fun authorize() {
		if (!this::token.isInitialized) {
			val cred = getAccessCodeCredentials(getAuthorizationToken())
			token = cred.accessToken
			Thread {
				startRefreshTokenWorkflow(cred)
			}.start()
		}
	}

	fun getToken(): String {
		return token
	}

	private fun getAuthorizationToken(): String {
		val serverSocket = ServerSocket(port)

		val spotifyApi = SpotifyApi.Builder()
				.setClientId(clientId)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build()

		Desktop.getDesktop().browse(spotifyApi.authorizationCodeUri()
				.scope(scope)
				.show_dialog(true)
				.build().execute())

		val client = serverSocket.accept()
		val token = BufferedReader(InputStreamReader(client.getInputStream())).readLine().removePrefix("GET /?code=").removeSuffix(" HTTP/1.1")
		client.close()
		serverSocket.close()
		return token
	}

	private fun getAccessCodeCredentials(authorizationCode: String): AuthorizationCodeCredentials {
		val spotifyApi = SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build()

		return spotifyApi.authorizationCode(authorizationCode).build().execute()
	}

	private fun startRefreshTokenWorkflow(cred: AuthorizationCodeCredentials) {
		val spotifyApi = SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build()

		Thread.sleep((cred.expiresIn * 1000 - 1000).toLong())
		var newCred = spotifyApi.authorizationCodeRefresh()
				.grant_type("refresh_token")
				.refresh_token(getRefreshToken(cred.refreshToken))
				.build().execute()

		Thread {
			while (true) {
				Thread.sleep((newCred.expiresIn * 1000 - 10000).toLong())
				newCred = spotifyApi.authorizationCodeRefresh()
						.grant_type("refresh_token")
						.refresh_token(getRefreshToken(newCred.refreshToken))
						.build().execute()
				token = newCred.accessToken
			}
		}.start()
	}

	private fun getRefreshToken(authorizationCode: String): String {
		val spotifyApi = SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build()

		return spotifyApi.authorizationCode(authorizationCode).build().execute().refreshToken
	}
}