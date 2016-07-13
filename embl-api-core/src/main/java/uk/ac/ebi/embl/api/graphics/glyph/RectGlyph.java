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
import java.awt.Stroke;

public class RectGlyph extends SimpleGlyph {
    public RectGlyph(Canvas canvas) {
    	super(canvas);
    }

    private GlyphPoint point = new GlyphPoint();
    private Stroke stroke;
    private int width = 0;
    private int height = 0;
    private boolean fill = false;

    @Override
    protected void drawSimpleGlyph(Graphics2D g) {
    	Stroke oldStroke = g.getStroke();
    	if (stroke != null)
    		g.setStroke(stroke);    	
        g.drawRect(getPoint().getX(), getPoint().getY(), getWidth(), 
                   getHeight());
        if (isFill()) {
            g.fillRect(getPoint().getX(), getPoint().getY(), getWidth(), 
                       getHeight());
        }
        if (stroke != null) 
        	g.setStroke(oldStroke);        
    }

    @Override
	public int getHeight() {
    	return height;
    }

    @Override
	public int getWidth() {
    	return width;
    }

    public GlyphPoint getPoint() {
        return point;
    }

    public void setPoint(GlyphPoint point) {
        this.point = point;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }
    
	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}    
}
