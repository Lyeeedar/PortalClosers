package com.lyeeedar.Util


class XmlDataClassLoader
{
	companion object
	{
		fun loadAbstractActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractActionSequenceAction
		{
			return when (classID)
			{
				"GenerateHate" -> com.lyeeedar.ActionSequence.Actions.GenerateHateAction()
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"ScreenShake" -> com.lyeeedar.ActionSequence.Actions.ScreenShakeAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"SetSourcePoint" -> com.lyeeedar.ActionSequence.Actions.SetSourcePointAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"Stun" -> com.lyeeedar.ActionSequence.Actions.StunAction()
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
				"GenerateHate" -> com.lyeeedar.ActionSequence.Actions.GenerateHateAction()
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"SetSourcePoint" -> com.lyeeedar.ActionSequence.Actions.SetSourcePointAction()
				"SpawnOneShotParticle" -> com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"Stun" -> com.lyeeedar.ActionSequence.Actions.StunAction()
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
		fun loadAbstractBehaviourTreeItem(classID: String): com.lyeeedar.AI.BehaviourTree.AbstractBehaviourTreeItem
		{
			return when (classID)
			{
				"GetAgro" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAgroBehaviourAction()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Actions.ImportBehaviourAction()
				"Node" -> com.lyeeedar.AI.BehaviourTree.Actions.NodeBehaviourAction()
				"ProcessInput" -> com.lyeeedar.AI.BehaviourTree.Actions.ProcessInputBehaviourAction()
				"RunUntilNotFailed" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotFailedBehaviourNode()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Actions.ClearValueBehaviourAction()
				"RunOneRandomly" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunOneRandomlyBehaviourNode()
				"Attack" -> com.lyeeedar.AI.BehaviourTree.Actions.AttackBehaviourAction()
				"Datascope" -> com.lyeeedar.AI.BehaviourTree.Actions.DatascopeBehaviourAction()
				"PickOneFrom" -> com.lyeeedar.AI.BehaviourTree.Actions.PickOneFromBehaviourAction()
				"Wait" -> com.lyeeedar.AI.BehaviourTree.Actions.WaitBehaviourAction()
				"RunUntilState" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilStateBehaviourNode()
				"MoveTo" -> com.lyeeedar.AI.BehaviourTree.Actions.MoveToBehaviourAction()
				"GetAllVisible" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAllVisibleBehaviourAction()
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
				"Attack" -> com.lyeeedar.AI.BehaviourTree.Actions.AttackBehaviourAction()
				"ClearValue" -> com.lyeeedar.AI.BehaviourTree.Actions.ClearValueBehaviourAction()
				"GetAgro" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAgroBehaviourAction()
				"Import" -> com.lyeeedar.AI.BehaviourTree.Actions.ImportBehaviourAction()
				"Node" -> com.lyeeedar.AI.BehaviourTree.Actions.NodeBehaviourAction()
				"ProcessInput" -> com.lyeeedar.AI.BehaviourTree.Actions.ProcessInputBehaviourAction()
				"Datascope" -> com.lyeeedar.AI.BehaviourTree.Actions.DatascopeBehaviourAction()
				"PickOneFrom" -> com.lyeeedar.AI.BehaviourTree.Actions.PickOneFromBehaviourAction()
				"Wait" -> com.lyeeedar.AI.BehaviourTree.Actions.WaitBehaviourAction()
				"MoveTo" -> com.lyeeedar.AI.BehaviourTree.Actions.MoveToBehaviourAction()
				"GetAllVisible" -> com.lyeeedar.AI.BehaviourTree.Actions.GetAllVisibleBehaviourAction()
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
				"RunOneRandomly" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunOneRandomlyBehaviourNode()
				"RunUntilNotFailed" -> com.lyeeedar.AI.BehaviourTree.Nodes.RunUntilNotFailedBehaviourNode()
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
				"AdditionalRenderable" -> com.lyeeedar.Components.AdditionalRenderableComponentData()
				"DirectionalSprite" -> com.lyeeedar.Components.DirectionalSpriteComponentData()
				"Renderable" -> com.lyeeedar.Components.RenderableComponentData()
				"Statistics" -> com.lyeeedar.Components.StatisticsComponentData()
				"EventHandler" -> com.lyeeedar.Components.EventHandlerComponentData()
				"Name" -> com.lyeeedar.Components.NameComponentData()
				"Dialogue" -> com.lyeeedar.Components.DialogueComponentData()
				"Position" -> com.lyeeedar.Components.PositionComponentData()
				"AI" -> com.lyeeedar.Components.AIComponentData()
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
				"Translate" -> com.lyeeedar.MapGeneration.Nodes.TranslateAction()
				"SquidlibDenseRoomGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDenseRoomGeneratorAction()
				"Node" -> com.lyeeedar.MapGeneration.Nodes.NodeAction()
				"Divide" -> com.lyeeedar.MapGeneration.Nodes.DivideAction()
				"ConnectRooms" -> com.lyeeedar.MapGeneration.Nodes.ConnectRoomsAction()
				"Defer" -> com.lyeeedar.MapGeneration.Nodes.DeferAction()
				"DefineSymbol" -> com.lyeeedar.MapGeneration.Nodes.DefineSymbolAction()
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
