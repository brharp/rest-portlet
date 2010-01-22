/*
 * $Id: ReadyStateChangeListener.java,v 1.1 2007/08/09 16:50:39 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.ws;

/**
 * Asynchronous callback interface for receiving state change
 * notifications from {@link XMLHttpRequestObject}.
 * <p>
 * Consider this code untested.
 */
public interface ReadyStateChangeListener
{
    public void onReadyStateChange(XMLHttpRequestObject req);
}
