package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.AI.Tasks.TaskUseAbility
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.Systems.*
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.Array2D
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.XmlData
import squidpony.squidmath.LightRNG
import java.lang.RuntimeException

class AbilityPreviewScreen :  AbstractWorldPreviewScreen("ability")
{
	var sequences: EntitySignature? = null

	val widgetTable = Table()

	override fun addOptions(table: Table)
	{

	}

	override fun create()
	{
		super.create()

		mainTable.add(widgetTable).grow()
	}

	override fun loadResource(xmlData: XmlData): World<Tile>
	{
		val grid = loadGrid(closeGrid)

		val world = World<Tile>(grid)
		for (tile in grid)
		{
			tile.world = world
			for (ref in tile.contents)
			{
				val entity = ref.get() ?: continue

				world.addEntity(entity)

				if (entity.name()?.name == "player")
				{
					world.player = entity
				}
			}
		}

		world.update(0f)

		val abilityDef = AbilityOrb()
		abilityDef.load(xmlData)

		val abilityData = abilityDef.getAbility(1)
		val ability = abilityData.get()

		val player = world.player!!
		for (target in ability.getValidTargets(player, world))
		{
			target.tileCol = Colour.YELLOW
		}
		val target = ability.pickTarget(player, world, LightRNG(seed))

		val sequenceHolder = player.addOrGet(ComponentType.ActionSequence) as ActionSequenceComponent
		sequenceHolder.actionSequence = ability.data.actionSequence
		sequenceHolder.actionSequenceState.set(player.getRef(), world, seed)
		sequenceHolder.actionSequenceState.targets.clear()
		sequenceHolder.actionSequenceState.targets.add(target)

		val abilityHolder = player.addOrGet(ComponentType.ActiveAbility) as ActiveAbilityComponent
		abilityHolder.ability = ability

		sequenceHolder.actionSequence.update(0f, sequenceHolder.actionSequenceState)

		// update
		world.systems.add(ActionSequenceSystem(world))
		world.systems.add(StatisticsSystem(world))
		world.systems.add(EventSystem(world))
		world.systems.add(AbilitySystem(world))

		// render
		world.systems.add(BloodSystem(world))
		world.systems.add(DialogueSystem(world))
		world.systems.add(DirectionalSpriteSystem(world))
		world.systems.add(RenderSystem(world))

		// cleanup
		world.systems.add(DeletionSystem(world))

		sequences = world.getEntitiesFor().all(ComponentType.ActionSequence).get()

		widgetTable.clear()
		widgetTable.add(RenderSystemWidget(world))

		return world
	}

	fun loadGrid(gridStr: String): Array2D<Tile>
	{
		val lines = gridStr.split('\n')
		val width = lines[0].length
		val height = lines.size

		val grid = Array2D<Tile>(width, height) { x,y -> Tile(x,y) }

		for (x in 0 until width)
		{
			for (y in 0 until height)
			{
				val c = lines[y][x]

				val tile = grid[x,height-y-1]
				tile.floor = AssetManager.tryLoadSpriteWrapper("Sprites/Defs/Grass")

				when (c)
				{
					'.' -> {}
					'#' -> {
						tile.wall = AssetManager.tryLoadSpriteWrapper("Sprites/Defs/Trees")
					}
					'e' -> {
						val entity = EntityPool.obtain()

						val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
						val stats =  entity.addOrGet(ComponentType.Statistics) as StatisticsComponent
						val renderable =  entity.addOrGet(ComponentType.Renderable) as RenderableComponent

						stats.faction = "enemy"
						stats.baseStatistics[Statistic.MAX_HP] = 100f
						stats.calculateStatistics(1)

						renderable.renderable = AssetManager.loadSprite("Oryx/uf_split/uf_heroes/rat")

						pos.position = tile
						tile.contents[pos.slot] = entity.getRef()
					}
					'a', '@' -> {
						val entity = EntityPool.obtain()

						val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
						val stats =  entity.addOrGet(ComponentType.Statistics) as StatisticsComponent
						val renderable =  entity.addOrGet(ComponentType.Renderable) as RenderableComponent

						stats.faction = "ally"
						stats.baseStatistics[Statistic.MAX_HP] = 100f
						stats.calculateStatistics(1)

						if (c == '@')
						{
							val name = entity.addOrGet(ComponentType.Name) as NameComponent
							name.name = "player"

							renderable.renderable = AssetManager.loadSprite("Oryx/uf_split/uf_heroes/knight")
						}
						else
						{
							renderable.renderable = AssetManager.loadSprite("Oryx/uf_split/uf_heroes/lady_a")
						}

						pos.position = tile
						tile.contents[pos.slot] = entity.getRef()
					}
					else -> throw RuntimeException("Unhandled grid char '$c'")
				}
			}
		}

		return grid
	}

	var turnTime = 0f
	override fun doRender(delta: Float)
	{
		super.doRender(delta)

		turnTime += delta
		if (turnTime > 2f)
		{
			turnTime = 0f

			if (world != null)
			{
				if (sequences!!.entities.size == 0)
				{
					lastSeed = 0L
				}
				else
				{
					world?.onTurn()
				}
			}
		}
	}
}

val closeGrid = """
	..e.......
	....a...e.
	..........
	.e.....e..
	....e.....
	....@...a.
	....a.....
	..a.......
	.......a..
	...a......
""".trimIndent()

val farGrid = """
	..e.......
	....a...e.
	..........
	.e.....e..
	..........
	....@...a.
	..........
	..a.......
	.......a..
	...a......
""".trimIndent()