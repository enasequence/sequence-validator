package uk.ac.ebi.embl.agp.writer;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.embl.api.entry.AgpRow;

public class AGPRowWriter
{
	private AgpRow agpRow;
	private Writer writer;
	private String agpRowString = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
	public AGPRowWriter(AgpRow agpRow, Writer writer)
	{
		this.agpRow =agpRow;
		this.writer= writer;
	}
	
	public void write() throws IOException
	{
		String rowString= null;
		rowString= agpRow.isGap()? 
				   String.format(agpRowString,
					       agpRow.getObject(),
					       agpRow.getObject_beg(),
					       agpRow.getObject_end(),
					       agpRow.getPart_number(),
					       agpRow.getComponent_type_id(),
					       agpRow.getGap_length(),
					       agpRow.getGap_type(),
					       agpRow.getLinkage(),
					       StringUtils.join(agpRow.getLinkageevidence(), ";")):
					String.format(agpRowString,
				           agpRow.getObject(),
				           agpRow.getObject_beg(),
				           agpRow.getObject_end(),
				           agpRow.getPart_number(),
				           agpRow.getComponent_type_id(),
				           agpRow.getComponent_id(),
				           agpRow.getComponent_beg(),
				           agpRow.getComponent_end(),
				           agpRow.getOrientation());
		  writer.write(rowString.trim());
	}
}
