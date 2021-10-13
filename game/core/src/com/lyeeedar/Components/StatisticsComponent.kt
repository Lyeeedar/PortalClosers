package com.lyeeedar.Components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Game.*
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*
import kotlin.math.sqrt

class StatisticsComponentData : AbstractComponentData()
{
	val baseStatistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	var faction: String = ""
	var difficultyRating: DifficultyRating? = null
	var bloodColour: Colour = Colour.RED
	var element: Elements = Elements.NONE

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val baseStatisticsEl = xmlData.getChildByName("BaseStatistics")
		if (baseStatisticsEl != null)
		{
			for (el in baseStatisticsEl.children)
			{
				val enumVal = Statistic.valueOf(el.name.toUpperCase(Locale.ENGLISH))
				baseStatistics[enumVal] = el.float()
			}
		}
		faction = xmlData.get("Faction", "")!!
		val difficultyRatingEl = xmlData.getChildByName("DifficultyRating")
		if (difficultyRatingEl != null)
		{
			difficultyRating = DifficultyRating()
			difficultyRating!!.load(difficultyRatingEl)
		}
		bloodColour = AssetManager.tryLoadColour(xmlData.getChildByName("BloodColour"))!!
		element = Elements.valueOf(xmlData.get("Element", Elements.NONE.toString())!!.toUpperCase(Locale.ENGLISH))
	}
	override val classID: String = "Statistics"
	//endregion
}

class StatisticsComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Statistics

	val baseStatistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	val statistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)

	var elementalType: Elements = Elements.NONE

	var difficultyRating: DifficultyRating? = null

	var faction: String = ""

	//region hp and damage
	var hp: Float = 0f
		get() = field
		private set(value)
		{
			if (value == field) return

			var v = Math.min(value, getStat(Statistic.MAX_HP))

			var diff = v - hp
			if (diff < 0)
			{
				if (invulnerable)
				{
					blockedDamage = true
					return
				}

				totalHpLost -= diff
			}

			v = hp + diff

			if (v < field)
			{
				tookDamage = true
			}

			lostHp -= diff
			if (lostHp < 0)
			{
				lostHp = 0f
			}
			maxLostHp = max(maxLostHp, lostHp)

			field = v
			if (godMode && field < 1) field = 1f

			if (hp <= 0)
			{
				lostHp = 0f
				maxLostHp = 0f
			}
		}
	var lostHp = 0f
	var maxLostHp = 0f
	var totalHpLost = 0f

	var blocking = false
	var invulnerable = false
	var godMode = false

	var tookDamage = false
	var blockedDamage = false
	var blockBroken = false

	var bloodColour: Colour = Colour.RED
	//endregion

	//region level buffs and equipment
	var level: Int = 1
	var statModifier = 0f
	val buffs = Array<Buff>(false, 4)
	//endregion

	//region state tracking
	var lastHitSource = Point()
	var summoner: Entity? = null
	var attackDamageDealt = 0f
	var abilityDamageDealt = 0f
	val damageDealt: Float
		get() = attackDamageDealt + abilityDamageDealt
	var healingDone: Float = 0f
	//endregion

	override fun reset()
	{
		lostHp = 0f
		maxLostHp = 0f
		blocking = false
		blockBroken = false
		invulnerable = false
		godMode = false
		tookDamage = false
		blockedDamage = false
		level = 1
		statModifier = 0f
		lastHitSource = Point()
		attackDamageDealt = 0f
		abilityDamageDealt = 0f
		healingDone = 0f
		messagesToShow.clear()
		totalHpLost = 0f
		buffs.clear()
		elementalType = Elements.NONE
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as StatisticsComponentData

		baseStatistics.addAll(data.baseStatistics)
		faction = data.faction
		difficultyRating = data.difficultyRating
		bloodColour = data.bloodColour
		elementalType = data.element
	}

	fun calculateStatistics(level: Int)
	{
		this.level = level
		for (stat in Statistic.Values)
		{
			statistics[stat] = baseStatistics[stat, 0f]
		}

		val rating = difficultyRating
		if (rating != null)
		{
			statistics[Statistic.POWER] = statistics[Statistic.POWER, 0f] + rating.calculateAttack(level)
			statistics[Statistic.ARMOUR] = statistics[Statistic.ARMOUR, 0f] + rating.calculateArmour(level)
			statistics[Statistic.MAX_HP] = statistics[Statistic.MAX_HP, 0f] + rating.calculateHP(level, statistics)
		}

		resetHP()
	}

	private fun resetHP()
	{
		hp = getStat(Statistic.MAX_HP)
		lostHp = 0f
		maxLostHp = 0f
		totalHpLost = 0f
		tookDamage = false
	}

	fun damage(damage: Float)
	{
		damage(AttackDamage(damage, elementalType))
	}

	fun damage(damage: AttackDamage)
	{
		if (damage.damage == 0f) return

		hp -= damage.damage

		val maxHP = getStat(Statistic.MAX_HP)
		val alpha = damage.damage / maxHP
		val size = MathUtils.lerp(0.25f, 1f, MathUtils.clamp(alpha, 0f, 1f))

		var message = ""
		message += damage.damage.ciel().toString()

		addMessage(message, Colour.RED, size)
	}

	fun heal(amount: Float)
	{
		if (amount == 0f) return

		hp += amount

		val maxHP = getStat(Statistic.MAX_HP)
		val alpha = amount / maxHP
		val size = MathUtils.lerp(0.25f, 1f, MathUtils.clamp(alpha, 0f, 1f))
		addMessage(amount.ciel().toString(), Colour.GREEN, size)
	}

	fun regenerate(amount: Float)
	{
		hp += amount
	}

	val messagesToShow = Array<MessageData>(1)

	fun addMessage(message: String, colour: Colour, size: Float)
	{
		messagesToShow.add(MessageData.obtain().set(message, colour, size))
	}

	fun get(key: String): Float
	{
		val key = key.toLowerCase(Locale.ENGLISH)
		if (key == "hp")
		{
			return hp
		}
		else if (key == "damage")
		{
			return getStat(Statistic.POWER)
		}

		for (stat in Statistic.Values)
		{
			if (stat.toString().toLowerCase(Locale.ENGLISH) == key)
			{
				return getStat(stat)
			}
		}

		return 0f
	}

	fun getStat(statistic: Statistic): Float
	{
		var value = statistics[statistic] ?: 0f

		// apply stat modifier, but only to the base stats
		if (Statistic.BaseValues.contains(statistic))
		{
			value += value * statModifier
		}

		// apply buffs and equipment
		var modifier = 0f
		for (i in 0 until buffs.size)
		{
			val buff = buffs[i]
			modifier += buff.statistics[statistic] ?: 0f
		}

		if (statistic.modifiersAreAdded)
		{
			value += modifier
		}
		else
		{
			value += modifier * value
		}

		return MathUtils.clamp(value, statistic.min, statistic.max)
	}

	val map = ObjectFloatMap<String>()
	fun variables(): ObjectFloatMap<String>
	{
		map.clear()
		write(map)
		return map
	}

	fun write(variableMap: ObjectFloatMap<String>, prefixName: String? = null): ObjectFloatMap<String>
	{
		val prefix = if (prefixName != null) "$prefixName.".toLowerCase(Locale.ENGLISH) else ""

		variableMap.put(prefix + "hp", hp)
		variableMap.put(prefix + "level", level.toFloat())

		val attack = getStat(Statistic.POWER)
		variableMap.put(prefix + "damage", attack)

		for (stat in Statistic.Values)
		{
			val statVal = getStat(stat)
			if (statVal != 0f)
			{
				variableMap.put(prefix + stat.toString().toLowerCase(Locale.ENGLISH), statVal)
			}
		}

		return variableMap
	}

	override fun toShortString(): String
	{
		val stats = StringBuilder()
		for (stat in Statistic.Values)
		{
			val statVal = getStat(stat)
			if (statVal != 0f)
			{
				stats.append(",").append(stat.toString().toLowerCase(Locale.ENGLISH), statVal)
			}
		}

		val buffs = StringBuilder()
		for (buff in this.buffs)
		{
			buffs.append(",").append(buff.name)
		}

		return super.toShortString() + "($faction,hp:$hp$stats$buffs)"
	}

	companion object
	{
		val defaultVariables: ObjectFloatMap<String> by lazy { writeDefaultVariables(ObjectFloatMap<String>()) }
		private fun writeDefaultVariables(variableMap: ObjectFloatMap<String>, prefixName: String? = null): ObjectFloatMap<String>
		{
			val prefix = if (prefixName != null) "$prefixName." else ""

			variableMap.put(prefix + "hp", 0f)
			variableMap.put(prefix + "damage", 0f)

			for (stat in Statistic.Values)
			{
				variableMap.put(prefix + stat.toString().toLowerCase(Locale.ENGLISH), 0f)
			}

			return variableMap
		}
	}
}

