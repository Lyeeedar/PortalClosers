package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.toGdxArray

@DataClass(category = "Permute", colour = "212,176,126", name = "PickEnts")
class SelectEntitiesAction : AbstractOneShotActionSequenceAction()
{
	enum class Mode
	{
		ALLIES,
		ENEMIES,
		ANY
	}
	lateinit var mode: Mode

	@DataCompiledExpression(knownVariables = "count", default = "count")
	lateinit var count: CompiledExpression

	@DataCompiledExpression(knownVariables = "random,dist,hp", default = "random")
	lateinit var condition: CompiledExpression

	var radius: Int = 5
	var minimum: Boolean = true

	var allowSelf: Boolean = true
	var allowCurrent: Boolean = true

	//region non-data
	val oldTargetsStore = ObjectSet<Point>()
	val entities = ObjectSet<Entity>()
	val variables = ObjectFloatMap<String>()
	val itemSortValueMap = ObjectFloatMap<Any>()
	//endregion

	private fun createVariables(entity: Entity, state: ActionSequenceState): ObjectFloatMap<String>
	{
		variables.clear()
		variables["dist"] = entity.position()!!.position.dist(state.source.get()!!.position()!!.position).toFloat()

		return variables
	}

	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get()!!

		oldTargetsStore.clear()
		oldTargetsStore.addAll(state.targets)

		state.targets.clear()
		state.lockedEntityTargets.clear()

		val pos = source.position()!!.position

		val xs = max(0, pos.x-radius)
		val xe = min(state.world.grid.width, pos.x+radius)
		val ys = max(0, pos.y-radius)
		val ye = min(state.world.grid.height, pos.y+radius)

		entities.clear()
		itemSortValueMap.clear()
		for (x in xs until xe)
		{
			for (y in ys until ye)
			{
				val tile = state.world.grid[x, y]
				if (!allowCurrent)
				{
					if (oldTargetsStore.contains(tile))
					{
						continue
					}
				}

				for (slot in SpaceSlot.EntityValues)
				{
					val entity = tile.contents[slot]?.get() ?: continue
					if (entity.position() == null || entity.isMarkedForDeletion() || (entity.statistics()?.hp ?: 0f) <= 0f) continue

					if (!allowSelf)
					{
						if (entity == source)
						{
							continue
						}
					}

					var add = false
					if (mode == Mode.ALLIES)
					{
						if (entity.isAllies(source))
						{
							add = true
						}
					}
					else if (mode == Mode.ENEMIES)
					{
						if (entity.isEnemies(source))
						{
							add = true
						}
					}
					else
					{
						if (entity.isAllies(source) || entity.isEnemies(source))
						{
							add = true
						}
					}

					if (add)
					{
						entities.add(entity)
						val value = condition.evaluate(createVariables(entity, state), state.rng)
						itemSortValueMap[entity] = value
					}
				}
			}
		}


		val sorted = if (minimum)
									entities.sortedBy { itemSortValueMap[it, 0f] }.toGdxArray()
								else
									entities.sortedByDescending { itemSortValueMap[it, 0f] }.toGdxArray()

		variables.clear()
		variables["count"] = sorted.size.toFloat()

		val numTiles = count.evaluate(variables, state.rng).round()
		for (i in 0 until numTiles)
		{
			if (i == sorted.size) break

			val entity = sorted[i]
			val pos = entity.position()?.position ?: continue
			state.targets.add(pos)
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		mode = Mode.valueOf(xmlData.get("Mode").toUpperCase(Locale.ENGLISH))
		count = CompiledExpression(xmlData.get("Count", "count")!!)
		condition = CompiledExpression(xmlData.get("Condition", "random")!!)
		radius = xmlData.getInt("Radius", 5)
		minimum = xmlData.getBoolean("Minimum", true)
		allowSelf = xmlData.getBoolean("AllowSelf", true)
		allowCurrent = xmlData.getBoolean("AllowCurrent", true)
	}
	override val classID: String = "SelectEntities"
	//endregion
}