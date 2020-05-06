package com.lyeeedar.Game

import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Util.CompiledExpression
import com.lyeeedar.Util.DataNeedsLocalisation
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass
import java.util.*

class Ability(val data: AbilityData)
{
	var remainingCooldown = data.cooldown.toFloat()

	var justUsed = false
}

class AbilityData : XmlDataClass()
{
	enum class CooldownType
	{
		TURN,
		WAIT,
		ATTACK,
		USE_ABILITY
	}

	enum class TargetType
	{
		TILE,
		EMPTY_TILE,
		ALLY,
		ANY_ENEMY,
		TARGET_ENEMY
	}

	@DataNeedsLocalisation(file = "Ability")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Ability")
	lateinit var description: String

	lateinit var actionSequence: ActionSequence

	var cooldown: Int = 10
	lateinit var cooldownType: CooldownType

	var singleUse: Boolean = false

	var range: Int = 1
	lateinit var targetType: TargetType
	lateinit var targetCondition: CompiledExpression

	fun get(): Ability = Ability(this)

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		val actionSequenceEl = xmlData.getChildByName("ActionSequence")!!
		actionSequence = ActionSequence()
		actionSequence.load(actionSequenceEl)
		cooldown = xmlData.getInt("Cooldown", 10)
		cooldownType = CooldownType.valueOf(xmlData.get("CooldownType").toUpperCase(Locale.ENGLISH))
		singleUse = xmlData.getBoolean("SingleUse", false)
		range = xmlData.getInt("Range", 1)
		targetType = TargetType.valueOf(xmlData.get("TargetType").toUpperCase(Locale.ENGLISH))
		targetCondition = CompiledExpression(xmlData.get("TargetCondition"))
	}
	//endregion
}