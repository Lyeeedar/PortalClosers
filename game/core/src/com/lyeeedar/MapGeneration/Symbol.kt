package com.lyeeedar.MapGeneration

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.EntityData
import com.lyeeedar.Pathfinding.IPathfindingTile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass

class Symbol: XmlDataClass(), IPathfindingTile
{
	var extends: Char = ' '
	var char: Char = ' '
	var content: EntityData? = null

	//region non-data
	var placerHashCode: Int = -1
	var locked = false
	//endregion

	fun write(data: Symbol, overwrite: Boolean = false)
	{
		content = data.content ?: content
		char = data.char
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
		char = xmlData.get("Char", " ")!![0]
		val contentEl = xmlData.getChildByName("Content")
		if (contentEl != null)
		{
			content = EntityData()
			content!!.load(contentEl)
		}
	}
	//endregion
}