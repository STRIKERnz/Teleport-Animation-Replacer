package com.strikernz.tpreplacer;

import com.google.inject.Provides;
import javax.inject.Inject;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PluginDescriptor(
		name = "Teleport Animation Replacer",
		description = "Replace teleport animations with Different teleport animations graphics and sounds",
		tags = {"animation", "teleport", "sound", "graphic",  "customization"}
)
public class TpReplacer extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TpreplacerConfig config;

	private boolean teleporting = false;
	private int arrivalSoundTicksRemaining = -1;
	private static final int ARRIVAL_SOUND_DELAY_TICKS = 2;

	// Track last played sound to avoid double-playing in the same tick
	private int lastPlayedSoundId = -1;
	private int lastPlayedSoundTick = -1;

	// Map: originalSoundId -> expireTick (inclusive)
	private final Map<Integer, Integer> mutedSoundUntilTick = new HashMap<>();

	// All known teleport sound IDs — built once from the enum
	private static final Set<Integer> KNOWN_TELEPORT_SOUNDS = new HashSet<>();
	static
	{
		for (TeleportAnimation ta : TeleportAnimation.values())
		{
			if (ta.getSoundId() != -1)
				KNOWN_TELEPORT_SOUNDS.add(ta.getSoundId());
		}
	}

	@Override
	protected void startUp()
	{
		teleporting = false;
	}

	@Override
	protected void shutDown()
	{
		teleporting = false;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != client.getLocalPlayer())
			return;

		Player player = client.getLocalPlayer();
		int animationId = player.getAnimation();

		// Cowbell arrival: play landing animation when teleport ends
		if (teleporting && animationId == -1)
		{
			teleporting = false;
			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			arrivalSoundTicksRemaining = ARRIVAL_SOUND_DELAY_TICKS;
			return;
		}

		// Only act on known teleport animations
		if (!AnimationConstants.isTeleportAnimation(animationId))
			return;

		TeleportAnimation selected = getSelectedForAnimation(animationId);

		// NONE on the global setting means do nothing
		if (selected == TeleportAnimation.NONE)
			return;

		// Already playing the target animation — nothing to do
		if (animationId == selected.getAnimationId())
			return;

		// The original teleport type, used to look up its native sound for muting
		TeleportAnimation original = TeleportAnimation.fromAnimationId(animationId);
		int originalSound = (original != null) ? original.getSoundId() : -1;

		if (originalSound != -1 && config.muteTeleportSound())
		{
			mutedSoundUntilTick.put(originalSound, client.getTickCount() + 3);
		}

		if (selected == TeleportAnimation.COWBELL)
		{
			teleporting = true;
			player.setAnimation(AnimationConstants.COWBELL_TELEPORT);
			player.setGraphic(AnimationConstants.COWBELL_TELEPORT_GRAPHIC);
			return;
		}


		// Generic override path — use data from the enum
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
			return;

		// Remove expired mute entries
		int tick = client.getTickCount();
		mutedSoundUntilTick.values().removeIf(expire -> expire < tick);

		if (arrivalSoundTicksRemaining < 0)
			return;

		if (arrivalSoundTicksRemaining == 0)
		{
			if (AnimationConstants.COWBELL_ARRIVAL_SOUND != -1)
			{
				playSoundOnce(AnimationConstants.COWBELL_ARRIVAL_SOUND);
			}
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
		if (!config.muteTeleportSound())
			return;

		if (event.getSource() != client.getLocalPlayer())
			return;

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// Don't suppress a sound the plugin itself just triggered
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

		if (mutedSoundUntilTick.getOrDefault(soundId, -1) >= tick)
		{
			event.consume();
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{
		if (!config.muteTeleportSound())
			return;

		int soundId = event.getSoundId();
		int tick = client.getTickCount();

		// Don't suppress a sound the plugin itself just triggered
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

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

		// Fallback: mute map covers late-arriving sounds
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

	// Resolve the override for a given animation ID, falling back to the global setting when per-teleport is NONE
	private TeleportAnimation getSelectedForAnimation(int animationId)
	{
		TeleportAnimation source = TeleportAnimation.fromAnimationId(animationId);
		if (source == null)
			return config.teleportAnimation();

		TeleportAnimation per;
		switch (source)
		{
			case STANDARD:      per = config.perOverrideNormal();        break;
			case ANCIENT:       per = config.perOverrideAncient();       break;
			case ARCEUUS:       per = config.perOverrideArceuus();       break;
			case LUNAR:         per = config.perOverrideLunar();         break;
			case TAB:           per = config.perOverrideTabs();          break;
			case SCROLL:        per = config.perOverrideScrolls();       break;
			case ECTOPHIAL:     per = config.perOverrideEctophial();     break;
			case ARDOUGNE:      per = config.perOverrideArdougne();      break;
			case DESERT_AMULET: per = config.perOverrideDesertAmulet(); break;
			default:            return config.teleportAnimation();
		}

		return (per != TeleportAnimation.NONE) ? per : config.teleportAnimation();
	}


	private void playSoundOnce(int soundId)
	{
		if (soundId == -1)
			return;

		int tick = client.getTickCount();
		if (soundId == lastPlayedSoundId && tick == lastPlayedSoundTick)
			return;

		lastPlayedSoundId = soundId;
		lastPlayedSoundTick = tick;
		client.playSoundEffect(soundId);
	}
}
