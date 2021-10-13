package com.lyeeedar.Util


actual class XmlDataClassLoader
{
	companion object
	{
		fun loadAbstractAbilityModifier(classID: String): com.lyeeedar.Game.Ability.AbstractAbilityModifier<*>
		{
			return when (classID)
			{
				"Colour" -> com.lyeeedar.Game.Ability.ColourAbilityModifier()
				"Repeat" -> com.lyeeedar.Game.Ability.RepeatAbilityModifier()
				"Damage" -> com.lyeeedar.Game.Ability.DamageAbilityModifier()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractAbilityModifier!")
			}
		}
		fun loadAbstractAbilityModifierKeyframe(classID: String): com.lyeeedar.Game.Ability.AbstractAbilityModifierKeyframe
		{
			return when (classID)
			{
				"ColourKeyframe" -> com.lyeeedar.Game.Ability.ColourKeyframeData()
				"RepeatKeyframe" -> com.lyeeedar.Game.Ability.RepeatKeyframeData()
				"DamageKeyframe" -> com.lyeeedar.Game.Ability.DamageKeyframeData()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractAbilityModifierKeyframe!")
			}
		}
		fun loadAbstractComboStep(classID: String): com.lyeeedar.Game.Combo.AbstractComboStep
		{
			return when (classID)
			{
				"Wait" -> com.lyeeedar.Game.Combo.WaitComboStep()
				"MeleeAttack" -> com.lyeeedar.Game.Combo.MeleeAttackComboStep()
				"Ability" -> com.lyeeedar.Game.Combo.AbilityComboStep()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractComboStep!")
			}
		}
		fun loadAbstractActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"SetSourcePoint" -> com.lyeeedar.ActionSequence.Actions.SetSourcePointAction()
				"Stun" -> com.lyeeedar.ActionSequence.Actions.StunAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"Pull" -> com.lyeeedar.ActionSequence.Actions.PullAction()
				"ReplaceSourceRenderable" -> com.lyeeedar.ActionSequence.Actions.ReplaceSourceRenderableAction()
				"Detach" -> com.lyeeedar.ActionSequence.Actions.DetachAction()
				"Heal" -> com.lyeeedar.ActionSequence.Actions.HealAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"ScreenShake" -> com.lyeeedar.ActionSequence.Actions.ScreenShakeAction()
				"Knockback" -> com.lyeeedar.ActionSequence.Actions.KnockbackAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"Animation" -> com.lyeeedar.ActionSequence.Actions.AnimationAction()
				"Buff" -> com.lyeeedar.ActionSequence.Actions.BuffAction()
				"MoveSource" -> com.lyeeedar.ActionSequence.Actions.MoveSourceAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"Damage" -> com.lyeeedar.ActionSequence.Actions.DamageAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"SpawnTrackedParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnTrackedParticleAction()
				"MarkAndWaitForPlayer" -> com.lyeeedar.ActionSequence.Actions.MarkAndWaitForPlayerAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"BuildResources" -> com.lyeeedar.ActionSequence.Actions.BuildResourcesAction()
				"FlightParticle" -> com.lyeeedar.ActionSequence.Actions.FlightParticleAction()
				"AttachParticle" -> com.lyeeedar.ActionSequence.Actions.AttachParticleAction()
				"ConsumeResources" -> com.lyeeedar.ActionSequence.Actions.ConsumeResourcesAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractActionSequenceAction!")
			}
		}
		fun loadAbstractOneShotActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractOneShotActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SetSourcePoint" -> com.lyeeedar.ActionSequence.Actions.SetSourcePointAction()
				"Stun" -> com.lyeeedar.ActionSequence.Actions.StunAction()
				"Pull" -> com.lyeeedar.ActionSequence.Actions.PullAction()
				"Detach" -> com.lyeeedar.ActionSequence.Actions.DetachAction()
				"Heal" -> com.lyeeedar.ActionSequence.Actions.HealAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"Knockback" -> com.lyeeedar.ActionSequence.Actions.KnockbackAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"MoveSource" -> com.lyeeedar.ActionSequence.Actions.MoveSourceAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"Damage" -> com.lyeeedar.ActionSequence.Actions.DamageAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"MarkAndWaitForPlayer" -> com.lyeeedar.ActionSequence.Actions.MarkAndWaitForPlayerAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"BuildResources" -> com.lyeeedar.ActionSequence.Actions.BuildResourcesAction()
				"ConsumeResources" -> com.lyeeedar.ActionSequence.Actions.ConsumeResourcesAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractOneShotActionSequenceAction!")
			}
		}
		fun loadAbstractDurationActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractDurationActionSequenceAction
		{
			return when (classID)
			{
				"Animation" -> com.lyeeedar.ActionSequence.Actions.AnimationAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"ReplaceSourceRenderable" -> com.lyeeedar.ActionSequence.Actions.ReplaceSourceRenderableAction()
				"ScreenShake" -> com.lyeeedar.ActionSequence.Actions.ScreenShakeAction()
				"Buff" -> com.lyeeedar.ActionSequence.Actions.BuffAction()
				"SpawnTrackedParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnTrackedParticleAction()
				"FlightParticle" -> com.lyeeedar.ActionSequence.Actions.FlightParticleAction()
				"AttachParticle" -> com.lyeeedar.ActionSequence.Actions.AttachParticleAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractDurationActionSequenceAction!")
			}
		}
		fun loadAbstractBehaviourTreeItem(classID: String): com.lyeeedar.AI.BehaviourTree.AbstractBehaviourTreeItem
		{
			return when (classID)
			{
				"Breakpoint" -> com.lyeeedar.AI.BehaviourTree.Actions.BreakpointBehaviourAction()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Actions.ClearValueBehaviourAction()
				"Combo" -> com.lyeeedar.AI.BehaviourTree.Actions.ComboBehaviourAction()
				"Datascope" -> com.lyeeedar.AI.BehaviourTree.Actions.DatascopeBehaviourAction()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Actions.ImportBehaviourAction()
				"MoveTo" -> com.lyeeedar.AI.BehaviourTree.Actions.MoveToBehaviourAction()
				"Node" -> com.lyeeedar.AI.BehaviourTree.Actions.NodeBehaviourAction()
				"RunUntilNotFailed" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotFailedBehaviourNode()
				"SetState" -> com.lyeeedar.AI.BehaviourTree.Actions.SetStateBehaviourAction()
				"UseAbility" -> com.lyeeedar.AI.BehaviourTree.Actions.UseAbilityBehaviourAction()
				"ProcessInput" -> com.lyeeedar.AI.BehaviourTree.Actions.ProcessInputBehaviourAction()
				"RunOneRandomly" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunOneRandomlyBehaviourNode()
				"PickOneFrom" -> com.lyeeedar.AI.BehaviourTree.Actions.PickOneFromBehaviourAction()
				"Wait" -> com.lyeeedar.AI.BehaviourTree.Actions.WaitBehaviourAction()
				"RunUntilState" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilStateBehaviourNode()
				"GetAllVisible" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAllVisibleBehaviourAction()
				"SetValue" -> com.lyeeedar.AI.BehaviourTree.Actions.SetValueBehaviourAction()
				"Branch" -> com.lyeeedar.AI.BehaviourTree.Actions.BranchBehaviourAction()
				"Kill" -> com.lyeeedar.AI.BehaviourTree.Actions.KillBehaviourAction()
				"RunAll" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunAllBehaviourNode()
				"ConvertToPosition" -> com.lyeeedar.AI.BehaviourTree.Actions.ConvertToPositionBehaviourAction()
				"RunUntilNotCompleted" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotCompletedBehaviourNode()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractBehaviourTreeItem!")
			}
		}
		fun loadAbstractBehaviourAction(classID: String): com.lyeeedar.AI.BehaviourTree.Actions.AbstractBehaviourAction
		{
			return when (classID)
			{
				"Breakpoint" -> com.lyeeedar.AI.BehaviourTree.Actions.BreakpointBehaviourAction()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Actions.ClearValueBehaviourAction()
				"Combo" -> com.lyeeedar.AI.BehaviourTree.Actions.ComboBehaviourAction()
				"Datascope" -> com.lyeeedar.AI.BehaviourTree.Actions.DatascopeBehaviourAction()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Actions.ImportBehaviourAction()
				"MoveTo" -> com.lyeeedar.AI.BehaviourTree.Actions.MoveToBehaviourAction()
				"Node" -> com.lyeeedar.AI.BehaviourTree.Actions.NodeBehaviourAction()
				"ProcessInput" -> com.lyeeedar.AI.BehaviourTree.Actions.ProcessInputBehaviourAction()
				"SetState" -> com.lyeeedar.AI.BehaviourTree.Actions.SetStateBehaviourAction()
				"UseAbility" -> com.lyeeedar.AI.BehaviourTree.Actions.UseAbilityBehaviourAction()
				"PickOneFrom" -> com.lyeeedar.AI.BehaviourTree.Actions.PickOneFromBehaviourAction()
				"Wait" -> com.lyeeedar.AI.BehaviourTree.Actions.WaitBehaviourAction()
				"GetAllVisible" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAllVisibleBehaviourAction()
				"SetValue" -> com.lyeeedar.AI.BehaviourTree.Actions.SetValueBehaviourAction()
				"Branch" -> com.lyeeedar.AI.BehaviourTree.Actions.BranchBehaviourAction()
				"Kill" -> com.lyeeedar.AI.BehaviourTree.Actions.KillBehaviourAction()
				"ConvertToPosition" -> com.lyeeedar.AI.BehaviourTree.Actions.ConvertToPositionBehaviourAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractBehaviourAction!")
			}
		}
		fun loadAbstractBehaviourNode(classID: String): com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
		{
			return when (classID)
			{
				"RunUntilNotFailed" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotFailedBehaviourNode()
				"RunOneRandomly" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunOneRandomlyBehaviourNode()
				"RunUntilState" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilStateBehaviourNode()
				"RunAll" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunAllBehaviourNode()
				"RunUntilNotCompleted" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotCompletedBehaviourNode()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractBehaviourNode!")
			}
		}
		fun loadAbstractComponentData(classID: String): com.lyeeedar.Components.AbstractComponentData
		{
			return when (classID)
			{
				"AI" -> com.lyeeedar.Components.AIComponentData()
				"Ability" -> com.lyeeedar.Components.AbilityComponentData()
				"AdditionalRenderable" -> com.lyeeedar.Components.AdditionalRenderableComponentData()
				"Dialogue" -> com.lyeeedar.Components.DialogueComponentData()
				"DirectionalSprite" -> com.lyeeedar.Components.DirectionalSpriteComponentData()
				"Light" -> com.lyeeedar.Components.LightComponentData()
				"Renderable" -> com.lyeeedar.Components.RenderableComponentData()
				"Statistics" -> com.lyeeedar.Components.StatisticsComponentData()
				"Water" -> com.lyeeedar.Components.WaterComponentData()
				"Weapon" -> com.lyeeedar.Components.WeaponComponentData()
				"Combo" -> com.lyeeedar.Components.ComboComponentData()
				"BakedLight" -> com.lyeeedar.Components.BakedLightComponentData()
				"EventHandler" -> com.lyeeedar.Components.EventHandlerComponentData()
				"Name" -> com.lyeeedar.Components.NameComponentData()
				"Position" -> com.lyeeedar.Components.PositionComponentData()
				"MetaRegion" -> com.lyeeedar.Components.MetaRegionComponentData()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractComponentData!")
			}
		}
		fun loadAbstractMapGenerationAction(classID: String): com.lyeeedar.MapGeneration.Nodes.AbstractMapGenerationAction
		{
			return when (classID)
			{
				"ChambersGenerator" -> com.lyeeedar.MapGeneration.Nodes.ChambersGeneratorAction()
				"Filter" -> com.lyeeedar.MapGeneration.Nodes.FilterAction()
				"FindRooms" -> com.lyeeedar.MapGeneration.Nodes.FindRoomsAction()
				"Scale" -> com.lyeeedar.MapGeneration.Nodes.ScaleAction()
				"SelectNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SelectNamedAreaAction()
				"SquidlibOrganicMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibOrganicMapGeneratorAction()
				"SquidlibSerpentMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSerpentMapGeneratorAction()
				"SquidlibSymmetryGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSymmetryGeneratorAction()
				"Translate" -> com.lyeeedar.MapGeneration.Nodes.TranslateAction()
				"ConnectRooms" -> com.lyeeedar.MapGeneration.Nodes.ConnectRoomsAction()
				"DefineSymbol" -> com.lyeeedar.MapGeneration.Nodes.DefineSymbolAction()
				"SetNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SetNamedAreaAction()
				"Condition" -> com.lyeeedar.MapGeneration.Nodes.ConditionAction()
				"PerPoint" -> com.lyeeedar.MapGeneration.Nodes.PerPointAction()
				"Split" -> com.lyeeedar.MapGeneration.Nodes.SplitAction()
				"Repeat" -> com.lyeeedar.MapGeneration.Nodes.RepeatAction()
				"SquidlibDungeonGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDungeonGeneratorAction()
				"DefineVariable" -> com.lyeeedar.MapGeneration.Nodes.DefineVariableAction()
				"SquidlibLanesMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibLanesMapGeneratorAction()
				"Fill" -> com.lyeeedar.MapGeneration.Nodes.FillAction()
				"Flip" -> com.lyeeedar.MapGeneration.Nodes.FlipAction()
				"SquidlibDenseRoomGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDenseRoomGeneratorAction()
				"Node" -> com.lyeeedar.MapGeneration.Nodes.NodeAction()
				"Divide" -> com.lyeeedar.MapGeneration.Nodes.DivideAction()
				"Defer" -> com.lyeeedar.MapGeneration.Nodes.DeferAction()
				"SquidlibFlowingCaveGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibFlowingCaveGeneratorAction()
				"Take" -> com.lyeeedar.MapGeneration.Nodes.TakeAction()
				"Datascope" -> com.lyeeedar.MapGeneration.Nodes.DatascopeAction()
				"Rotate" -> com.lyeeedar.MapGeneration.Nodes.RotateAction()
				"SquidlibSectionGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSectionGeneratorAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractMapGenerationAction!")
			}
		}
		fun loadAbstractImageModifier(classID: String): com.lyeeedar.Renderables.AbstractImageModifier
		{
			return when (classID)
			{
				"Gradient" -> com.lyeeedar.Renderables.GradientModifier()
				"Grayscale" -> com.lyeeedar.Renderables.GrayscaleModifier()
				"Tint" -> com.lyeeedar.Renderables.TintModifier()
				"Stroke" -> com.lyeeedar.Renderables.StrokeModifier()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractImageModifier!")
			}
		}

	}
}
