package com.lyeeedar.headless

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityTemplate
import com.lyeeedar.Game.Tile
import com.lyeeedar.Screens.addAbilityToWorld
import com.lyeeedar.Screens.closeGrid
import com.lyeeedar.Screens.farGrid
import com.lyeeedar.Screens.loadAbilityTestWorld
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Array2D
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getXml
import com.lyeeedar.Util.random
import org.junit.Assert.assertEquals
import java.util.*

class AbilityTest
{
	companion object
	{
		fun runFast()
		{
			val paths = XmlData.enumeratePaths("", "AbilityOrb").toList()

			var total = 0
			for (path in paths)
			{
				System.out.println("Testing $path")

				val orb = AbilityTemplate()
				orb.load(getXml(path))
				for (i in 0 until 3)
				{
					testAbility(orb.getAbility(1), closeGrid, i.toLong(), 2, path)
				}
				for (i in 0 until 3)
				{
					testAbility(orb.getAbility(1), farGrid, i.toLong(), 2, path)
				}

				total++
				System.out.println("Tested $total/${paths.size}")
			}
		}

		fun runSlow()
		{
			val paths = XmlData.enumeratePaths("", "AbilityOrb").toList()

			val ran = Random()

			var total = 0
			for (path in paths)
			{
				System.out.println("Testing $path")

				val orb = AbilityTemplate()
				orb.load(getXml(path))
				for (i in 0 until 10)
				{
					testAbility(orb.getAbility(1), closeGrid, ran.nextLong(), 4, path)
				}
				for (i in 0 until 10)
				{
					testAbility(orb.getAbility(1), farGrid, ran.nextLong(), 4, path)
				}
				for (i in 0 until 10)
				{
					val grid = genRandomGrid()
					println(grid)
					for (i in 0 until 10)
					{
						testAbility(orb.getAbility(1), grid, ran.nextLong(), 4, path)
					}
				}

				total++
				System.out.println("Tested $total/${paths.size}")
			}
		}

		fun genRandomGrid(): String
		{
			val ran = Random()

			val grid = Array2D<Char>(10, 10) {_,_ -> '.'}

			for (i in 0 until 5)
			{
				val x = ran.nextInt(10)
				val y = ran.nextInt(10)

				grid[x,y] = 'a'
			}

			for (i in 0 until 5)
			{
				val x = ran.nextInt(10)
				val y = ran.nextInt(10)

				grid[x,y] = 'e'
			}

			val x = ran.nextInt(10)
			val y = ran.nextInt(10)
			grid[x,y] = '@'

			val output = StringBuilder()
			for (y in 0 until 10)
			{
				for (x in 0 until 10)
				{
					output.append(grid[x,y])
				}
				output.append("\n")
			}

			return output.toString().trim()
		}

		fun testAbility(abilityOrb: Ability, grid: String, seed: Long, numIterations: Int, fileName: String)
		{
			try
			{
				val world = loadAbilityTestWorld(grid)
				addAbilityToWorld(abilityOrb, world, seed)

				val sequences = world.getEntitiesFor().all(ComponentType.ActionSequence).get()

				val stateHistory = Array<String>()
				while (true)
				{
					for (i in 0 until 10)
					{
						world.update(0.5f)
						stateHistory.add(worldToString(world))
					}

					if (sequences.entities.size == 0)
					{
						break
					}
					else
					{
						world.onTurn()
					}
				}

				for (n in 0 until numIterations)
				{
					val world = loadAbilityTestWorld(grid)
					addAbilityToWorld(abilityOrb, world, seed)

					val sequences = world.getEntitiesFor().all(ComponentType.ActionSequence).get()

					var hi = 0
					while (true)
					{
						for (i in 0 until 10)
						{
							world.update(0.5f)

							val state = worldToString(world)
							val message = "Diverged at update $hi"

							assertEquals(message, stateHistory[hi++], state)
						}

						if (sequences.entities.size == 0)
						{
							break
						}
						else
						{
							world.onTurn()
						}
					}

					assertEquals(stateHistory.size, hi)
				}
			}
			catch (ex: Exception)
			{
				ex.printStackTrace()
				throw RuntimeException("$fileName:$seed failed with " + ex.message)
			}
		}

		fun worldToString(world: World<Tile>): String
		{
			val builder = StringBuilder()

			builder.append("Entities:").append(world.entities.size).append("\n\n")

			for (y in 0 until world.grid.height)
			{
				for (x in 0 until world.grid.width)
				{
					val tile = world.grid[x,y]
					if (tile.contents.size != 0)
					{
						builder.append("(${tile.toShortString()})")

						for (slot in SpaceSlot.Values)
						{
							val contents = tile.contents[slot] ?: continue
							builder.append(contents.toString())
						}

						builder.append("\n")
					}
				}
			}

			return builder.toString()
		}
	}
}