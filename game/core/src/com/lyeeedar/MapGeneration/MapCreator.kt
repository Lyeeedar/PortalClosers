package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Faction
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.Game.create
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG

class MapCreator
{
	companion object
	{
		fun floodFill(foundSet: ObjectSet<Tile>, current: Tile, source: Tile, dist: Int, world: World<*>)
		{
			if (foundSet.contains(current)) return
			if (current.dist(source) > dist) return
			foundSet.add(current)

			for (dir in Direction.CardinalValues)
			{
				val tile = world.grid.tryGet(current, dir, null) as? Tile ?: continue
				floodFill(foundSet, tile, source, dist, world)
			}
		}

		fun processSymbol(symbol: Symbol, tile: Tile, faction: Faction, level: Int, world: World<*>, rng: LightRNG)
		{
			for (slot in SpaceSlot.Values)
			{
				val entityData = symbol.contents[slot] ?: continue
				val entity = entityData.create(level, world, rng.nextLong())

				entity.addToTile(tile)

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

				val tileset = ObjectSet<Tile>()
				floodFill(tileset, tile, tile, 4, world)
				val tiles = tileset.toGdxArray()

				val leader = pack.leader.get()!!
				leader.addToTile(tile)
				world.addEntity(leader)

				tiles.removeValue(tile, true)

				for (mob in pack.mobs)
				{
					val tile = tiles.removeRandom(rng)
					val mob = mob.get()!!
					mob.addToTile(tile)
					world.addEntity(mob)
				}
			}
		}

		fun generateWorld(path: String, faction: String, player: Entity, level: Int, seed: Long): World<Tile>
		{
			val rng = Random.obtainTS(seed)
			val faction = Faction.load(faction)

			val xml = getXml(path)

			val generator = MapGenerator()
			generator.load(xml)

			val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

			val map = Array2D<Tile>(symbolGrid.width, symbolGrid.height) { x,y -> Tile(x, y) }
			val world = World(map)
			world.addSystems()
			world.rng = LightRNG(seed)

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
			val playerSpawnArea = generator.namedAreas["playerspawn"][0]
			val playerSpawnPos = playerSpawnArea.getAllPoints().random(rng)

			player.position()!!.position = map[playerSpawnPos.x, playerSpawnPos.y]
			player.position()!!.addToTile(player)
			player.ai()!!.state.set(EntityReference(player), world, 0)
			player.addComponent(ComponentType.Task)
			player.addComponent(ComponentType.Renderable)

			world.player = player

			world.addEntity(player)

			rng.freeTS()

			return world
		}
	}
}