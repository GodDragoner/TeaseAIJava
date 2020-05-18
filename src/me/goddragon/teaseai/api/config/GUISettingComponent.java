package me.goddragon.teaseai.api.config;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

public abstract class GUISettingComponent {

    protected Label settingLabel;
    protected String description;
    protected Node setting;
    protected int columnNumber = -1;
    protected HoveringTooltip tooltip;

    public GUISettingComponent(String settingString, int columnNumber) {
        this(settingString);
        this.columnNumber = columnNumber;
    }

    public GUISettingComponent(String settingString) {
        settingString += ":";
        settingLabel = new Label(settingString);
    }

    public GUISettingComponent(String settingString, String description, int columnNumber) {
        this(settingString, description);
        this.columnNumber = columnNumber;
    }

    public GUISettingComponent(String settingString, String description) {
        this(settingString);
        this.description = description;
        //Tooltip.install(settingLabel, makeBubble(new Tooltip(this.description)));
        HoveringTooltip hoveringTooltip = new HoveringTooltip(0);
        //hoveringTooltip.setDuration(999999);
        hoveringTooltip.setText(description);
        //hoveringTooltip.isHoveringTargetSecondary(hoveringTooltip.getGraphic());
        tooltip = hoveringTooltip;
        settingLabel.setTooltip(makeBubble(hoveringTooltip));
    }

    private Tooltip makeBubble(HoveringTooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 14px;");
        tooltip.setMaxWidth(300);
        tooltip.setWrapText(true);
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        tooltip.setShowDelay(new Duration(100));

        return tooltip;
    }

    public Label getLabel() {
        return settingLabel;
    }

    public Node getSetting() {
        return setting;
    }

    public int getColumnID() {
        return columnNumber;
    }
    
    public void setSetting(Node toSet)
    {
        setting = toSet;
        //tooltip.isHoveringTargetPrimary(settingLabel);
        tooltip.addHoveringTarget(settingLabel);
    }
}