fun Entity.isAllies(other: Entity): Boolean
{
	val stats = this.statistics() ?: return false
	val ostats = other.statistics() ?: return false

	return stats.faction == ostats.faction
}
fun Entity.isEnemies(other: Entity): Boolean
{
	val stats = this.statistics() ?: return false
	val ostats = other.statistics() ?: return false

	return stats.faction != ostats.faction
}

class MessageData()
{
	lateinit var text: String
	lateinit var colour: Colour
	var size: Float = 0f

	fun set(text: String, colour: Colour, size: Float): MessageData
	{
		this.text = text
		this.colour = colour
		this.size = size

		return this
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<MessageData> = object : Pool<MessageData>() {
			override fun newObject(): MessageData
			{
				return MessageData()
			}
		}

		fun obtain(): MessageData
		{
			val obj = pool.obtain()

			if (obj.obtained) throw RuntimeException()

			obj.obtained = true
			return obj
		}
	}
	fun free() { if (obtained) { pool.free(this); obtained = false } }
}

class DifficultyRating : XmlDataClass()
{
	// Equations:
	// raw = 100 + (20 * level)
	// armour = factor * raw
	// hp = (dr * raw^2 * ttk) / (armour + raw)
	// dpt=damage/ttk, atk = 0.5*(sqrt(dpt) * sqrt(4*rawarmour-dpt) + dpt)

	var timeToKill: Int = 8
	var armourFactor: Float = 0.7f
	var damage: Float = 0.1f

	private fun calculateRaw(level: Int): Float
	{
		return 100f + 20f * level
	}

	fun calculateArmour(level: Int): Float
	{
		val raw = calculateRaw(level)
		return armourFactor * raw
	}

	private fun calculatePlayerHP(level: Int): Float
	{
		return 1000f + level * 100f
	}

	fun calculateHP(level: Int, statistics: FastEnumMap<Statistic, Float>): Float
	{
		val raw = calculateRaw(level)
		val dr = 1f - (statistics[Statistic.DR] ?: 0f)
		val armour = calculateArmour(level)

		return (dr * raw * raw * timeToKill) / (armour + raw)
	}

	fun calculateAttack(level: Int): Float
	{
		val raw = calculateRaw(level)
		val damage = this.damage * calculatePlayerHP(level)

		val dpt = damage / timeToKill

		return 0.5f * (sqrt(dpt) * (sqrt(4*raw - dpt)) + dpt)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		timeToKill = xmlData.getInt("TimeToKill", 8)
		armourFactor = xmlData.getFloat("ArmourFactor", 0.7f)
		damage = xmlData.getFloat("Damage", 0.1f)
	}
	//endregion
}