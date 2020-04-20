package com.lyeeedar.Components

enum class ComponentType private constructor(val constructor: ()->AbstractComponent<*>)
{
	// Engine
	ArchetypeBuilder({ ArchetypeBuilderComponent() }),
	AdditionalRenderable({ AdditionalRenderableComponent(AdditionalRenderableComponentData()) }),
	Dialogue({ DialogueComponent(DialogueComponentData()) }),
	DirectionalSprite({ DirectionalSpriteComponent(DirectionalSpriteComponentData()) }),
	Event({ EventComponent() }),
	LoadData({ LoadDataComponent() }),
	MarkedForDeletion({ MarkedForDeletionComponent() }),
	MetaRegion({ MetaRegionComponent(MetaRegionComponentData()) }),
	Name({ NameComponent(NameComponentData()) }),
	Position({ PositionComponent(PositionComponentData()) }),
	Renderable({ RenderableComponent(RenderableComponentData()) }),
	Transient({ TransientComponent() });

	companion object
	{
		val Values = ComponentType.values()
		val Temporary = arrayOf( MarkedForDeletion, Transient )
	}
}