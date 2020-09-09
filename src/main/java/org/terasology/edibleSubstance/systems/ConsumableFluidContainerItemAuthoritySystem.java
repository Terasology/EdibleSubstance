// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.edibleSubstance.systems;

import com.google.common.base.Strings;
import org.terasology.edibleSubstance.components.ConsumableFluidContainerItemComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.modifiable.ModifiableValue;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.system.FluidRegistry;
import org.terasology.fluid.system.FluidUtils;
import org.terasology.hunger.component.FoodComponent;
import org.terasology.hunger.event.FoodConsumedEvent;
import org.terasology.thirst.component.DrinkComponent;
import org.terasology.thirst.event.DrinkConsumedEvent;


@RegisterSystem(RegisterMode.AUTHORITY)
public class ConsumableFluidContainerItemAuthoritySystem extends BaseComponentSystem {
    @In
    FluidRegistry fluidRegistry;
    @In
    PrefabManager prefabManager;

    @ReceiveEvent(components = {ItemComponent.class})
    public void eatFluidContainerItem(FoodConsumedEvent event, EntityRef item,
                                      FluidContainerItemComponent fluidContainer,
                                      ConsumableFluidContainerItemComponent consumableFluidContainerItemComponent) {
        if (!Strings.isNullOrEmpty(fluidContainer.fluidType)) {
            FluidUtils.setFluidForContainerItem(item, null);
        }
    }

    @ReceiveEvent(components = {ItemComponent.class})
    public void fillContainerWithFoodSubstance(OnChangedComponent onChangedComponent, EntityRef item,
                                               FluidContainerItemComponent fluidContainer,
                                               ConsumableFluidContainerItemComponent consumableFluidContainerItemComponent) {
        Prefab fluidSubstance = fluidRegistry.getPrefab(fluidContainer.fluidType);
        // only allow a food component if there is a valid substance
        if (fluidSubstance != null) {
            // get the food component from the substance
            FoodComponent substanceFoodComponent = fluidSubstance.getComponent(FoodComponent.class);
            if (substanceFoodComponent != null) {
                // there is a valid food on this substance

                // create a new food component to put on this item
                FoodComponent itemFoodComponent = item.getComponent(FoodComponent.class);
                if (itemFoodComponent == null) {
                    itemFoodComponent = new FoodComponent();
                }

                // set the volume corrected amount of food for this container
                itemFoodComponent.filling =
                        new ModifiableValue(substanceFoodComponent.filling.getValue() * fluidContainer.volume);

                // save the item's food component
                item.addOrSaveComponent(itemFoodComponent);
                return;
            }
        }

        // as a fallback,  remove any food component from this item that should not be present
        item.removeComponent(FoodComponent.class);
    }

    @ReceiveEvent(components = {ItemComponent.class})
    public void drinkFluidContainerItem(DrinkConsumedEvent event, EntityRef item,
                                        FluidContainerItemComponent fluidContainer,
                                        ConsumableFluidContainerItemComponent consumableFluidContainerItemComponent) {
        if (!Strings.isNullOrEmpty(fluidContainer.fluidType)) {
            FluidUtils.setFluidForContainerItem(item, null);
        }
    }


    @ReceiveEvent(components = {ItemComponent.class})
    public void fillContainerWithDrinkSubstance(OnChangedComponent onChangedComponent, EntityRef item,
                                                FluidContainerItemComponent fluidContainer,
                                                ConsumableFluidContainerItemComponent consumableFluidContainerItemComponent) {
        Prefab fluidSubstance = fluidRegistry.getPrefab(fluidContainer.fluidType);
        // only allow a drink component if there is a valid substance
        if (fluidSubstance != null) {
            // get the drink component from the substance
            DrinkComponent substanceDrinkComponent = fluidSubstance.getComponent(DrinkComponent.class);
            if (substanceDrinkComponent != null) {
                // there is a valid drink on this substance

                // create a new drink component to put on this item
                DrinkComponent itemDrinkComponent = item.getComponent(DrinkComponent.class);
                if (itemDrinkComponent == null) {
                    itemDrinkComponent = new DrinkComponent();
                }

                // set the volume corrected amount of drink for this container
                itemDrinkComponent.filling = substanceDrinkComponent.filling * fluidContainer.volume;

                // save the item's drink component
                item.addOrSaveComponent(itemDrinkComponent);
                return;
            }
        }

        // as a fallback,  remove any drink component from this item that should not be present
        item.removeComponent(DrinkComponent.class);
    }

}
