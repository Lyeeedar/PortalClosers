package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.toGdxArray

class GetAllVisibleBehaviourAction : AbstractBehaviourAction()
{
	enum class Type
	{
		TILES, ALLIES, ENEMIES
	}
	lateinit var type: Type
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val pos = state.entity.pos()
		val stats = state.entity.stats()

		if (pos == null || stats == null) return EvaluationState.FAILED

		val vision = state.entity.addOrGet(ComponentType.Vision) as VisionComponent
		val points = vision.visionCache.getShadowCast(pos.position.x, pos.position.y, 10)

		if (type == Type.TILES)
		{
			state.setData(key, 0, points.toGdxArray())
		}
		else
		{
			val output = Array<Entity>()
			for (point in points)
			{
				val tile = state.world.grid.tryGet(point, null) ?: continue
				for (slot in SpaceSlot.EntityValues)
				{
					val entity = tile.contents[slot] ?: continue

					if (entity.isMarkedForDeletion() || (entity.stats()?.hp ?: 0f) <= 0f) continue

					if (type == Type.ALLIES)
					{
						if (entity != state.entity && entity.isAllies(state.entity))
						{
							output.add(entity)
						}
					}
					else
					{
						if (entity.isEnemies(state.entity))
						{
							output.add(entity)
						}
					}
				}
			}

			state.setData(key, 0, output)
		}

		return EvaluationState.COMPLETED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		type = Type.valueOf(xmlData.get("Type").toUpperCase(Locale.ENGLISH))
		key = xmlData.get("Key")
	}
	override val classID: String = "GetAllVisible"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}