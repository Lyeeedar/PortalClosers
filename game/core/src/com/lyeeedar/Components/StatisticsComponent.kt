package com.lyeeedar.Components

import com.lyeeedar.Util.XmlData

class StatisticsComponentData : AbstractComponentData()
{

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
	}
	override val classID: String = "Statistics"
	//endregion
}

inline fun Entity.stats(): StatisticsComponent? = this.components[ComponentType.Statistics] as StatisticsComponent?
class StatisticsComponent(data: StatisticsComponentData) : AbstractComponent<StatisticsComponentData>(data)
{
	override val type: ComponentType = ComponentType.Statistics

	var faction: String = ""

	override fun reset()
	{

	}
}

fun Entity.isAllies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.faction == ostats.faction
}
fun Entity.isEnemies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.faction != ostats.faction
}