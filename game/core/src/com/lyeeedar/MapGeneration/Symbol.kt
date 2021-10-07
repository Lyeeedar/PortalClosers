package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.EntityData
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import java.util.*

class Symbol: XmlDataClass(), IMapGeneratorSymbol
{
	var extends: Char = ' '
	override var char: Char = '.'

	@DataFileReference(resourceType = "SpriteWrapper")
	var floor: String? = null
	@DataFileReference(resourceType = "SpriteWrapper")
	var wall: String? = null
	var contents: FastEnumMap<SpaceSlot, EntityData> = FastEnumMap(SpaceSlot::class.java)
	var enemyDescription: EnemyDescription? = null
	var packDescription: PackDescription? = null

	//region non-data
	var placerHashCode: Int = -1
	var locked = false
	//endregion

	override fun evaluateExtends(symbolTable: ObjectMap<Char, IMapGeneratorSymbol>)
	{
		if (extends != ' ')
		{
			val sym = symbolTable[extends] as? Symbol ?: return

			floor = floor ?: sym.floor
			wall = wall ?: sym.wall
			enemyDescription = enemyDescription ?: sym.enemyDescription
			packDescription = packDescription ?: sym.packDescription

			for (slot in SpaceSlot.Values)
			{
				contents[slot] = contents[slot] ?: sym.contents[slot]
			}
		}
	}

	override fun write(other: IMapGeneratorSymbol, overwrite: Boolean)
	{
		val data = other as Symbol

		if (overwrite)
		{
			contents.clear()
			contents.addAll(data.contents)

			floor = data.floor
			wall = data.wall
			enemyDescription = data.enemyDescription
			packDescription = data.packDescription
		}
		else
		{
			for (slot in SpaceSlot.Values)
			{
				contents[slot] = data.contents[slot] ?: contents[slot]
			}

			floor = data.floor ?: floor
			wall = data.wall ?: wall
			enemyDescription = data.enemyDescription ?: enemyDescription
			packDescription = data.packDescription ?: packDescription
		}

		char = data.char
	}

	override fun clear()
	{
		wall = null
		enemyDescription = null
		contents.clear()
	}

	override fun isEmpty(): Boolean
	{
		return contents.size == 0 && wall == null && enemyDescription == null
	}

	override fun copy(): IMapGeneratorSymbol
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

	fun toStringFull(): String
	{
		val builder = StringBuilder()
		builder.append(char)
		builder.append(if (floor != null) "1" else "0")
		builder.append(if (wall != null) "1" else "0")
		for (slot in SpaceSlot.Values)
		{
			builder.append(if (contents[slot] != null) "1" else "0")
		}
		builder.append(if (enemyDescription != null) "1" else "0")
		builder.append(if (packDescription != null) "1" else "0")

		return builder.toString()
	}

	override fun toString(): String
	{
		return char.toString()
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		extends = xmlData.get("Extends", " ")!![0]
		char = xmlData.get("Char", ".")!![0]
		floor = xmlData.get("Floor", null)
		wall = xmlData.get("Wall", null)
		val contentsEl = xmlData.getChildByName("Contents")
		if (contentsEl != null)
		{
			for (el in contentsEl.children)
			{
				val enumVal = SpaceSlot.valueOf(el.name.toUpperCase(Locale.ENGLISH))
				val objcontents: EntityData
				val objcontentsEl = el
				objcontents = EntityData()
				objcontents.load(objcontentsEl)
				contents[enumVal] = objcontents
			}
		}
		val enemyDescriptionEl = xmlData.getChildByName("EnemyDescription")
		if (enemyDescriptionEl != null)
		{
			enemyDescription = EnemyDescription()
			enemyDescription!!.load(enemyDescriptionEl)
		}
		val packDescriptionEl = xmlData.getChildByName("PackDescription")
		if (packDescriptionEl != null)
		{
			packDescription = PackDescription()
			packDescription!!.load(packDescriptionEl)
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

class PackDescription : XmlDataClass()
{
	var faction: String? = null
	var difficulty: Int = 1
	var isBoss: Boolean = false

	@DataVector(name1 = "min", name2 = "max")
	var size: Point = Point(3,5)

	//region generated
	override fun load(xmlData: XmlData)
	{
		faction = xmlData.get("Faction", null)
		difficulty = xmlData.getInt("Difficulty", 1)
		isBoss = xmlData.getBoolean("IsBoss", false)
		val sizeRaw = xmlData.get("Size", "3,5")!!.split(',')
		size = Point(sizeRaw[0].trim().toInt(), sizeRaw[1].trim().toInt())
	}
	//endregion
}