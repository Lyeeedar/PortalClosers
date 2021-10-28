package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.set
import ktx.collections.toGdxArray

@DataClass(category = "Entity")
class GetAllVisibleBehaviourAction : AbstractBehaviourAction()
{
	enum class Type
	{
		TILES,
		ALLIES,
		ENEMIES
	}
	lateinit var type: Type
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val pos = entity.position()
		val stats = entity.statistics()

		if (pos == null || stats == null) return EvaluationState.FAILED

		val vision = entity.addOrGet(ComponentType.Vision) as VisionComponent
		val points = vision.getVision(pos.position.x, pos.position.y)

		if (type == Type.TILES)
		{
			state.setData(key, 0, points.toGdxArray())
		}
		else
		{
			val output = Array<EntityReference>()
			for (point in points)
			{
				val tile = state.world.grid.tryGet(point, null) ?: continue
				for (slot in SpaceSlot.EntityValues)
				{
					val other = tile.contents[slot]?.get() ?: continue

					if (other.isMarkedForDeletion() || (other.statistics()?.hp ?: 0f) <= 0f) continue

					if (type == Type.ALLIES)
					{
						if (other != entity && other.isAllies(entity))
						{
							output.add(other.getRef())
						}
					}
					else
					{
						if (other.isEnemies(entity))
						{
							output.add(other.getRef())
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
		type = Type.valueOf(xmlData.get("Type").uppercase(Locale.ENGLISH))
		key = xmlData.get("Key")
	}
	override val classID: String = "GetAllVisible"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}