package com.ducklingvivi.voxelweapons.library;

import com.mojang.serialization.Lifecycle;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import org.apache.commons.lang3.Validate;

import java.util.IdentityHashMap;

public class RemoveableRegistry<T> extends MappedRegistry<T> {
    public RemoveableRegistry(ResourceKey<? extends Registry<T>> p_249899_, Lifecycle p_252249_) {
        this(p_249899_, p_252249_, false);
    }

    public RemoveableRegistry(ResourceKey p_252132_, Lifecycle p_249215_, boolean p_251014_) {
        super(p_252132_, p_249215_, p_251014_);
    }
}
//    public void RemoveKey(ResourceKey key){
//
//        this.byKey.put(key, reference);
//        this.byLocation.put(key.location(), reference);
//        this.byValue.put(value, reference);
//        this.byId.size(Math.max(this.byId.size(), id + 1));
//        this.byId.set(id, reference);
//        this.toId.put(value, id);
//        if (this.nextId <= id) {
//            this.nextId = id + 1;
//        }
//
//        this.lifecycles.put(value, lifecycle);
//        this.registryLifecycle = this.registryLifecycle.add(lifecycle);
//        this.holdersInOrder = null;
//        return reference;
//    }
//
//    public Holder.Reference<T> registerMapping(int id, ResourceKey<T> key, T value, Lifecycle lifecycle) {
//        markKnown();
//        this.validateWrite(key);
//        Validate.notNull(key);
//        Validate.notNull(value);
//        if (this.byLocation.containsKey(key.location())) {
//            Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + key + "' to registry"));
//        }
//
//        if (this.byValue.containsKey(value)) {
//            Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + value + "' to registry"));
//        }
//
//        Holder.Reference<T> reference;
//        if (this.unregisteredIntrusiveHolders != null) {
//            reference = this.unregisteredIntrusiveHolders.remove(value);
//            if (reference == null) {
//                throw new AssertionError("Missing intrusive holder for " + key + ":" + value);
//            }
//
//            reference.bindKey(key);
//        } else {
//            reference = this.byKey.computeIfAbsent(key, (p_258168_) -> {
//                return Holder.Reference.createStandAlone(this.holderOwner(), p_258168_);
//            });
//            // Forge: Bind the value immediately so it can be queried while the registry is not frozen
//            reference.bindValue(value);
//        }
//
//        this.byKey.put(key, reference);
//        this.byLocation.put(key.location(), reference);
//        this.byValue.put(value, reference);
//        this.byId.size(Math.max(this.byId.size(), id + 1));
//        this.byId.set(id, reference);
//        this.toId.put(value, id);
//        if (this.nextId <= id) {
//            this.nextId = id + 1;
//        }
//
//        this.lifecycles.put(value, lifecycle);
//        this.registryLifecycle = this.registryLifecycle.add(lifecycle);
//        this.holdersInOrder = null;
//        return reference;
//    }
//}
