/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.edibleSubstance.systems;

import com.google.common.base.Strings;
import org.terasology.edibleSubstance.components.ConsumableFluidContainerItemComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.system.FluidUtils;
import org.terasology.hunger.component.FoodComponent;
import org.terasology.hunger.event.FoodConsumedEvent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.registry.In;
import org.terasology.thirst.component.DrinkComponent;
import org.terasology.thirst.event.DrinkConsumedEvent;
import org.terasology.utilities.modifiable.ModifiableValue;


@RegisterSystem(RegisterMode.AUTHORITY)
public class ConsumableFluidContainerItemAuthoritySystem extends BaseComponentSystem {
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
        Prefab fluidSubstance = prefabManager.getPrefab(fluidContainer.fluidType);
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
                itemFoodComponent.filling = new ModifiableValue(substanceFoodComponent.filling.getValue() * fluidContainer.volume);

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
        Prefab fluidSubstance = prefabManager.getPrefab(fluidContainer.fluidType);
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
