import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with predetermined minimum and maximum values.
 * 
 * <p>Note that RangeSlider makes use of the default BoundedRangeModel, which 
 * supports an inner range defined by a value and an extent.  The upper value
 * returned by RangeSlider is simply the lower value plus the extent.</p>
 */
public class RangeSlider extends JSlider {

    Controller controller;

    /**
     * Constructs a RangeSlider with default minimum and maximum values of 0
     * and 100.
     */
    public RangeSlider() {
        initSlider();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            RangeSlider source = (RangeSlider)e.getSource();
            controller.movedSlider(source);   
        }
    }


    /**
     * Constructs a RangeSlider with the specified default minimum and maximum 
     * values.
     */
    public RangeSlider(int min, int max) {
        super(min, max);
        initSlider();
    }

    /**
     * Initializes the slider by setting default properties.
     */
    private void initSlider() {
        setOrientation(HORIZONTAL);
    }

    public void addListener() {
        addChangeListener(new SliderListener());
    }

    /**
     * Overrides the superclass method to install the UI delegate to draw two
     * thumbs.
     */
    @Override
    public void updateUI() {
        setUI(new RangeSliderUI(this));
        // Update UI for slider labels.  This must be called after updating the
        // UI of the slider.  Refer to JSlider.updateUI().
        updateLabelUIs();
    }

    /**
     * Returns the lower value in the range.
     */
    @Override
    public int getValue() {
        return super.getValue();
    }

    /**
     * Sets the lower value in the range.
     */
    @Override
    public void setValue(int value) {
        int oldValue = getValue();
        if (oldValue == value) {
            return;
        }

        // Compute new value and extent to maintain upper value.
        int oldExtent = getExtent();
        int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
        int newExtent = oldExtent + oldValue - newValue;

        // Set new value and extent, and fire a single change event.
        getModel().setRangeProperties(newValue, newExtent, getMinimum(), 
            getMaximum(), getValueIsAdjusting());
    }

    /**
     * Returns the upper value in the range.
     */
    public int getUpperValue() {
        return getValue() + getExtent();
    }

    /**
     * Sets the upper value in the range.
     */
    public void setUpperValue(int value) {
        // Compute new extent.
        int lowerValue = getValue();
        int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);
        
        // Set extent to set upper value.
        setExtent(newExtent);
    }

}
