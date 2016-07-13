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
package uk.ac.ebi.embl.api.entry.reference;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ElectronicReference extends Publication 
		implements Comparable<ElectronicReference> {
	
	private static final long serialVersionUID = 6743707173965018448L;
	
	private String text;

	protected ElectronicReference() {
	}

	protected ElectronicReference(Publication publication) {
		this();
		if(publication != null) {
			setTitle(publication.getTitle());
			setConsortium(publication.getConsortium());
			addAuthors(publication.getAuthors());
			addXRefs(publication.getXRefs());
		}
	}
	
	protected ElectronicReference(String title, String text) {
		this();
		setTitle(title);
		this.text = text;
	}
		
	public String getText() {
		return text;
	}

	public void setText(String firstPage) {
		this.text = firstPage;
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(super.hashCode());
		return builder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ElectronicReference) {
			final ElectronicReference other = (ElectronicReference) obj;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.appendSuper(super.equals(other));
			builder.append(this.text, other.text);
			return builder.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.appendSuper(super.toString());
		builder.append("text", text);
		return builder.toString();
	}	
	
	public int compareTo(ElectronicReference o) {
		final ElectronicReference other = (ElectronicReference) o;
		final CompareToBuilder builder = new CompareToBuilder();
		builder.appendSuper(super.compareTo(other));
		builder.append(this.text, other.text);
		return builder.toComparison();
	}	
	
}
