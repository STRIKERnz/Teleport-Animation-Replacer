package com.strikernz.tpreplacer;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Teleport Animation Replacer",
	description = "Replace teleport animations with different animations, graphics, and sounds",
	tags = {"animation", "teleport", "sound", "graphic", "customization", "cowbell", "cosmetic"}
)
public class TpReplacer extends Plugin
{
	private static final int ARRIVAL_SOUND_DELAY_TICKS = 2;

	/** All known teleport sound IDs, built once from the enum for fast lookup. */
	private static final Set<Integer> KNOWN_TELEPORT_SOUNDS = new HashSet<>();

	static
	{
		for (TeleportAnimation ta : TeleportAnimation.values())
		{
			if (ta.getSoundId() != -1)
			{
				KNOWN_TELEPORT_SOUNDS.add(ta.getSoundId());
			}
		}
	}

	@Inject
	private Client client;

	@Inject
	private TpreplacerConfig config;

	/** Whether the local player is currently in a cowbell teleport (waiting for landing). */
	private boolean teleporting;

	/** Ticks remaining before the cowbell arrival sound should play ({@code -1} = inactive). */
	private int arrivalSoundTicksRemaining = -1;

	/** Tracks the last sound the plugin played so we don't suppress our own effects. */
	private int lastPlayedSoundId = -1;
	private int lastPlayedSoundTick = -1;

	/** Original-sound-id to tick at which the mute expires (inclusive). */
	private final Map<Integer, Integer> mutedSoundUntilTick = new HashMap<>();

	@Override
	protected void startUp()
	{
		teleporting = false;
		arrivalSoundTicksRemaining = -1;
		lastPlayedSoundId = -1;
		lastPlayedSoundTick = -1;
		mutedSoundUntilTick.clear();
	}

	@Override
	protected void shutDown()
	{
		teleporting = false;
		arrivalSoundTicksRemaining = -1;
		lastPlayedSoundId = -1;
		lastPlayedSoundTick = -1;
		mutedSoundUntilTick.clear();
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != client.getLocalPlayer())
		{
			return;
		}

		Player player = client.getLocalPlayer();
		int animationId = player.getAnimation();

		// Cowbell arrival: play the landing graphic and sound when the teleport ends
		if (teleporting && animationId == -1)
		{
			teleporting = false;
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			arrivalSoundTicksRemaining = ARRIVAL_SOUND_DELAY_TICKS;
			return;
		}

		if (!AnimationConstants.isTeleportAnimation(animationId))
		{
			return;
		}

		TeleportAnimation selected = getSelectedForAnimation(animationId);

		// NONE means "don't override"
		if (selected == TeleportAnimation.NONE)
		{
			return;
		}

		// Already playing the target animation
		if (animationId == selected.getAnimationId())
		{
			return;
		}

		// Mute the original teleport sound so it doesn't double-play
		TeleportAnimation original = TeleportAnimation.fromAnimationId(animationId);
		int originalSound = (original != null) ? original.getSoundId() : -1;

		if (originalSound != -1)
		{
			mutedSoundUntilTick.put(originalSound, client.getTickCount() + 3);
		}

		// Cowbell has a special two-phase animation (cast + landing)
		if (selected == TeleportAnimation.COWBELL)
		{
			teleporting = true;
			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			return;
		}

		// Generic override path
		player.setAnimation(selected.getAnimationId());

		if (selected.getGraphicId() != -1)
		{
			player.setGraphic(selected.getGraphicId());
		}

		if (selected.getSoundId() != -1)
		{
			playSoundOnce(selected.getSoundId());
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		// Expire old mute entries
		int tick = client.getTickCount();
		mutedSoundUntilTick.values().removeIf(expire -> expire < tick);

		if (arrivalSoundTicksRemaining < 0)
		{
			return;
		}

		if (arrivalSoundTicksRemaining == 0)
		{
			playSoundOnce(AnimationConstants.COWBELL_ARRIVAL_SOUND);
			arrivalSoundTicksRemaining = -1;
		}
		else
		{
			arrivalSoundTicksRemaining--;
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{

		if (event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// Don't suppress a sound the plugin itself just triggered
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
		{
			return;
		}

		if (mutedSoundUntilTick.getOrDefault(soundId, -1) >= tick)
		{
			event.consume();
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// Don't suppress a sound the plugin itself just triggered
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
		{
			return;
		}

		if (KNOWN_TELEPORT_SOUNDS.contains(soundId))
		{
			for (TeleportAnimation ta : TeleportAnimation.values())
			{
				if (ta.getSoundId() == soundId)
				{
					TeleportAnimation selected = getSelectedForAnimation(ta.getAnimationId());
					if (selected != TeleportAnimation.NONE && selected != ta)
					{
						event.consume();
						return;
					}
					break;
				}
			}
		}

		// Fallback: the mute map covers late-arriving area sounds
		if (mutedSoundUntilTick.getOrDefault(soundId, -1) >= tick)
		{
			event.consume();
		}
	}

	@Provides
	TpreplacerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TpreplacerConfig.class);
	}

	/**
	 * Resolves the override animation for a given source animation ID.
	 * Per-teleport settings take priority; if set to NONE the global setting is used.
	 */
	private TeleportAnimation getSelectedForAnimation(int animationId)
	{
		TeleportAnimation source = TeleportAnimation.fromAnimationId(animationId);
		if (source == null)
		{
			return config.teleportAnimation();
		}

		TeleportAnimation perOverride;
		switch (source)
		{
			case STANDARD:
				perOverride = config.perOverrideNormal();
				break;
			case ANCIENT:
				perOverride = config.perOverrideAncient();
				break;
			case ARCEUUS:
				perOverride = config.perOverrideArceuus();
				break;
			case LUNAR:
				perOverride = config.perOverrideLunar();
				break;
			case TAB:
				perOverride = config.perOverrideTabs();
				break;
			case SCROLL:
				perOverride = config.perOverrideScrolls();
				break;
			case ECTOPHIAL:
				perOverride = config.perOverrideEctophial();
				break;
			case ARDOUGNE:
				perOverride = config.perOverrideArdougne();
				break;
			case DESERT_AMULET:
				perOverride = config.perOverrideDesertAmulet();
				break;
			case GIANTSOUL_AMULET:
				perOverride = config.perOverrideGiantsoulAmulet();
				break;
			default:
				return config.teleportAnimation();
		}

		return (perOverride != TeleportAnimation.NONE) ? perOverride : config.teleportAnimation();
	}

	/**
	 * Plays a sound effect, de-duplicating within the same game tick.
	 */
	private void playSoundOnce(int soundId)
	{
		if (soundId == -1)
		{
			return;
		}

		int tick = client.getTickCount();
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
		{
			return;
		}

		lastPlayedSoundId = soundId;
		lastPlayedSoundTick = tick;
		client.playSoundEffect(soundId);
	}
}
