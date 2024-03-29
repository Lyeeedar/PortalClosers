package com.lyeeedar.Game.Portal

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Game.Elements
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*

@DataFile(colour = "200,250,150", icon="Sprites/Oryx/uf_split/uf_terrain/wall_hedge_7.png")
class Biome : XmlDataClass()
{
	lateinit var element: Elements

	val normalPacks: Array<Pack> = Array()

	@DataFileReference(resourceType = "MapGenerator")
	lateinit var roomGenerator: String

	var ambientLight: Colour = Colour(0.6f, 0.6f, 0.6f, 1f)

	//region generated
	override fun load(xmlData: XmlData)
	{
		element = Elements.valueOf(xmlData.get("Element").uppercase(Locale.ENGLISH))
		val normalPacksEl = xmlData.getChildByName("NormalPacks")
		if (normalPacksEl != null)
		{
			for (el in normalPacksEl.children)
			{
				val objnormalPacks: Pack
				val objnormalPacksEl = el
				objnormalPacks = Pack()
				objnormalPacks.load(objnormalPacksEl)
				normalPacks.add(objnormalPacks)
			}
		}
		roomGenerator = xmlData.get("RoomGenerator")
		ambientLight = AssetManager.tryLoadColour(xmlData.getChildByName("AmbientLight"))!!
	}
	//endregion
}

class Pack : XmlDataClass()
{
	val creatures: Array<PackCreature> = Array()

	//region generated
	override fun load(xmlData: XmlData)
	{
		val creaturesEl = xmlData.getChildByName("Creatures")
		if (creaturesEl != null)
		{
			for (el in creaturesEl.children)
			{
				val objcreatures: PackCreature
				val objcreaturesEl = el
				objcreatures = PackCreature()
				objcreatures.load(objcreaturesEl)
				creatures.add(objcreatures)
			}
		}
	}
	//endregion
}

class PackCreature : XmlDataClass()
{
	@DataFileReference(resourceType = "Entity")
	lateinit var entity: String

	//region generated
	override fun load(xmlData: XmlData)
	{
		entity = xmlData.get("Entity")
	}
	//endregion
}