package soulflamehorn;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SoulflameHornTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SoulflameHornPlugin.class);
		RuneLite.main(args);
	}
}