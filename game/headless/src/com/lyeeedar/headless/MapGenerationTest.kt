package com.lyeeedar.headless

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.MapGeneration.MapGenerator
import com.lyeeedar.MapGeneration.Symbol
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.*
import org.junit.Assert.*
import org.junit.Test
import java.util.Random

class MapGenerationTest
{
	companion object
	{
		fun runFast()
		{
			val paths = XmlData.enumeratePaths("", "MapGenerator").toList()

			var total = 0
			for (path in paths)
			{
				System.out.println("Generating $path")

				for (i in 0 until 5)
				{
					testLoadFile(path, i.toLong(), 2)
				}

				total++
				System.out.println("Generated $total/${paths.size}")
			}
		}

		fun runSlow()
		{
			val paths = XmlData.enumeratePaths("", "MapGenerator").toList()

			val ran = Random()

			var total = 0
			for (path in paths)
			{
				System.out.println("Generating $path")

				for (i in 0 until 50)
				{
					val seed = ran.nextLong()
					testLoadFile(path, seed, 4)
				}

				total++
				System.out.println("Generated $total/${paths.size}")
			}
		}

		fun testLoadFile(file: String, seed: Long, numIterations: Int)
		{
			testSymbolGridGeneration(file, seed, numIterations)
			testWorldCreation(file, seed, numIterations)
		}

		fun testWorldCreation(file: String, seed: Long, numIterations: Int)
		{
			val player = EntityLoader.load("Entities/player")
			player.statistics()!!.calculateStatistics(1)

			val world = MapCreator.generateWorld(file, "Factions/Rats", player, 1, seed)

			for (n in 0 until numIterations)
			{
				val player2 = EntityLoader.load("Entities/player")
				player2.statistics()!!.calculateStatistics(1)

				val world2 = MapCreator.generateWorld(file, "Factions/Rats", player2, 1, seed)

				for (x in 0 until world.grid.width)
				{
					for (y in 0 until world.grid.height)
					{
						val tile1 = world.grid[x,y]
						val tile2 = world2.grid[x,y]

						if (tile1.floor == null) assertNull(tile2.floor)
						else assertNotNull(tile2.floor)

						if (tile1.wall == null) assertNull(tile2.wall)
						else assertNotNull(tile2.wall)

						for (slot in SpaceSlot.Values)
						{
							if (tile1.contents[slot] == null) assertNull(tile2.contents[slot])
							else assertNotNull(tile2.contents[slot])
						}
					}
				}
			}
		}

		fun testSymbolGridGeneration(file: String, seed: Long, numIterations: Int)
		{
			val xml = getXml(file)

			var generator = MapGenerator()
			generator.load(xml)

			val genGridHistory = Array<Pair<String, Array<Pair<String, String>>>>()
			val genGridHistoryFull = Array<Pair<String, Array<Pair<String, String>>>>()
			val genArgsHistory = Array<Pair<String, Array<Pair<String, String>>>>()
			generator.debugExecuteNode += { node, args ->
				genGridHistory.add(Pair(gridToString(generator.rootArea.grid as Array2D<Symbol>), Array()))
				genGridHistoryFull.add(Pair(gridToStringFull(generator.rootArea.grid as Array2D<Symbol>), Array()))
				genArgsHistory.add(Pair(args.toStringFull(), Array()))

				HandlerAction.KeepAttached
			}
			generator.debugExecuteAction += { _, action, args ->
				genGridHistory.last().second.add(Pair(action.javaClass.simpleName, gridToString(generator.rootArea.grid as Array2D<Symbol>)))
				genGridHistoryFull.last().second.add(Pair(action.javaClass.simpleName, gridToStringFull(generator.rootArea.grid as Array2D<Symbol>)))
				genArgsHistory.last().second.add(Pair(action.javaClass.simpleName, args.toStringFull()))

				HandlerAction.KeepAttached
			}

			val symbolGrid = generator.execute(seed) { _,_ -> Symbol() } as Array2D<Symbol>

			for (n in 0 until numIterations)
			{
				generator = MapGenerator()
				generator.load(xml)
				generator.debugExecuteNode.clear()
				generator.debugExecuteAction.clear()

				var ni = -1
				var ai = 0
				var lastAction = ""
				generator.debugExecuteNode += { node,args ->
					ai = 0
					ni++

					val gridState = gridToString(generator.rootArea.grid as Array2D<Symbol>)
					val gridStateFull = gridToStringFull(generator.rootArea.grid as Array2D<Symbol>)
					val message = "Diverged after n:$ni, a:$ai, $lastAction"

					assertEquals(message, genArgsHistory[ni].first, args.toStringFull())
					assertEquals(message, genGridHistory[ni].first, gridState)
					assertEquals(message, genGridHistoryFull[ni].first, gridStateFull)

					HandlerAction.KeepAttached
				}
				generator.debugExecuteAction += { _, action, args ->
					val gridState = gridToString(generator.rootArea.grid as Array2D<Symbol>)
					val gridStateFull = gridToStringFull(generator.rootArea.grid as Array2D<Symbol>)
					val message = "Diverged after n:$ni, a:$ai, " + genGridHistory[ni].second[ai].first

					assertEquals(message, action.javaClass.simpleName, genGridHistory[ni].second[ai].first)
					assertEquals(message, genArgsHistory[ni].second[ai].second, args.toStringFull())
					assertEquals(message, genGridHistory[ni].second[ai].second, gridState)
					assertEquals(message, genGridHistoryFull[ni].second[ai].second, gridStateFull)

					ai++

					lastAction = action.javaClass.simpleName

					HandlerAction.KeepAttached
				}

				val symbolGrid2 = generator.execute(seed) { _, _ -> Symbol() } as Array2D<Symbol>

				areSymbolGridsSame(symbolGrid, symbolGrid2)
			}
		}

		fun gridToString(symbolGrid: Array2D<Symbol>): String
		{
			val builder = StringBuilder()

			for (y in 0 until symbolGrid.height)
			{
				for (x in 0 until symbolGrid.width)
				{
					builder.append(symbolGrid[x, y].char)
				}
				builder.append("\n")
			}

			return builder.toString()
		}

		fun gridToStringFull(symbolGrid: Array2D<Symbol>): String
		{
			val builder = StringBuilder()

			for (y in 0 until symbolGrid.height)
			{
				for (x in 0 until symbolGrid.width)
				{
					builder.append(symbolGrid[x, y].toStringFull()).append(" , ")
				}
				builder.append("\n")
			}

			return builder.toString()
		}

		fun areSymbolGridsSame(symbolGrid: Array2D<Symbol>, symbolGrid2: Array2D<Symbol>)
		{
			for (x in 0 until symbolGrid.width)
			{
				for (y in 0 until symbolGrid.height)
				{
					val s1 = symbolGrid[x,y]
					val s2 = symbolGrid2[x,y]

					if (s1.char != s2.char) throw RuntimeException()
					if ((s1.enemyDescription != null) != (s2.enemyDescription != null)) throw RuntimeException()
					if ((s1.packDescription != null) != (s2.packDescription != null)) throw RuntimeException()

					if (s1.wall != s2.wall) throw RuntimeException()
					if (s1.floor != s2.floor) throw RuntimeException()

					for (slot in SpaceSlot.values())
					{
						if ((s1.contents[slot] != null) != (s2.contents[slot] != null)) throw RuntimeException()
					}
				}
			}
		}
	}
}