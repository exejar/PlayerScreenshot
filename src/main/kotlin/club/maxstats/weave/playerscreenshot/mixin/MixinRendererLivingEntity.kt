package club.maxstats.weave.playerscreenshot.mixin

import club.maxstats.weave.playerscreenshot.event.RenderEntityLayersEvent
import club.maxstats.weave.playerscreenshot.event.RenderEntityModelEvent
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.EntityLivingBase
import net.weavemc.weave.api.event.EventBus
import net.weavemc.weave.api.mixin.At
import net.weavemc.weave.api.mixin.CallbackInfo
import net.weavemc.weave.api.mixin.Inject
import net.weavemc.weave.api.mixin.Mixin

@Mixin(RendererLivingEntity::class)
class MixinRendererLivingEntity {
    @Inject(method = "renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V")
    fun modelHead(entity: EntityLivingBase, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, ci: CallbackInfo) {
        EventBus.callEvent(RenderEntityModelEvent.Pre(entity))
    }
    @Inject(method = "renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V", at = At(value = At.Location.TAIL))
    fun modelTail(entity: EntityLivingBase, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, ci: CallbackInfo) {
        EventBus.callEvent(RenderEntityModelEvent.Post(entity))
    }
    @Inject(method = "renderLayers(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V")
    fun layersHead(entity: EntityLivingBase, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, f7: Float, ci: CallbackInfo) {
        EventBus.callEvent(RenderEntityLayersEvent.Pre(entity))
    }
    @Inject(method = "renderLayers(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", at = At(value = At.Location.TAIL))
    fun layersTail(entity: EntityLivingBase, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, f7: Float, ci: CallbackInfo) {
        EventBus.callEvent(RenderEntityLayersEvent.Post(entity))
    }
}