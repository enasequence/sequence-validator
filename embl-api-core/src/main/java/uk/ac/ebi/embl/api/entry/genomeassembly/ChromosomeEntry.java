/*
 * Copyright 2018 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.ac.ebi.embl.api.entry.genomeassembly;

public class ChromosomeEntry extends GCSEntry
{
	
	private String chromosomeName;
	private String chromosomeType;
	private String chromosomeLocation;
	private String objectName;
	
	public String getObjectName()
	{
		return objectName;
	}
	
	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}
	
	public String getChromosomeName()
	{
		return chromosomeName;
	}
	
	public void setChromosomeName(String chromosomeName)
	{
		this.chromosomeName = chromosomeName;
	}
	
	public String getChromosomeType()
	{
		return chromosomeType;
	}
	
	public void setChromosomeType(String chromosomeType)
	{
		this.chromosomeType = chromosomeType;
	}
	
	public String getChromosomeLocation()
	{
		return chromosomeLocation;
	}
	
	public void setChromosomeLocation(String chromosomeLocation)
	{
		this.chromosomeLocation = chromosomeLocation;
	}
	
	public boolean equals(ChromosomeEntry entry)
	{
		return (entry.getObjectName()!=null&&this.getObjectName()!=null&&this.getObjectName().equals(entry.getObjectName()))
				&&(entry.getChromosomeName()!=null&&this.getChromosomeName()!=null&&this.getChromosomeName().equals(entry.getChromosomeName()))
				&&(entry.getChromosomeLocation()!=null&&this.getChromosomeLocation()!=null&&this.getChromosomeLocation().equals(entry.getChromosomeLocation()))
				&&(entry.getChromosomeType()!=null&&this.getChromosomeType()!=null&&this.getChromosomeType().equals(entry.getChromosomeType()));
	}
}
