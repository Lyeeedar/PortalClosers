package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Components.EventAndCondition
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*
import squidpony.squidmath.LightRNG

@DataFile(colour = "154,186,114", icon = "Sprites/Oryx/uf_split/uf_items/weapon_axe_exotic1.png")
class Weapon : XmlDataClass()
{
	val moves: Array<WeaponMove> = Array()

	var defaultResources: Int = 0
	var minResources: Int = 0
	var maxResources: Int = 5

	val handlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	//region generated
	override fun load(xmlData: XmlData)
	{
		val movesEl = xmlData.getChildByName("Moves")
		if (movesEl != null)
		{
			for (el in movesEl.children)
			{
				val objmoves: WeaponMove
				val objmovesEl = el
				objmoves = WeaponMove()
				objmoves.load(objmovesEl)
				moves.add(objmoves)
			}
		}
		defaultResources = xmlData.getInt("DefaultResources", 0)
		minResources = xmlData.getInt("MinResources", 0)
		maxResources = xmlData.getInt("MaxResources", 5)
		val handlersEl = xmlData.getChildByName("Handlers")
		if (handlersEl != null)
		{
			for (el in handlersEl.children)
			{
				val enumVal = EventType.valueOf(el.name.toUpperCase(Locale.ENGLISH))
				val objhandlers: Array<EventAndCondition> = Array()
				val objhandlersEl = el
				if (objhandlersEl != null)
				{
					for (el in objhandlersEl.children)
					{
						val objobjhandlers: EventAndCondition
						val objobjhandlersEl = el
						objobjhandlers = EventAndCondition()
						objobjhandlers.load(objobjhandlersEl)
						objhandlers.add(objobjhandlers)
					}
				}
				handlers[enumVal] = objhandlers
			}
		}
	}
	//endregion
}

class WeaponMove : XmlDataClass()
{
	val variants: Array<MoveVariant> = Array()

	fun updateAvailability(variables: ObjectFloatMap<String>, rng: LightRNG)
	{
		for (variant in variants)
		{
			variant.getAsAbility().available = variant.availableCondition.evaluate(variables, rng) > 0f
		}
	}

	fun getAsAbility(): Ability
	{
		var ability: Ability = variants[0].getAsAbility()

		for (variant in variants)
		{
			ability = variant.getAsAbility()
			if (ability.available)
			{
				return ability
			}
		}
		return ability
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		val variantsEl = xmlData.getChildByName("Variants")
		if (variantsEl != null)
		{
			for (el in variantsEl.children)
			{
				val objvariants: MoveVariant
				val objvariantsEl = el
				objvariants = MoveVariant()
				objvariants.load(objvariantsEl)
				variants.add(objvariants)
			}
		}
	}
	//endregion
}

class MoveVariant : XmlDataClass()
{
	@DataCompiledExpression(default = "1", knownVariables = "resources")
	lateinit var availableCondition: CompiledExpression

	lateinit var ability: AbilityData

	@Transient
	var actualAbility: Ability? = null

	fun getAsAbility(): Ability
	{
		if (actualAbility == null)
		{
			actualAbility = Ability(ability)
		}
		return actualAbility!!
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		availableCondition = CompiledExpression(xmlData.get("AvailableCondition", "1")!!)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityData()
		ability.load(abilityEl)
	}
	//endregion
}