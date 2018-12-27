package com.beyondbell.spotifyDiscordBot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.core.audio.AudioSendHandler

class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {
	private var lastFrame: AudioFrame? = null

	override fun canProvide(): Boolean {
		if (lastFrame == null) {
			lastFrame = audioPlayer.provide()
		}
		return lastFrame != null
	}

	override fun provide20MsAudio(): ByteArray? {
		return if (canProvide()) {
			val data = lastFrame!!.data
			lastFrame = null
			data
		} else {
			lastFrame = null
			null
		}
	}

	override fun isOpus(): Boolean {
		return true
	}
}
