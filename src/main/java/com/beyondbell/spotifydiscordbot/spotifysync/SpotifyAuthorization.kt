package com.beyondbell.spotifydiscordbot.spotifysync

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.ServerSocket

object SpotifyAuthorization {
	private const val clientId = "ae6c980068344712bc710a85752e6e7d"
	private const val clientSecret = "df6ed0bdad614229a2a7a694d014f310"
	private const val port = 8888
	private const val scope = "user-read-playback-state"

	private lateinit var token: String

	@Synchronized
	fun authorize() {
		if (!this::token.isInitialized) {
			val cred = getAccessCodeCredentials(getAuthorizationToken())
			token = cred.accessToken
			startRefreshTokenWorkflow(cred)
		}
	}

	fun getToken(): String {
		return if (this::token.isInitialized) {
			token
		} else {
			""
		}
	}

	private fun getAuthorizationToken(): String {
		return ServerSocket(8888).use { ss ->
			Desktop.getDesktop().browse(SpotifyApi.Builder()
					.setClientId(clientId)
					.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
					.build().authorizationCodeUri()
					.scope(scope)
					.show_dialog(true)
					.build().execute())

			ss.accept().use { s ->
				s.inputStream.bufferedReader().use {
					it.readLine().removePrefix("GET /?code=").removeSuffix(" HTTP/1.1")
				}
			}
		}
	}

	private fun getAccessCodeCredentials(authorizationCode: String): AuthorizationCodeCredentials {
		return SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build().authorizationCode(authorizationCode).build().execute()
	}

	private fun startRefreshTokenWorkflow(cred: AuthorizationCodeCredentials) {
		println(cred)
		GlobalScope.launch {
			var newCred = cred
			while (this.isActive) {
				delay((newCred.expiresIn * 1000 - 5000).toLong())
				newCred = SpotifyApi.Builder()
						.setClientId(clientId)
						.setClientSecret(clientSecret)
						.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
						.build().authorizationCodeRefresh()
						.grant_type("refresh_token")
						.refresh_token(getRefreshToken(newCred.refreshToken))
						.build().execute()
				token = newCred.accessToken
			}
		}
	}

	private fun getRefreshToken(authorizationCode: String): String {
		return SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:$port"))
				.build().authorizationCode(authorizationCode).build().execute().refreshToken
	}
}