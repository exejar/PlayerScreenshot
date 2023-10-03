package club.maxstats.weave.playerscreenshot.event

import net.minecraft.entity.EntityLivingBase
import net.weavemc.weave.api.event.Event

sealed class RenderEntityModelEvent(
    val entity: EntityLivingBase
): Event() {
    class Pre(entity: EntityLivingBase): RenderEntityModelEvent(entity)
    class Post(entity: EntityLivingBase): RenderEntityModelEvent(entity)
}

sealed class RenderEntityLayersEvent(
    val entity: EntityLivingBase
): Event() {
    class Pre(entity: EntityLivingBase): RenderEntityLayersEvent(entity)
    class Post(entity: EntityLivingBase): RenderEntityLayersEvent(entity)
}