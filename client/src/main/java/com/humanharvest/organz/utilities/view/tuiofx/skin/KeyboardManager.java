package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.css.StyleManager;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Vector;

public class KeyboardManager<T extends TextInputControl> {
    private static final KeyboardManager INSTANCE = new KeyboardManager();
    private HashMap<String, OnScreenKeyboard> kbGroup = null;
    private HashMap<String, Vector<T>> groupTextFields = null;
    private HashMap<Node, OnScreenKeyboard> kbTextInput = null;

    private KeyboardManager() {
        String fontUrl = this.getClass().getResource("/font/FontKeyboardFX.ttf").toExternalForm();
        Font f = Font.loadFont(fontUrl, -1.0D);
        String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();
        StyleManager.getInstance().addUserAgentStylesheet(css);
    }

    public static KeyboardManager getInstance() {
        return INSTANCE;
    }

    public OnScreenKeyboard getKeyboard(Node textInput) {
        if (this.kbTextInput == null) {
            this.kbTextInput = new HashMap();
        }

        return this.kbTextInput.computeIfAbsent(textInput, k -> new OnScreenKeyboard());
    }

    public OnScreenKeyboard getKeyboard(String groupName) {
        if (this.kbGroup == null) {
            this.kbGroup = new HashMap();
        }

        return this.kbGroup.computeIfAbsent(groupName, k -> new OnScreenKeyboard());
    }

    public String getNodeKbGroup(Node node) {
        Object groupValue = node.getProperties().get("kbGroup");
        String groupStr = null;
        if (groupValue instanceof String) {
            groupStr = ((String) groupValue).toLowerCase();
        }

        return groupStr;
    }

    public Boolean isUsingFocusAreaProp(Node node) {
        Object useFocusAreaPropValue = node.getProperties().get("useFocusArea");
        Boolean useFocusAreaBoolean = true;
        if (useFocusAreaPropValue instanceof String) {
            useFocusAreaBoolean = Boolean.valueOf(useFocusAreaPropValue.toString());
        } else if (useFocusAreaPropValue instanceof Boolean) {
            return (Boolean) useFocusAreaPropValue;
        }

        return useFocusAreaBoolean;
    }
}
