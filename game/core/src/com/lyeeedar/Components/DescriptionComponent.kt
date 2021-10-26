package com.lyeeedar.Components

import com.lyeeedar.Util.DataNeedsLocalisation
import com.lyeeedar.Util.XmlData
class DescriptionComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Description

	lateinit var nameID: String
	lateinit var descriptionID: String

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as DescriptionComponentData
		nameID = data.name
		descriptionID = data.description
	}

	override fun reset()
	{

	}

}

class DescriptionComponentData : AbstractComponentData()
{
	@DataNeedsLocalisation(file = "Entity")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Entity")
	lateinit var description: String

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		name = xmlData.get("Name")
		description = xmlData.get("Description")
	}
	override val classID: String = "Description"
	//endregion
}