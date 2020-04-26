package com.lyeeedar.MapGeneration

import com.lyeeedar.Components.*
import com.lyeeedar.Game.Faction
import com.lyeeedar.Game.Tile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.*

class MapCreator
{
	fun generateMap(path: String, faction: String, player: Entity, seed: Long): Array2D<Tile>
	{
		val rng = Random.obtainTS(seed)
		val faction = Faction.load(faction)

		val xml = getXml(path)

		val generator = MapGenerator()
		generator.load(xml)

		val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

		val map = Array2D<Tile>(symbolGrid.width, symbolGrid.height) { x,y -> Tile(x, y) }

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
				}

				val enemyDesc = symbol.enemyDescription
				if (enemyDesc != null)
				{
					val faction = if (enemyDesc.faction != null) Faction.load(enemyDesc.faction!!) else faction

					val entityData = if (enemyDesc.isBoss) faction.bosses.random(rng) else faction.enemies.random(rng)
					val entity = entityData.create()

					val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
					pos.position = tile
					pos.addToTile(entity)
				}
			}
		}

		val playerSpawnArea = generator.namedAreas["playerspawn"][0]
		val playerSpawnPos = playerSpawnArea.getAllPoints().random(rng)

		player.position()!!.position = map[playerSpawnPos.x, playerSpawnPos.y]
		player.position()!!.addToTile(player)

		rng.freeTS()

		return map
	}
}