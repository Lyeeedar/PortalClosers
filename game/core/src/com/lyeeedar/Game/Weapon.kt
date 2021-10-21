package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Components.EventAndCondition
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Ability.AbilityTemplate
import com.lyeeedar.Renderables.Renderable
import com.lyeeedar.Renderables.SkeletonData
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*
import squidpony.squidmath.LightRNG

@DataFile(colour = "154,186,114", icon = "Sprites/Oryx/uf_split/uf_items/weapon_axe_exotic1.png")
class Weapon : XmlDataClass()
{
	@DataNeedsLocalisation(file = "Weapon")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Weapon")
	lateinit var description: String

	@DataLayeredSprite
	lateinit var weaponIcon: Sprite

	@DataLayeredSprite
	lateinit var resourcesIcon: Sprite

	@DataNeedsLocalisation(file = "Weapon")
	lateinit var resourcesName: String

	lateinit var renderable: Renderable

	var attackMove: WeaponMove? = null
	var waitMove: WeaponMove? = null

	@DataValue(dataName = "Moves")
	val otherMoves: Array<WeaponMove> = Array()

	@Transient
	val moves: Sequence<WeaponMove> = sequence {
		if (attackMove != null) yield(attackMove!!)
		if (waitMove != null) yield(waitMove!!)
		for (move in otherMoves)
		{
			yield(move)
		}
	}

	var defaultResources: Int = 0
	var maxResources: Int = 5

	val handlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		weaponIcon = AssetManager.loadLayeredSprite(xmlData.getChildByName("WeaponIcon")!!)
		resourcesIcon = AssetManager.loadLayeredSprite(xmlData.getChildByName("ResourcesIcon")!!)
		resourcesName = xmlData.get("ResourcesName")
		renderable = AssetManager.loadRenderable(xmlData.getChildByName("Renderable")!!)
		val attackMoveEl = xmlData.getChildByName("AttackMove")
		if (attackMoveEl != null)
		{
			attackMove = WeaponMove()
			attackMove!!.load(attackMoveEl)
		}
		val waitMoveEl = xmlData.getChildByName("WaitMove")
		if (waitMoveEl != null)
		{
			waitMove = WeaponMove()
			waitMove!!.load(waitMoveEl)
		}
		val otherMovesEl = xmlData.getChildByName("Moves")
		if (otherMovesEl != null)
		{
			for (el in otherMovesEl.children)
			{
				val objotherMoves: WeaponMove
				val objotherMovesEl = el
				objotherMoves = WeaponMove()
				objotherMoves.load(objotherMovesEl)
				otherMoves.add(objotherMoves)
			}
		}
		defaultResources = xmlData.getInt("DefaultResources", 0)
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

@DataClassCollection
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
		val variantsEl = xmlData
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

	lateinit var ability: AbilityTemplate

	@Transient
	var actualAbility: Ability? = null

	fun getAsAbility(): Ability
	{
		if (actualAbility == null)
		{
			actualAbility = ability.getAbility(1)
		}
		return actualAbility!!
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		availableCondition = CompiledExpression(xmlData.get("AvailableCondition", "1")!!)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityTemplate()
		ability.load(abilityEl)
	}
	//endregion
}