package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*

class AbilityData : XmlDataClass()
{
	enum class CooldownType
	{
		TURN,
		WAIT,
		ATTACK,
		USE_ABILITY,
		KILL,
		PACK_KILL
	}

	enum class TargetType
	{
		TILE,
		EMPTY_TILE,
		ALLY,
		ANY_ENEMY,
		TARGET_ENEMY,
		SELF
	}

	@DataNeedsLocalisation(file = "Ability")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Ability")
	lateinit var description: String

	var icon: Sprite? = null

	lateinit var actionSequence: ActionSequence

	var cooldown: Int = 10
	lateinit var cooldownType: CooldownType

	var singleUse: Boolean = false

	@DataVector(name1 = "Min", name2 = "Max")
	var range: Point = Point(1, 1)
	lateinit var targetType: TargetType

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	lateinit var targetCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	@DataCompiledExpression(default = "dist")
	lateinit var sortCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	var selectMinByCondition: Boolean = true

	//region non-data
	val nameTransforms = Array<(String)->String>()
	val descriptionTransforms = Array<(String)->String>()
	//endregion

	fun get(): Ability = Ability(this)

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.tryLoadSprite(xmlData.getChildByName("Icon"))
		val actionSequenceEl = xmlData.getChildByName("ActionSequence")!!
		actionSequence = ActionSequence.load(actionSequenceEl)
		cooldown = xmlData.getInt("Cooldown", 10)
		cooldownType = CooldownType.valueOf(xmlData.get("CooldownType").toUpperCase(Locale.ENGLISH))
		singleUse = xmlData.getBoolean("SingleUse", false)
		val rangeRaw = xmlData.get("Range", "1, 1")!!.split(',')
		range = Point(rangeRaw[0].trim().toInt(), rangeRaw[1].trim().toInt())
		targetType = TargetType.valueOf(xmlData.get("TargetType").toUpperCase(Locale.ENGLISH))
		targetCondition = CompiledExpression(xmlData.get("TargetCondition", "1")!!)
		sortCondition = CompiledExpression(xmlData.get("SortCondition", "dist")!!)
		selectMinByCondition = xmlData.getBoolean("SelectMinByCondition", true)
	}
	//endregion
}