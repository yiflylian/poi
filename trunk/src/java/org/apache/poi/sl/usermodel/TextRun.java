/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.sl.usermodel;

import java.awt.Color;

import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;

/**
 * Some text.
 */
public interface TextRun {
    enum TextCap {
        NONE,
        SMALL,
        ALL
    }

    String getRawText();
    void setText(String text);

    TextCap getTextCap();

    /**
     * Returns the font color.
     * This usually returns a {@link SolidPaint}, but but also other classes are possible
     *
     * @return the font color/paint
     *
     * @see org.apache.poi.sl.draw.DrawPaint#getPaint(java.awt.Graphics2D, PaintStyle)
     * @see SolidPaint#getSolidColor()
     * @see org.apache.poi.sl.draw.DrawPaint#applyColorTransform(ColorStyle)
     */
    PaintStyle getFontColor();

    /**
     * Sets the (solid) font color - convenience function
     *
     * @param color the color
     */
    void setFontColor(Color color);

    /**
     * Sets the font color
     *
     * @param color the color
     *
     * @see org.apache.poi.sl.draw.DrawPaint#createSolidPaint(Color)
     */
    void setFontColor(PaintStyle color);


    /**
     * Returns the font size which is either set directly on this text run or
     * given from the slide layout
     *
     * @return font size in points or null if font size is not set.
     */
    Double getFontSize();

    /**
     * Sets the font size directly on this text run, if null is given, the
     * font size defaults to the values given from the slide layout
     *
     * @param fontSize font size in points, if null the underlying fontsize will be unset
     */
    void setFontSize(Double fontSize);

    /**
     * @return  font family or null if not set
     */
    String getFontFamily();

    /**
     * Specifies the typeface, or name of the font that is to be used for this text run.
     *
     * @param typeface  the font to apply to this text run.
     * The value of <code>null</code> unsets the Typeface attrubute from the underlying xml.
     */
    void setFontFamily(String typeface);

    /**
     * @return true, if text is bold
     */
    boolean isBold();

    /**
     * Sets the bold state
     *
     * @param bold set to true for bold text, false for normal weight
     */
    void setBold(boolean bold);
    
    /**
     * @return true, if text is italic
     */
    boolean isItalic();

    /**
     * Sets the italic state
     *
     * @param italic set to true for italic text, false for non-italics
     */
    void setItalic(boolean italic);

    /**
     * @return true, if text is underlined
     */
    boolean isUnderlined();

    /**
     * Sets the underlined state
     *
     * @param underlined set to true for underlined text, false for no underlining
     */
    void setUnderlined(boolean underlined);

    /**
     * @return true, if text is stroked
     */
    boolean isStrikethrough();

    /**
     * Sets the strikethrough state
     *
     * @param stroked set to true for stroked text, false for no stroking
     */
    void setStrikethrough(boolean stroked);

    /**
     * @return true, if text is sub scripted
     */
    boolean isSubscript();

    /**
     * @return true, if text is super scripted
     */
    boolean isSuperscript();

    /**
     * @return the pitch and family id or -1 if not applicable
     */
    byte getPitchAndFamily();

    /**
     * Return the associated hyperlink
     * 
     * @return the associated hyperlink or null if no hyperlink was set
     * 
     * @since POI 3.14-Beta2
     */
    Hyperlink<?,?> getHyperlink();
    
    
    /**
     * Creates a new hyperlink and assigns it to this text run.
     * If the text run has already a hyperlink assigned, return it instead
     *
     * @return the associated hyperlink
     * 
     * @since POI 3.14-Beta2
     */
    Hyperlink<?,?> createHyperlink();
}
