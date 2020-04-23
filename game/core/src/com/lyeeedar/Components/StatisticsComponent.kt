package com.lyeeedar.Components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Renderables.Particle.ParticleEffectDescription
import com.lyeeedar.Util.*

class StatisticsComponentData : AbstractComponentData()
{
	val statistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	var faction: String = ""

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		Statistic.parse(xmlData.getChildByName("Statistics")!!, statistics)
		faction = xmlData.get("Faction", "")!!
	}
	override val classID: String = "Statistics"
	//endregion
}

inline fun Entity.stats(): StatisticsComponent? = this.components[ComponentType.Statistics] as StatisticsComponent?
class StatisticsComponent(data: StatisticsComponentData) : AbstractComponent<StatisticsComponentData>(data)
{
	override val type: ComponentType = ComponentType.Statistics

	var hp: Float = 0f
		get() = field
		private set(value)
		{
			if (value == field) return

			var v = Math.min(value, getStat(Statistic.MAXHP))

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
	var healing = 0f

	var blocking = false
	var invulnerable = false
	var godMode = false

	var tookDamage = false
	var blockedDamage = false
	var blockBroken = false

	var level: Int = 1
	var statModifier = 0f

	var lastHitSource = Point()

	var summoner: Entity? = null
	var attackDamageDealt = 0f
	var abilityDamageDealt = 0f
	val damageDealt: Float
		get() = attackDamageDealt + abilityDamageDealt

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
		messagesToShow.clear()
		totalHpLost = 0f
		healing = 0f
	}

	fun resetHP()
	{
		hp = getStat(Statistic.MAXHP)
		lostHp = 0f
		maxLostHp = 0f
		totalHpLost = 0f
		tookDamage = false
	}

	fun checkAegis(): Boolean
	{
		val aegisChance = getStat(Statistic.AEGIS)
		if (aegisChance > 0f && Random.random() < aegisChance)
		{
			return true
		}

		return false
	}

	fun dealDamage(amount: Float, wasCrit: Boolean): Float
	{
		val baseDam = amount
		val dr = getStat(Statistic.DR)

		val dam = baseDam - dr * baseDam
		hp -= dam

		if (dam > 0)
		{
			val maxHP = getStat(Statistic.MAXHP)
			val alpha = dam / maxHP
			val size = MathUtils.lerp(0.25f, 1f, MathUtils.clamp(alpha, 0f, 1f))

			var message = dam.ciel().toString()

			if (wasCrit)
			{
				message += "!!"
			}

			messagesToShow.add(MessageData.obtain().set(message, Colour.RED, size))
		}

		return dam
	}

	fun heal(amount: Float)
	{
		hp += amount

		val maxHP = getStat(Statistic.MAXHP)
		val alpha = amount / maxHP
		val size = MathUtils.lerp(0.25f, 1f, MathUtils.clamp(alpha, 0f, 1f))
		messagesToShow.add(MessageData.obtain().set(amount.ciel().toString(), Colour.GREEN, size))
	}

	fun regenerate(amount: Float)
	{
		hp += amount
	}

	val messagesToShow = Array<MessageData>(1)

	fun get(key: String): Float
	{
		val key = key.toLowerCase()
		if (key == "hp")
		{
			return hp
		}

		for (stat in Statistic.Values)
		{
			if (stat.toString().toLowerCase() == key)
			{
				return getStat(stat)
			}
		}

		return 0f
	}

	fun getStat(statistic: Statistic): Float
	{
		var value = data.statistics[statistic] ?: 0f

		// apply level / rarity, but only to the base stats
		if (Statistic.BaseValues.contains(statistic))
		{
			value = value.applyLevel(level)
			value += value * statModifier
		}
		else
		{
			value *= 1f + level.toFloat() / 100f
		}

		// apply buffs and equipment
		var modifier = 0f

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

	fun getCritMultiplier(extraCritChance: Float = 0f, extraCritDam: Float = 0f): Pair<Float, Boolean>
	{
		val critChance = getStat(Statistic.CRITCHANCE) + extraCritChance
		if (Random.random() <= critChance)
		{
			val mult = getStat(Statistic.CRITDAMAGE) + extraCritDam
			return Pair(mult, true)
		}

		return Pair(1f, false)
	}

	fun getAttackDam(multiplier: Float, extraCritChance: Float = 0f, extraCritDam: Float = 0f): Pair<Float, Boolean>
	{
		val baseAttack = getStat(Statistic.POWER)

		var modifier = Random.random()
		modifier *= modifier
		modifier *= 0.2f // 20% range
		modifier *= Random.sign()

		val attack = baseAttack + baseAttack * modifier

		val critMult = getCritMultiplier(extraCritChance, extraCritDam)

		return Pair(attack * critMult.first * multiplier, critMult.second)
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
		val prefix = if (prefixName != null) "$prefixName.".toLowerCase() else ""

		variableMap.put(prefix + "hp", hp)
		variableMap.put(prefix + "level", level.toFloat())

		for (stat in Statistic.Values)
		{
			variableMap.put(prefix + stat.toString().toLowerCase(), getStat(stat))
		}

		return variableMap
	}

	companion object
	{
		val defaultVariables: ObjectFloatMap<String> by lazy { writeDefaultVariables(ObjectFloatMap<String>()) }
		fun writeDefaultVariables(variableMap: ObjectFloatMap<String>, prefixName: String? = null): ObjectFloatMap<String>
		{
			val prefix = if (prefixName != null) "$prefixName." else ""

			variableMap.put(prefix + "hp", 0f)

			for (stat in Statistic.Values)
			{
				variableMap.put(prefix + stat.toString().toLowerCase(), 0f)
			}

			return variableMap
		}
	}
}

fun Entity.isAllies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.data.faction == ostats.data.faction
}
fun Entity.isEnemies(other: Entity): Boolean
{
	val stats = stats() ?: return false
	val ostats = other.stats() ?: return false

	return stats.data.faction != ostats.data.faction
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

		@JvmStatic fun obtain(): MessageData
		{
			val obj = pool.obtain()

			if (obj.obtained) throw RuntimeException()

			obj.obtained = true
			return obj
		}
	}
	fun free() { if (obtained) { pool.free(this); obtained = false } }
}

class AttackDefinition
{
	var damage: Float = 1f
	var range: Int = 1
	var hitEffect: ParticleEffectDescription? = null
	var flightEffect: ParticleEffectDescription? = null

	fun parse(xml: XmlData)
	{
		damage = xml.getFloat("Damage", 1f)
		range = xml.getInt("Range", 1)

		val hitEffectEl = xml.getChildByName("HitEffect")
		if (hitEffectEl != null) hitEffect = AssetManager.loadParticleEffect(hitEffectEl)

		val flightEffectEl = xml.getChildByName("FlightEffect")
		if (flightEffectEl != null) flightEffect = AssetManager.loadParticleEffect(flightEffectEl)
	}
}

fun Float.applyLevel(level: Int): Float
{
	var value = this
	value *= Math.pow(1.05, level.toDouble()).toFloat() // 5% per level

	return value
}