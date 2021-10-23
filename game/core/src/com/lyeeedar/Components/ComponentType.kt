package com.lyeeedar.Components

actual enum class ComponentType private constructor(val constructor: ()->AbstractComponent)
{
	AI({ AIComponent() }),
	Ability({ AbilityComponent() }),
	ActionSequence({ ActionSequenceComponent() }),
	ActiveAbility({ ActiveAbilityComponent() }),
	AdditionalRenderable({ AdditionalRenderableComponent() }),
	ArchetypeBuilder({ ArchetypeBuilderComponent() }),
	BakedLight({ BakedLightComponent() }),
	Blood({ BloodComponent() }),
	Combo({ ComboComponent() }),
	Dialogue({ DialogueComponent() }),
	DirectionalSprite({ DirectionalSpriteComponent() }),
	Equipment({ EquipmentComponent() }),
	Event({ EventComponent() }),
	EventHandler({ EventHandlerComponent() }),
	Light({ LightComponent() }),
	LoadData({ LoadDataComponent() }),
	MarkedForDeletion({ MarkedForDeletionComponent() }),
	MetaRegion({ MetaRegionComponent() }),
	Name({ NameComponent() }),
	Position({ PositionComponent() }),
	Renderable({ RenderableComponent() }),
	Shadow({ ShadowComponent() }),
	Statistics({ StatisticsComponent() }),
	Task({ TaskComponent() }),
	Transient({ TransientComponent() }),
	Variables({ VariablesComponent() }),
	Vision({ VisionComponent() }),
	Water({ WaterComponent() });

	companion object
	{
		val Values = ComponentType.values()
		val Temporary = arrayOf( MarkedForDeletion, Transient )
	}
}

inline fun Entity.ai(): AIComponent? = this.components[ComponentType.AI.ordinal] as AIComponent?
inline fun Entity.ability(): AbilityComponent? = this.components[ComponentType.Ability.ordinal] as AbilityComponent?
inline fun Entity.actionSequence(): ActionSequenceComponent? = this.components[ComponentType.ActionSequence.ordinal] as ActionSequenceComponent?
inline fun Entity.activeAbility(): ActiveAbilityComponent? = this.components[ComponentType.ActiveAbility.ordinal] as ActiveAbilityComponent?
inline fun Entity.additionalRenderable(): AdditionalRenderableComponent? = this.components[ComponentType.AdditionalRenderable.ordinal] as AdditionalRenderableComponent?
inline fun Entity.archetypeBuilder(): ArchetypeBuilderComponent? = this.components[ComponentType.ArchetypeBuilder.ordinal] as ArchetypeBuilderComponent?
inline fun Entity.bakedLight(): BakedLightComponent? = this.components[ComponentType.BakedLight.ordinal] as BakedLightComponent?
inline fun Entity.blood(): BloodComponent? = this.components[ComponentType.Blood.ordinal] as BloodComponent?
inline fun Entity.combo(): ComboComponent? = this.components[ComponentType.Combo.ordinal] as ComboComponent?
inline fun Entity.dialogue(): DialogueComponent? = this.components[ComponentType.Dialogue.ordinal] as DialogueComponent?
inline fun Entity.directionalSprite(): DirectionalSpriteComponent? = this.components[ComponentType.DirectionalSprite.ordinal] as DirectionalSpriteComponent?
inline fun Entity.equipment(): EquipmentComponent? = this.components[ComponentType.Equipment.ordinal] as EquipmentComponent?
inline fun Entity.event(): EventComponent? = this.components[ComponentType.Event.ordinal] as EventComponent?
inline fun Entity.eventHandler(): EventHandlerComponent? = this.components[ComponentType.EventHandler.ordinal] as EventHandlerComponent?
inline fun Entity.light(): LightComponent? = this.components[ComponentType.Light.ordinal] as LightComponent?
inline fun Entity.loadData(): LoadDataComponent? = this.components[ComponentType.LoadData.ordinal] as LoadDataComponent?
inline fun Entity.markedForDeletion(): MarkedForDeletionComponent? = this.components[ComponentType.MarkedForDeletion.ordinal] as MarkedForDeletionComponent?
inline fun Entity.metaRegion(): MetaRegionComponent? = this.components[ComponentType.MetaRegion.ordinal] as MetaRegionComponent?
inline fun Entity.name(): NameComponent? = this.components[ComponentType.Name.ordinal] as NameComponent?
inline fun Entity.position(): PositionComponent? = this.components[ComponentType.Position.ordinal] as PositionComponent?
inline fun Entity.renderable(): RenderableComponent? = this.components[ComponentType.Renderable.ordinal] as RenderableComponent?
inline fun Entity.shadow(): ShadowComponent? = this.components[ComponentType.Shadow.ordinal] as ShadowComponent?
inline fun Entity.statistics(): StatisticsComponent? = this.components[ComponentType.Statistics.ordinal] as StatisticsComponent?
inline fun Entity.task(): TaskComponent? = this.components[ComponentType.Task.ordinal] as TaskComponent?
inline fun Entity.transient(): TransientComponent? = this.components[ComponentType.Transient.ordinal] as TransientComponent?
inline fun Entity.variables(): VariablesComponent? = this.components[ComponentType.Variables.ordinal] as VariablesComponent?
inline fun Entity.vision(): VisionComponent? = this.components[ComponentType.Vision.ordinal] as VisionComponent?
inline fun Entity.water(): WaterComponent? = this.components[ComponentType.Water.ordinal] as WaterComponent?

