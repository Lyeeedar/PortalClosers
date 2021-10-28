package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Portal.Biome
import com.lyeeedar.Game.Portal.Pack
import com.lyeeedar.Game.Tile
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

		private fun processSymbol(symbol: Symbol, tile: Tile, level: Int, world: World<*>, rng: LightRNG, deferredActions: Array<()->Unit>, creatures: Array<Entity>)
		{
			for (slot in SpaceSlot.Values)
			{
				val entityData = symbol.contents[slot] ?: continue
				val entity = entityData.create()

				if (entity.hasComponent(ComponentType.Statistics) && entity.hasComponent(ComponentType.AI))
				{
					creatures.add(entity)
				}

				entity.addToTile(tile, slot)

				world.addEntity(entity)
			}
		}

		fun generateWorld(biome: Biome, pack: Pack, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val generator = MapGenerator()
			generator.load(getXml(biome.roomGenerator))

			val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

			return generateWorld(biome, pack, symbolGrid, generator.namedAreas, player, level, seed)
		}

		fun generateWorld(biome: Biome, pack: Pack, symbolGrid: Array2D<Symbol>, namedAreas: ObjectMap<String, Array<Area>>, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val rng = Random.obtainTS(seed)

			val map = Array2D<Tile>(symbolGrid.width, symbolGrid.height) { x,y -> Tile(x, y) }
			val world = World(map)
			world.rng = LightRNG(seed)
			world.ambientLight.set(biome.ambientLight)

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
			val creatures = Array<Entity>()
			for (x in 0 until map.width)
			{
				for (y in 0 until map.height)
				{
					val symbol = symbolGrid[x, y]
					val tile = map[x, y]

					processSymbol(symbol, tile, level, world, rng, deferredActions, creatures)
				}
			}

			// add pack entities
			val possibleEnemyTiles = namedAreas["enemyspawn"][0].getAllPoints().map { it.toPoint() }.toGdxArray()
			for (creature in pack.creatures)
			{
				val entity = EntityLoader.load(creature.entity)
				val ePos = entity.addOrGet(ComponentType.Position) as PositionComponent

				var i = 0
				while (true)
				{
					val pos = possibleEnemyTiles.random(rng)
					val tile = map[pos]
					if (ePos.isValidTile(tile, entity))
					{
						entity.addToTile(tile)

						for (x in 0 until ePos.size)
						{
							for (y in 0 until ePos.size)
							{
								val tile = map[pos.x+x, pos.y+y]
								possibleEnemyTiles.removeValue(tile, false)
							}
						}

						break
					}

					i++
					if (i > 10)
					{
						throw RuntimeException("Unable to find spot to place entity: $entity")
					}
				}

				creatures.add(entity)
				world.addEntity(entity)
			}

			// load creatures
			for (creature in creatures)
			{
				creature.statistics()!!.calculateStatistics(level)
				creature.statistics()!!.faction = "enemy"
				creature.ai()!!.state.set(creature.getRef(), world, rng.nextLong())
				creature.addComponent(ComponentType.Task)
			}

			// finish actions
			for (action in deferredActions)
			{
				action.invoke()
			}

			// add player
			val playerSpawnArea = namedAreas["playerspawn"][0]
			val playerStartTile = map[playerSpawnArea.getAllPoints()[0].toPoint()]

			player.addToTile(playerStartTile)
			player.ai()!!.state.set(player.getRef(), world, rng.nextLong())
			player.addComponent(ComponentType.Task)

			world.player = player
			world.addEntity(player)

			rng.freeTS()

			return world
		}
	}
}