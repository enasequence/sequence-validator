/*******************************************************************************
 * Copyright 2012 EMBL-EBI, Hinxton outstation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package uk.ac.ebi.embl.api.graphics.glyph;

import java.awt.Graphics2D;

public class HorizontalSpacerGlyph extends SimpleGlyph {
    public HorizontalSpacerGlyph(Canvas canvas) {
    	super(canvas);
    }

    public HorizontalSpacerGlyph(Canvas canvas, int width) {
    	super(canvas);
    	this.width = width;
    }
        
    public static final int DEFAULT_WIDTH = 2;
    private int width = DEFAULT_WIDTH;

    @Override
    protected void drawSimpleGlyph(Graphics2D g) {
    }
    
    @Override
	public int getHeight() {
        return 0;
    }

    @Override
	public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
