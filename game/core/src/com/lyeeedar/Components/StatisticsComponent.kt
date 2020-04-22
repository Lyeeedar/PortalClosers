package com.lyeeedar.Components

import com.lyeeedar.Game.Statistic
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.XmlData

class StatisticsComponentData : AbstractComponentData()
{
	val statistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	var faction: String = ""

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		Statistic.parse(xmlData.getChildByName("Statistics")!!, statistics)
		faction = xmlData.get("Faction", "")!!
	}
	override val classID: String = "Statistics"
	//endregion
}

inline fun Entity.stats(): StatisticsComponent? = this.components[ComponentType.Statistics] as StatisticsComponent?
class StatisticsComponent(data: StatisticsComponentData) : AbstractComponent<StatisticsComponentData>(data)
{
	override val type: ComponentType = ComponentType.Statistics

	override fun reset()
	{

	}
}

fun Entity.isAllies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.data.faction == ostats.data.faction
}
fun Entity.isEnemies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.data.faction != ostats.data.faction
}