package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.EventAndCondition
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.XmlDataClass
import com.lyeeedar.Util.getXml

class Buff : XmlDataClass()
{
	lateinit var name: String
	lateinit var description: String
	var icon: Sprite? = null

	var duration: Int = 5

	val statistics: FastEnumMap<Statistic, Float> = FastEnumMap(Statistic::class.java)
	val eventHandlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	var isPositive: Boolean = true

	companion object
	{
		fun load(path: String): Buff
		{
			val xml = getXml(path)

			val buff = Buff()
			buff.load(xml)

			return buff
		}
	}
}