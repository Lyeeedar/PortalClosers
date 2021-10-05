package com.lyeeedar.Game.Combo

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.ActionSequence.Actions.DamageAction
import com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Combo.AbstractComboStep
import com.lyeeedar.Renderables.Particle.ParticleEffect
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader
import ktx.collections.set

@DataFile(colour="121,252,121", icon="Sprites/Icons/CardIcon.png")
class Combo : GraphXmlDataClass<AbstractComboStep>()
{
	@DataGraphNodes
	val nodeMap: ObjectMap<String, AbstractComboStep> = ObjectMap<String, AbstractComboStep>()

	val roots: Array<ComboStep> = Array<ComboStep>()

	//region generated
	override fun load(xmlData: XmlData)
	{
		val nodeMapEl = xmlData.getChildByName("NodeMap")
		if (nodeMapEl != null)
		{
			for (el in nodeMapEl.children)
			{
				val obj = XmlDataClassLoader.loadAbstractComboStep(el.get("classID", el.name)!!)
				obj.load(el)
				val guid = el.getAttribute("GUID")
				nodeMap[guid] = obj
			}
		}
		val rootsEl = xmlData.getChildByName("Roots")
		if (rootsEl != null)
		{
			for (el in rootsEl.children)
			{
				val objroots: ComboStep
				val objrootsEl = el
				objroots = ComboStep()
				objroots.load(objrootsEl)
				roots.add(objroots)
			}
		}
		resolve(nodeMap)
	}
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		for (item in nodeMap.values())
		{
			item.resolve(nodes)
		}
		for (item in roots)
		{
			item.resolve(nodes)
		}
	}
	//endregion
}

class ComboStep : GraphXmlDataClass<AbstractComboStep>()
{
	@DataGraphReference
	var next: AbstractComboStep? = null

	//region generated
	override fun load(xmlData: XmlData)
	{
		nextGUID = xmlData.get("Next", null)
	}
	private var nextGUID: String? = null
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		if (!nextGUID.isNullOrBlank()) next = nodes[nextGUID]!!
	}
	//endregion
}

@DataGraphNode
abstract class AbstractComboStep : GraphXmlDataClass<AbstractComboStep>()
{
	var cooldown: Int = 0

	@DataCompiledExpression(default = "1")
	lateinit var chance: CompiledExpression

	var canStop: Boolean = false
	var stepForward: Boolean = false
	var canTurn: Boolean = false

	val next: Array<ComboStep> = Array<ComboStep>()

	abstract fun getAsAbility(): Ability

	//region generated
	override fun load(xmlData: XmlData)
	{
		cooldown = xmlData.getInt("Cooldown", 0)
		chance = CompiledExpression(xmlData.get("Chance", "1")!!)
		val nextEl = xmlData.getChildByName("Next")
		if (nextEl != null)
		{
			for (el in nextEl.children)
			{
				val objnext: ComboStep
				val objnextEl = el
				objnext = ComboStep()
				objnext.load(objnextEl)
				next.add(objnext)
			}
		}
	}
	abstract val classID: String
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		for (item in next)
		{
			item.resolve(nodes)
		}
	}
	//endregion
}

class AbilityComboStep : AbstractComboStep()
{
	lateinit var ability: AbilityData

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityData()
		ability.load(abilityEl)
		stepForward = xmlData.getBoolean("StepForward", false)
		canTurn = xmlData.getBoolean("CanTurn", false)
	}
	override val classID: String = "Ability"
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		super.resolve(nodes)
	}
	//endregion
}

class MeleeAttackComboStep : AbstractComboStep()
{
	lateinit var effect: SpawnOneShotParticleAction
	lateinit var damage: DamageAction

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val effectEl = xmlData.getChildByName("Effect")!!
		effect = SpawnOneShotParticleAction()
		effect.load(effectEl)
		val damageEl = xmlData.getChildByName("Damage")!!
		damage = DamageAction()
		damage.load(damageEl)
		stepForward = xmlData.getBoolean("StepForward", false)
		canTurn = xmlData.getBoolean("CanTurn", false)
	}
	override val classID: String = "MeleeAttack"
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		super.resolve(nodes)
	}
	//endregion
}

class WaitComboStep : AbstractComboStep()
{
	var turns: Int = 1

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		turns = xmlData.getInt("Turns", 1)
	}
	override val classID: String = "Wait"
	override fun resolve(nodes: ObjectMap<String, AbstractComboStep>)
	{
		super.resolve(nodes)
	}
	//endregion
}