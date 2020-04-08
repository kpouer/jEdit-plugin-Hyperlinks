/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

import org.gjt.sp.jedit.Buffer;

import java.util.List;

/**
 * The fallback hyperlink source lets you have several hyperlinks sources for a buffer.
 * If the first one do not return a link, the next one is tested until we have a link or
 * are at the end of the source list.
 * @author Matthieu Casanova
 */
public class FallbackHyperlinkSource implements HyperlinkSource
{
    private final List<HyperlinkSource> hyperlinkSources;

    public FallbackHyperlinkSource(List<HyperlinkSource> hyperlinkSources)
    {
        this.hyperlinkSources = hyperlinkSources;
    }

    @Override
    public Hyperlink getHyperlink(Buffer buffer, int offset)
    {
        for (HyperlinkSource hyperlinkSource : hyperlinkSources)
        {
            Hyperlink hyperlink = hyperlinkSource.getHyperlink(buffer, offset);
            if (hyperlink != null)
                return hyperlink;
        }
        return null;
    }
}
