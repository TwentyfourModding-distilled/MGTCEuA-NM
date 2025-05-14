package io.github.mireole.mgtceua.client.widgets;

import gregtech.api.gui.Widget;
import gregtech.api.gui.ingredient.IGhostIngredientTarget;
import gregtech.api.gui.resources.ColorRectTexture;
import gregtech.api.gui.widgets.PhantomFluidWidget;
import gregtech.api.gui.widgets.PhantomSlotWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.common.inventory.handlers.SingleItemStackHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class PhantomWidget
        extends WidgetGroup
        implements IGhostIngredientTarget {
    private final IItemHandlerModifiable itemHandler = new SingleItemStackHandler(1);
    private FluidStack fluidStack = null;
    private PhantomFluidWidget fluidWidget = new PhantomFluidWidget(0, 0, 18, 18, null, null).setFluidStackUpdater(fluid -> {
        this.fluidStack = fluid.copy();
        if (this.fluidStack != null && this.fluidStack.amount > 0) {
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.slotWidget.setVisible(false);
            this.fluidWidget.setVisible(true);
            if (this.onChanged != null) {
                this.onChanged.accept(this.fluidStack);
            }
        }
    }, true).setBackgroundTexture(new ColorRectTexture(new Color(0, 0, 0, 160))).showTip(true).setFluidStackSupplier(() -> this.fluidStack, true);
    private PhantomSlotWidget slotWidget = new PhantomSlotWidget(this.itemHandler, 0, 0, 0){

        @Override
        public boolean isEnabled() {
            return this.isActive();
        }
    };
    private Consumer<Object> onChanged;

    public PhantomWidget(int x, int y, Object defaultObj) {
        super(x, y, 18, 18);
        this.slotWidget.setChangeListener(() -> {
            if (!this.itemHandler.getStackInSlot(0).isEmpty()) {
                this.fluidStack = null;
                this.fluidWidget.setVisible(false);
                this.slotWidget.setVisible(true);
                if (this.onChanged != null) {
                    this.onChanged.accept(this.itemHandler.getStackInSlot(0));
                }
            }
        }).setBackgroundTexture(new ColorRectTexture(new Color(0, 0, 0, 160)));
        this.addWidget(this.fluidWidget);
        this.addWidget(this.slotWidget);
        if (defaultObj instanceof ItemStack) {
            this.itemHandler.setStackInSlot(0, (ItemStack)defaultObj);
            this.fluidWidget.setVisible(false);
            this.slotWidget.setVisible(true);
        } else if (defaultObj instanceof FluidStack) {
            this.fluidStack = (FluidStack)defaultObj;
            this.slotWidget.setVisible(false);
            this.fluidWidget.setVisible(true);
        }
    }

    public PhantomWidget setChangeListener(Consumer<Object> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    public void setObject(FluidStack fluid) {
        if (fluid != null) {
            this.fluidStack = fluid.copy();
            if (this.fluidStack != null && this.fluidStack.amount > 0) {
                this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                this.slotWidget.setVisible(false);
                this.fluidWidget.setVisible(true);
                if (this.onChanged != null) {
                    this.onChanged.accept(this.fluidStack);
                }
            }
        }
    }

    public void setObject(ItemStack item) {
        if (item != null && !item.isEmpty()) {
            ItemStack copy = item.copy();
            copy.setCount(1);
            this.itemHandler.setStackInSlot(0, copy);
            this.fluidStack = null;
            this.fluidWidget.setVisible(false);
            this.slotWidget.setVisible(true);
            if (this.onChanged != null) {
                this.onChanged.accept(copy);
            }
        }
    }

    @Override
    public List<IGhostIngredientHandler.Target<?>> getPhantomTargets(Object ingredient) {
        if (!this.isVisible()) {
            return Collections.emptyList();
        }
        ArrayList targets = new ArrayList();
        for (Widget widget : this.widgets) {
            if (!(widget instanceof IGhostIngredientTarget)) continue;
            targets.addAll(((IGhostIngredientTarget)((Object)widget)).getPhantomTargets(ingredient));
        }
        return targets;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseOverElement(mouseX, mouseY)) {
            ItemStack itemStack = this.gui.entityPlayer.inventory.getItemStack();
            if (!itemStack.isEmpty()) {
                FluidStack fluidStack;
                IFluidHandler handlerItem = (IFluidHandler)itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (handlerItem != null && handlerItem.getTankProperties().length > 0 && (fluidStack = handlerItem.getTankProperties()[0].getContents()) != null) {
                    this.setObject(fluidStack);
                    return true;
                }
                this.setObject(itemStack);
                return true;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}