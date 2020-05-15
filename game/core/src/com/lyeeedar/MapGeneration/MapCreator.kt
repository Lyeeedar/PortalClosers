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
		private fun floodFill(foundSet: ObjectSet<Tile>, current: Tile, source: Tile, dist: Int, world: World<*>, slot: SpaceSlot)
		{
			if (foundSet.contains(current)) return
			if (current.dist(source) > dist) return
			foundSet.add(current)

			for (dir in Direction.CardinalValues)
			{
				val tile = world.grid.tryGet(current, dir, null) as? Tile ?: continue

				if (tile.wall != null) continue
				if (tile.contents[SpaceSlot.WALL] != null) continue
				if (tile.contents[slot] != null) continue

				floodFill(foundSet, tile, source, dist, world, slot)
			}
		}

		private fun floodFill(source: Tile, dist: Int, world: World<*>, slot: SpaceSlot): Array<Tile>
		{
			val set = ObjectSet<Tile>()
			floodFill(set, source, source, dist, world, slot)
			set.remove(source)

			return set.sorted().toGdxArray()
		}

		private fun processSymbol(symbol: Symbol, tile: Tile, faction: Faction, level: Int, world: World<*>, rng: LightRNG)
		{
			for (slot in SpaceSlot.Values)
			{
				val entityData = symbol.contents[slot] ?: continue
				val entity = entityData.create(level, world, rng.nextLong())

				entity.addToTile(tile, slot)

				world.addEntity(entity)
			}

			val enemyDesc = symbol.enemyDescription
			if (enemyDesc != null)
			{
				val faction = if (enemyDesc.faction != null) Faction.load(enemyDesc.faction!!) else faction

				val sourcePool = if (enemyDesc.isBoss) faction.bosses else faction.mobs

				val entityData = sourcePool.random(rng)
				val entity = entityData.entity.create(level+(enemyDesc.difficulty-1), world, rng.nextLong())

				entity.addToTile(tile)

				world.addEntity(entity)
			}

			val packDesc = symbol.packDescription
			if (packDesc != null)
			{
				val faction = if (packDesc.faction != null) Faction.load(packDesc.faction!!) else faction
				val pack = faction.getPack(rng, rng.nextInt(packDesc.size.x, packDesc.size.y+1), packDesc.isBoss).create(level+(packDesc.difficulty-1), world, rng.nextLong())

				val leader = pack.leader.get()!!
				leader.addToTile(tile)
				world.addEntity(leader)

				for (mob in pack.mobs)
				{
					val mob = mob.get()!!

					val tiles = floodFill(tile, 4, world, mob.position()!!.slot)
					val tile = tiles.random(rng)

					mob.addToTile(tile)
					world.addEntity(mob)
				}
			}
		}

		fun generateWorld(path: String, faction: String, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val xml = getXml(path)
			return generateWorld(xml, faction, player, level, seed)
		}

		fun generateWorld(xml: XmlData, faction: String, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val generator = MapGenerator()
			generator.load(xml)

			val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

			return generateWorld(symbolGrid, generator.namedAreas, faction, player, level, seed)
		}

		fun generateWorld(symbolGrid: Array2D<Symbol>, namedAreas: ObjectMap<String, Array<Area>>, faction: String, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val rng = Random.obtainTS(seed)
			val faction = Faction.load(faction)

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
			for (x in 0 until map.width)
			{
				for (y in 0 until map.height)
				{
					val symbol = symbolGrid[x, y]
					val tile = map[x, y]

					processSymbol(symbol, tile, faction, level, world, rng)
				}
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

			val saurena = EntityLoader.load("Entities/Saurena")
			saurena.addToTile(startRoomPoints.removeRandom(rng))
			saurena.statistics()!!.calculateStatistics(level)
			saurena.ai()!!.state.set(saurena.getRef(), world, 1)
			saurena.ai()!!.state.setData("leader", 0, player.getRef())
			world.addEntity(saurena)

			val julianna = EntityLoader.load("Entities/Julianna")
			julianna.addToTile(startRoomPoints.removeRandom(rng))
			julianna.statistics()!!.calculateStatistics(level)
			julianna.ai()!!.state.set(julianna.getRef(), world, 1)
			julianna.ai()!!.state.setData("leader", 0, player.getRef())
			world.addEntity(julianna)

			Pack.createFrom(player.getRef(), arrayOf(saurena.getRef(), julianna.getRef()))

			rng.freeTS()

			return world
		}
	}
}