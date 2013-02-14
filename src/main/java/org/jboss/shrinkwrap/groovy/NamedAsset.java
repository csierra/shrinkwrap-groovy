package org.jboss.shrinkwrap.groovy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that allow us to return Lists using as operator in Groovy
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
public class NamedAsset extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NamedAsset(Collection<Object> c) {
		this.addAll(c);
	}

}
