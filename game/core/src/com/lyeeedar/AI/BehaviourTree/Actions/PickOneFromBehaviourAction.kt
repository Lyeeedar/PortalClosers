package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectMap
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityReference
import com.lyeeedar.Components.position
import com.lyeeedar.Components.statistics
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.lang.RuntimeException

class PickOneFromBehaviourAction : AbstractBehaviourAction()
{
	lateinit var input: String
	lateinit var output: String

	@DataCompiledExpression(knownVariables = "dist,hp,level,damage,random", default = "dist")
	lateinit var condition: CompiledExpression

	var minimum: Boolean = true

	//region non-data
	val map = ObjectFloatMap<String>()
	val itemSortValueMap = ObjectFloatMap<Any>()
	//endregion

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val entity = state.entity.get() ?: return EvaluationState.FAILED

		val array = state.getData<Array<*>>(input, 0) ?: return EvaluationState.FAILED
		if (array.size == 0) return EvaluationState.FAILED

		itemSortValueMap.clear()
		for (item in array)
		{
			val sortValue = when (item)
			{
				is EntityReference -> {
					map.clear()
					val dist = item.entity.position()!!.position.taxiDist(entity.position()!!.position)
					map.put("dist", dist.toFloat())
					map.put("random", state.rng.nextFloat())
					item.entity.statistics()?.write(map)

					condition.evaluate(map)
				}
				is Point -> {
					map.clear()
					val dist = item.taxiDist(entity.position()!!.position)
					map.put("dist", dist.toFloat())
					map.put("random", state.rng.nextFloat())

					condition.evaluate(map)
				}
				else -> throw RuntimeException("Cannot pick one from array of " + item::class.java.name)
			}
			itemSortValueMap[item] = sortValue
		}

		val sorted = if (minimum) array.sortedBy{ itemSortValueMap[it, 0f] } else array.sortedByDescending{ itemSortValueMap[it, 0f] }
		val item = sorted.firstOrNull() ?: return EvaluationState.FAILED

		state.setData(output, 0, item)

		return EvaluationState.COMPLETED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		input = xmlData.get("Input")
		output = xmlData.get("Output")
		condition = CompiledExpression(xmlData.get("Condition"), "dist,hp,level,damage,random")
		minimum = xmlData.getBoolean("Minimum", true)
	}
	override val classID: String = "PickOneFrom"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}