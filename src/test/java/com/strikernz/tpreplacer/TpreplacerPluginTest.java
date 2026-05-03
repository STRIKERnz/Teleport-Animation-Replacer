package com.strikernz.tpreplacer;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TpreplacerPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(TpReplacer.class);
        RuneLite.main(args);
    }
}