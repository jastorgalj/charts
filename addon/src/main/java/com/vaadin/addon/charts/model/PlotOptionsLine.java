package com.vaadin.addon.charts.model;

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
 * Plot options for {@link ChartType#LINE} graphs
 * 
 * @see AbstractPlotOptions
 * @see AbstractLinePlotOptions
 */
public class PlotOptionsLine extends AbstractLinePlotOptions {

    private StepType step;

    @Override
    public ChartType getChartType() {
        return ChartType.LINE;
    }

    /**
     * @see #setStepType(StepType)
     * 
     * @return the stepType
     */
    public StepType getStepType() {
        return step;
    }

    /**
     * Sets the step type used in the line. Defaults to normal line.
     * 
     * @param stepType
     *            the stepType to set
     */
    public void setStepType(StepType stepType) {
        this.step = stepType;
    }

}
