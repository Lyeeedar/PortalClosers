package com.lyeeedar.Screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.Game.Tile
import com.lyeeedar.Systems.RenderSystem
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.UI.addClickListener
import com.lyeeedar.Util.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractWorldPreviewScreen(val resourceName: String) : AbstractScreen()
{
	var world: World<Tile>? = null
	val viewPos = Vector2()

	lateinit var loadFailureLabel: Label

	var seed = 1L

	val worldTable = Table()

	override fun create()
	{
		drawFPS = false

		val options = Table()
		options.background = TextureRegionDrawable(AssetManager.loadTextureRegion("white")).tint(Color(0.4f, 0.4f, 0.4f, 0.4f))
		options.defaults().pad(5f).growX()

		val newSeedButton = TextButton("New Seed", Statics.skin)
		newSeedButton.addClickListener {
			seed = Random.sharedRandom.nextLong()
		}

		options.add(Table())
		options.add(newSeedButton)
		options.row()

		addOptions(options)

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
		mainTable.add(worldTable).grow()

		tryLoad()
	}

	abstract fun addOptions(table: Table)

	var lastDataChange = 0L
	var lastModified = 0L
	var lastSeed = 0L
	fun tryLoad()
	{
		try
		{
			val modified = getLastModified(File("."))
			if (modified != lastDataChange)
			{
				lastDataChange = modified

				AssetManager.invalidate()

				lastModified = 0L
			}
		} catch (ex: Exception) {}

		val wasNull = world == null

		try
		{
			val tempFile = File("../caches/editor/$resourceName.xml")
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

					world = loadResource(xmlData)
					val world = world!!

					for (tile in world.grid)
					{
						tile.isSeen = true
						tile.updateVisibility(1000f, true, true)
					}

					world.systems.add(WorldPreviewRenderSystem(world, this))

					if (lastTileSize != null)
					{
						world.tileSize = lastTileSize
					}

					if (wasNull)
					{
						viewPos.set(world.grid.width * 0.5f, world.grid.height * 0.5f)
					}

					loadFailureLabel.setText("")
					loadFailureLabel.isVisible = false

					worldTable.clear()
					worldTable.add(RenderSystemWidget(world))
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

	abstract fun loadResource(xmlData: XmlData): World<Tile>

	fun getLastModified(directory: File): Long
	{
		val files = directory.listFiles()!!
		if (files.isEmpty()) return directory.lastModified()
		return files.map { it.lastModified() }.maxOrNull()!!
	}

	override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean
	{
		val world = world
		if (world != null) viewPos.sub(deltaX / world.tileSize, -deltaY / world.tileSize)
		return true
	}

	override fun scrolled(amountX: Float, amountY: Float): Boolean
	{
		val amount = max(amountX, amountY)
		val world = world
		if (world != null) world.tileSize -= amount

		return super.scrolled(amountX, amountY)
	}

	override fun doRender(delta: Float)
	{
		try
		{
			tryLoad()
			world?.update(delta)
		}
		catch (ex: Exception)
		{
			val sw = StringWriter()
			val pw = PrintWriter(sw)
			ex.printStackTrace(pw)
			val sStackTrace: String = sw.toString()

			loadFailureLabel.setText(sStackTrace)
			loadFailureLabel.isVisible = true

			world = null
		}
	}
}

class WorldPreviewRenderSystem(world: World<*>, val screen: AbstractWorldPreviewScreen) : RenderSystem(world)
{
	init
	{
		doStaticRender = false
	}

	override fun getPlayerPosition(deltaTime: Float?): Vector2
	{
		return screen.viewPos
	}
}