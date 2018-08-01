package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.scene.control.skin.SliderSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;

import java.util.Iterator;

public class MTSliderSkin extends SliderSkin {
    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("mtpressed");
    private StackPane thumb;
    private BooleanProperty touchPressed;

    public MTSliderSkin(Slider slider) {
        super(slider);
        Iterator var2 = this.getChildren().iterator();

        while (var2.hasNext()) {
            Node child = (Node) var2.next();
            if (child.getStyleClass().contains("thumb")) {
                this.thumb = (StackPane) child;
                break;
            }
        }

        EventHandler<TouchEvent> touchHandler = event -> {
            Boolean value = !MTSliderSkin.this.touchPressed.getValue();
            MTSliderSkin.this.touchPressed.set(value);
        };
        this.thumb.addEventHandler(TouchEvent.TOUCH_PRESSED, touchHandler);
        this.thumb.addEventHandler(TouchEvent.TOUCH_RELEASED, touchHandler);
        this.touchPressed = new BooleanPropertyBase(false) {
            protected void invalidated() {
                MTSliderSkin.this.thumb.pseudoClassStateChanged(MTSliderSkin.PRESSED_PSEUDO_CLASS, this.get());
                new Rectangle();
            }

            public Object getBean() {
                return MTSliderSkin.this.thumb;
            }

            public String getName() {
                return "pressed";
            }
        };
    }
}
