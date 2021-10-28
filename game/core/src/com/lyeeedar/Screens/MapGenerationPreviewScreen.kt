package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Portal.Biome
import com.lyeeedar.Game.Tile
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.addClickListener
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getXml

class MapGenerationPreviewScreen : AbstractWorldPreviewScreen("map")
{
	override fun addOptions(table: Table)
	{
		val forceRegenButton = TextButton("Force Regenerate", Statics.skin)
		forceRegenButton.addClickListener {
			lastSeed = 0L
		}

		table.add(Table())
		table.add(forceRegenButton)
		table.row()
	}

	override fun loadResource(xmlData: XmlData): World<Tile>
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		val biome = Biome()
		biome.load(getXml("Biomes/metalBiome"))

		return MapCreator.generateWorld(biome, biome.normalPacks.random(), player, 1, seed)
	}
}