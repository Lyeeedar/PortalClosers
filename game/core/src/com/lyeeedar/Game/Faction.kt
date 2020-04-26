package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.EntityData
import com.lyeeedar.Util.DataFile
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass
import com.lyeeedar.Util.getXml
import ktx.collections.set

@DataFile(colour="250,136,136", icon="Sprites/Oryx/uf_split/uf_heroes/rat_giant_1.png")
class Faction : XmlDataClass()
{
	val enemies: Array<EntityData> = Array()
	val bosses: Array<EntityData> = Array()

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
				val objenemies: EntityData
				val objenemiesEl = el
				objenemies = EntityData()
				objenemies.load(objenemiesEl)
				enemies.add(objenemies)
			}
		}
		val bossesEl = xmlData.getChildByName("Bosses")
		if (bossesEl != null)
		{
			for (el in bossesEl.children)
			{
				val objbosses: EntityData
				val objbossesEl = el
				objbosses = EntityData()
				objbosses.load(objbossesEl)
				bosses.add(objbosses)
			}
		}
	}
	//endregion
}