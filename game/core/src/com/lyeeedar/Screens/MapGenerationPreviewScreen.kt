package com.lyeeedar.Screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Tile
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.AbstractRenderSystem
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.addClickListener
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getRawXml
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MapGenerationPreviewScreen : AbstractScreen()
{
	var world: World<Tile>? = null
	val viewPos = Vector2()

	lateinit var loadFailureLabel: Label

	var seed = 1L

	override fun create()
	{
		drawFPS = false

		val options = Table()
		options.defaults().pad(5f).growX()

		val newSeedButton = TextButton("New Seed", Statics.skin)
		newSeedButton.addClickListener {
			seed = Random.sharedRandom.nextLong()
		}

		options.add(Table())
		options.add(newSeedButton)
		options.row()

		val forceRegenButton = TextButton("Force Regenerate", Statics.skin)
		forceRegenButton.addClickListener {
			lastSeed = 0L
		}

		options.add(Table())
		options.add(forceRegenButton)
		options.row()

		val optionsToggle = TextButton("Options", Statics.skin)
		optionsToggle.addClickListener {
			options.isVisible = !options.isVisible
		}

		options.isVisible = false

		loadFailureLabel = Label("", Statics.skin, "small")
		loadFailureLabel.setWrap(true)
		loadFailureLabel.isVisible = false

		mainTable.add(optionsToggle).expandX().right().pad(5f)
		mainTable.row()
		mainTable.add(options).expandX().right().pad(5f)
		mainTable.row()
		mainTable.add(loadFailureLabel).growX()
		mainTable.row()
		mainTable.add(Table()).grow()

		tryLoadMap()
	}

	var lastModified = 0L
	var lastSeed = 0L
	fun tryLoadMap()
	{
		val wasNull = world == null

		try
		{
			val tempFile = File("../caches/editor/map.xml")
			if (tempFile.exists())
			{
				val modified = tempFile.lastModified()

				if (lastModified != modified || lastSeed != seed)
				{
					lastModified = modified
					lastSeed = seed

					val rawxml = getRawXml(tempFile.absolutePath)
					val xmlData = XmlData.loadFromElement(rawxml)

					val lastTileSize = world?.tileSize
					world?.free()
					world = null

					val player = EntityLoader.load("Entities/player")
					player.statistics()!!.calculateStatistics(1)

					world = MapCreator.generateWorld(xmlData, "Factions/Rats", player, 1, seed)
					val world = world!!

					for (tile in world.grid)
					{
						tile.isSeen = true
						tile.updateVisibility(1000f, true, true)
					}

					world.systems.add(MapGenRenderSystem(world, this))

					if (lastTileSize != null)
					{
						world.tileSize = lastTileSize
					}

					if (wasNull)
					{
						viewPos.set(world.grid.width * 0.5f, world.grid.height * 0.5f)
					}

					loadFailureLabel.isVisible = false
				}
			}
		}
		catch (ex: Exception)
		{
			val sw = StringWriter()
			val pw = PrintWriter(sw)
			ex.printStackTrace(pw)
			val sStackTrace: String = sw.toString()

			loadFailureLabel.setText(sStackTrace)
			loadFailureLabel.isVisible = true
		}
	}

	override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean
	{
		val world = world
		if (world != null) viewPos.sub(deltaX / world.tileSize, -deltaY / world.tileSize)
		return true
	}

	override fun scrolled(amount: Int): Boolean
	{
		val world = world
		if (world != null) world.tileSize -= amount

		return super.scrolled(amount)
	}

	override fun doRender(delta: Float)
	{
		tryLoadMap()
		world?.update(delta)
	}
}

class MapGenRenderSystem(world: World<*>, val screen: MapGenerationPreviewScreen) : AbstractRenderSystem(world)
{
	init
	{
		doStaticRender = false
	}

	override fun drawExtraEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun getPlayerPosition(deltaTime: Float): Vector2
	{
		return screen.viewPos
	}
}