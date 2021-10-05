package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG

class MapCreator
{
	companion object
	{
		private fun floodFill(foundSet: ObjectSet<Tile>, current: Tile, source: Tile, dist: Int, world: World<*>)
		{
			if (foundSet.contains(current)) return
			if (current.dist(source) > dist) return
			foundSet.add(current)

			for (dir in Direction.CardinalValues)
			{
				val tile = world.grid.tryGet(current, dir, null) as? Tile ?: continue

				if (tile.wall != null) continue
				if (tile.contents[SpaceSlot.WALL] != null) continue

				floodFill(foundSet, tile, source, dist, world)
			}
		}

		private fun floodFill(source: Tile, dist: Int, world: World<*>, slot: SpaceSlot): Array<Tile>
		{
			val set = ObjectSet<Tile>()
			floodFill(set, source, source, dist, world)
			set.remove(source)

			return set.filter { it.contents[slot] == null }.sorted().toGdxArray()
		}

		private fun processSymbol(symbol: Symbol, tile: Tile, level: Int, world: World<*>, rng: LightRNG, deferredActions: Array<()->Unit>)
		{
			for (slot in SpaceSlot.Values)
			{
				val entityData = symbol.contents[slot] ?: continue
				val entity = entityData.create()

				entity.addToTile(tile, slot)

				world.addEntity(entity)
			}
		}

		fun generateWorld(path: String, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val xml = getXml(path)
			return generateWorld(xml, player, level, seed)
		}

		fun generateWorld(xml: XmlData, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val generator = MapGenerator()
			generator.load(xml)

			val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

			return generateWorld(symbolGrid, generator.namedAreas, player, level, seed)
		}

		fun generateWorld(symbolGrid: Array2D<Symbol>, namedAreas: ObjectMap<String, Array<Area>>, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val rng = Random.obtainTS(seed)

			val map = Array2D<Tile>(symbolGrid.width, symbolGrid.height) { x,y -> Tile(x, y) }
			val world = World(map)
			world.rng = LightRNG(seed)
			world.ambientLight.set(0.6f, 0.6f, 0.6f, 1f)

			// setup tiles base
			for (x in 0 until map.width)
			{
				for (y in 0 until map.height)
				{
					val symbol = symbolGrid[x, y]
					val tile = map[x, y]

					tile.floor = AssetManager.tryLoadSpriteWrapper(symbol.floor)
					tile.wall = AssetManager.tryLoadSpriteWrapper(symbol.wall)
					tile.world = world
				}
			}

			// add entities
			val deferredActions = Array<()->Unit>()
			for (x in 0 until map.width)
			{
				for (y in 0 until map.height)
				{
					val symbol = symbolGrid[x, y]
					val tile = map[x, y]

					processSymbol(symbol, tile, level, world, rng, deferredActions)
				}
			}
			for (action in deferredActions)
			{
				action.invoke()
			}

			// add player
			val playerSpawnArea = namedAreas["playerspawn"][0]
			val playerStartTile = map[playerSpawnArea.getAllPoints()[0].toPoint()]
			val startRoomPoints = floodFill(playerStartTile, 4, world, SpaceSlot.ENTITY)

			player.addToTile(playerStartTile)
			player.ai()!!.state.set(player.getRef(), world, 0)
			player.addComponent(ComponentType.Task)
			player.addComponent(ComponentType.Renderable)

			world.player = player
			world.addEntity(player)

			rng.freeTS()

			return world
		}
	}
}