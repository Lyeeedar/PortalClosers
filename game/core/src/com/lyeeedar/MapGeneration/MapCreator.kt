package com.lyeeedar.MapGeneration

import com.lyeeedar.Components.*
import com.lyeeedar.Game.Faction
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*

class MapCreator
{
	companion object
	{
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

			var seed = seed
			for (x in 0 until map.width)
			{
				for (y in 0 until map.height)
				{
					val symbol = symbolGrid[x, y]
					val tile = map[x, y]

					tile.floor = symbol.floor!!
					tile.wall = symbol.wall

					for (slot in SpaceSlot.Values)
					{
						val entityData = symbol.contents[slot] ?: continue
						val entity = entityData.create()

						val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
						pos.position = tile
						pos.addToTile(entity)

						entity.statistics()?.calculateStatistics(level)
						entity.ai()?.state?.set(entity, world, seed++)
					}

					val enemyDesc = symbol.enemyDescription
					if (enemyDesc != null)
					{
						val faction = if (enemyDesc.faction != null) Faction.load(enemyDesc.faction!!) else faction

						val sourcePool = if (enemyDesc.isBoss) faction.bosses else faction.enemies
						val filtered = sourcePool.filter { it.levelRange.x <= level && it.levelRange.y >= level }

						val entityData = filtered.random(rng)
						val entity = entityData.entity.create()

						val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
						pos.position = tile
						pos.addToTile(entity)

						entity.statistics()?.calculateStatistics(level+(enemyDesc.difficulty-1))
						entity.ai()?.state?.set(entity, world, seed++)
					}
				}
			}

			val playerSpawnArea = generator.namedAreas["playerspawn"][0]
			val playerSpawnPos = playerSpawnArea.getAllPoints().random(rng)

			player.position()!!.position = map[playerSpawnPos.x, playerSpawnPos.y]
			player.position()!!.addToTile(player)
			player.ai()!!.state.set(player, world, 0)

			rng.freeTS()

			return world
		}
	}
}