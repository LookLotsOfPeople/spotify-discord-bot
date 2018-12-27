package com.beyondbell.spotifyDiscordBot.music

import com.beyondbell.spotifyDiscordBot.getLogger
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

object Music {
	private val scheduler: TrackScheduler
	private val player: AudioPlayer
	private val playerManager: AudioPlayerManager

	init {
		playerManager = DefaultAudioPlayerManager()
		playerManager.configuration.opusEncodingQuality = AudioConfiguration.OPUS_QUALITY_MAX
		playerManager.configuration.resamplingQuality = AudioConfiguration.ResamplingQuality.HIGH
		playerManager.configuration.outputFormat = StandardAudioDataFormats.DISCORD_OPUS
		AudioSourceManagers.registerRemoteSources(playerManager)
		player = playerManager.createPlayer()
		scheduler = TrackScheduler(player)
		player.addListener(scheduler)
	}

	fun playTrack(trackUrl: String, timestamp: Long) {
		playerManager.loadItem(trackUrl, object : AudioLoadResultHandler {
			override fun trackLoaded(track: AudioTrack) {
				track.position = timestamp
				scheduler.play(track)
				getLogger().info("Added to Queue ${track.info.title} @ ${timestamp/1000/60}:${timestamp/1000%60}")
			}

			override fun playlistLoaded(playlist: AudioPlaylist) {
				val firstTrack: AudioTrack = playlist.selectedTrack ?: playlist.tracks[0]
				firstTrack.position = timestamp
				scheduler.play(firstTrack)
				getLogger().info("Added to Queue ${firstTrack.info.title} (First Track of Playlist ${playlist.name})")
			}

			override fun noMatches() {
				getLogger().warn("Nothing Found by $trackUrl")
			}

			override fun loadFailed(exception: FriendlyException) {
				getLogger().warn("Could Not Load $trackUrl")
				getLogger().debug(exception.message)
			}
		})
	}

	fun goToTimestamp(timestamp: Long) {
		scheduler.goToTimestamp(timestamp)
	}

	fun pause() {
		player.isPaused = true
	}

	fun unpause() {
		player.isPaused = false
	}

	fun getSendHandler(): AudioPlayerSendHandler {
		return AudioPlayerSendHandler(player)
	}
}