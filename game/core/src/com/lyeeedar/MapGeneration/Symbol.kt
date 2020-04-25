package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityData
import com.lyeeedar.Pathfinding.IPathfindingTile
import com.lyeeedar.Renderables.Sprite.SpriteWrapper
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass
import java.util.*

class Symbol: XmlDataClass(), IPathfindingTile
{
	var extends: Char = ' '
	var char: Char = '.'

	var floor: SpriteWrapper? = null
	var wall: SpriteWrapper? = null
	var content: FastEnumMap<SpaceSlot, EntityData> = FastEnumMap(SpaceSlot::class.java)
	var enemyDescription: EnemyDescription? = null

	//region non-data
	var placerHashCode: Int = -1
	var locked = false
	//endregion

	fun evaluateExtends(symbolTable: ObjectMap<Char, Symbol>)
	{
		if (extends != ' ')
		{
			val sym = symbolTable[extends] ?: return

			floor = floor ?: sym.floor
			wall = wall ?: sym.wall
			enemyDescription = enemyDescription ?: sym.enemyDescription

			for (slot in SpaceSlot.Values)
			{
				content[slot] = content[slot] ?: sym.content[slot]
			}
		}
	}

	fun write(data: Symbol, overwrite: Boolean = false)
	{
		if (overwrite)
		{
			content.clear()
			content.addAll(data.content)

			floor = data.floor
			wall = data.wall
			enemyDescription = data.enemyDescription
		}
		else
		{
			for (slot in SpaceSlot.Values)
			{
				content[slot] = data.content[slot] ?: content[slot]
			}

			floor = data.floor ?: floor
			wall = data.wall ?: wall
			enemyDescription = data.enemyDescription ?: enemyDescription
		}

		char = data.char
	}

	fun clear()
	{
		wall = null
		enemyDescription = null
		content.clear()
	}

	fun copy(): Symbol
	{
		val symbol = Symbol()
		symbol.char = char
		symbol.write(this)
		return symbol
	}

	override fun getPassable(travelType: SpaceSlot, self: Any?): Boolean
	{
		return true
	}

	override fun getInfluence(travelType: SpaceSlot, self: Any?): Int
	{
		return 0
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		extends = xmlData.get("Extends", " ")!![0]
		char = xmlData.get("Char", ".")!![0]
		floor = AssetManager.tryLoadSpriteWrapper(xmlData.getChildByName("Floor"))
		wall = AssetManager.tryLoadSpriteWrapper(xmlData.getChildByName("Wall"))
		val contentEl = xmlData.getChildByName("Content")
		if (contentEl != null)
		{
			for (el in contentEl.children)
			{
				val enumVal = SpaceSlot.valueOf(el.name.toUpperCase(Locale.ENGLISH))
				val objcontent: EntityData
				val objcontentEl = xmlData.getChildByName("Content")!!
				objcontent = EntityData()
				objcontent.load(objcontentEl)
				content[enumVal] = objcontent
			}
		}
		val enemyDescriptionEl = xmlData.getChildByName("EnemyDescription")
		if (enemyDescriptionEl != null)
		{
			enemyDescription = EnemyDescription()
			enemyDescription!!.load(enemyDescriptionEl)
		}
	}
	//endregion
}

class EnemyDescription : XmlDataClass()
{
	var faction: String? = null
	var difficulty: Int = 1
	var isBoss: Boolean = false

	//region generated
	override fun load(xmlData: XmlData)
	{
		faction = xmlData.get("Faction", null)
		difficulty = xmlData.getInt("Difficulty", 1)
		isBoss = xmlData.getBoolean("IsBoss", false)
	}
	//endregion
}