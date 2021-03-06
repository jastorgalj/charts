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
 * The chart's subtitle
 */
@SuppressWarnings("serial")
public class SubTitle extends AbstractTitle {

    /**
     * Constructs an empty subtitle
     */
    public SubTitle() {
    }

    /**
     * Constructs a subtitle using the given text
     * 
     * @param text
     */
    public SubTitle(String text) {
        super(text);
    }
}
