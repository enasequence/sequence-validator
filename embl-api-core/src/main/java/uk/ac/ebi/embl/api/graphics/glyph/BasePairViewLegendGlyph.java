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

import java.awt.Color;

public class BasePairViewLegendGlyph extends HorizontalCompositeGlyph {
    public BasePairViewLegendGlyph(Canvas canvas) {
        super(canvas);
    }

    @Override
	protected void initGlyph() {
    	AcidGlyph acidGlyph = new AcidGlyph(getCanvas());            
    	RectGlyph rectGlyph = new RectGlyph(getCanvas());
    	rectGlyph.setColor(new Color(0x00, 0x00, 0x00));
    	rectGlyph.setFill(false);
    	rectGlyph.setHeight(acidGlyph.getHeight());
    	rectGlyph.setWidth(acidGlyph.getWidth() - 1);
    	addGlyph(rectGlyph);
    	addGlyph(new HorizontalSpacerGlyph(getCanvas()));
    	TextGlyph textGlyph = new TextGlyph(getCanvas());
    	textGlyph.setText("Alternative start codon");
    	addGlyph(textGlyph);
    }
}
