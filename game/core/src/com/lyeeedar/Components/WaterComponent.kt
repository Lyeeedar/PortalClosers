package com.lyeeedar.Components

import com.lyeeedar.Direction
import com.lyeeedar.Util.XmlData
import java.util.*

class WaterComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Water

	var flowTowards: String? = null
	var flowDir: Direction = Direction.CENTER
	var flowChance: Float = 0.3f
	var depth: Float = 0.3f
	var flowForce: Int = 1

	override fun reset()
	{
		flowTowards = null
		flowDir = Direction.CENTER
		flowChance = 0.3f
		depth = 0.3f
		flowForce = 1
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as WaterComponentData

		this.flowTowards = data.flowTowards
		this.flowDir = data.flowDir
		this.flowChance = data.flowChance
		this.depth = data.depth
		this.flowForce = data.flowForce
	}
}

class WaterComponentData : AbstractComponentData()
{
	var flowTowards: String? = null
	var flowDir: Direction = Direction.CENTER
	var flowChance: Float = 0.3f
	var depth: Float = 0.3f
	var flowForce: Int = 1

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		flowTowards = xmlData.get("FlowTowards", null)
		flowDir = Direction.valueOf(xmlData.get("FlowDir", Direction.CENTER.toString())!!.uppercase(Locale.ENGLISH))
		flowChance = xmlData.getFloat("FlowChance", 0.3f)
		depth = xmlData.getFloat("Depth", 0.3f)
		flowForce = xmlData.getInt("FlowForce", 1)
	}
	override val classID: String = "Water"
	//endregion
}