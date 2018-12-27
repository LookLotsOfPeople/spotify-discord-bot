package com.beyondbell.spotifyDiscordBot

import com.beyondbell.spotifyDiscordBot.music.Music
import com.beyondbell.spotifyDiscordBot.spotifySync.SpotifyAuthorization
import com.beyondbell.spotifyDiscordBot.spotifySync.SpotifySync
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties
import javax.security.auth.login.LoginException

fun main(args: Array<String>) {
	SpotifyAuthorization.authorize()

	val tokenProperties = Properties()
	tokenProperties.setProperty("token", "")
	tokenProperties.setProperty("ownerId", "")
	try {
		tokenProperties.load(FileInputStream("config"))
	} catch (e: IOException) {
		getLogger().fatal("Cannot Find Bot Config File, Defaulting One. Please Populate Before Running Again!")
		tokenProperties.store(FileOutputStream("config"), "Config File")
		return
	}
	val ownerId = if (args.isNotEmpty()) {
		args[0].toLong()
	} else {
		tokenProperties.getProperty("ownerId").toLong()
	}

	connect(try {
		JDABuilder(tokenProperties.getProperty("token"))
				.addEventListener(EventListener {
					when (it) {
						is PrivateMessageReceivedEvent -> {
							if (it.author.idLong == ownerId && it.message.contentRaw == "exit") {
								leave(it.jda)
								System.exit(0)
							}
						}
					}
				}).build()
	} catch (e: LoginException) {
		getLogger().fatal("Please Place the Correct Token Inside of the Bot Config File! Attempted token was [${tokenProperties.getProperty("token")}].")
		return
	}.awaitReady(), ownerId)

	SpotifySync().start()
}

fun connect(jda: JDA, ownerId: Long) {
	for (guild in jda.getMutualGuilds(jda.getUserById(ownerId))) {
		for (voiceChannel in guild.voiceChannels) {
			for (member in voiceChannel.members) {
				if (member.user == jda.getUserById(ownerId)) {
					guild.audioManager.openAudioConnection(voiceChannel)
					guild.audioManager.sendingHandler = Music.getSendHandler()
					break
				}
			}
		}
	}
}

fun leave(jda: JDA) {
	jda.guilds.forEach {
		it.audioManager.closeAudioConnection()
	}
}

fun getLogger(): Logger {
	return LogManager.getRootLogger()
}