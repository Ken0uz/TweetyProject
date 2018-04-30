/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 /**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * @author Matthias Thimm
 * This reasoner for ABA theories performs inference on the ideal extension.
 * @param <T>	the language of the underlying ABA theory
 */
public class WellFoundedReasoner<T extends Formula> extends GeneralABAReasoner<T> {
	
	/**
	 * Creates a new well-founded reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public WellFoundedReasoner(int inferenceType) {
		super(inferenceType);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.GeneralABAReasoner#computeExtensions()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions(ABATheory<T> abat) {
		Collection<Collection<Assumption<T>>> complete_exts = new CompleteReasoner(getInferenceType()).computeExtensions(abat);
		Iterator<Collection<Assumption<T>>> iter = complete_exts.iterator();
		Collection<Assumption<T>> ext = iter.hasNext() ? iter.next() : new HashSet<Assumption<T>>();
		while (iter.hasNext()) {
			ext.retainAll(iter.next());
		}
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		result.add(ext);
		return result;
	}
}
