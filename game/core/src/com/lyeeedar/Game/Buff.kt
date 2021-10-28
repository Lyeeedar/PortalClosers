package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.EntityReference
import com.lyeeedar.Components.EventAndCondition
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*

@DataFile(icon = "Sprites/GUI/Buff.png")
class Buff : XmlDataClass()
{
	@DataNeedsLocalisation(file = "Buffs")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Buffs")
	lateinit var description: String
	var icon: Sprite? = null

	var duration: Int = 5

	val statistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	val eventHandlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	var isPositive: Boolean = true

	//region non-data
	var source: EntityReference? = null
	//endregion

	companion object
	{
		fun load(path: String): Buff
		{
			val xml = getXml(path)

			return load(xml)
		}

		fun load(xml: XmlData): Buff
		{
			val buff = Buff()
			buff.load(xml)

			return buff
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.tryLoadSprite(xmlData.getChildByName("Icon"))
		duration = xmlData.getInt("Duration", 5)
		val statisticsEl = xmlData.getChildByName("Statistics")
		if (statisticsEl != null)
		{
			for (el in statisticsEl.children)
			{
				val enumVal = Statistic.valueOf(el.name.uppercase(Locale.ENGLISH))
				statistics[enumVal] = el.float()
			}
		}
		val eventHandlersEl = xmlData.getChildByName("EventHandlers")
		if (eventHandlersEl != null)
		{
			for (el in eventHandlersEl.children)
			{
				val enumVal = EventType.valueOf(el.name.uppercase(Locale.ENGLISH))
				val objeventHandlers: Array<EventAndCondition> = Array()
				val objeventHandlersEl = el
				if (objeventHandlersEl != null)
				{
					for (el in objeventHandlersEl.children)
					{
						val objobjeventHandlers: EventAndCondition
						val objobjeventHandlersEl = el
						objobjeventHandlers = EventAndCondition()
						objobjeventHandlers.load(objobjeventHandlersEl)
						objeventHandlers.add(objobjeventHandlers)
					}
				}
				eventHandlers[enumVal] = objeventHandlers
			}
		}
		isPositive = xmlData.getBoolean("IsPositive", true)
	}
	//endregion
}