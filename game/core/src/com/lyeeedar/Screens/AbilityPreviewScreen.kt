package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.*
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG
import java.lang.RuntimeException

class AbilityPreviewScreen :  AbstractWorldPreviewScreen("ability")
{
	var deltaMultiplier = 1f
	var sequences: EntitySignature? = null

	enum class Range
	{
		CLOSE,
		LONG
	}
	lateinit var rangeBox: SelectBox<Range>

	override fun addOptions(table: Table)
	{
		rangeBox = SelectBox<Range>(Statics.skin)
		rangeBox.setItems(Range.values().toGdxArray())
		rangeBox.selected = Range.CLOSE
		rangeBox.addListener(object : ChangeListener()
		                     {
			                     override fun changed(event: ChangeEvent?, actor: Actor?)
			                     {
				                     lastSeed = 0L
			                     }
		                     })

		table.add(Label("Range", Statics.skin))
		table.add(rangeBox)
		table.row()

		val playbackSpeedBox = SelectBox<Float>(Statics.skin)
		playbackSpeedBox.setItems(0.01f, 0.05f, 0.1f, 0.25f, 0.5f, 0.75f, 1f, 1.5f, 2f, 3f, 4f, 5f)
		playbackSpeedBox.selected = 1f

		playbackSpeedBox.addListener(object : ChangeListener()
		                             {
			                             override fun changed(event: ChangeEvent?, actor: Actor?)
			                             {
				                             deltaMultiplier = playbackSpeedBox.selected
				                             lastSeed = 0L
			                             }

		                             })

		table.add(Label("Time Multiplier", Statics.skin))
		table.add(playbackSpeedBox)
		table.row()
	}

	override fun modifyDelta(delta: Float): Float
	{
		return delta * deltaMultiplier
	}

	override fun loadResource(xmlData: XmlData): World<Tile>
	{
		val gridStr = when(rangeBox.selected)
		{
			Range.CLOSE -> closeGrid
			Range.LONG -> farGrid
		}

		val world = loadAbilityTestWorld(gridStr)

		world.systems.add(BloodSystem(world))
		world.systems.add(DialogueSystem(world))
		world.systems.add(DirectionalSpriteSystem(world))

		val abilityDef = AbilityOrb()
		abilityDef.load(xmlData)

		addAbilityToWorld(abilityDef, world, seed)

		sequences = world.getEntitiesFor().all(ComponentType.ActionSequence).get()
		turnTime = 0f

		return world
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

fun loadAbilityTestGrid(gridStr: String): Array2D<Tile>
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
					stats.baseStatistics[Statistic.MAX_HP] = 200f
					stats.baseStatistics[Statistic.ATK_POWER] = 100f
					stats.baseStatistics[Statistic.ARMOUR] = 100f
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
					stats.baseStatistics[Statistic.MAX_HP] = 200f
					stats.baseStatistics[Statistic.ATK_POWER] = 100f
					stats.baseStatistics[Statistic.ARMOUR] = 100f
					stats.baseStatistics[Statistic.CRIT_CHANCE] = 0.2f
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

fun loadAbilityTestWorld(gridStr: String): World<Tile>
{
	val grid = loadAbilityTestGrid(gridStr)

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

	// update
	world.systems.add(ActionSequenceSystem(world))
	world.systems.add(StatisticsSystem(world))
	world.systems.add(EventSystem(world))
	world.systems.add(AbilitySystem(world))

	// cleanup
	world.systems.add(DeletionSystem(world))

	return world
}

fun addAbilityToWorld(abilityOrb: AbilityOrb, world: World<Tile>, seed: Long)
{
	val abilityData = abilityOrb.getAbility(1)
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
	if (target != null) sequenceHolder.actionSequenceState.targets.add(target)

	val abilityHolder = player.addOrGet(ComponentType.ActiveAbility) as ActiveAbilityComponent
	abilityHolder.ability = ability

	sequenceHolder.actionSequence.update(0f, sequenceHolder.actionSequenceState)
}

val closeGrid = """
	..e.......
	....a...e.
	..........
	.e...e.e..
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
	.e...e.e..
	..........
	....@...a.
	..........
	..a.......
	.......a..
	...a......
""".trimIndent()