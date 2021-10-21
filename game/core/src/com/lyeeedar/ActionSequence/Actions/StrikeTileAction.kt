package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.Animation.BlinkAnimation
import com.lyeeedar.Renderables.CurveRenderable
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import ktx.collections.set
import ktx.math.div

@DataClass(category = "Renderable", name = "StrikeFX")
class StrikeTileAction : AbstractDurationActionSequenceAction()
{
	val key = "StrikeTile" + this.hashCode()

	lateinit var texture: TextureRegion
	var thickness: Float = 0.5f
	var samples: Int = 20
	var curveLag: Float = 0.5f
	var colour: Colour = Colour.WHITE.copy()
	var startAtSource: Boolean = true
	var loop: Boolean = true

	override fun enter(state: ActionSequenceState)
	{
		if (state.targets.size == 0)
		{
			return
		}

		val source = state.sourcePoint

		val entity = EntityPool.obtain()
		val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
		val renderable = entity.addOrGet(ComponentType.Renderable) as RenderableComponent

		pos.position = source

		// turn targets into path
		val targets = Array<Point>(state.targets)
		val points = Array<Vector2>()

		val targetDelayMap = ObjectFloatMap<Point>()

		var current = source
		while (targets.size > 0)
		{
			val min = targets.minByOrNull { it.dist(current) }!!
			targets.removeValue(min, true)
			current = min

			points.add(min.toVec().add(0.5f, 0.5f))

			targetDelayMap[min] = (duration / (state.targets.size + 1)) * points.size
		}

		state.data[targetDelayKey] = targetDelayMap

		if (state.targets.size == 1)
		{
			// insert control points
			val sourceVec = source.toVec()
			val targetVec = state.targets[0].toVec()

			val vec = targetVec.cpy().sub(sourceVec).nor()
			val perp = vec.set(-vec.y, vec.x)

			val midPoint = sourceVec.cpy().lerp(targetVec, 0.5f)
			val control1 = perp.cpy().scl(0.5f).add(midPoint).add(0.5f, 0.5f)
			val control2 = perp.cpy().scl(-0.5f).add(midPoint).add(0.5f, 0.5f)

			points.insert(0, control1)
			points.add(control2)
		}
		else
		{
			points.add(points[0])
		}

		if (startAtSource)
		{
			points.insert(0, source.toVec().add(0.5f, 0.5f))
		}
		points.add(points[0].cpy())

		val curve = CurveRenderable(BSpline(points.toArray(Vector2::class.java), 3, loop), thickness * state.world.tileSize, texture, samples)
		curve.setAnimation(duration, curveLag)
		renderable.renderable = curve
		renderable.renderable.colour = colour
		state.world.addEntity(entity)

		state.data[key] = entity.getRef()
	}

	override fun exit(state: ActionSequenceState)
	{
		val spawnedEntity = state.data[key] as? EntityReference? ?: return
		spawnedEntity.get()?.markForDeletion(0f)
		state.data.remove(key)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		texture = AssetManager.loadTextureRegion(xmlData.getChildByName("Texture")!!)
		thickness = xmlData.getFloat("Thickness", 0.5f)
		samples = xmlData.getInt("Samples", 20)
		curveLag = xmlData.getFloat("CurveLag", 0.5f)
		colour = AssetManager.tryLoadColour(xmlData.getChildByName("Colour"))!!
		startAtSource = xmlData.getBoolean("StartAtSource", true)
		loop = xmlData.getBoolean("Loop", true)
	}
	override val classID: String = "StrikeTile"
	//endregion
}