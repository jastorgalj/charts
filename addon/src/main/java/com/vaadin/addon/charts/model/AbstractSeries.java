package com.vaadin.addon.charts.model;

import com.vaadin.addon.charts.Chart;

/*
 * #%L
 * Vaadin Charts
 * %%
 * Copyright (C) 2012 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv2 along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0>.
 * #L%
 */

/**
 * Abstract base class for series
 */
public abstract class AbstractSeries extends AbstractConfigurationObject
        implements Series {

    private String name;
    private String stack;

    private AbstractPlotOptions plotOptions;

    private boolean visible = true;

    private transient Configuration configuration;

    private Integer xAxis;
    private Integer yAxis;

    public AbstractSeries() {
    }

    /**
     * Constructs a named series
     */
    public AbstractSeries(String name) {
        setName(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see #setStack(String)
     */
    public String getStack() {
        return stack;
    }

    /**
     * This option allows grouping series in a stacked chart. The stack option
     * can be a string or a number or anything else, as long as the grouped
     * series' stack options match each other. Defaults to null.
     * 
     * @param stack
     */
    public void setStack(String stack) {
        this.stack = stack;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the {@link Configuration} that this series is linked to.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the plot options related to this specific series. This is needed
     * e.g. in combined charts.
     * 
     * @return
     */
    public AbstractPlotOptions getPlotOptions() {
        return plotOptions;
    }

    /**
     * Sets the plot options for this specific series. The type of the plot
     * options also explicitly sets the chart type used when rendering this
     * particular series. If plot options is null, the component wide chart type
     * is used.
     * <p>
     * Options that are not defined at this level will be inherited from the
     * chart and theme levels.
     * 
     * @param plotOptions
     */
    public void setPlotOptions(AbstractPlotOptions plotOptions) {
        this.plotOptions = plotOptions;
    }

    /**
     * Control the visibility of the series. Although the series is invisible in
     * the client it is still "cached" there and thus setting it visible happens
     * quickly.
     * 
     * @see #setVisible(boolean, boolean)
     * 
     * @param visible
     *            true if the series should be displayed, false if not
     */
    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    /**
     * Control the visibility of the series.
     * <p>
     * With this version of the method developer can disable immediate chart
     * update for already rendered chart, if e.g. multiple changes to the chart
     * configuration are wished to be applied at once.
     * 
     * @see #setVisible(boolean)
     * @see Chart#drawChart()
     * 
     * @param visible
     *            true if the series should be displayed, false if not
     * @param updateChartImmediately
     *            Updates the chart immediately if true.
     */
    public void setVisible(boolean visible, boolean updateChartImmediately) {
        boolean doDynamicChange = updateChartImmediately
                && getConfiguration() != null && this.visible != visible;
        this.visible = visible;
        if (doDynamicChange) {
            getConfiguration().fireSeriesEnabled(this, visible);
        }

    }

    /**
     * @return true if the series is displayed on the client
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @see #setxAxis(Number)
     * @return The index of the X-axis that this data series is bound to.
     *         Returns null if undefined.
     */
    public Integer getxAxis() {
        return xAxis;
    }

    /**
     * When using dual or multiple X-axes, this number defines which X-axis the
     * particular series is connected to. It refers to the index of the axis in
     * the X-axis array, with 0 being the first. Defaults to 0.
     * 
     * @param xAxis
     *            The index of the X-axis to bind this data series to.
     */
    public void setxAxis(Integer xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * @see #setyAxis(Number)
     * @return The index of the Y-axis that this data series is bound to.
     *         Returns null if undefined.
     */
    public Integer getyAxis() {
        return yAxis;
    }

    /**
     * When using dual or multiple Y-axes, this number defines which Y-axis the
     * particular series is connected to. It refers to the index of the axis in
     * the Y-axis array, with 0 being the first. Defaults to 0.
     * 
     * @param yAxis
     *            The index of the Y-axis to bind this data series to.
     */
    public void setyAxis(Integer yAxis) {
        this.yAxis = yAxis;
    }

    /**
     * * When using dual or multiple Y-axes, this number defines which Y-axis
     * the particular series is connected to.
     * 
     * <p>
     * Note, that this method cannot be used until series and axis are both
     * attached to same configuration object.
     * 
     * @see #setyAxis(Number)
     * 
     * @param secondaryAxis
     */
    public void setyAxis(YAxis secondaryAxis) {
        if (configuration == null) {
            throw new IllegalStateException(
                    "This method must be called only when series is attached to configuration options");
        }
        int indexOf = configuration.getyAxes().getAxes().indexOf(secondaryAxis);
        if (indexOf == -1) {
            throw new IllegalStateException(
                    "This method can only be used if axis is already attached to the same configuration object");
        }
        setyAxis(indexOf);
    }

}
