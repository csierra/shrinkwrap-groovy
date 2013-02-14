package org.jboss.shrinkwrap.groovy


/**
 * Class that holds all the closure definitions. It also delegates all method invocations
 * inside the closures to the actual implementation. 
 * User of this LazyBuilder needs to call <link>LazyBuilder#build()</link> for getting the
 * actual artifact built. 
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
public abstract class LazyBuilder<T> {
		
	def T instance;
	def closures = []
	
	private <T> Closure<T> prepareClosure(Closure<T> closure) {
		def Closure<T> c = closure.clone()
		c.resolveStrategy = Closure.DELEGATE_FIRST
		c.delegate = this
		return c
	}
	
	def LazyBuilder(instance) {
		this.instance = instance;
	}
	
	def appendClosures(Closure<?> ... closures) {
		for (Closure<?> c: closures) {
			this.closures.add(this.prepareClosure (c))
		}
	}
	
	def abstract methodMissing(String name, args)
	
	def include(Closure<?> i) {
		prepareClosure(i)()
	}
	
	def <T> T build() {
		this.closures.each {
			it()
		}
		return this.instance
	}
	
	def leftShift (Closure<?> c) { 
		this.appendClosures(c)
		return this
	}

}
