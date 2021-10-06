package com.lyeeedar.Game.Combo

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.ActionSequence.Actions.*
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Util.*
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

	@Transient
	var actual: Ability? = null

	fun getAsAbility(): Ability
	{
		if (actual == null)
		{
			val data = getAbilityData()
			data.cooldown = cooldown
			actual = Ability(data)
		}

		return actual!!
	}

	abstract fun getAbilityData(): AbilityData

	//region generated
	override fun load(xmlData: XmlData)
	{
		cooldown = xmlData.getInt("Cooldown", 0)
		chance = CompiledExpression(xmlData.get("Chance", "1")!!)
		canStop = xmlData.getBoolean("CanStop", false)
		stepForward = xmlData.getBoolean("StepForward", false)
		canTurn = xmlData.getBoolean("CanTurn", false)
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

	override fun getAbilityData(): AbilityData = ability

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityData()
		ability.load(abilityEl)
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

	override fun getAbilityData(): AbilityData
	{
		val data = AbilityData()
		data.targetType = AbilityData.TargetType.TARGET_ENEMY
		data.actionSequence = ActionSequence(XmlData())

		effect.time = 0.01f
		damage.time = 0.01f + effect.particle.getParticleEffect().blockinglifetime * 0.7f

		val bump = AnimationAction()
		bump.anim = AnimationAction.Animation.BUMP
		bump.time = 0.01f
		bump.duration = 0.1f

		data.actionSequence.rawActions.add(MarkAndWaitForPlayerAction())
		data.actionSequence.rawActions.add(effect)
		data.actionSequence.rawActions.add(bump)
		data.actionSequence.rawActions.add(damage)
		data.actionSequence.afterLoad()

		return data
	}

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

	override fun getAbilityData(): AbilityData
	{
		val data = AbilityData()
		data.targetType = AbilityData.TargetType.SELF
		data.actionSequence = ActionSequence(XmlData())

		for (i in 1 until turns)
		{
			data.actionSequence.rawActions.add(BlockTurnAction())
		}
		data.actionSequence.afterLoad()

		return data
	}

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