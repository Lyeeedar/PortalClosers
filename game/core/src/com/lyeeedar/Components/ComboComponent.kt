package com.lyeeedar.Components

import com.lyeeedar.Direction
import com.lyeeedar.Game.Combo.AbstractComboStep
import com.lyeeedar.Game.Combo.Combo
import com.lyeeedar.Game.Tile
import com.lyeeedar.Util.*

class ComboComponentData : AbstractComponentData()
{
	@DataFileReference(resourceType = "Combo")
	lateinit var comboPath: String

	@Transient
	lateinit var combo: Combo

	override fun afterLoad()
	{
		val xml = getXml(comboPath)

		combo = Combo()
		combo.load(xml)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		comboPath = xmlData.get("ComboPath")
		afterLoad()
	}
	override val classID: String = "Combo"
	//endregion
}

class ComboComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Combo

	lateinit var combo: Combo
	var currentStep: AbstractComboStep? = null
	var lastTarget: Point? = null
	var fixedDirection: Direction? = null

	override fun reset()
	{
		currentStep = null
		lastTarget = null
		fixedDirection = null
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as ComboComponentData

		this.combo = data.combo
	}
}