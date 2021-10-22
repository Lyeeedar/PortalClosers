package com.lyeeedar.Game.Combo

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.ActionSequence.Actions.*
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Combo.ComboStep
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader
import ktx.collections.set

@DataFile(colour="121,252,121", icon="Sprites/Icons/CardIcon.png")
class Combo : GraphXmlDataClass<ComboStep>()
{
	@DataGraphNodes
	val nodeMap: ObjectMap<String, ComboStep> = ObjectMap()

	val roots: Array<ComboStepConnection> = Array()

	val actions: Array<AbstractComboAction> = Array()

	override fun afterLoad()
	{
		val actionMap = ObjectMap<String, AbstractComboAction>()
		for (action in actions)
		{
			actionMap[action.name] = action
		}

		for (node in nodeMap.values())
		{
			node.action = actionMap[node.actionName]
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		val nodeMapEl = xmlData.getChildByName("NodeMap")
		if (nodeMapEl != null)
		{
			for (el in nodeMapEl.children)
			{
				val obj = ComboStep()
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
				val objroots: ComboStepConnection
				val objrootsEl = el
				objroots = ComboStepConnection()
				objroots.load(objrootsEl)
				roots.add(objroots)
			}
		}
		val actionsEl = xmlData.getChildByName("Actions")
		if (actionsEl != null)
		{
			for (el in actionsEl.children)
			{
				val objactions: AbstractComboAction
				val objactionsEl = el
				objactions = XmlDataClassLoader.loadAbstractComboAction(objactionsEl.get("classID", objactionsEl.name)!!)
				objactions.load(objactionsEl)
				actions.add(objactions)
			}
		}
		resolve(nodeMap)
		afterLoad()
	}
	override fun resolve(nodes: ObjectMap<String, ComboStep>)
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

class ComboStepConnection : GraphXmlDataClass<ComboStep>()
{
	@DataGraphReference
	var next: ComboStep? = null

	//region generated
	override fun load(xmlData: XmlData)
	{
		nextGUID = xmlData.get("Next", null)
	}
	private var nextGUID: String? = null
	override fun resolve(nodes: ObjectMap<String, ComboStep>)
	{
		if (!nextGUID.isNullOrBlank()) next = nodes[nextGUID]!!
	}
	//endregion
}

@DataGraphNode
class ComboStep : GraphXmlDataClass<ComboStep>()
{
	val next: Array<ComboStepConnection> = Array()

	lateinit var actionName: String
	var cooldown: Int = 0

	@DataCompiledExpression(default = "1")
	lateinit var chance: CompiledExpression

	var canStop: Boolean = false
	var stepForward: Boolean = false
	var canTurn: Boolean = false

	@Transient
	lateinit var action: AbstractComboAction

	@Transient
	var actual: Ability? = null

	fun getAsAbility(): Ability
	{
		if (actual == null)
		{
			val data = action.getAbilityData()
			data.cooldown = cooldown
			actual = Ability(data)
		}

		return actual!!
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		val nextEl = xmlData.getChildByName("Next")
		if (nextEl != null)
		{
			for (el in nextEl.children)
			{
				val objnext: ComboStepConnection
				val objnextEl = el
				objnext = ComboStepConnection()
				objnext.load(objnextEl)
				next.add(objnext)
			}
		}
		actionName = xmlData.get("ActionName")
		cooldown = xmlData.getInt("Cooldown", 0)
		chance = CompiledExpression(xmlData.get("Chance", "1")!!)
		canStop = xmlData.getBoolean("CanStop", false)
		stepForward = xmlData.getBoolean("StepForward", false)
		canTurn = xmlData.getBoolean("CanTurn", false)
	}
	override fun resolve(nodes: ObjectMap<String, ComboStep>)
	{
		for (item in next)
		{
			item.resolve(nodes)
		}
	}
	//endregion
}

abstract class AbstractComboAction : XmlDataClass()
{
	lateinit var name: String

	abstract fun getAbilityData(): AbilityData

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
	}
	abstract val classID: String
	//endregion
}

class AbilityComboAction : AbstractComboAction()
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
	//endregion
}

class MeleeAttackComboAction : AbstractComboAction()
{
	lateinit var strike: StrikeTileAction
	var effect: SpawnOneShotParticleAction? = null
	lateinit var damage: DamageAction
	var permute: PermuteAction? = null
	var attackTurns: Int = 1

	override fun getAbilityData(): AbilityData
	{
		val data = AbilityData()
		data.targetType = AbilityData.TargetType.TARGET_ENEMY
		data.actionSequence = ActionSequence(XmlData())

		val bump = AnimationAction()
		bump.anim = AnimationAction.Animation.BUMP
		bump.time = 0.01f
		bump.duration = 0.1f

		val mark = MarkAndWaitForPlayerAction()
		mark.time = 0.005f
		mark.turns = attackTurns

		if (permute != null)
		{
			permute!!.time = 0f
			data.actionSequence.rawActions.add(permute)
		}

		strike.time = 0.02f
		damage.time = 0.04f

		val individually = ProcessTargetsIndividuallyAction()
		individually.time = 0.03f
		individually.duration = strike.duration + 0.1f

		data.actionSequence.rawActions.add(mark)
		data.actionSequence.rawActions.add(bump)
		data.actionSequence.rawActions.add(strike)
		data.actionSequence.rawActions.add(individually)
		if (effect != null)
		{
			effect!!.time = 0.04f
			data.actionSequence.rawActions.add(effect)
		}
		data.actionSequence.rawActions.add(damage)
		data.actionSequence.afterLoad()

		return data
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val strikeEl = xmlData.getChildByName("Strike")!!
		strike = StrikeTileAction()
		strike.load(strikeEl)
		val effectEl = xmlData.getChildByName("Effect")
		if (effectEl != null)
		{
			effect = SpawnOneShotParticleAction()
			effect!!.load(effectEl)
		}
		val damageEl = xmlData.getChildByName("Damage")!!
		damage = DamageAction()
		damage.load(damageEl)
		val permuteEl = xmlData.getChildByName("Permute")
		if (permuteEl != null)
		{
			permute = PermuteAction()
			permute!!.load(permuteEl)
		}
		attackTurns = xmlData.getInt("AttackTurns", 1)
	}
	override val classID: String = "MeleeAttack"
	//endregion
}

class WaitComboAction : AbstractComboAction()
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
	//endregion
}