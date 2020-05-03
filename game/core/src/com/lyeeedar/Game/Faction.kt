package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.*
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.set
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG

@DataFile(colour="250,136,136", icon="Sprites/Oryx/uf_split/uf_heroes/rat_giant_1.png")
class Faction : XmlDataClass()
{
	val mobs: Array<FactionMonster> = Array()
	val leaders: Array<FactionMonster> = Array()
	val bosses: Array<FactionMonster> = Array()

	fun getPack(rng: LightRNG, numMobs: Int, isBoss: Boolean): PackData
	{
		val leaderArray: Array<FactionMonster>
		val mobArray: Array<FactionMonster>
		if (isBoss)
		{
			leaderArray = bosses
			mobArray = leaders
		}
		else
		{
			leaderArray = leaders
			mobArray = mobs
		}

		val pack = PackData()
		pack.leader = leaderArray.random(rng).entity

		for (i in 0 until numMobs)
		{
			pack.mobs.add(mobArray.random(rng).entity)
		}

		return pack
	}

	fun applyRarity(array: Array<FactionMonster>)
	{
		val original = array.toGdxArray()
		array.clear()

		for (item in original)
		{
			val count = when (item.rarity)
			{
				Rarity.COMMON -> 15
				Rarity.UNCOMMON -> 11
				Rarity.RARE -> 6
				Rarity.SUPERRARE -> 3
				Rarity.LEGENDARY -> 1
			}

			for (i in 0 until count)
			{
				array.add(item)
			}
		}
	}

	override fun afterLoad()
	{
		applyRarity(mobs)
		applyRarity(leaders)
		applyRarity(bosses)
	}

	companion object
	{
		private val loaded = ObjectMap<String, Faction>()
		fun load(path: String): Faction
		{
			val existing = loaded[path]
			if (existing != null) return existing

			val xml = getXml(path)
			val faction = Faction()
			faction.load(xml)

			loaded[path] = faction

			return faction
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		val mobsEl = xmlData.getChildByName("Mobs")
		if (mobsEl != null)
		{
			for (el in mobsEl.children)
			{
				val objmobs: FactionMonster
				val objmobsEl = el
				objmobs = FactionMonster()
				objmobs.load(objmobsEl)
				mobs.add(objmobs)
			}
		}
		val leadersEl = xmlData.getChildByName("Leaders")
		if (leadersEl != null)
		{
			for (el in leadersEl.children)
			{
				val objleaders: FactionMonster
				val objleadersEl = el
				objleaders = FactionMonster()
				objleaders.load(objleadersEl)
				leaders.add(objleaders)
			}
		}
		val bossesEl = xmlData.getChildByName("Bosses")
		if (bossesEl != null)
		{
			for (el in bossesEl.children)
			{
				val objbosses: FactionMonster
				val objbossesEl = el
				objbosses = FactionMonster()
				objbosses.load(objbossesEl)
				bosses.add(objbosses)
			}
		}
		afterLoad()
	}
	//endregion
}

class FactionMonster : XmlDataClass()
{
	lateinit var rarity: Rarity
	lateinit var entity: EntityData

	//region generated
	override fun load(xmlData: XmlData)
	{
		rarity = Rarity.valueOf(xmlData.get("Rarity").toUpperCase(Locale.ENGLISH))
		val entityEl = xmlData.getChildByName("Entity")!!
		entity = EntityData()
		entity.load(entityEl)
	}
	//endregion
}
fun EntityData.create(difficulty: Int, world: World<*>, seed: Long): Entity
{
	val entity = create()
	entity.statistics()?.calculateStatistics(difficulty)
	entity.ai()?.state?.set(entity.getRef(), world, seed)

	return entity
}

class Pack
{
	lateinit var leader: EntityReference
	val mobs = Array<EntityReference>()

	val entities: Sequence<EntityReference>
		get() = sequence {
			if (leader.isValid()) yield(leader)
			for (mob in mobs)
			{
				if (mob.isValid()) yield(mob)
			}
		}
}

class PackData
{
	lateinit var leader: EntityData
	val mobs = Array<EntityData>()

	fun create(difficulty: Int, world: World<*>, seed: Long): Pack
	{
		val seedRng = LightRNG(seed)

		val pack = Pack()
		pack.leader = leader.create(difficulty, world, seedRng.nextLong()).getRef()
		pack.leader.entity.setPack(pack)

		for (mobData in mobs)
		{
			val mob = mobData.create(difficulty, world, seedRng.nextLong()).getRef()
			mob.entity.setPack(pack)
			pack.mobs.add(mob)

			mob.entity.ai()!!.state.setData("leader", 0, pack.leader)
		}

		return pack
	}
}