package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive

/**
 * Class that holds all the closure definitions. It also delegates all method invocations
 * inside the closures to the actual Archive implementation. 
 * User of this LazyBuilder needs to call <link>LazyBuilder#build()</link> for getting the
 * actual archive built. 
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
public class LazyBuilder {
		
	def include(Closure<?> i) {
		ShrinkWrapGroovy.executeClosureonDelegate(this, i)
	}
	
	@Deprecated def build() {
		return this.instance
	}
	
	def leftShift (Closure<?> c) { 
		ShrinkWrapGroovy.executeClosureonDelegate(this, c)
	}

}
