package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.EntityData
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*
import ktx.collections.set
import ktx.collections.toGdxArray

@DataFile(colour="250,136,136", icon="Sprites/Oryx/uf_split/uf_heroes/rat_giant_1.png")
class Faction : XmlDataClass()
{
	val enemies: Array<FactionMonster> = Array()
	val bosses: Array<FactionMonster> = Array()

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
		applyRarity(enemies)
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
		val enemiesEl = xmlData.getChildByName("Enemies")
		if (enemiesEl != null)
		{
			for (el in enemiesEl.children)
			{
				val objenemies: FactionMonster
				val objenemiesEl = el
				objenemies = FactionMonster()
				objenemies.load(objenemiesEl)
				enemies.add(objenemies)
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
	var levelRange: Point = Point(1, 999)
	lateinit var entity: EntityData

	//region generated
	override fun load(xmlData: XmlData)
	{
		rarity = Rarity.valueOf(xmlData.get("Rarity").toUpperCase(Locale.ENGLISH))
		val levelRangeRaw = xmlData.get("LevelRange", "1, 999")!!.split(',')
		levelRange = Point(levelRangeRaw[0].trim().toInt(), levelRangeRaw[1].trim().toInt())
		val entityEl = xmlData.getChildByName("Entity")!!
		entity = EntityData()
		entity.load(entityEl)
		afterLoad()
	}
	//endregion
}