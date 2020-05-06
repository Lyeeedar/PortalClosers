package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Components.*
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG

class Ability(val data: AbilityData)
{
	var remainingCooldown = data.cooldown.toFloat()

	var justUsed = false

	val name: String
		get() = Localisation.getText(data.name, "Ability")

	val description: String
		get() = Localisation.getText(data.description, "Ability")

	fun getValidTargets(entity: Entity, world: World<*>): List<AbstractTile>
	{
		val pos = entity.position()!!
		val vision = entity.addOrGet(ComponentType.Vision) as VisionComponent
		val visiblePoints = vision.getVision(pos.x, pos.y)
		val visibleTiles = visiblePoints.filter { it.dist(pos.position) in data.range.x..data.range.y }.mapNotNull { world.grid.tryGet(it, null) }

		return when (data.targetType)
		{
			AbilityData.TargetType.TILE -> visibleTiles
			AbilityData.TargetType.EMPTY_TILE -> visibleTiles.filter { it.getPassable(pos.slot, entity) }

			AbilityData.TargetType.ALLY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isAllies(entity) ?: false }

			AbilityData.TargetType.ANY_ENEMY, AbilityData.TargetType.TARGET_ENEMY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isEnemies(entity) ?: false }
		}
	}

	private val tempVariableMap = ObjectFloatMap<String>()
	private val values: Array<Pair<AbstractTile, Float>> = Array()
	fun pickTarget(entity: Entity, world: World<*>, rng: LightRNG): AbstractTile?
	{
		val validTargets = getValidTargets(entity, world)
		if (validTargets.isEmpty()) return null

		if (data.targetType == AbilityData.TargetType.TILE || data.targetType == AbilityData.TargetType.EMPTY_TILE)
		{
			return validTargets.random(rng)
		}

		val pos = entity.position()!!

		values.clear()
		for (target in validTargets)
		{
			val other = target.contents[pos.slot].get()!!

			tempVariableMap.clear()
			other.statistics()!!.write(tempVariableMap)
			tempVariableMap["dist"] = other.position()!!.position.dist(pos.position).toFloat()

			if (data.targetCondition.evaluate(tempVariableMap, rng).round() != 0)
			{
				val value = data.sortCondition.evaluate(tempVariableMap, rng)
				values.add(Pair(target, value))
			}
		}

		val sorted = if (data.selectMinByCondition) values.sortedBy { it.second } else values.sortedByDescending { it.second }
		return sorted.first().first
	}
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

	@DataVector(name1 = "Min", name2 = "Max")
	var range: Point = Point(1,1)
	lateinit var targetType: TargetType

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	lateinit var targetCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	lateinit var sortCondition: CompiledExpression

	@DataValue(visibleIf = "TargetType != Target_enemy && TargetType != Tile && TargetType != Empty_tile")
	var selectMinByCondition: Boolean = true

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
		val rangeRaw = xmlData.get("Range", "1,1")!!.split(',')
		range = Point(rangeRaw[0].trim().toInt(), rangeRaw[1].trim().toInt())
		targetType = TargetType.valueOf(xmlData.get("TargetType").toUpperCase(Locale.ENGLISH))
		targetCondition = CompiledExpression(xmlData.get("TargetCondition", "1")!!)
		sortCondition = CompiledExpression(xmlData.get("SortCondition", "1")!!)
		selectMinByCondition = xmlData.getBoolean("SelectMinByCondition", true)
	}
	//endregion
}