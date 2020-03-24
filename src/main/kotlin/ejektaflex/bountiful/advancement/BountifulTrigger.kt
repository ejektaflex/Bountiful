package ejektaflex.bountiful.advancement

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import net.minecraft.advancements.ICriterionTrigger
import net.minecraft.advancements.PlayerAdvancements
import net.minecraft.advancements.criterion.CriterionInstance
import net.minecraft.util.ResourceLocation

class BountifulTrigger(val resId: ResourceLocation) : ICriterionTrigger<CriterionInstance> {

    var listeners: MutableMap<PlayerAdvancements, MutableSet<ICriterionTrigger.Listener<CriterionInstance>>?> = mutableMapOf()

    override fun getId() = resId

    override fun addListener(adv: PlayerAdvancements, listener: ICriterionTrigger.Listener<CriterionInstance>) {
        listeners.computeIfAbsent(adv) { a: PlayerAdvancements? -> mutableSetOf() }!!.add(listener)
    }

    override fun removeListener(adv: PlayerAdvancements, listener: ICriterionTrigger.Listener<CriterionInstance>) {
        listeners.computeIfAbsent(adv) { a: PlayerAdvancements? -> mutableSetOf() }!!.remove(listener)
    }

    override fun removeAllListeners(adv: PlayerAdvancements) {
        listeners.remove(adv)
    }

    override fun deserializeInstance(json: JsonObject, context: JsonDeserializationContext): CriterionInstance {
        return CriterionInstance(id)
    }


    fun trigger(adv: PlayerAdvancements) {
        if (listeners.containsKey(adv)) {
            listeners[adv]!!.toList().forEach { listener ->
                listener.grantCriterion(adv)
            }
        }
    }

}