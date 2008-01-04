/*
 *  WindowResponder.java
 *  SwingOSC
 *
 *  Copyright (c) 2005-2008 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either
 *	version 2, june 1991 of the License, or (at your option) any later version.
 *
 *	This software is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *	General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public
 *	License (gpl.txt) along with this software; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 *
 *
 *  Changelog:
 *		12-Nov-05	created
 *		02-Feb-07	added ComponentListener so we can track window resizing
 */
 
package de.sciss.swingosc;

import java.awt.Rectangle;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.*;

import de.sciss.net.OSCMessage;


/**
 *	An <code>WindowResponder</code> is created for a frame component.
 *	When instantiating, the responder starts to listen to
 *	<code>WindowEvent</code>s fired from that component, until
 *	<code>remove</code> is called. When an event occurs, the
 *	responder will send a <code>/window</code> OSC message to
 *	the client that has created the responder, with the first
 *	argument being the component's ID, followed by the state change,
 *	e.g. <code>opened</code>, <code>closing</code>, <code>iconified</code> etc.,
 *	followed key-value pairs of properties. The property key names are specified
 *	in he constructor.
 *
 *	@author		Hanns Holger Rutz
 *	@version	0.53, 02-Jul-07
 */
public class WindowResponder
extends AbstractResponder
implements ComponentListener, WindowListener, WindowFocusListener
{
	private static final Class[] listenerClasses =
		{ WindowListener.class, WindowFocusListener.class, ComponentListener.class };
	private static final String[] listenerNames	 =
		{ "WindowListener", "WindowFocusListener", "ComponentListener" };

	private final Object[] shortReplyArgs = new Object[ 2 ];

	public WindowResponder( Object objectID )
	throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		super( objectID, 6 );
		add();

		shortReplyArgs[ 0 ]	= replyArgs[ 0 ];
	}

	protected Class[] getListenerClasses()
	{
		return listenerClasses;
	}
	
	protected String[] getListenerNames()
	{
		return listenerNames;
	}
	
	protected String getOSCCommand()
	{
		return "/window";
	}

	private void reply( String stateName, ComponentEvent e )
	{
		Rectangle b = e.getComponent().getBounds();
		
		try {
			// [ "/window", <componentID>, <state>, <x>, <y>, <w>, <h> ]
			replyArgs[ 1 ] = stateName;
			replyArgs[ 2 ] = new Integer( b.x );
			replyArgs[ 3 ] = new Integer( b.y );
			replyArgs[ 4 ] = new Integer( b.width );
			replyArgs[ 5 ] = new Integer( b.height );
			client.reply( new OSCMessage( getOSCCommand(), replyArgs ));
		}
		catch( IOException ex ) {
			SwingOSC.printException( ex, getOSCCommand() );
		}
	}
		
	private void replyShort( String stateName )
	{
		try {
			// [ "/window", <componentID>, <state> ]
			shortReplyArgs[ 1 ] = stateName;
			client.reply( new OSCMessage( getOSCCommand(), shortReplyArgs ));
		}
		catch( IOException ex ) {
			SwingOSC.printException( ex, getOSCCommand() );
		}
	}
		
	// -------- WindowListener interface --------

	public void windowOpened( WindowEvent e )
	{
		replyShort( "opened" );
	}

	public void windowClosing( WindowEvent e )
	{
		replyShort( "closing" );
	}
	
	public void windowClosed( WindowEvent e )
	{
		replyShort( "closed" );
	}

	public void windowIconified( WindowEvent e )
	{
		replyShort( "iconified" );
	}

	public void windowDeiconified( WindowEvent e )
	{
		replyShort( "deiconified" );
	}
	
	public void windowActivated( WindowEvent e )
	{
		replyShort( "activated" );
	}
	
	public void windowDeactivated( WindowEvent e )
	{
		replyShort( "deactivated" );
	}

	// -------- WindowFocusListener interface --------
	
	public void windowGainedFocus( WindowEvent e )
	{
		replyShort( "gainedFocus" );
	}

	public void windowLostFocus( WindowEvent e )
	{
		replyShort( "lostFocus" );
	}

	// -------- ComponentListener interface --------

	public void componentResized( ComponentEvent e )
	{
		reply( "resized", e );
	}

	public void componentMoved( ComponentEvent e )
	{
		reply( "moved", e );
	}
	
	public void componentShown( ComponentEvent e ) {}
	public void componentHidden( ComponentEvent e ) {}
}