package com.lyeeedar.Components

enum class ComponentType private constructor(val constructor: ()->AbstractComponent)
{
	AI({ AIComponent() }),
	ActionSequence({ ActionSequenceComponent() }),
	AdditionalRenderable({ AdditionalRenderableComponent() }),
	ArchetypeBuilder({ ArchetypeBuilderComponent() }),
	Blood({ BloodComponent() }),
	Dialogue({ DialogueComponent() }),
	DirectionalSprite({ DirectionalSpriteComponent() }),
	Event({ EventComponent() }),
	EventHandler({ EventHandlerComponent() }),
	Hate({ HateComponent() }),
	LoadData({ LoadDataComponent() }),
	MarkedForDeletion({ MarkedForDeletionComponent() }),
	MetaRegion({ MetaRegionComponent() }),
	Name({ NameComponent() }),
	Pack({ PackComponent() }),
	Position({ PositionComponent() }),
	Renderable({ RenderableComponent() }),
	Statistics({ StatisticsComponent() }),
	Task({ TaskComponent() }),
	Transient({ TransientComponent() }),
	Vision({ VisionComponent() }),
	Water({ WaterComponent() });

	companion object
	{
		val Values = ComponentType.values()
		val Temporary = arrayOf( MarkedForDeletion, Transient )
	}
}

inline fun Entity.ai(): AIComponent? = this.components[ComponentType.AI] as AIComponent?
inline fun Entity.actionSequence(): ActionSequenceComponent? = this.components[ComponentType.ActionSequence] as ActionSequenceComponent?
inline fun Entity.additionalRenderable(): AdditionalRenderableComponent? = this.components[ComponentType.AdditionalRenderable] as AdditionalRenderableComponent?
inline fun Entity.archetypeBuilder(): ArchetypeBuilderComponent? = this.components[ComponentType.ArchetypeBuilder] as ArchetypeBuilderComponent?
inline fun Entity.blood(): BloodComponent? = this.components[ComponentType.Blood] as BloodComponent?
inline fun Entity.dialogue(): DialogueComponent? = this.components[ComponentType.Dialogue] as DialogueComponent?
inline fun Entity.directionalSprite(): DirectionalSpriteComponent? = this.components[ComponentType.DirectionalSprite] as DirectionalSpriteComponent?
inline fun Entity.event(): EventComponent? = this.components[ComponentType.Event] as EventComponent?
inline fun Entity.eventHandler(): EventHandlerComponent? = this.components[ComponentType.EventHandler] as EventHandlerComponent?
inline fun Entity.hate(): HateComponent? = this.components[ComponentType.Hate] as HateComponent?
inline fun Entity.loadData(): LoadDataComponent? = this.components[ComponentType.LoadData] as LoadDataComponent?
inline fun Entity.markedForDeletion(): MarkedForDeletionComponent? = this.components[ComponentType.MarkedForDeletion] as MarkedForDeletionComponent?
inline fun Entity.metaRegion(): MetaRegionComponent? = this.components[ComponentType.MetaRegion] as MetaRegionComponent?
inline fun Entity.name(): NameComponent? = this.components[ComponentType.Name] as NameComponent?
inline fun Entity.pack(): PackComponent? = this.components[ComponentType.Pack] as PackComponent?
inline fun Entity.position(): PositionComponent? = this.components[ComponentType.Position] as PositionComponent?
inline fun Entity.renderable(): RenderableComponent? = this.components[ComponentType.Renderable] as RenderableComponent?
inline fun Entity.statistics(): StatisticsComponent? = this.components[ComponentType.Statistics] as StatisticsComponent?
inline fun Entity.task(): TaskComponent? = this.components[ComponentType.Task] as TaskComponent?
inline fun Entity.transient(): TransientComponent? = this.components[ComponentType.Transient] as TransientComponent?
inline fun Entity.vision(): VisionComponent? = this.components[ComponentType.Vision] as VisionComponent?
inline fun Entity.water(): WaterComponent? = this.components[ComponentType.Water] as WaterComponent?

