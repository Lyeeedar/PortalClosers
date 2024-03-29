package com.lyeeedar.Components

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.CompiledExpression
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass
import java.util.*

class EventAndCondition() : XmlDataClass()
{
	lateinit var condition: CompiledExpression
	lateinit var sequence: ActionSequence

	//region generated
	override fun load(xmlData: XmlData)
	{
		condition = CompiledExpression(xmlData.get("Condition", "1")!!)
		val sequenceEl = xmlData.getChildByName("Sequence")!!
		sequence = ActionSequence.load(sequenceEl)
	}
	//endregion
}

class EventHandlerComponentData : AbstractComponentData()
{
	val handlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val handlersEl = xmlData.getChildByName("Handlers")
		if (handlersEl != null)
		{
			for (el in handlersEl.children)
			{
				val enumVal = EventType.valueOf(el.name.uppercase(Locale.ENGLISH))
				val objhandlers: Array<EventAndCondition> = Array()
				val objhandlersEl = el
				if (objhandlersEl != null)
				{
					for (el in objhandlersEl.children)
					{
						val objobjhandlers: EventAndCondition
						val objobjhandlersEl = el
						objobjhandlers = EventAndCondition()
						objobjhandlers.load(objobjhandlersEl)
						objhandlers.add(objobjhandlers)
					}
				}
				handlers[enumVal] = objhandlers
			}
		}
	}
	override val classID: String = "EventHandler"
	//endregion
}

class EventHandlerComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.EventHandler

	var handlers: FastEnumMap<EventType, Array<EventAndCondition>> = FastEnumMap(EventType::class.java)

	override fun reset()
	{
		handlers.clear()
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as EventHandlerComponentData

		handlers.clear()
		handlers.addAll(data.handlers)
	}
}