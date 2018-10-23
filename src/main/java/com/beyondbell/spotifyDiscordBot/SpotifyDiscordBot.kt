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

private val logger: Logger = LogManager.getRootLogger()

private var jda: JDA? = null
private var ownerId: Long = -1

fun main(args: Array<String>) {
	SpotifyAuthorization.authorize()

	val tokenProperties = Properties()
	tokenProperties.setProperty("token", "")
	tokenProperties.setProperty("ownerId", "")
	try {
		val tokenFile = FileInputStream("config")
		tokenProperties.load(tokenFile)
		tokenFile.close()
		try {
			jda = JDABuilder(tokenProperties.getProperty("token"))
					.addEventListener(EventListener {
						when (it) {
							is PrivateMessageReceivedEvent -> {
								if (it.author.idLong == ownerId) {
									if (it.message.contentRaw == "exit") {
										leave()
										System.exit(0)
									}
								}
							}
						}
					}).build()
		} catch (e: LoginException) {
			logger.fatal("Please Place the Correct Token Inside of the Bot Config File! Attempted token was [${tokenProperties.getProperty("token")}].")
			return
		}
	} catch (e: IOException) {
		logger.fatal("Cannot Find Bot Config File! Defaulting One.")
		val tokenFile = FileOutputStream("config")
		tokenProperties.store(tokenFile, "Config File")
		tokenFile.close()
		return
	}

	ownerId = if (args.isNotEmpty()) {
		args[0].toLong()
	} else {
		tokenProperties.getProperty("ownerId").toLong()
	}

	Thread.sleep(5000) // TODO Replace?

	connect()
	SpotifySync.start()
}

fun connect() {
	for (guild in getJDA().getMutualGuilds(getJDA().getUserById(getOwnerId()))) {
		for (voiceChannel in guild.voiceChannels) {
			for (member in voiceChannel.members) {
				if (member.user == getJDA().getUserById(getOwnerId()) &&
						!guild.audioManager.isConnected && !guild.audioManager.isAttemptingToConnect) {
					guild.audioManager.openAudioConnection(voiceChannel)
					guild.audioManager.sendingHandler = Music.getSendHandler()
				}
			}
		}
	}
}

fun leave() {
	for (guild in getJDA().getMutualGuilds(getJDA().getUserById(getOwnerId()))) {
		guild.audioManager.closeAudioConnection()
	}
}

fun getJDA(): JDA {
	return jda!!
}

fun getOwnerId(): Long {
	return ownerId
}

fun getLogger(): Logger {
	return logger
}