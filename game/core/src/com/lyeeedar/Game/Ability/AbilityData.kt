package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*

class AbilityData : XmlDataClass()
{
	enum class TargetType
	{
		TILE,
		EMPTY_TILE,
		ALLY,
		ANY_ENEMY,
		TARGET_ENEMY,
		SELF
	}

	lateinit var actionSequence: ActionSequence

	var cooldown: Int = 0

	var usages: Int = -1

	@DataValue(visibleIf = "TargetType != Self")
	@DataVector(name1 = "Min", name2 = "Max")
	var range: Point = Point(1, 1)
	lateinit var targetType: TargetType

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile && TargetType != Self")
	lateinit var targetCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile && TargetType != Self")
	@DataCompiledExpression(default = "dist")
	lateinit var sortCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile && TargetType != Self")
	var pickSortedMin: Boolean = true

	//region non-data
	val nameTransforms = Array<(String)->String>()
	val descriptionTransforms = Array<(String)->String>()
	//endregion

	fun get(): Ability = Ability(this)

	//region generated
	override fun load(xmlData: XmlData)
	{
		val actionSequenceEl = xmlData.getChildByName("ActionSequence")!!
		actionSequence = ActionSequence.load(actionSequenceEl)
		cooldown = xmlData.getInt("Cooldown", 0)
		usages = xmlData.getInt("Usages", -1)
		val rangeRaw = xmlData.get("Range", "1, 1")!!.split(',')
		range = Point(rangeRaw[0].trim().toInt(), rangeRaw[1].trim().toInt())
		targetType = TargetType.valueOf(xmlData.get("TargetType").toUpperCase(Locale.ENGLISH))
		targetCondition = CompiledExpression(xmlData.get("TargetCondition", "1")!!)
		sortCondition = CompiledExpression(xmlData.get("SortCondition", "dist")!!)
		pickSortedMin = xmlData.getBoolean("PickSortedMin", true)
	}
	//endregion
}