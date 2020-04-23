package com.lyeeedar.Util


class XmlDataClassLoader
{
	companion object
	{
		fun loadAbstractActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"ScreenShake" -> com.lyeeedar.ActionSequence.Actions.ScreenShakeAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"Animation" -> com.lyeeedar.ActionSequence.Actions.AnimationAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"Damage" -> com.lyeeedar.ActionSequence.Actions.DamageAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"SpawnTrackedParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnTrackedParticleAction()
				"ReplaceSourceRenderable" -> com.lyeeedar.ActionSequence.Actions.ReplaceSourceRenderableAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"Heal" -> com.lyeeedar.ActionSequence.Actions.HealAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				"FlightParticle" -> com.lyeeedar.ActionSequence.Actions.FlightParticleAction()
				"AttachParticle" -> com.lyeeedar.ActionSequence.Actions.AttachParticleAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractActionSequenceAction!")
			}
		}
		fun loadAbstractOneShotActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractOneShotActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"Damage" -> com.lyeeedar.ActionSequence.Actions.DamageAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"Heal" -> com.lyeeedar.ActionSequence.Actions.HealAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractOneShotActionSequenceAction!")
			}
		}
		fun loadAbstractDurationActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractDurationActionSequenceAction
		{
			return when (classID)
			{
				"Animation" -> com.lyeeedar.ActionSequence.Actions.AnimationAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"ScreenShake" -> com.lyeeedar.ActionSequence.Actions.ScreenShakeAction()
				"SpawnTrackedParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnTrackedParticleAction()
				"ReplaceSourceRenderable" -> com.lyeeedar.ActionSequence.Actions.ReplaceSourceRenderableAction()
				"FlightParticle" -> com.lyeeedar.ActionSequence.Actions.FlightParticleAction()
				"AttachParticle" -> com.lyeeedar.ActionSequence.Actions.AttachParticleAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractDurationActionSequenceAction!")
			}
		}
		fun loadAbstractNodeContainer(classID: String): com.lyeeedar.AI.BehaviourTree.AbstractNodeContainer
		{
			return when (classID)
			{
				"Any" -> com.lyeeedar.AI.BehaviourTree.Selectors.AnySelector()
				"DataScope" -> com.lyeeedar.AI.BehaviourTree.Decorators.DataScopeDecorator()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Decorators.ImportDecorator()
				"Priority" -> com.lyeeedar.AI.BehaviourTree.Selectors.PrioritySelector()
				"Random" -> com.lyeeedar.AI.BehaviourTree.Selectors.RandomSelector()
				"Repeat" -> com.lyeeedar.AI.BehaviourTree.Decorators.RepeatDecorator()
				"Sequence" -> com.lyeeedar.AI.BehaviourTree.Selectors.SequenceSelector()
				"SetState" -> com.lyeeedar.AI.BehaviourTree.Decorators.SetStateDecorator()
				"Until" -> com.lyeeedar.AI.BehaviourTree.Selectors.UntilSelector()
				"Invert" -> com.lyeeedar.AI.BehaviourTree.Decorators.InvertDecorator()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractNodeContainer!")
			}
		}
		fun loadAbstractTreeNode(classID: String): com.lyeeedar.AI.BehaviourTree.AbstractTreeNode
		{
			return when (classID)
			{
				"Any" -> com.lyeeedar.AI.BehaviourTree.Selectors.AnySelector()
				"CheckValue" -> com.lyeeedar.AI.BehaviourTree.Action.CheckValueAction()
				"DataScope" -> com.lyeeedar.AI.BehaviourTree.Decorators.DataScopeDecorator()
				"Priority" -> com.lyeeedar.AI.BehaviourTree.Selectors.PrioritySelector()
				"Random" -> com.lyeeedar.AI.BehaviourTree.Selectors.RandomSelector()
				"Repeat" -> com.lyeeedar.AI.BehaviourTree.Decorators.RepeatDecorator()
				"Sequence" -> com.lyeeedar.AI.BehaviourTree.Selectors.SequenceSelector()
				"SetState" -> com.lyeeedar.AI.BehaviourTree.Decorators.SetStateDecorator()
				"Until" -> com.lyeeedar.AI.BehaviourTree.Selectors.UntilSelector()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Decorators.ImportDecorator()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Action.ClearValueAction()
				"Invert" -> com.lyeeedar.AI.BehaviourTree.Decorators.InvertDecorator()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractTreeNode!")
			}
		}
		fun loadAbstractAction(classID: String): com.lyeeedar.AI.BehaviourTree.Action.AbstractAction
		{
			return when (classID)
			{
				"CheckValue" -> com.lyeeedar.AI.BehaviourTree.Action.CheckValueAction()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Action.ClearValueAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractAction!")
			}
		}
		fun loadAbstractDecorator(classID: String): com.lyeeedar.AI.BehaviourTree.Decorators.AbstractDecorator
		{
			return when (classID)
			{
				"DataScope" -> com.lyeeedar.AI.BehaviourTree.Decorators.DataScopeDecorator()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Decorators.ImportDecorator()
				"Repeat" -> com.lyeeedar.AI.BehaviourTree.Decorators.RepeatDecorator()
				"SetState" -> com.lyeeedar.AI.BehaviourTree.Decorators.SetStateDecorator()
				"Invert" -> com.lyeeedar.AI.BehaviourTree.Decorators.InvertDecorator()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractDecorator!")
			}
		}
		fun loadAbstractSelector(classID: String): com.lyeeedar.AI.BehaviourTree.Selectors.AbstractSelector
		{
			return when (classID)
			{
				"Any" -> com.lyeeedar.AI.BehaviourTree.Selectors.AnySelector()
				"Priority" -> com.lyeeedar.AI.BehaviourTree.Selectors.PrioritySelector()
				"Random" -> com.lyeeedar.AI.BehaviourTree.Selectors.RandomSelector()
				"Sequence" -> com.lyeeedar.AI.BehaviourTree.Selectors.SequenceSelector()
				"Until" -> com.lyeeedar.AI.BehaviourTree.Selectors.UntilSelector()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractSelector!")
			}
		}
		fun loadAbstractComponentData(classID: String): com.lyeeedar.Components.AbstractComponentData
		{
			return when (classID)
			{
				"AdditionalRenderable" -> com.lyeeedar.Components.AdditionalRenderableComponentData()
				"DirectionalSprite" -> com.lyeeedar.Components.DirectionalSpriteComponentData()
				"Renderable" -> com.lyeeedar.Components.RenderableComponentData()
				"Statistics" -> com.lyeeedar.Components.StatisticsComponentData()
				"EventHandler" -> com.lyeeedar.Components.EventHandlerComponentData()
				"Name" -> com.lyeeedar.Components.NameComponentData()
				"Dialogue" -> com.lyeeedar.Components.DialogueComponentData()
				"Empty" -> com.lyeeedar.Components.EmptyComponentData()
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
				"Condition" -> com.lyeeedar.MapGeneration.Nodes.ConditionAction()
				"DefineVariable" -> com.lyeeedar.MapGeneration.Nodes.DefineVariableAction()
				"Fill" -> com.lyeeedar.MapGeneration.Nodes.FillAction()
				"Filter" -> com.lyeeedar.MapGeneration.Nodes.FilterAction()
				"FindRooms" -> com.lyeeedar.MapGeneration.Nodes.FindRoomsAction()
				"Flip" -> com.lyeeedar.MapGeneration.Nodes.FlipAction()
				"PerPoint" -> com.lyeeedar.MapGeneration.Nodes.PerPointAction()
				"Repeat" -> com.lyeeedar.MapGeneration.Nodes.RepeatAction()
				"Scale" -> com.lyeeedar.MapGeneration.Nodes.ScaleAction()
				"SelectNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SelectNamedAreaAction()
				"Split" -> com.lyeeedar.MapGeneration.Nodes.SplitAction()
				"SquidlibDungeonGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDungeonGeneratorAction()
				"SquidlibLanesMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibLanesMapGeneratorAction()
				"SquidlibOrganicMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibOrganicMapGeneratorAction()
				"SquidlibSerpentMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSerpentMapGeneratorAction()
				"SquidlibSymmetryGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSymmetryGeneratorAction()
				"Symbol" -> com.lyeeedar.MapGeneration.Nodes.SymbolAction()
				"Translate" -> com.lyeeedar.MapGeneration.Nodes.TranslateAction()
				"SquidlibDenseRoomGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDenseRoomGeneratorAction()
				"Node" -> com.lyeeedar.MapGeneration.Nodes.NodeAction()
				"Divide" -> com.lyeeedar.MapGeneration.Nodes.DivideAction()
				"ConnectRooms" -> com.lyeeedar.MapGeneration.Nodes.ConnectRoomsAction()
				"Defer" -> com.lyeeedar.MapGeneration.Nodes.DeferAction()
				"SquidlibFlowingCaveGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibFlowingCaveGeneratorAction()
				"Take" -> com.lyeeedar.MapGeneration.Nodes.TakeAction()
				"Datascope" -> com.lyeeedar.MapGeneration.Nodes.DatascopeAction()
				"Rotate" -> com.lyeeedar.MapGeneration.Nodes.RotateAction()
				"SetNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SetNamedAreaAction()
				"SquidlibSectionGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSectionGeneratorAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractMapGenerationAction!")
			}
		}

	}
}
