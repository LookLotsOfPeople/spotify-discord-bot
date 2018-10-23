package com.beyondbell.spotifyDiscordBot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {
	private lateinit var track: AudioTrack

	@Synchronized
	fun play(track: AudioTrack) {
		if (this::track.isInitialized) {
			if (track.identifier != this.track.identifier) {
				player.startTrack(track, false)
				this.track = track
			}
		} else {
			player.startTrack(track, false)
			this.track = track
		}
	}

	@Synchronized
	fun goToTimestamp(timestamp: Long) {
		val nTrack = track.makeClone()
		nTrack.position = timestamp
		player.startTrack(nTrack, false)
	}
}