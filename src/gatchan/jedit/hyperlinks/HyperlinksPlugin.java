/*
 * HyperlinksPlugin.java - The Hyperlinks plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 * @author Matthieu Casanova
 * @version $Id: HighlightPlugin.java,v 1.20 2006/06/21 09:40:32 kpouer Exp $
 */
public class HyperlinksPlugin extends EBPlugin
{
	public void start()
	{
		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				initTextArea(textArea);
			}
			view = view.getNext();
		}
	}

	public void stop()
	{
		Buffer[] buffers = jEdit.getBuffers();
		for (int i = 0; i < buffers.length; i++)
		{
			buffers[i].unsetProperty(HyperlinkSource.PROPERTY);
		}

		View view = jEdit.getFirstView();
		while (view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for (int i = 0; i < panes.length; i++)
			{
				JEditTextArea textArea = panes[i].getTextArea();
				uninitTextArea(textArea);
			}
			view = view.getNext();
		}
	}

	private static void uninitTextArea(JEditTextArea textArea)
	{
		HyperlinkManager clientProperty = (HyperlinkManager) textArea.getClientProperty(HyperlinkManager.class);

		clientProperty.dispose();
	}

	private static void initTextArea(JEditTextArea textArea)
	{
		textArea.putClientProperty(HyperlinkManager.class, new HyperlinkManager(textArea));
	}


	public void handleMessage(EBMessage message)
	{
		if (message instanceof EditPaneUpdate)
		{
			handleEditPaneMessage((EditPaneUpdate) message);
		}
		else if (message instanceof BufferUpdate)
		{
			BufferUpdate bufferUpdate = (BufferUpdate) message;
			Buffer buffer = bufferUpdate.getBuffer();
			if (bufferUpdate.getWhat() == BufferUpdate.LOADED)
			{
				setSource(buffer);
			}
			if (bufferUpdate.getWhat() == BufferUpdate.CLOSED)
			{
				buffer.unsetProperty(HyperlinkSource.PROPERTY);
			}
			else if (bufferUpdate.getWhat() == BufferUpdate.PROPERTIES_CHANGED)
			{
				buffer.unsetProperty(HyperlinkSource.PROPERTY);
				setSource(buffer);
			}
		}
		else if (message instanceof PropertiesChanged)
		{
			HyperlinkTextAreaPainter.color = jEdit.getColorProperty("options.hyperlink.color.value");
		}
	}

	private static void setSource(Buffer buffer)
	{
		String prop = "mode." + buffer.getMode() + HyperlinkSource.PROPERTY;
		String sourceName = jEdit.getProperty(prop);
		HyperlinkSource service = null;
		if (sourceName != null)
		{
			service = (HyperlinkSource) ServiceManager.getService(HyperlinkSource.SERVICE, sourceName);
		}

		if (service == null)
		{
			sourceName = jEdit.getProperty(HyperlinkSource.DEFAULT_PROPERTY);
			if (sourceName != null)
				service = (HyperlinkSource) ServiceManager.getService(HyperlinkSource.SERVICE, sourceName);
		}
		if (service != null)
		{
			buffer.setProperty(HyperlinkSource.PROPERTY, service);
		}
	}


	private static void handleEditPaneMessage(EditPaneUpdate message)
	{
		JEditTextArea textArea = message.getEditPane().getTextArea();
		Object what = message.getWhat();

		if (what == EditPaneUpdate.CREATED)
		{
			initTextArea(textArea);
		}
		else if (what == EditPaneUpdate.DESTROYED)
		{
			uninitTextArea(textArea);
		}
	}

}